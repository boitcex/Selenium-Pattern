package PB.DataBase;

import org.testng.Assert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase
{

	//метод для чтения данных с базы
	public ResultSet executeRequest(String jdbcClassName, String DataBaseURL, String UserLogin,
									String UserPassword,
									String sqlQuery) throws Exception
	{
		//Загружаем класс в память
		Class.forName(jdbcClassName);

		//Устанавливаем соединение
		Connection connection = DriverManager.getConnection(DataBaseURL, UserLogin, UserPassword);

		//выполним запрос и сохраним результат
		Statement statement = (Statement) connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = null;
		try
		{
			resultSet = (ResultSet) statement.executeQuery(sqlQuery);
		}
		catch (Exception e)
		{
			Assert.fail("Не удалось выполнить SQL запрос. Ошибка:" + e.getMessage());
		}
		finally
		{
			return resultSet;
		}
	}

	//метод для чтения данных с базы
	public int executeUpdate(String jdbcClassName, String DataBaseURL, String UserLogin,
							 String UserPassword,
							 String sqlQuery) throws Exception
	{
		//Загружаем класс в память
		Class.forName(jdbcClassName);

		//Устанавливаем соединение
		Connection connection = DriverManager.getConnection(DataBaseURL, UserLogin, UserPassword);

		//Выполним запрос и сохраним результат
		Statement statement = (Statement) connection.createStatement();
		int resultCode = 0;
		try
		{
			int a = statement.executeUpdate(sqlQuery);
			if(resultCode < 0) Assert.fail("Не удалось выполнить SQL запрос. Код ответа: " + resultCode);
		}
		catch (Exception e)
		{
			Assert.fail("Не удалось выполнить SQL запрос. Ошибка:" + e.getMessage());
		}
		finally
		{
			connection.close();
			return resultCode;
		}
	}
}
