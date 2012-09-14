package com.amazon.SSOF.ut;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PowerMockIgnore({ "javax.management.*", "javax.xml.parsers.*",
		"com.sun.org.apache.xerces.internal.jaxp.*", "org.slf4j.*",
		"javax.xml.*", "org.dbunit.*", "com.sun.*", "org.hibernate.*" })
// @RunWith(PowerMockRunner.class)
public class DAOTestBase {

	@Rule
	public DBUnitRule dbRule = new DBUnitRule();
	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	protected String jdbcDriver = "org.h2.Driver";

	protected String connectionStr = "jdbc:h2:mem:test";

	// protected String jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	//
	// protected String connectionStr = "jdbc:derby:memory:test;create=true";

	private SessionFactory hibernateSessionFactory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		loadDriver();
	}

	@After
	public void tearDown() throws Exception {

	}

	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionStr);
	}

	protected void loadDriver() throws ClassNotFoundException {
		Class.forName(jdbcDriver);
	}

	protected SessionFactory getSessionFactory() {
		return hibernateSessionFactory;
	}

}
