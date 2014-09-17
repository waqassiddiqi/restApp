package net.waqassiddiqi.app.crew.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

public class ConnectionManager {
	private static ConnectionManager instance = null;
	private Connection connection = null;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public synchronized static ConnectionManager getInstance() {
		if(instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	public Connection getConnection() throws SQLException {
		if(this.connection == null) {
			connection = createConnection();
		} 
		
		return connection;
	}
	
	private Connection createConnection() throws SQLException {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:~/restdb;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9090;");
        ds.setUser("sa");
        ds.setPassword("sa");
        
        return ds.getConnection();
	}
	
	public void setupDatabase() throws SQLException {
		
		createConnection();
		
		String strSqlVessels = "CREATE TABLE IF NOT EXISTS vessels("
                + "id bigint auto_increment, "
                + "name VARCHAR(30), "
                + "imo VARCHAR(30), " 
                + "flag VARCHAR(30) " +
                ")";	
		
		executeUpdate(strSqlVessels);
		
		String strSqlCrew = "CREATE TABLE IF NOT EXISTS crews("
                + "id bigint auto_increment, "
                + "vessel_id bigint, "
                + "first_name VARCHAR(30), " 
                + "last_name VARCHAR(30) " 
                + "rank VARCHAR(30) "
                + "nationality VARCHAR(30) "
                + "book_number_or_passport VARCHAR(30) "
                + "signon_date VARCHAR(30) "
                + "is_watch_keeper BOOLEAN "
                + ")";	
		
		executeUpdate(strSqlCrew);
	}
	
	public ResultSet executeQuery(String strSql) {
		Statement stmt = null;
		
		if(log.isDebugEnabled()) {
			log.debug("executing: " + strSql);
		}
		
		try {
			
			stmt = this.getConnection().createStatement();
			return stmt.executeQuery(strSql);
			
		} catch (SQLException e) {
			log.error("executeQuery failed: " + e.getMessage(), e);
		}
		
		return null;
	}
	
	public int executeUpdate(String strSql) {
		Statement  stmt = null;
		int result = -1;
		
		if(log.isDebugEnabled()) {
			log.debug("executing: " + strSql);
		}
		
		try {
			
			stmt = this.getConnection().createStatement();
			result = stmt.executeUpdate(strSql);
			
		} catch (SQLException e) {
			log.error("executeUpdate failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
			
			if(log.isDebugEnabled()) {
				log.debug("result: " + result);
			}
		}
		
		return result;
	}
	
	public int executeUpdate(String strSql, String... args) {		
		PreparedStatement stmt = null;
		
		if(log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Executing: " + strSql + " with parameters: [ ");
			
			for(String arg : args) {
				sb.append(arg + ", ");
			}
			
			sb.setLength(sb.length() -1);
			sb.append("]");
			
			log.debug(sb.toString());
		}
		
		try {
			stmt = this.getConnection().prepareStatement(strSql);
			
			for(int i=1; i<= args.length; i++) {
				stmt.setString(i, args[i-1]);
			}
			
			return stmt.executeUpdate();
		} catch (SQLException e) {
			log.error("executeUpdate failed: " + e.getMessage(), e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return -1;
	}
}