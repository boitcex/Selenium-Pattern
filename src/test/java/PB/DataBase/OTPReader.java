package PB.DataBase;

import PB.AutoTests.ConfigurationVariables;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class OTPReader
{
	ConfigurationVariables configVariables = ConfigurationVariables.getInstance();
	public OTPData readOTPFromDataBase() throws Exception
	{
		String jdbcClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		DataBase dataBase = new DataBase();
		OTPData otpData = new OTPData();
		ResultSet resultSet = null;

		//Выполняем запрос
		resultSet = dataBase.executeRequest(
				jdbcClassName,
				configVariables.oTPDataBaseURL,
				configVariables.oTPDataBaseUserLogin,
				configVariables.oTPDataBaseUserPassword,
				"select top 1 * from OTP where PhoneNumber like '%0974779320%' order by id desc"
		);

		//Конвертируем полученный результат в переменную класса OTPData
		if(resultSet.next())
		{
			otpData.id = resultSet.getString("id");
			otpData.timeStamp = resultSet.getString("TimeStamp");
			otpData.otp = resultSet.getString("OTP");
			otpData.sms = resultSet.getString("SMS");
			otpData.phoneNumber = resultSet.getString("PhoneNumber");
		}
		return otpData;
	}

	public ArrayList<OTPData> getOTPListByPhone(String phone)
	{
		String url = configVariables.oTPServiseURL + "/GetOtpByPhone";
		ArrayList<OTPData> otpDataList = new ArrayList<OTPData>();
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		NodeList nList = null;
		StringBuffer result = null;
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		try
		{
			//Выполняем запрос http post-запрос к сервису
			String requestString = "phoneNumber=" + URLEncoder.encode(phone, "UTF-8");
			StringEntity entity = new StringEntity(
					requestString,
					ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)
			);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			//Читаем ответ сервиса
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null)
			{
				result.append(line);
			}

			//Конвертируем полученный ответ чтобы прочесть его как xml файл
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(result.toString())));
			doc.getDocumentElement().normalize();

			//Получаем ноды ОТП сообещний и сохраняем информацию из них в поля объекта класса OTPData
			nList = doc.getElementsByTagName("Otp");
		}
		catch (Exception e)
		{
			Assert.fail("Ошибка! Информация об ОТП получена не была." + e.getMessage());
		}
		for (int i = 0; i < nList.getLength(); i++)
		{
			Element eElement = (Element) nList.item(i);
			try
			{
				//Проверяем что сервис не вернул нам ошибку
				if(
					eElement.getElementsByTagName("Sms").item(0).getTextContent().equals(" ") &&
					eElement.getElementsByTagName("PhoneNumber").item(0).getTextContent().equals(" ")
				)
					return null;
				otpDataList.add(
					new OTPData(
						eElement.getElementsByTagName("TimeStamp").item(0).getTextContent().replace("\\s+",""),
						eElement.getElementsByTagName("Password").item(0).getTextContent().replace("\\s+",""),
						eElement.getElementsByTagName("Sms").item(0).getTextContent().replace("\\s+",""),
						eElement.getElementsByTagName("PhoneNumber").item(0).getTextContent().replace("\\s+","")
					)
				);
			}
			catch (Exception e)
			{
				Assert.fail(
					"Ошибка! При чтении результат html запроса возникла ошибка. Текст ответа: " +
					result.toString()
				);
			}

		}

		return otpDataList;
	}


	public String getLastSmsSinceThisMoment(String phone) throws InterruptedException
	{
		int i = 6;  //Делаем 6 попыток
		int correctionValue = 7;
		String OTPFromSMS = null;
		long unixTimeStampOfLastOTP;

		//получим текущую метку времени
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		String dataDELETE = new Date().toString();

		do
		{
			//ожидаем доставки СМС D:\Current_version\NKKProject\dependencies\photo.jpg

			Thread.sleep(5000);

			//получаем дату получения последнго из СМС с ОТП
			ArrayList<OTPData> otpReader1 = this.getOTPListByPhone(phone);
			String timeStampOfLastOTPFromService;
			if(otpReader1 == null) timeStampOfLastOTPFromService = "1/1/1991 00:00:00 AM";
			else timeStampOfLastOTPFromService = otpReader1.get(otpReader1.size()-1).timeStamp;

			//получаем метку времени для даты получения последного из СМС с ОТП
			DateFormat formatter;
			formatter = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
			Date date = null;
			try
			{
				date = formatter.parse(timeStampOfLastOTPFromService);
			}
			catch (ParseException ex)
			{
				ex.printStackTrace();
			}
			unixTimeStampOfLastOTP = (date != null ? date.getTime() : 0) / 1000L;

			//Получаем ОТП из СМС
			String messageFromSMS;
			if(unixTimeStampOfLastOTP >= (currentUnixTime-correctionValue)) //Делаем поправку на погрешность при согласовании времени
			{
				messageFromSMS = otpReader1.get(otpReader1.size()-1).sms.trim();
				int length = messageFromSMS.length();
				OTPFromSMS = messageFromSMS.substring(length-2, length);
			}
			i--;
		}
		while (i > 0 && (unixTimeStampOfLastOTP < (currentUnixTime-correctionValue)));
		Assert.assertFalse((OTPFromSMS == null), "Ошибка!! ОТП из СМС получен не был.");

		return OTPFromSMS;
	}
}
