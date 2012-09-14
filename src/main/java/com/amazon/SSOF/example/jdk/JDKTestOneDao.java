package com.amazon.SSOF.example.jdk;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDKTestOneDao {

	public long countAll() throws SQLException {
		Connection conn = ConnectionUtil.getConnection();
		ResultSet rs = conn.createStatement().executeQuery(
				"select count(*) from test1");
		long counter = 0;
		while (rs.next()) {
			counter = rs.getLong(1);
		}
		return counter;
	}

}
