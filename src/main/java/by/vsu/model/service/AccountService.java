package by.vsu.model.service;

import by.vsu.domain.Account;
import by.vsu.domain.Transfer;
import by.vsu.exception.ApplicationException;
import by.vsu.exception.NonZeroBalanceException;
import by.vsu.exception.NotActiveException;
import by.vsu.exception.NotFoundException;
import by.vsu.model.repository.AccountRepository;
import by.vsu.model.repository.TransferRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountService {
	private AccountRepository accountRepository;
	private TransferRepository transferRepository;

	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public void setTransferRepository(TransferRepository transferRepository) {
		this.transferRepository = transferRepository;
	}

	public List<Account> showAll() throws ApplicationException {
		try {
			return accountRepository.readAll();
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}

	public Optional<Account> showOne(String accountNumber) throws ApplicationException {
		try {
			Optional<Account> account = accountRepository.readByNumber(accountNumber);
			if(account.isPresent()) {
				List<Transfer> history = transferRepository.readByAccount(account.get().getId());
				for(Transfer transfer : history) {
					Account from = transfer.getFromAccount();
					if(from != null && !from.getId().equals(account.get().getId())) {
						from = accountRepository.read(from.getId()).orElse(null);
						transfer.setFromAccount(from);
					}
					Account to = transfer.getToAccount();
					if(to != null && !to.getId().equals(account.get().getId())) {
						to = accountRepository.read(to.getId()).orElse(null);
						transfer.setToAccount(to);
					}
				}
				account.get().setHistory(history);
			}
			return account;
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

	public boolean update(Account account) throws ApplicationException {
		try {
			Optional<Account> accountOld = accountRepository.readByNumber(account.getAccountNumber());
			if(accountOld.isEmpty()) return false;
			accountOld.get().setClient(account.getClient());
			accountRepository.update(accountOld.get());
			return true;
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}

	public void delete(String accountNumber) throws ApplicationException {
		try {
			Optional<Account> foundAccount = accountRepository.readByNumber(accountNumber);
			if(foundAccount.isPresent()) {
				Account account = foundAccount.get();
				if(account.isActive()) {
					if(account.getBalance() == 0) {
						List<Transfer> history = transferRepository.readByAccount(foundAccount.get().getId());
						if(history.isEmpty()) {
							accountRepository.delete(account.getId());
						} else {
							account.setActive(false);
							accountRepository.update(account);
						}
					} else {
						throw new NonZeroBalanceException(String.format("Account has non zero balance %d", account.getBalance()));
					}
				} else {
					throw new NotActiveException("Account is not active");
				}
			} else {
				throw new NotFoundException("Account not exists");
			}
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}
}
