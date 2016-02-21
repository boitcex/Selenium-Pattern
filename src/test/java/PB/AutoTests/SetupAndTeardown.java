package PB.AutoTests;

import com.opera.core.systems.OperaDriver;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.proxy.ProxyServer;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.internal.TestResult;
import ru.stqa.selenium.factory.WebDriverFactory;
import ru.stqa.selenium.factory.WebDriverFactoryMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SetupAndTeardown
{

	public WebDriver driver;
	static ConfigurationVariables configVariables = ConfigurationVariables.getInstance();
	static File profileDir;
	ChromeOptions options;
	ScreenRecorder screenRecorder;
	BrowserMobProxy browserMobProxy = null;
	Proxy proxy = null;
	DesiredCapabilities capabilities;


	/************************************* В каком браузере будем проводить тестирование? ******************************
	 * CurrentBrowser = 0   Mozilla Firefox
	 * CurrentBrowser = 1   Google Chrome
	 * CurrentBrowser = 2   Internet Explorer
	 * CurrentBrowser = 3   Safari
	 * CurrentBrowser = 4   Opera
	 * CurrentBrowser = 5   HtmlUnitDriver
	 * CurrentBrowser = 6   Android Browser
	 * CurrentBrowser = 7   Remote HTMLunitDriverWithJS
	 * CurrentBrowser = 8   Remote Google Chrome
	 * CurrentBrowser = 9   Remote Firefox
	 * CurrentBrowser = 10  Remote Internet Explorer
	 * CurrentBrowser = 11  Remote Safari
	 * CurrentBrowser = 12  Remote Opera
	 * ****************************************************************************************************************/
	@BeforeTest(groups = "WithProxy")
	public void startProxy() throws Exception
	{
		browserMobProxy = new BrowserMobProxyServer();
		browserMobProxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
		//browserMobProxy.setChainedProxy(new InetSocketAddress("proxy.pbank.com.ua", 8080));
		browserMobProxy.start(5002);
		proxy = ClientUtil.createSeleniumProxy(browserMobProxy);
	}

	@BeforeTest(alwaysRun = true, dependsOnMethods = {"startProxy"})
	public void SetUp() throws IOException
	{
		String hubURL = configVariables.seleniumHubURL;
		//Указываем в системных настройках где хранятся драйвера
		System.setProperty("webdriver.chrome.driver", configVariables.chromeDriverDirectory);
		System.setProperty("webdriver.ie.driver", configVariables.iEDriverDirectory);

		switch (Integer.parseInt(configVariables.currentBrowser))
		{
			case 0:
				profileDir = new File(configVariables.firefoxDirectoryVirtualMachine);
				if(!profileDir.exists())
				{
					profileDir = new File(configVariables.firefoxDirectoryLocalMachine);
				}

				FirefoxProfile firefoxProfile = new FirefoxProfile(profileDir);
				firefoxProfile.setEnableNativeEvents(false);
				firefoxProfile.setAcceptUntrustedCertificates(true);
				firefoxProfile.setAssumeUntrustedCertificateIssuer(true);
				firefoxProfile.setPreference("javascript.enabled", true);
				firefoxProfile.setPreference("geo.enabled", false);
				firefoxProfile.setPreference("network.proxy.no_proxies_on", "localhost, 127.0.0.1, 10.0.0.0/8, *.it.loc, youtube.com, 192.168.59.0/16, *.privatbank.ua, *.pb.ua");

				capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
				capabilities.setCapability("unexpectedAlertBehaviour", "ignore");

				//Указываем использовать проксисервер в случае если он запущен
				if(browserMobProxy != null && browserMobProxy.isStarted())
					capabilities.setCapability(CapabilityType.PROXY, proxy);
				/*{
					firefoxProfile.setPreference("network.proxy.type", 1);
					firefoxProfile.setPreference("network.proxy.http", "localhost");
					firefoxProfile.setPreference("network.proxy.http_port", browserMobProxy.getPort());
				}*/

				System.out.println("Tests will be run (or rerun) in Firefox with custom profile...");
				break;
			case 1:
				capabilities = DesiredCapabilities.chrome();
				options = new ChromeOptions();
				//при параллельном запуске проблемы с кастомным профилем
				/*if(new File(configVariables.ChromeDirectoryVirtualMachine).exists()) {
					options.addArguments(configVariables.ChromeDirectoryVirtualMachine);
				}
				else options.addArguments(configVariables.ChromeDirectoryLocalMachine);*/
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);

				//Указываем использовать проксисервер в случае если он запущен
				if(browserMobProxy != null && browserMobProxy.isStarted())
					capabilities.setCapability(CapabilityType.PROXY, proxy);

				System.out.println("Tests will be run (or rerun) in Chrome with custom profile...");
				break;
			case 2:
				this.driver = new InternetExplorerDriver();
				System.out.println("Tests will be run (or rerun) in Internet Explorer...");
				break;
			case 3:
				this.driver = new SafariDriver();
				System.out.println("Tests will be run (or rerun) in Apple Safari...");
				break;
			case 4:
				this.driver = new OperaDriver();
				System.out.println("Tests will be run (or rerun) in Opera...");
				break;
			case 5:
				this.driver = new HtmlUnitDriver();
				System.out.println("Tests will be run (or rerun) in HtmlUnitDriverWithJs...");
				break;
			case 6:
				//driver = new AndroidDriver();
				break;
			case 7:
				this.driver = new RemoteWebDriver(new URL(hubURL), DesiredCapabilities.htmlUnitWithJs());
				System.out.println("Tests will be run (or rerun) in Remote HtmlUnitDriverWithJs...");
				break;
			case 8:
				options = new ChromeOptions();

				if(new File(configVariables.chromeDirectoryVirtualMachine).exists())
					options.addArguments(configVariables.chromeDirectoryVirtualMachine);
				else options.addArguments(configVariables.chromeDirectoryLocalMachine);

				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				this.driver = new RemoteWebDriver(new URL(hubURL), capabilities);
				System.out.println("Tests will be run (or rerun) in Remote Google Chrome...");
				break;
			case 9:
				profileDir = new File(configVariables.firefoxDirectoryVirtualMachine);
				if(!profileDir.exists())
				{
					profileDir = new File(configVariables.firefoxDirectoryLocalMachine);
				}

				firefoxProfile = new FirefoxProfile(profileDir);
				firefoxProfile.setEnableNativeEvents(true);
				DesiredCapabilities ffCapabilities = new DesiredCapabilities();
				ffCapabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
				ffCapabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
				this.driver = new RemoteWebDriver(new URL(hubURL), ffCapabilities);
				System.out.println("Tests will be run (or rerun) in Remote Firefox...");
				break;
			case 10:
				this.driver = new RemoteWebDriver(new URL(hubURL), DesiredCapabilities.internetExplorer());
				System.out.println("Tests will be run (or rerun) in Remote Internet Explorer...");
				break;
			case 11:
				this.driver = new RemoteWebDriver(new URL(hubURL), DesiredCapabilities.safari());
				System.out.println("Tests will be run (or rerun) in Remote Apple Safari...");
				break;
			case 12:
				this.driver = new RemoteWebDriver(new URL(hubURL), DesiredCapabilities.opera());
				System.out.println("Tests will be run (or rerun) in Remote Opera...");
				break;
			case 13:
				ProxyServer proxyServer_ = new ProxyServer(5013);
				try
				{
					proxyServer_.start();
				}
				catch(Exception e)
				{
					//do nothing
				}

				DesiredCapabilities capab = new DesiredCapabilities();
				try
				{
					capab.setCapability(CapabilityType.PROXY, proxyServer_.seleniumProxy());
				}
				catch (Exception e)
				{
					//do nothing
				}

				this.driver = new FirefoxDriver(capab);
				System.out.println("Tests will be run (or rerun) in Firefox with BrowserMobProxy...");
				break;
			default:
				this.driver = new FirefoxDriver();
				System.out.println("Tests will be run (or rerun) in Firefox...");
				break;


		}

		driver = WebDriverFactory.getDriver(capabilities);
		this.driver.manage().timeouts().implicitlyWait(configVariables.implicitTimeWait, TimeUnit.SECONDS);
		this.driver.manage().window().maximize();
		this.driver.manage().deleteAllCookies();

		//Начинаем запись
		if (configVariables.recordVideo == 1)
			this.screenRecorder = new CustomMethods().startRecording(this.driver);
	}

	@BeforeMethod(alwaysRun = true)
	public void doLogin() throws Exception
	{
		driver = WebDriverFactory.getDriver(capabilities);

		/*код для прохождения авторизации*/
	}

	@AfterMethod(alwaysRun = true)
	public void saveVideo() throws Exception
	{
		ITestResult testResult = new TestResult();
		if (configVariables.recordVideo == 1)
		{
			new CustomMethods().stopRecording(this.screenRecorder, testResult.getMethod().getMethodName());
		}
	}

	@AfterMethod(alwaysRun = true)
	public void takeScreenshot(ITestResult result) throws Exception
	{
		//Для того чтобы передавать html теги и спец-символы в reporter.log
		//Или можно передать параметр в командную строку при выполнении TestNG:
		//-Dorg.uncommons.reportng.escape-output=false
		System.setProperty("org.uncommons.reportng.escape-output", "false");

		Reporter.setCurrentTestResult(result);
		boolean success = (new File("TestReport/html/Screens/")).mkdirs();

		Calendar calendar = new CustomMethods().getCurrentCalendar();
		String SuccsessLogMessage =
				"The test - \"" +
						result.getMethod().getMethodName().toString() +
						"\" was successfully ended" +
						"(" +
						calendar.get(Calendar.DATE) +
						"." +
						(calendar.get(Calendar.MONTH)+1) +
						"." +
						calendar.get(Calendar.YEAR) +
						" " +
						calendar.get(Calendar.HOUR_OF_DAY) +
						":" +
						calendar.get(Calendar.MINUTE) +
						":" +
						calendar.get(Calendar.SECOND) +
						")";

		String ErrorLogMessage =
				"The test - \"" +
						result.getMethod().getMethodName().toString() +
						"\" was failed!" +
						"(" +
						calendar.get(Calendar.DATE) +
						"." +
						(calendar.get(Calendar.MONTH) + 1) +
						"." +
						calendar.get(Calendar.YEAR) +
						" " +
						calendar.get(Calendar.HOUR_OF_DAY) +
						":" +
						calendar.get(Calendar.MINUTE) +
						":" +
						calendar.get(Calendar.SECOND) +
						")";

		try
		{
			if (!result.isSuccess())
			{
				try
				{
					FileOutputStream fileOuputStream = new FileOutputStream("TestReport/html/Screens/" + result.getMethod().getMethodName() + ".png");
					fileOuputStream.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
				Reporter.log(
						"<center>Скриншот снят при падении теста "
								+ result.getMethod().getMethodName() + ".png"
								+ ", URL = "
								+ driver.getCurrentUrl()
								+ "<br><div><a target=\"_blank\" href=\"Screens/"
								+ result.getMethod().getMethodName()
								+ ".png\"><img  style=\"height:400px; width: 600px;\"  src=\"" + "Screens/"
								+ result.getMethod().getMethodName()
								+ ".png"
								+ "\"></a></div><center><br><br>",
						true);
				System.out.println(ErrorLogMessage);

			}
			else
			{
				System.out.println(SuccsessLogMessage);
				Reporter.log(SuccsessLogMessage);
			}
		}
		catch (Exception e)
		{
			CustomMethods.addErrorToTheReport("Connection with browser was lost.");
		}
	}

	@AfterSuite(alwaysRun = true)
	public void deleteFiles() throws Exception
	{
		if(!WebDriverFactory.isEmpty()) WebDriverFactory.dismissAll();

		//Удаляем временные папки и файлы...
		File directory = new File("target");
		CustomMethods.deleteFileOrDirectory(directory);

		directory = new File("surefire");
		CustomMethods.deleteFileOrDirectory(directory);

		directory = new File("chromedriver.log");
		CustomMethods.deleteFileOrDirectory(directory);
	}

}
