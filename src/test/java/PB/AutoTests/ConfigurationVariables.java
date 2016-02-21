package PB.AutoTests;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigurationVariables
{
	//используем Singleton
	private static final ConfigurationVariables instance;

	private static String configFilePath = "src/test/resources/config.properties";
	private static String testDataFilePath = "src/test/resources/testData.properties";
	private static Properties configurationData = new Properties();
	private static Properties testData = new Properties();

	public static void fillMyProperties(Properties properties, String filePath)
	{
		InputStreamReader input;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(filePath);
			input = new InputStreamReader(fileInputStream, "UTF8");

			// считываем свойства
			properties.load(input);
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("Ошибка. Файл config.properties не был найден.");
		}
		catch (java.io.IOException e) {
			System.out.println("IO ошибка в пользовательском методе.");
		}
	}

	private static String getProperty(Properties properties, String propertyKey)
	{
		// получаем значение свойства
		return properties.getProperty(propertyKey).toString();
	}

	public String seleniumHubURL;
	public String currentBrowser;
	public int technicalPause;
	public int implicitTimeWait;
	public int waitPageForLoad;
	public int longPause;
	public String chameleonServerURL;
	public String techLogin;
	public String techPassword;
	public String chromeDriverDirectory;
	public String iEDriverDirectory;
	public String userLogin;
	public String userPassword;
	public String oTPDataBaseUserLogin;
	public String oTPDataBaseUserPassword;
	public String oTPDataBaseURL;
	public String oTPServiseURL;
	public String testStartPage;
	public String directoryForRecord;
	public String videoFormat;
	public String dateFormat;
	public int recordVideo;
	public String prominSession;
	public String firefoxDirectoryVirtualMachine;
	public String chromeDirectoryVirtualMachine;
	public String firefoxDirectoryLocalMachine;
	public String chromeDirectoryLocalMachine;
	public String userFIO;
	public int useProxy;

	static
	{
		fillMyProperties(configurationData, configFilePath);
		fillMyProperties(testData, testDataFilePath);

		instance = new ConfigurationVariables();

		instance.prominSession = getProperty(testData, "prominSession");
		if(instance.prominSession.equals("null"))
		{
			instance.prominSession = new CustomMethods()
					.getProminSession("ldap", instance.techLogin, instance.techPassword);
		}
	}

	private ConfigurationVariables()
	{
		/********************************************** конфигурационные данные ***************************************/
		currentBrowser = getProperty(configurationData, "currentBrowser");
		useProxy = Integer.parseInt(getProperty(configurationData, "useProxy"));
		seleniumHubURL = getProperty(configurationData, "seleniumHubURL");
		firefoxDirectoryVirtualMachine = getProperty(configurationData, "firefoxDirectoryVirtualMachine");
		chromeDirectoryVirtualMachine = getProperty(configurationData, "chromeDirectoryVirtualMachine");
		firefoxDirectoryLocalMachine = getProperty(configurationData, "firefoxDirectoryLocalMachine");
		chromeDirectoryLocalMachine = getProperty(configurationData, "chromeDirectoryLocalMachine");
		chromeDriverDirectory = getProperty(configurationData, "chromeDriverDirectory");
		iEDriverDirectory = getProperty(configurationData, "iEDriverDirectory");
		oTPDataBaseURL = getProperty(configurationData, "oTPDataBaseURL");
		oTPServiseURL = getProperty(configurationData, "oTPServiseURL");
		testStartPage = getProperty(configurationData, "authorizationPageURL");

		technicalPause = Integer.parseInt(getProperty(configurationData, "technicalPause"));
		implicitTimeWait = Integer.parseInt(getProperty(configurationData, "implicitTimeWait"));
		waitPageForLoad = Integer.parseInt(getProperty(configurationData, "waitPageForLoad"));
		longPause = Integer.parseInt(getProperty(configurationData, "longPause"));

		chameleonServerURL = getProperty(configurationData, "chameleonServerURL");
		techLogin = getProperty(configurationData, "techLogin");
		techPassword = getProperty(configurationData, "techPassword");

		directoryForRecord = getProperty(configurationData, "directoryForRecord");
		videoFormat = getProperty(configurationData, "videoFormat");
		dateFormat = getProperty(configurationData, "dateFormat");
		recordVideo = Integer.parseInt(getProperty(configurationData, "recordVideo"));

		userLogin = System.getProperty("userLogin");
		if(System.getProperty("userLogin") == null)
		{
			userLogin = getProperty(configurationData, "userLogin");
		}

		userPassword = System.getProperty("userPassword");
		if(System.getProperty("userPassword") == null)
		{
			userPassword = getProperty(configurationData, "userPassword");
		}

		oTPDataBaseUserLogin = System.getProperty("oTPDataBaseUserLogin");
		if(System.getProperty("oTPDataBaseUserLogin") == null)
		{
			oTPDataBaseUserLogin = getProperty(configurationData, "oTPDataBaseUserLogin");
		}

		oTPDataBaseUserPassword = System.getProperty("oTPDataBaseUserPassword");
		if(System.getProperty("OTPDataBaseUserPassword") == null)
		{
			oTPDataBaseUserPassword = getProperty(configurationData, "oTPDataBaseUserPassword");
		}

		/********************************************** тестовые данные ***********************************************/
		userFIO = getProperty(testData, "userFIO");
	}

	//возвращаем инстанс объекта
	public static ConfigurationVariables getInstance()
	{
		return instance;
	}
}