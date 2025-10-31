package by.vsu.model.repository;

import by.vsu.domain.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
	private Connection connection;

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void create(Account account) throws SQLException {
		String sql = "INSERT INTO \"account\" (\"account_number\", \"client\") VALUES (?, ?)";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, account.getAccountNumber());
			statement.setString(2, account.getClient());
			statement.executeUpdate();
		} finally {
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
		}
	}

	public Optional<Account> readByNumber(String accountNumber) throws SQLException {
		String sql = "SELECT \"id\", \"client\", \"balance\", \"active\" FROM \"account\" WHERE \"account_number\" = ?";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, accountNumber);
			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				Account account = new Account();
				account.setId(resultSet.getLong("id"));
				account.setAccountNumber(accountNumber);
				account.setClient(resultSet.getString("client"));
				account.setBalance(resultSet.getLong("balance"));
				account.setActive(resultSet.getBoolean("active"));
				return Optional.of(account);
			} else {
				return Optional.empty();
			}
		} finally {
			if(resultSet != null) try { resultSet.close(); } catch(SQLException ignored) {}
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
		}
	}

	public List<Account> readAll() throws SQLException {
		String sql = "SELECT \"id\", \"account_number\", \"client\", \"balance\", \"active\" FROM \"account\"";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
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
		}
	}
}
