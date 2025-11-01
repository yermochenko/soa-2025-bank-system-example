package by.vsu.model.repository;

import by.vsu.domain.Account;
import by.vsu.domain.Transfer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransferRepository {
	private Connection connection;

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void create(Transfer transfer) throws SQLException {
		String sql = "INSERT INTO \"transfer\" (\"from_account_id\", \"to_account_id\", \"amount\") VALUES (?, ?, ?)";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			if(transfer.getFromAccount() != null) {
				statement.setLong(1, transfer.getFromAccount().getId());
			} else {
				statement.setNull(1, Types.BIGINT);
			}
			if(transfer.getToAccount() != null) {
				statement.setLong(2, transfer.getToAccount().getId());
			} else {
				statement.setNull(2, Types.BIGINT);
			}
			statement.setLong(3, transfer.getAmount());
			statement.executeUpdate();
		} finally {
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
		}
	}

	public List<Transfer> readByAccount(Long accountId) throws SQLException {
		String sql = "SELECT \"id\", \"from_account_id\", \"to_account_id\", \"transfer_date\", \"amount\" FROM \"transfer\" WHERE \"from_account_id\" = ? OR \"to_account_id\" = ?";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setLong(1, accountId);
			statement.setLong(2, accountId);
			resultSet = statement.executeQuery();
			List<Transfer> transfers = new ArrayList<>();
			while(resultSet.next()) {
				Transfer transfer = new Transfer();
				transfer.setId(resultSet.getLong("id"));
				long fromAccountId = resultSet.getLong("from_account_id");
				if(!resultSet.wasNull()) {
					transfer.setFromAccount(new Account());
					transfer.getFromAccount().setId(fromAccountId);
				}
				long toAccountId = resultSet.getLong("to_account_id");
				if(!resultSet.wasNull()) {
					transfer.setToAccount(new Account());
					transfer.getToAccount().setId(toAccountId);
				}
				transfer.setTransferDate(new Date(resultSet.getTimestamp("transfer_date").getTime()));
				transfer.setAmount(resultSet.getLong("amount"));
				transfers.add(transfer);
			}
			return transfers;
		} finally {
			if(resultSet != null) try { resultSet.close(); } catch(SQLException ignored) {}
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
		}
	}
}
