package com.amazon.SSOF.example.hibernate;

import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.amazon.SSOF.ut.DAOTestBase;
import com.amazon.SSOF.ut.SSOFDaoUTData;
import com.amazon.SSOF.ut.SSOFDaoUTHibernate;
import com.amazon.SSOF.ut.SSOFDaoUTSchema;

@SSOFDaoUTHibernate("data-hibernate/hibernate.cfg.xml")
@SSOFDaoUTSchema({ "data-hibernate/merchant_shipment_items.schema" })
@PrepareForTest(HibernateUtil.class)
public class HibernateDaoTest extends DAOTestBase {

	HibernateTest1Dao hibernateTest1Dao;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		hibernateTest1Dao = new HibernateTest1Dao();
	}

	@Test
	@SSOFDaoUTData({ "data-hibernate/data1.xml", "data-hibernate/data2.xml" })
	public void testOne() throws SQLException {
		PowerMock.mockStatic(HibernateUtil.class);
		EasyMock.expect(HibernateUtil.createSession()).andReturn(
				getSessionFactory().openSession());
		PowerMock.replayAll();
		long counter = hibernateTest1Dao.countAll();
		Assert.assertSame(4l, counter);
		PowerMock.verifyAll();
	}

}
