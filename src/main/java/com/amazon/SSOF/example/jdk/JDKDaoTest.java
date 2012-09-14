package com.amazon.SSOF.example.jdk;

import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.amazon.SSOF.ut.DAOTestBase;
import com.amazon.SSOF.ut.SSOFDaoUTData;
import com.amazon.SSOF.ut.SSOFDaoUTSchema;

@SSOFDaoUTSchema({ "data-jdk/create.sql" })
@PrepareForTest(ConnectionUtil.class)
public class JDKDaoTest extends DAOTestBase {

	JDKTestOneDao jdkTestOneDao;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		jdkTestOneDao = new JDKTestOneDao();
	}

	@Test
	@SSOFDaoUTData({ "data-jdk/data1.xml", "data-jdk/data2.xml" })
	public void testOne() throws SQLException {
		PowerMock.mockStatic(ConnectionUtil.class);
		EasyMock.expect(ConnectionUtil.getConnection()).andReturn(
				getConnection());
		PowerMock.replayAll();
		long counter = jdkTestOneDao.countAll();
		Assert.assertSame(4l, counter);
		PowerMock.verifyAll();
	}

}
