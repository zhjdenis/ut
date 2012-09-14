package com.amazon.SSOF.ut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class DBUnitRule implements MethodRule {

	public Statement apply(final Statement base, final FrameworkMethod method,
			final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {

				SSOFDaoUTSchema schema = (SSOFDaoUTSchema) getAnnotationFromPredecessor(
						target, SSOFDaoUTSchema.class);
				if (schema == null) {
					throw new SSOFDaoUTSchemaNotFoundException();
				}
				importSchema(target);
				SSOFDaoUTData dataAnnotation = method
						.getAnnotation(SSOFDaoUTData.class);
				if (dataAnnotation != null) {
					importData(target, method);
				}
				base.evaluate();
			}
		};
	}

	protected void importSchema(Object target) throws Exception {
		SSOFDaoUTHibernate hibernateCfg = (SSOFDaoUTHibernate) getAnnotationFromPredecessor(
				target, SSOFDaoUTHibernate.class);
		if (hibernateCfg != null) {
			configHibernate(target);
			return;
		}
		SSOFDaoUTSchema schema = (SSOFDaoUTSchema) getAnnotationFromPredecessor(
				target, SSOFDaoUTSchema.class);
		if (schema != null) {
			Connection conn = getJDKConnection(target);
			if (conn == null) {
				throw new SSOFDaoUTSchemaNotFoundException();
			}
			String[] schemaFiles = schema.value();
			for (String schemaFile : schemaFiles) {
				File file = new File(schemaFile);
				if (!file.exists()) {
					continue;
				}
				if (file.getName().endsWith(".sql")) {
					conn.createStatement().execute(getSQL(file));
				} else {
					conn.createStatement().execute(convertToSQL(file));
				}
			}
		}

	}

	protected void importData(Object target, FrameworkMethod method)
			throws Exception {
		SSOFDaoUTData dataAnnotation = method
				.getAnnotation(SSOFDaoUTData.class);
		if (dataAnnotation != null && dataAnnotation.value().length > 0) {
			System.out.println("start to load data");
			Connection conn = null;
			SSOFDaoUTHibernate hibernateCfg = (SSOFDaoUTHibernate) getAnnotationFromPredecessor(
					target, SSOFDaoUTHibernate.class);
			Session session = null;
			if (hibernateCfg == null) {
				conn = getJDKConnection(target);
			} else {
				conn = getHibernateConnection(target);
			}
			DatabaseConnection connection = new H2Connection(conn, "");
			String[] dataSetFiles = dataAnnotation.value();
			for (String dataSetFile : dataSetFiles) {
				File file = new File(dataSetFile);
				if (file.exists()) {
					IDataSet dataSet = new FlatXmlDataSetBuilder()
							.build(new FileInputStream(file));
					DatabaseOperation.REFRESH.execute(connection, dataSet);
				}
			}
			conn.close();
			System.out.println("Complete loading data");
		}
	}

	protected void loadDriver(Object targetObj) {
		Class targetClass = targetObj.getClass();
		Method driverMethod = null;
		while (true) {
			try {
				driverMethod = targetClass.getDeclaredMethod("loadDriver");
				driverMethod.invoke(targetObj);
				return;
			} catch (Exception e) {
				targetClass = targetClass.getSuperclass();
				if (targetClass == Object.class) {
					return;
				}
				continue;
			}
		}
	}

	protected Connection getJDKConnection(Object targetObj) {
		loadDriver(targetObj);
		Connection result = null;
		Class targetClass = targetObj.getClass();
		Method getConnectionMethod = null;
		while (true) {
			try {
				getConnectionMethod = targetClass
						.getDeclaredMethod("getConnection");
				result = (Connection) getConnectionMethod.invoke(targetObj);
				return result;
			} catch (Exception e) {
				targetClass = targetClass.getSuperclass();
				if (targetClass == Object.class) {
					return result;
				}
				continue;
			}
		}
	}

	protected Connection getHibernateConnection(Object target) {
		Session session = getHibernateSession(target);
		if (session != null) {
			return session.connection();
		} else {
			return null;
		}
	}

	protected Session getHibernateSession(Object target) {
		SSOFDaoUTHibernate hibernateCfg = (SSOFDaoUTHibernate) getAnnotationFromPredecessor(
				target, SSOFDaoUTHibernate.class);
		if (hibernateCfg == null) {
			return null;
		}
		Class targetClass = target.getClass();
		Field sessionField = null;
		while (true) {
			try {
				sessionField = targetClass
						.getDeclaredField("hibernateSessionFactory");
				sessionField.setAccessible(true);
				return ((SessionFactory) sessionField.get(target))
						.openSession();
			} catch (Exception e) {
				targetClass = targetClass.getSuperclass();
				if (targetClass == Object.class) {
					return null;
				}
				continue;
			}
		}
	}

	protected Object getAnnotationFromPredecessor(Object targetObj,
			Class annotationClass) {
		if (targetObj == null) {
			return null;
		}
		Object result = null;
		Class targetClass = targetObj.getClass();
		while (true) {
			try {
				result = targetClass.getAnnotation(annotationClass);
				return result;
			} catch (Exception e) {
				targetClass = targetClass.getSuperclass();
				if (targetClass == Object.class) {
					return result;
				}
				continue;
			}
		}
	}

	protected String getSQL(File sqlFile) throws IOException {
		StringBuilder result;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sqlFile));
			String line = null;
			result = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result.toString();
	}

	protected void configHibernate(Object target) throws Exception {
		SSOFDaoUTHibernate hibernateCfg = (SSOFDaoUTHibernate) getAnnotationFromPredecessor(
				target, SSOFDaoUTHibernate.class);
		if (hibernateCfg == null) {
			return;
		}
		File configFile = new File(hibernateCfg.value());
		if (!configFile.exists()) {
			throw new SSOFConfigFileNotFoundException(
					"hibernate config file not found in:"
							+ configFile.getAbsolutePath());
		}
		Configuration conf = new Configuration();
		conf.configure(configFile);
		SessionFactory sessionFactory = conf.buildSessionFactory();
		Class targetClass = target.getClass();
		Field sessionField = null;
		while (true) {
			try {
				sessionField = targetClass
						.getDeclaredField("hibernateSessionFactory");
				sessionField.setAccessible(true);
				sessionField.set(target, sessionFactory);
				break;
			} catch (Exception e) {
				targetClass = targetClass.getSuperclass();
				if (targetClass == Object.class) {
					break;
				}
				continue;
			}
		}
		Session session = sessionFactory.openSession();
	}

	protected String convertToSQL(File sqlFile) throws IOException {
		StringBuilder result;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sqlFile));
			String line = null;
			result = new StringBuilder();
			String tableName = sqlFile.getName().substring(0,
					sqlFile.getName().indexOf("."));
			result.append("create table ");
			result.append(tableName);
			result.append("(");
			boolean findPK = false;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) {
					continue;
				}
				line = line.trim().replace("NOT NULL", "");
				if (line.indexOf("PRIMARY KEY") != -1) {
					result.append(line.trim());
					findPK = true;
					break;
				}
				String[] array = line.split(" ");
				String columnName = null;
				int index = 0;
				for (; index < array.length; index++) {
					if (array[index].trim().length() > 0) {
						columnName = array[index].trim();
						index++;
						break;
					}
				}
				String dataType = null;
				for (; index < array.length; index++) {
					if (array[index].trim().length() > 0) {
						dataType = array[index].trim();
						break;
					}
				}
				result.append(columnName + " " + dataType + ",");
			}
			result.append(")");
			if (!findPK) {
				throw new IOException("You must define a primary key");
			}
			reader.close();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result.toString();
	}
}
