package PB.AutoTests;


import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.awt.*;
import java.io.*;
import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class CustomMethods
{

	ConfigurationVariables configVariables = ConfigurationVariables.getInstance();
	private ScreenRecorder screenRecorder;

	//Старт записи видео
	public ScreenRecorder startRecording(WebDriver driver)
	{
		try
		{
			GraphicsConfiguration gc = GraphicsEnvironment
					.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice()
					.getDefaultConfiguration();

			File dir = new File(configVariables.directoryForRecord);

			//Записываем только область окна драйвера для уменьшения размера видео файла
			Point point = driver.manage().window().getPosition();
			Dimension dimension = driver.manage().window().getSize();

			Rectangle rectangle = new Rectangle(
					point.x,
					point.y,
					dimension.width,
					dimension.height
			);

			this.screenRecorder = new ScreenRecorder(
					gc,
					rectangle,
					new Format(
							MediaTypeKey,
							MediaType.FILE,
							MimeTypeKey,
							MIME_AVI
					),
					new Format(
							MediaTypeKey,
							MediaType.VIDEO,
							EncodingKey,
							ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
							CompressorNameKey,
							ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
							DepthKey,
							24,
							FrameRateKey,
							Rational.valueOf(15),
							QualityKey,
							1.0f,
							KeyFrameIntervalKey,
							15 * 60
					),
					new Format(
							MediaTypeKey,
							MediaType.VIDEO,
							EncodingKey,
							"black",
							FrameRateKey,
							Rational.valueOf(30)
					),
					null,
					dir
			);

			this.screenRecorder.start();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		return screenRecorder;
	}

	//Стоп записи видео
	public void stopRecording(ScreenRecorder screenRecorder, String recordName)
	{
		try
		{
			screenRecorder.stop();

			if (recordName != null)
			{
				SimpleDateFormat dateFormat = new SimpleDateFormat(configVariables.dateFormat);

				File newFileName = new File(
						String.format(
								"%s%s %s" + configVariables.videoFormat,
								configVariables.directoryForRecord,
								recordName,
								dateFormat.format(new Date())
						)
				);

				screenRecorder
						.getCreatedMovieFiles()
						.get(0)
						.renameTo(newFileName);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	//Открыть новую вкладку
	public void openNewTab(WebDriver driver)
	{
		WebElement body = driver.findElement(By.tagName("body"));
		body.sendKeys(Keys.CONTROL + "t");
	}

	//Закрыть вкладку
	public void closeTab(WebDriver driver)
	{
		WebElement body = driver.findElement(By.tagName("body"));
		body.sendKeys(Keys.CONTROL + "w");

		ArrayList <String> tabs = new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(tabs.get(0));

	}

	public int getRandomNumber(int n)
	{
		Random random = new Random();
		int RandomNumber = random.nextInt(n);
		return RandomNumber;
	}

	public void waitForElementRemoved(WebDriver driver, By locator, int timeoutInSeconds, int pollingInterval)
			throws Exception
	{
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds, pollingInterval);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public void waitForElementRemoved(WebDriver driver, WebElement webElement, int timeoutInSeconds, int pollingInterval)
			throws Exception
	{
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

		boolean flag = true;
		int counter = 0;
		while(flag)
		{
			if (counter> (int)(timeoutInSeconds*1000/pollingInterval))
			{
				flag = false;
				throw new Exception ("Ошибка во время выполнения теста. " +
						"В метод waitForElementRemoved передан WebElement " +
						webElement +
						" который не удаляется"
				);
			}
			try
			{
				Thread.sleep(pollingInterval);
				counter++;
				if (!webElement.isDisplayed()) flag = false;
			}
			catch (Exception e)
			{
				flag = false;
			}
		}
		driver.manage().timeouts().implicitlyWait(configVariables.implicitTimeWait, TimeUnit.SECONDS);
	}

	public void waitForElementPresent(WebDriver driver, By locator, int timeoutInSeconds, int pollingInterval)
			throws Exception
	{
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds, pollingInterval);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(locator)));
	}

	public void waitForElementPresent(WebDriver driver, WebElement webElement, int timeoutInSeconds, int pollingInterval)
			throws Exception
	{
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds, pollingInterval);
		wait.until(ExpectedConditions.visibilityOf(webElement));
	}

	public Calendar getCurrentCalendar()
	{
		// http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html
		// get the supported ids for GMT+02:00 ("Среднеевропейское (Центральноевропейское) летнее время")

		String[] ids = TimeZone.getAvailableIDs(+2 * 60 * 60 * 1000);
		// if no ids were returned, something is wrong. get out.
		if (ids.length == 0) System.exit(0);
		// create a (CEST - Central Europe Summer Time Zone) UTC/GMT+2 time zone
		SimpleTimeZone GMT = new SimpleTimeZone(+2 * 60 * 60 * 1000, ids[0]);
		// create a GregorianCalendar with the current date and time
		Calendar calendar = new GregorianCalendar(GMT);
		Date trialTime = new Date();
		calendar.setTime(trialTime);
		//// print out a bunch of interesting things
		// System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		// System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		// System.out.println("DATE: " + calendar.get(Calendar.DATE));
		// System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		// System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		// System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		// System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		//
		// System.out.println("Current Time, with hour reset to 3");
		// calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
		// calendar.set(Calendar.HOUR, 3);
		return calendar;

	}

	public static void delete(File file)  throws IOException
	{
		if(file.isDirectory())
		{
			//directory is empty, then delete it
			if(file.list().length==0)
			{
				file.delete();
				//System.out.println("Directory is deleted : " + file.getAbsolutePath());
			}
			else
			{
				//list all the directory contents
				String files[] = file.list();
				for (String temp : files)
				{
					//construct the file structure
					File fileDelete = new File(file, temp);
					//recursive delete
					delete(fileDelete);
				}
				//check the directory again, if empty then delete it
				if(file.list().length==0)
				{
					file.delete();
					//System.out.println("Directory is deleted : " + file.getAbsolutePath());
				}
			}

		}
		else
		{
			//if file, then delete it
			file.delete();
		}
	}

	public static void deleteFileOrDirectory(File directory)
	{
		//make sure directory exists
		if(!directory.exists())
		{
			//System.out.println("Directory "+directory+" does not exist.");
		}
		else
		{
			try
			{
				delete(directory);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	//convert from UTF-8 -> internal Java String format
	public String convertFromUTF8(String s)
	{
		String out = null;
		try
		{
			out = new String(s.getBytes("Windows-1251"), "UTF-8");
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			return null;
		}
		return out;
	}

	//convert from internal Java String format -> UTF-8
	public String convertToUTF8(String s)
	{
		String out = null;
		try
		{
			out = new String(s.getBytes("UTF-8"), "Windows-1251");
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			return null;
		}
		return out;
	}

	//Проверяем что элемент присутствует и видем
	public void CheckElementPresent(WebElement element) throws InterruptedException
	{
		Assert.assertEquals(true, element.isDisplayed());
		Assert.assertEquals(true, element.isEnabled());
	}

	protected boolean switchToWindowUsingTitle(WebDriver driver, String title)
	{
		String currentWindow = driver.getWindowHandle();
		Set<String> availableWindows = driver.getWindowHandles();
		if (!availableWindows.isEmpty())
		{
			for (String windowId : availableWindows)
			{
				if (driver.switchTo().window(windowId).getTitle().equals(title))
				{
					return true;
				}
				else
				{
					driver.switchTo().window(currentWindow);
				}
			}
		}
		return false;
	}

	protected void switchToCurrentWindow(WebDriver driver)
	{
		//Switch to new opened window
		for(String winHandle : driver.getWindowHandles())
		{
			driver.switchTo().window(winHandle);
		}

	}

	public void choseElementFromListBox(WebElement listbox, String aim)
	{
		Select droplist = new Select(listbox);
		droplist.selectByValue(aim);
	}

	public String getProminSession(String systemType, String login, String password)
	{
		String url = configVariables.chameleonServerURL;

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "text/xml; charset=UTF-8");

		try
		{
			String xmlString =
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<session>\n" +
					"<user auth=\"" + systemType + "\" login=\"" + login + "\" password=\"" + password + "\"/>\n" +
					"</session>";

			StringEntity entity = new StringEntity(xmlString, ContentType.create("text/xml", Consts.UTF_8));
			post.setEntity(entity);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder result = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null)
			{
				result.append(line);
			}
			return result.toString().substring(result.toString().indexOf("value=\"")+"value=\"".length(),
					result.toString().length()-3);
		}
		catch (Exception e)
		{
			Assert.fail("Ошибка! Получение сессии Проминя не было выполнено. " + e.getMessage());
		}
		return null;
	}

	public void addStepToTheReport(String stepName) throws Exception
	{
		Reporter.log("<b>" + stepName + "</b><br>");
	}

	public static void addTestNameToTheReport(String testName, String methodPath) throws Exception
	{
		methodPath = methodPath.substring(0, methodPath.indexOf("("));

		//получим id теста
		String testId = methodPath.substring(methodPath.lastIndexOf(".") + 1, methodPath.length());

		//отделим имя теста от имени класса символом '#'
		StringBuilder tempPath = new StringBuilder(methodPath);
		methodPath =
				tempPath.substring(0, methodPath.lastIndexOf(".")) +
				URLEncoder.encode("#", "UTF8") +
				tempPath.substring(methodPath.lastIndexOf(".") + 1, methodPath.length());

		Reporter.log(
			"<form id = \"" + testId + "form\" action= \"\" method=\"post\">\n" +
					"<font color=\"blue\" size=\"3\">" + testName + "</font>\n" +
					"<input type=\"Submit\" value=\"Выполнить\">\n" +
					"</form> \n" +
					"<script type=\"text/javascript\">\n" +
					"\tvar currentURL = document.URL;\n" +
					"\tcurrentURL = currentURL.substring(0,currentURL.indexOf(\"/HTML_Report/\"));\n" +
					"var jobNameStartIndex = currentURL.indexOf(\"AT.SELENIUM.\");\n" +
					"while(currentURL.lastIndexOf(\"/\") > jobNameStartIndex)\n" +
					"  currentURL = currentURL.substring(0,currentURL.lastIndexOf(\"/\")); "+
					"\tdocument.getElementById('" + testId +
					"form').action = currentURL + \"/buildWithParameters?suiteFile=testng.xml&test=" + methodPath + "\";\n" +
					"</script>"
		);
	}

	public static void addErrorToTheReport(String testName) throws Exception
	{
		Reporter.log("<b><font color=\"red\" size=\"3\">" + testName + "</font></b><br>");
	}
}
