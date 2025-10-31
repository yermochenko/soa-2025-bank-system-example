package by.vsu.model.service;

import by.vsu.domain.Account;
import by.vsu.domain.Transfer;
import by.vsu.exception.ApplicationException;
import by.vsu.exception.InsufficientFundsException;
import by.vsu.exception.NotActiveException;
import by.vsu.exception.NotFoundException;
import by.vsu.model.repository.AccountRepository;
import by.vsu.model.repository.TransferRepository;

import java.sql.SQLException;
import java.util.Optional;

public class TransferService {
	private AccountRepository accountRepository;
	private TransferRepository transferRepository;

	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public void setTransferRepository(TransferRepository transferRepository) {
		this.transferRepository = transferRepository;
	}

	public Transfer creditFunds(String accountNumber, long amount) throws ApplicationException {
		try {
			Optional<Account> foundAccount = accountRepository.readByNumber(accountNumber);
			if(foundAccount.isPresent()) {
				Account account = foundAccount.get();
				if(account.isActive()) {
					account.setBalance(account.getBalance() + amount);
					accountRepository.update(account);
					Transfer transfer = new Transfer();
					transfer.setFromAccount(null);
					transfer.setToAccount(account);
					transfer.setAmount(amount);
					transferRepository.create(transfer);
					return transfer;
				} else {
					throw new NotActiveException("Account was blocked or closed");
				}
			} else {
				throw new NotFoundException("Account not found");
			}
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}

	public Transfer transfer(String fromAccountNumber, String toAccountNumber, long amount) throws ApplicationException {
		try {
			Optional<Account> foundFromAccount = accountRepository.readByNumber(fromAccountNumber);
			if(foundFromAccount.isEmpty()) {
				throw new NotFoundException("Source account not found");
			}
			Account fromAccount = foundFromAccount.get();
			if(!fromAccount.isActive()) {
				throw new NotActiveException("Source account was blocked or closed");
			}
			Optional<Account> foundToAccount = accountRepository.readByNumber(toAccountNumber);
			if(foundToAccount.isEmpty()) {
				throw new NotFoundException("Destination account not found");
			}
			Account toAccount = foundToAccount.get();
			if(!toAccount.isActive()) {
				throw new NotActiveException("Destination account was blocked or closed");
			}
			if(amount > fromAccount.getBalance()) {
				throw new InsufficientFundsException("Source account has insufficient funds");
			}
			fromAccount.setBalance(fromAccount.getBalance() - amount);
			accountRepository.update(fromAccount);
			toAccount.setBalance(toAccount.getBalance() + amount);
			accountRepository.update(toAccount);
			Transfer transfer = new Transfer();
			transfer.setFromAccount(fromAccount);
			transfer.setToAccount(toAccount);
			transfer.setAmount(amount);
			transferRepository.create(transfer);
			return transfer;
		} catch(SQLException e) {
			throw new ApplicationException(e);
		}
	}
}
