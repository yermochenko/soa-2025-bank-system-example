package by.vsu;

import java.sql.*;

public class Test {
	public static void main(String[] args) {
		String sql = "SELECT \"id\", \"account_number\", \"client\", \"balance\", \"active\" FROM \"account\"";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soa-2025-bank", "root", "root");
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				System.out.printf(
					"[%c%d] %s, %s, balance $%d\n",
					resultSet.getBoolean("active") ? '+' : '-',
					resultSet.getLong("id"),
					resultSet.getString("account_number"),
					resultSet.getString("client"),
					resultSet.getLong("balance")
				);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace(System.out);
		} finally {
			if(resultSet != null) try { resultSet.close(); } catch(SQLException ignored) {}
			if(statement != null) try { statement.close(); } catch(SQLException ignored) {}
			if(connection != null) try { connection.close(); } catch(SQLException ignored) {}
		}
	}
}
