package PB.AutoTests.Pages;

import PB.AutoTests.ConfigurationVariables;
import PB.AutoTests.CustomMethods;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage 
{
	WebDriver driver;
	ConfigurationVariables configVariables = ConfigurationVariables.getInstance();
	
	public LoginPage(WebDriver driver) 
	{
		PageFactory.initElements(driver, this);
		this.driver = driver;
	}
	/******************************************************** Анимация ************************************************/

	/*************************************************** Выпадающие списки ********************************************/

	/******************************************************* Информация ***********************************************/

	/****************************************************** Изображения ***********************************************/

	/********************************************************* Кнопки *************************************************/

	/********************************************************** Поля **************************************************/
	@FindBy(name = "login")
	public WebElement fieldLogin;

	@FindBy(name = "password")
	public WebElement fieldPassword;

	@FindBy(id = "firstAuth")
	public WebElement buttonSubmit;

	@FindBy(id = "region")
	public WebElement listRegion;

	/******************************************************* Радиокнопки **********************************************/

	/************************************************** Сообщения об ошибке *******************************************/

	/******************************************************** Ссылки **************************************************/

	/******************************************************* Чек-бокс *************************************************/

	/******************************************************** Методы **************************************************/
	public void getPage()
	{
		driver.navigate().to(configVariables.testStartPage);
	}

	public void authorization(String login, String password) throws Exception {
		//Заполняем форму авторизации
		(new CustomMethods()).waitForElementPresent(driver, this.fieldLogin, configVariables.waitPageForLoad, 500);
		this.fieldLogin.click();
		this.fieldLogin.clear();
		this.fieldLogin.sendKeys(login);
		this.fieldPassword.click();
		this.fieldPassword.sendKeys(password);
		this.buttonSubmit.click();

		//Подтверждаем регион местонахождения
		try
		{
			(new CustomMethods()).waitForElementPresent(driver, this.listRegion, configVariables.waitPageForLoad, 500);
			this.buttonSubmit.click();
		}
		catch(Exception e)
		{
			//Просто пропускаем данный шаг, если элемент отсутствует
		}
	}
}
