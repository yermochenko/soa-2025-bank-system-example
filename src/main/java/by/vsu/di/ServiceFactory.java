package by.vsu.di;

import by.vsu.exception.ApplicationException;
import by.vsu.model.repository.AccountRepository;
import by.vsu.model.service.AccountService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServiceFactory implements AutoCloseable {
	public static final String JDBC_DRIVER = "org.postgresql.Driver";
	public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/soa-2025-bank";
	public static final String JDBC_USERNAME = "root";
	public static final String JDBC_PASSWORD = "root";

	public static void init() throws ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
	}

	public AccountService getAccountService() throws ApplicationException {
		AccountService service = new AccountService();
		service.setAccountRepository(getAccountRepository());
		return service;
	}

	private AccountRepository accountRepository;
	private AccountRepository getAccountRepository() throws ApplicationException {
		if(accountRepository == null) {
			accountRepository = new AccountRepository();
			accountRepository.setConnection(getConnection());
		}
		return accountRepository;
	}

	private Connection connection;
	private Connection getConnection() throws ApplicationException {
		if(connection == null) {
			try {
				connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
			} catch(SQLException e) {
				throw new ApplicationException(e);
			}
		}
		return connection;
	}

	@Override
	public void close() {
		if(connection != null) {
			try { connection.close(); } catch(SQLException ignored) {}
		}
	}
}
