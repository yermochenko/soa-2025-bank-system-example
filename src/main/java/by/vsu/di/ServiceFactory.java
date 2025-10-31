package by.vsu.di;

import by.vsu.exception.ApplicationException;
import by.vsu.model.repository.AccountRepository;
import by.vsu.model.repository.TransferRepository;
import by.vsu.model.service.AccountService;
import by.vsu.model.service.TransferService;

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

	private AccountService accountService;
	public AccountService getAccountService() throws ApplicationException {
		if(accountService == null) {
			accountService = new AccountService();
			accountService.setAccountRepository(getAccountRepository());
		}
		return accountService;
	}

	private TransferService transferService;
	public TransferService getTransferService() throws ApplicationException {
		if(transferService == null) {
			transferService = new TransferService();
			transferService.setAccountRepository(getAccountRepository());
			transferService.setTransferRepository(getTransferRepository());
		}
		return transferService;
	}

	private AccountRepository accountRepository;
	private AccountRepository getAccountRepository() throws ApplicationException {
		if(accountRepository == null) {
			accountRepository = new AccountRepository();
			accountRepository.setConnection(getConnection());
		}
		return accountRepository;
	}

	private TransferRepository transferRepository;
	private TransferRepository getTransferRepository() throws ApplicationException {
		if(transferRepository == null) {
			transferRepository = new TransferRepository();
			transferRepository.setConnection(getConnection());
		}
		return transferRepository;
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
