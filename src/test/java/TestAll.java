import java.io.File;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazon.SSOF.example.hibernate.HibernateUtil;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HibernateUtil.class)
public class TestAll extends DBTestCase{

	private SessionFactory factory;
	@Before
	public void init() {
		Configuration conf = new Configuration();
		File configFile = new File("./src/main/resources/hibernate.cfg.xml");
		if (!configFile.exists()) {
			System.out.println(configFile.getAbsolutePath());
			return;
		}
		conf.configure();
		conf.buildSessionFactory().openSession().connection();
		
	}
	@Test
	public void test() {
		mockStatic(HibernateUtil.class);
		expect(HibernateUtil.createSessionFactory()).andReturn(factory);
	}
	@Override
	protected IDataSet getDataSet() throws Exception {
		return null;
	}

}
