package com.amazon.SSOF.example.hibernate;

import java.sql.SQLException;

import org.hibernate.Session;

public class HibernateTest1Dao {

	public long countAll() throws SQLException {
		Session session = HibernateUtil.createSession();
		return Long.valueOf(session.createQuery("select count(*) from Test1DO")
				.list().get(0).toString());
	}
}
