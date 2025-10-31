package by.vsu.model.repository;

import by.vsu.domain.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
	private final String url;
	private final String user;
	private final String password;

	public AccountRepository(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public List<Account> readAll() throws SQLException {
		String sql = "SELECT \"id\", \"account_number\", \"client\", \"balance\", \"active\" FROM \"account\"";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			List<Account> accounts = new ArrayList<>();
			while(resultSet.next()) {
				Account account = new Account();
				account.setId(resultSet.getLong("id"));
				account.setAccountNumber(resultSet.getString("account_number"));
				account.setClient(resultSet.getString("client"));
				account.setBalance(resultSet.getLong("balance"));
				account.setActive(resultSet.getBoolean("active"));
				accounts.add(account);
			}
			return accounts;
		} finally {
			if(resultSet != null) try { resultSet.close(); } catch(SQLException ignored) {}
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
			if(connection != null) try { connection.close(); } catch(SQLException ignored) {}
		}
	}
}
