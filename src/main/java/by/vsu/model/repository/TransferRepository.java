package by.vsu.model.repository;

import by.vsu.domain.Transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

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
}
