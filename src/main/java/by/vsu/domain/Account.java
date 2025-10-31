package by.vsu.domain;

import java.util.List;

public class Account {
	private Long id;
	private String accountNumber;
	private String client;
	private Long balance;
	private boolean active;
	private List<Transfer> history;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Transfer> getHistory() {
		return history;
	}

	public void setHistory(List<Transfer> history) {
		this.history = history;
	}
}
