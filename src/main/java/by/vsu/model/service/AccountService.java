package by.vsu.model.service;

import by.vsu.domain.Account;
import by.vsu.exception.ApplicationException;
import by.vsu.model.repository.AccountRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountService {
	private AccountRepository accountRepository;

	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<Account> showAll() throws ApplicationException {
		try {
			return accountRepository.readAll();
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}

	public void create(Account account) throws ApplicationException {
		try {
			Random random = new Random();
			String accountNumber;
			do {
				accountNumber = Stream.generate(() -> String.format("%04d", random.nextInt(10001) - 1)).limit(4).collect(Collectors.joining(" "));
			} while(accountRepository.readByNumber(accountNumber).isPresent());
			account.setAccountNumber(accountNumber);
			account.setActive(true);
			accountRepository.create(account);
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}
}
