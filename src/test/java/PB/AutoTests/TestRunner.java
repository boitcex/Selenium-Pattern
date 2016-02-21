package PB.AutoTests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRunner extends SetupAndTeardown
{

	TestSuite testSuite = new TestSuite();

	@Test(enabled = true, groups = {"Authorization", "Критический функционал", "WithProxy"}, priority = 1)
	public void A1() throws Exception
	{
		CustomMethods.addTestNameToTheReport(
				"Проверка авторизации",
				Thread.currentThread().getStackTrace()[1].toString()
		);
		//Thread.sleep(5000);
		testSuite.A1_AuthorizationInEsa(driver, browserMobProxy);
	}

	@Test(enabled = true, groups = {"Authorization", "Критический функционал"}, priority = 1)
	public void A2() throws Exception
	{
		CustomMethods.addTestNameToTheReport(
				"Проверка авторизации",
				Thread.currentThread().getStackTrace()[1].toString()
		);
		Thread.sleep(5000);
		testSuite.A1_AuthorizationInEsa(driver, browserMobProxy);
	}
}
