package PB.AutoTests;

import PB.AutoTests.Pages.*;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TestSuite extends CustomMethods
{
	ConfigurationVariables configVariables = ConfigurationVariables.getInstance();

	//<editor-fold desc="A1 - авторизация в ЕСА (позитивный сценарий).">
	public void A1_AuthorizationInEsa(WebDriver driver, BrowserMobProxy browserMobProxy) throws Exception
	{
		LoginPage loginPage;
		browserMobProxy.newHar("HAR");

		addStepToTheReport("Перейдем на единую страницу авторизации и выполним авторизацию.");
		loginPage = new LoginPage(driver);
		loginPage.getPage();
		loginPage.authorization(configVariables.userLogin, configVariables.userPassword);


		addStepToTheReport("Проверим что мы перешли на главную страницу рабочего стола сотрудника.");
		driver.get("https://admin-pre.corezoid.com/");
		browserMobProxy.getHar();

	}
	//</editor-fold>
}
