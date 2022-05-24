package generic.utils;

import java.beans.PropertyVetoException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBConnectionPool {
	public static ComboPooledDataSource getDefaultReportingDBDataSource() throws PropertyVetoException {
		ComboPooledDataSource connectionPool = new ComboPooledDataSource();
		connectionPool.setDriverClass("com.mysql.jdbc.Driver");
		String dburl = "jdbc:mysql://13.235.186.44:3306/core";
		String dbuserName = "admin";
		String dbpassword = "Nykaa@123";
        connectionPool.setJdbcUrl(dburl);
        connectionPool.setUser(dbuserName);
        connectionPool.setPassword(dbpassword);
 
        // Optional Settings
		connectionPool.setInitialPoolSize(25);
        connectionPool.setAcquireIncrement(5);
        connectionPool.setMaxPoolSize(30);
 
        return connectionPool;
	}
	
	public static ComboPooledDataSource getCustomDBDataSource(String dburl,String dbuserName,String dbpassword) {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setJdbcUrl(dburl);
        cpds.setUser(dbuserName);
        cpds.setPassword(dbpassword);
 
        // Optional Settings
		cpds.setInitialPoolSize(5);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);
		cpds.setMaxStatements(100);
 
        return cpds;
	}
}
