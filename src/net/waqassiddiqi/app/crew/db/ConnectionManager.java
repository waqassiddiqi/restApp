package net.waqassiddiqi.app.crew.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.ErrorReportEntry;

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
		
		ds.setURL("jdbc:h2:tcp://localhost:9090/./resource/restdb;IFEXISTS=TRUE;");
		
        ds.setUser("shipip");
        ds.setPassword("letmeinasadmin");
        
        return ds.getConnection();
	}
	
	public void setupDatabase() throws SQLException {
		
		createConnection();
		
		String strSqlUpgrade = "CREATE TABLE IF NOT EXISTS upgrade("
                + "id bigint auto_increment, "
                + "version VARCHAR(30), "
                + "upgrade_required BOOLEAN, " 
                + "release_date VARCHAR(30), "
                + "tables VARCHAR(1500) " +
                ")";
		
		executeUpdate(strSqlUpgrade);
		prepareForUpgrade();
		
		List<String> tablesToUprade = upgrade();
		
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
                + "is_active BOOLEAN DEFAULT true, "
                + "signoff_date bigint "
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
		
		if(tablesToUprade.contains("entry_times")) {
			EntryTimeDAO dao = new EntryTimeDAO();
			
			List<EntryTime> le = dao.getAll();
			executeUpdate("DROP TABLE entry_times");
			
			String strSqlEntryTime = "CREATE TABLE IF NOT EXISTS entry_times("
	                + "id bigint auto_increment, "
	                + "crew_id bigint, "
	                + "entry_date bigint, "
	                + "schedule VARCHAR(48), "
	                + "is_on_port BOOLEAN, "
	                + "work_in_24_hours REAL, "
	                + "rest_in_24_hours REAL, "
	                + "comments VARCHAR(500), "
	                + "day bigint, "
	                + "month bigint, "
	                + "year bigint "
	                + ")";	
			
			executeUpdate(strSqlEntryTime);
			
			for(EntryTime e: le) {
				dao.addUpdateEntry(e);
			}
			
			log.info("entry_times table updated and backed up");
 		}
		
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
		
		if(tablesToUprade.contains("error_report")) {
			ReportDAO rptDao = new ReportDAO();
			List<ErrorReportEntry> eList = rptDao.getAll();
			executeUpdate("DROP TABLE error_report");
			
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
					"rest_hrs_greater_36_3_days REAL,"
	                + "day bigint, "
	                + "month bigint, "
	                + "year bigint "
					+ ")";
			
			executeUpdate(strPivotReport);
			
			for(ErrorReportEntry e: eList) {
				rptDao.addErrorReportEntry(e);
			}
			
			log.info("error_report table updated and backed up");
		}
		
		String strAppSettings = "CREATE TABLE IF NOT EXISTS app_settings("
                + "id bigint auto_increment, "
                + "is_server boolean, "
                + "server_ip VARCHAR(25), "
                + "server_port VARCHAR(5), "
                + "logo_image blob, "
                + "custom_text_rest_report VARCHAR(1500), "
                + "custom_text_error_report VARCHAR(1500), "
                + "custom_text_nc_report VARCHAR(1500), "
                + "custom_text_working_report VARCHAR(1500) "
                + ")";	
		
		executeUpdate(strAppSettings);
		
		String strQuickComment = "CREATE TABLE IF NOT EXISTS quick_comments("
				+ "id bigint auto_increment, "
				+ "quick_comment VARCHAR(45) "
				+ ")";	
		
		executeUpdate(strQuickComment);
		
		String strHolidayList = "CREATE TABLE IF NOT EXISTS holiday_list("
				+ "id bigint auto_increment, "
				+ "name VARCHAR(45), "
				+ "from_day bigint, "
				+ "from_month bigint, "
				+ "from_year bigint, "
				+ "to_day bigint, "
				+ "to_month bigint, "
				+ "to_year bigint "
				+ ")";	
		
		executeUpdate(strHolidayList);
		
		String strHoliday = "CREATE TABLE IF NOT EXISTS holiday("
				+ "id bigint auto_increment, "
				+ "holiday_id bigint, "
				+ "description VARCHAR(45), "
				+ "day bigint, "
				+ "month bigint, "
				+ "year bigint "
				+ ")";	
		
		executeUpdate(strHoliday);

		String strWageRate = "CREATE TABLE IF NOT EXISTS wage("
				+ "id bigint auto_increment, "
				+ "crew_id bigint, "
				+ "hours_paid_basic_weekday REAL, "
				+ "hours_paid_basic_saturday REAL, "
				+ "hours_paid_basic_sunday REAL, "
				+ "hours_paid_basic_holidays REAL, "
				+ "monthly_fixed_overttime_hours REAL, "
				+ "hourly_rate REAL, "
				+ "holiday_list_id bigint "
				+ ")";	
		
		executeUpdate(strWageRate);
		
		executeUpdate("UPDATE upgrade SET upgrade_required = false");
	}
	
	public void prepareForUpgrade() {
		ResultSet rs = null;
		
		try {
			rs = executeQuery("SELECT * FROM upgrade");
			if(rs.next() == false) {
				executeUpdate("INSERT INTO upgrade VALUES(1, '0.1', true, '2015-02-17', 'ENTRY_TIMES,ERROR_REPORT')");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			rs = null;
		}
	}
	
	public List<String> upgrade() throws SQLException {
		ResultSet rs = null;
		boolean bUpgradeRequired = false;
		List<String> listTables = new ArrayList<String>();
		
		try {
		
			rs = executeQuery("SELECT * FROM upgrade");
		
			if(rs != null && rs.next()) {
				bUpgradeRequired = rs.getBoolean("upgrade_required");
				if(bUpgradeRequired) {
					String csvTables = rs.getString("tables");
					if(csvTables != null) {
						String[] tables = csvTables.split(",");
						for(String tableName : tables) {
							listTables.add(tableName.trim().toLowerCase());
						}
					}
				}
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			rs = null;
		}
		
		return listTables;
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