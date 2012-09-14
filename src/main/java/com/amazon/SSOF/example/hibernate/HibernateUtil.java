package com.amazon.SSOF.example.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory factory;

	public static SessionFactory createSessionFactory() {
		Configuration conf = new Configuration();
		File configFile = new File("data-hibernate/hibernate.cfg.xml");
		if (!configFile.exists()) {
			System.out.println(configFile.getAbsolutePath());
			return null;
		}
		conf.configure(configFile);
		return conf.buildSessionFactory();
	}

	public static Session createSession() {
		if (factory == null) {
			factory = createSessionFactory();
		}
		return factory.openSession();
	}

	public static void main(String[] args) {
		System.out.println(createSession());
	}
}
