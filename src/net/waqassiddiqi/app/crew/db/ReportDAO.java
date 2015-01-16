package net.waqassiddiqi.app.crew.db;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.model.ErrorReportEntry;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class ReportDAO {
	protected ConnectionManager db;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public ReportDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public void addErrorReportEntry(ErrorReportEntry entry) {
		
		//db.executeUpdate("DELETE FROM error_report WHERE entry_date = ? AND crew_id = ?", new String[] { 
		//		Long.toString(entry.getEntryDate().getTime()), Integer.toString(entry.getCrew().getId()) });
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(entry.getEntryDate());
		
		db.executeUpdate("DELETE FROM error_report WHERE day = ? AND month = ? AND year = ? AND crew_id = ?", new String[] { 
				Long.toString(cal.get(Calendar.DAY_OF_MONTH)), 
				Long.toString(cal.get(Calendar.MONTH)),
				Long.toString(cal.get(Calendar.YEAR)),
				Integer.toString(entry.getCrew().getId()) });
		
		db.executeInsert("INSERT INTO error_report(" +
				"crew_id, " +
				"entry_date, " +
				"work_24hr, " +
				"rest_24hr, " +
				"any_rest_24hr, " +
				"rest_7days, " +
				"rest_greater_10hrs, " +
				"work_less_14hrs, " +
				"total_rest_24hr_greater_10hrs, " +
				"total_rest_7days_greater_77hrs, " +
				"one_rest_period_6hrs, " +
				"total_rest_periods, " +
				"rest_hrs_greater_36_3_days, day, month, year) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new String[] { 
					Integer.toString(entry.getCrew().getId()),
					Long.toString(entry.getEntryDate().getTime()),
					Double.toString(entry.getWorkIn24hours()),
					Double.toString(entry.getRestIn24hours()),
					Double.toString(entry.getAnyRest24hours()),
					Double.toString(entry.getRest7days()),
					entry.isRestGreater10hrs() == true ? "1" : "0",
					entry.isWorkLess14hrs() == true ? "1" : "0",
					entry.isTotalRest24hrsGreater10hrs() == true ? "1" : "0",
					entry.isTotalRest7daysGreater77hrs() == true ? "1" : "0",
					entry.isOneRestPeriod6hrs() == true ? "1" : "0",
					Integer.toString(entry.getTotalRestPeriods()),
					Double.toString(entry.getRestHour3daysGreater36hrs()),
					Long.toString(cal.get(Calendar.DAY_OF_MONTH)), 
					Long.toString(cal.get(Calendar.MONTH)),
					Long.toString(cal.get(Calendar.YEAR))
				}
				
				
				);
	}
	
	public String getPivotDataByYearAndMonth(Date startDate, Date endDate) {
		Writer writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',');
		
		ResultSet rs = null;
		
		int hoursOffset = Calendar.getInstance().getTimeZone().getOffset(new Date().getTime()) / 1000;
		
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		
		calStart.setTime(startDate);
		calEnd.setTime(endDate);
		
		String strSql = "select FORMATDATETIME(DATEADD('SECOND', (e.entry_date/1000) + " + hoursOffset + ", DATE '1970-01-01'), 'MMMYYYY') MONTH, v.NAME VESSELNAME,  " +
					"V.IMO IMONUMBER, CONCAT(c.FIRST_NAME, c.LAST_NAME) CREWNAME, book_number_or_passport PASSPORT, RANK, " +
					"(CASE WHEN IS_WATCH_KEEPER = true THEN 'YES' ELSE 'NO' END) AS WATCHKEEPER, " + 
					"FORMATDATETIME(DATEADD('SECOND', (e.entry_date/1000) + " + hoursOffset + ", DATE '1970-01-01'), 'dd/MM/yyyy') DATE, " +
					"WORK_24HR, REST_24HR, ANY_REST_24HR, REST_7DAYS, " +
					"(CASE WHEN REST_GREATER_10HRS = true THEN 'ERROR' ELSE '' END) AS \"Total period of REST > 10 Hrs\", " +
					"(CASE WHEN WORK_LESS_14HRS = true THEN 'ERROR' ELSE '' END) AS \"Total Period of WORK < 14 Hrs\", " +
					"(CASE WHEN TOTAL_REST_24HR_GREATER_10HRS = true THEN 'ERROR' ELSE '' END) AS \"24-hour Total Period of REST > 10 Hrs\", " +
					"(CASE WHEN TOTAL_REST_7DAYS_GREATER_77HRS = true THEN 'ERROR' ELSE '' END) AS \"7-days Total Period of REST > 77 Hrs\", " +
					"(CASE WHEN ONE_REST_PERIOD_6HRS = true THEN 'ERROR' ELSE '' END) AS \"At least one period of rest 6 hours in length\", " +
					"TOTAL_REST_PERIODS \"Total Number of Rest Periods More than 2\", REST_HRS_GREATER_36_3_DAYS \"Rest Hours > 36 hours in any 3 days\" " + 
					"FROM ERROR_REPORT e " + 
					"INNER JOIN CREWS c ON c.id = e.crew_id " + 
					"INNER JOIN VESSELS v on v.id = c.vessel_id " +
					"WHERE c.is_active = true AND " +
					"e.entry_date >= " +
					"e.month = " + calStart.get(Calendar.MONTH) + " AND e.year = " + calStart.get(Calendar.YEAR) + 
					" AND (e.day >= " + calStart.get(Calendar.DAY_OF_MONTH) + " AND e.day <= " + calEnd.get(Calendar.DAY_OF_MONTH) + ")"; 
					
					
					//startDate.getTime() + " AND e.entry_date <= " + endDate.getTime() + " ORDER BY e.entry_date";
		
		try {
			
			rs = this.db.executeQuery(strSql);
			csvWriter.writeAll(rs, true);
			
		} catch (Exception e) {
			log.error("Error executing EntryTimeDAO.getByYearMonthAndCrew(): " + e.getMessage(), e);
		} finally {
			try {
				
				if(csvWriter != null)
					csvWriter.close();
				
				if (rs != null) rs.close();
			} catch (SQLException | IOException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return writer.toString();
	}
	
	public ResultSet getWorkingArragements() {
		String strSql = "SELECT CONCAT(c.FIRST_NAME, ' ', c.LAST_NAME, ' / ', c.RANK) AS CREW, c.id AS CREW_ID, c.is_watch_keeper, t.* " +
				"FROM SCHEDULE_TEMPLATES t " +
				"INNER JOIN CREW_SCHEDULE_TEMPLATE ct ON ct.schedule_id = t.id " +
				"INNER JOIN CREWS c " +
				"ON c.id = ct.crew_id " +
				"WHERE c.IS_ACTIVE = true AND c.IS_WATCH_KEEPER = IS_WATCH_KEEPING ORDER BY c.id";
		
		return db.executeQuery(strSql);
	}
	
	public List<String[]> getPotentialNonConformities(Date startDate, Date endDate) {
		List<String[]> data = new ArrayList<String[]>();
		String[] row;
		StringBuilder sb = new StringBuilder();
		ResultSet rs = null;
		String colData;
		int hoursOffset = Calendar.getInstance().getTimeZone().getOffset(new Date().getTime()) / 1000;
		
		String strSql = "select FORMATDATETIME(DATEADD('SECOND', (e.entry_date/1000) + " + hoursOffset + ", DATE '1970-01-01'), 'dd/MM') MONTH, CONCAT(c.first_name, ' / ', c.Rank), " +
				"t.comments, " +
				"(CASE WHEN REST_GREATER_10HRS = true THEN 'Total period of REST > 10 Hrs' ELSE '' END) AS \"Total period of REST > 10 Hrs\", " +
				"(CASE WHEN WORK_LESS_14HRS = true THEN 'Total Period of WORK < 14 Hrs' ELSE '' END) AS \"Total Period of WORK < 14 Hrs\", " +
				"(CASE WHEN TOTAL_REST_24HR_GREATER_10HRS = true THEN '24-hour Total Period of REST > 10 Hrs' ELSE '' END) AS \"24-hour Total Period of REST > 10 Hrs\", " +
				"(CASE WHEN TOTAL_REST_7DAYS_GREATER_77HRS = true THEN '7-days Total Period of REST > 77 Hrs' ELSE '' END) AS \"7-days Total Period of REST > 77 Hrs\", " +
				"(CASE WHEN ONE_REST_PERIOD_6HRS = true THEN 'At least one period of rest 6 hours in length' ELSE '' END) AS \"At least one period of rest 6 hours in length\", " +
				"(CASE WHEN TOTAL_REST_PERIODS > 2 THEN 'Total Number of Rest Periods More than 2' ELSE '' END) \"Total Number of Rest Periods More than 2\", " +
				"(CASE WHEN REST_HRS_GREATER_36_3_DAYS > 36 THEN 'Rest Hours > 36 hours in any 3 days' ELSE '' END) AS \"Rest Hours > 36 hours in any 3 days\", " +
				"e.entry_date " +
				"FROM ERROR_REPORT e INNER JOIN CREWS c ON c.id = e.crew_id INNER JOIN entry_times t ON c.id = t.crew_id " + 
				"WHERE c.is_active = true AND " + 
				"e.entry_date >= " + startDate.getTime() + " AND e.entry_date <= " + endDate.getTime() + " and e.entry_date = t.entry_date ORDER BY e.entry_date";
		
		try {
			
			rs = db.executeQuery(strSql);
			while(rs.next()) {
				sb.setLength(0);
				row = new String[5];
				
				row[0] = rs.getString(1);
				row[1] = rs.getString(2);
				row[2] = rs.getString(3);
				
				for(int i=1; i<=7; i++) {
					colData = rs.getString(3 + i);
					if(colData != null && colData.trim().isEmpty() == false) {
						sb.append(colData);
						sb.append(System.getProperty("line.separator"));
					}
				}
				
				row[3] = sb.toString();
				row[4] = rs.getString(11);
				
				if(row[3].trim().length() > 0)
					data.add(row);
			}
			
		} catch (Exception e) {
			log.error("Error executing ErrorReportDAO.getPotentialNonConformities(): " + e.getMessage(), e);
		}
		
		return data;
	}
}
