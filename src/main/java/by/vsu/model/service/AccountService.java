package by.vsu.model.service;

import by.vsu.domain.Account;
import by.vsu.exception.ApplicationException;
import by.vsu.model.repository.AccountRepository;

import java.sql.SQLException;
import java.util.List;

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
}
