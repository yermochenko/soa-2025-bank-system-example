package by.vsu.di;

import by.vsu.model.repository.AccountRepository;
import by.vsu.model.service.AccountService;

public class ServiceFactory {
	public static final String JDBC_DRIVER = "org.postgresql.Driver";
	public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/soa-2025-bank";
	public static final String JDBC_USERNAME = "root";
	public static final String JDBC_PASSWORD = "root";

	public static void init() throws ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
	}

	public static AccountService getAccountService() {
		AccountService service = new AccountService();
		service.setAccountRepository(getAccountRepository());
		return service;
	}

	private static AccountRepository getAccountRepository() {
		return new AccountRepository(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
	}
}
