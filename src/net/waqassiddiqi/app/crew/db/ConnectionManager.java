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
		
		ds.setURL("jdbc:h2:tcp://localhost:9090/~/restdb;");
		
		//ds.setURL("jdbc:h2:~/restdb;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9090;");
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
                + "last_name VARCHAR(30), " 
                + "rank VARCHAR(30), "
                + "nationality VARCHAR(30), "
                + "book_number_or_passport VARCHAR(30), "
                + "signon_date bigint, "
                + "is_watch_keeper BOOLEAN, "
                + "is_active BOOLEAN DEFAULT true"
                + ")";	
		
		executeUpdate(strSqlCrew);
		
		String strSqlScheduleTemplate = "CREATE TABLE IF NOT EXISTS schedule_templates("
                + "id bigint auto_increment, "
                + "schedule VARCHAR(48), "
                + "is_on_port BOOLEAN, "
                + "is_watch_keeping BOOLEAN "
                + ")";	
		
		executeUpdate(strSqlScheduleTemplate);
		
		String strSqlScheduleRank = "CREATE TABLE IF NOT EXISTS ranks("
                + "id bigint auto_increment, "
                + "rank VARCHAR(30) "
                + ")";	
		
		executeUpdate(strSqlScheduleRank);
		
		String strSqlRankScheduleTemplate = "CREATE TABLE IF NOT EXISTS ranks_schedule_template("
                + "id bigint auto_increment, "
                + "rank_id bigint, "
                + "schedule_id bigint, "
                + "is_on_port BOOLEAN "
                + ")";	
		
		executeUpdate(strSqlRankScheduleTemplate);
		
		String strSqlCrewScheduleTemplate = "CREATE TABLE IF NOT EXISTS crew_schedule_template("
                + "id bigint auto_increment, "
                + "crew_id bigint, "
                + "schedule_id bigint, "
                + "is_on_port BOOLEAN "
                + ")";	
		
		executeUpdate(strSqlCrewScheduleTemplate);
		
		String strSqlEntryTime = "CREATE TABLE IF NOT EXISTS entry_times("
                + "id bigint auto_increment, "
                + "crew_id bigint, "
                + "entry_date bigint, "
                + "schedule VARCHAR(48), "
                + "is_on_port BOOLEAN, "
                + "work_in_24_hours REAL, "
                + "rest_in_24_hours REAL, "
                + "comments VARCHAR(500) "
                + ")";	
		
		executeUpdate(strSqlEntryTime);
		
		String strRegistrationDetail = "CREATE TABLE IF NOT EXISTS reg_settings("
                + "id bigint auto_increment, "
                + "username VARCHAR(250), "
                + "is_registered BOOLEAN, "
                + "expiry bigint, "
                + "product_key VARCHAR(24), "
                + "app_used REAL, "
                + "registered_on bigint, "
                + "system_id VARCHAR(100), "
                + "usage_started_on bigint, "
                + ")";	
		
		executeUpdate(strRegistrationDetail);
		
		String strPivotReport = "CREATE TABLE IF NOT EXISTS error_report(" +
				"id bigint auto_increment, " +
				"crew_id bigint, " +
				"entry_date bigint, " +
				"work_24hr REAL, " +
				"rest_24hr REAL, " +
				"any_rest_24hr REAL, " +
				"rest_7days REAL, " +
				"rest_greater_10hrs BOOLEAN, " +
				"work_less_14hrs BOOLEAN, " +
				"total_rest_24hr_greater_10hrs BOOLEAN, " +
				"total_rest_7days_greater_77hrs BOOLEAN, " +
				"one_rest_period_6hrs BOOLEAN, " +
				"total_rest_periods BIGINT, " +
				"rest_hrs_greater_36_3_days REAL)";
		
		executeUpdate(strPivotReport);
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
	
	public int executeInsert(String strSql, String... args) {		
		PreparedStatement stmt = null;
		
		if(log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Executing: " + strSql + " with parameters: [ ");
			
			for(String arg : args) {
				sb.append(arg + ", ");
			}
			
			sb.setLength(sb.length()-2);
			sb.append(" ]");
			
			log.debug(sb.toString());
		}
		
		try {
			stmt = this.getConnection().prepareStatement(strSql);
			
			for(int i=1; i<= args.length; i++) {
				stmt.setString(i, args[i-1]);
			}
			
			if(stmt.executeUpdate() > 0) {
				
				ResultSet generatedKeys = stmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}
				
			}
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
	
	public int executeUpdate(String strSql, String... args) {		
		PreparedStatement stmt = null;
		
		if(log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Executing: " + strSql + " with parameters: [ ");
			
			for(String arg : args) {
				sb.append(arg + ", ");
			}
			
			sb.setLength(sb.length()-2);
			sb.append(" ]");
			
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