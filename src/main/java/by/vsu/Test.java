package by.vsu;

import by.vsu.domain.Account;
import by.vsu.model.repository.AccountRepository;

import java.sql.SQLException;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
			AccountRepository repository = new AccountRepository("jdbc:postgresql://localhost:5432/soa-2025-bank", "root", "root");
			List<Account> accounts = repository.readAll();
			for(Account account : accounts) {
				System.out.printf(
					"[%c%d] %s, %s, balance $%d\n",
					account.isActive() ? '+' : '-',
					account.getId(),
					account.getAccountNumber(),
					account.getClient(),
					account.getBalance()
				);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace(System.out);
		}
	}
}
