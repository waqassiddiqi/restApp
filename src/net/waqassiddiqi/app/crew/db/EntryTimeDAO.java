package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;

import org.apache.log4j.Logger;

public class EntryTimeDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public EntryTimeDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public EntryTime getByDateAndCrew(Date date, Crew crew) {
		EntryTime entry = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		final ResultSet rs = this.db.executeQuery(
				"SELECT * FROM entry_times WHERE crew_id = " + crew.getId() + " AND day = " + cal.get(Calendar.DAY_OF_MONTH) 
					+ " AND month = " + cal.get(Calendar.MONTH) + " AND year = " + cal.get(Calendar.YEAR));
		
		if(log.isDebugEnabled()) {
			log.debug("Fetching entry for: " + date.toString());
		}
		
		try {
			
			if(rs.next()) {
				entry = new EntryTime();
				
				entry.setEntryDate(date);
				entry.setCrewId(crew.getId());
				entry.setComments(rs.getString("comments"));
				entry.setOnPort(rs.getBoolean("is_on_port"));
				entry.parseSchedule(rs.getString("schedule"));
			}
			
		} catch (Exception e) {
			log.error("Error executing EntryTimeDAO.getByDateAndCrew(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return entry;
	}
	
	public List<EntryTime> getByYearMonthAndCrew(Date startDate, Date endDate, Crew crew) {
		List<EntryTime> entryList = new ArrayList<EntryTime>();
		EntryTime entry;
		
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		final ResultSet rs = this.db.executeQuery(
				"SELECT * FROM entry_times WHERE crew_id = " + crew.getId() + " AND year = " + calStart.get(Calendar.YEAR) + 
				" AND month = " + calStart.get(Calendar.MONTH) + " AND (day >= " + calStart.get(Calendar.DAY_OF_MONTH) + " "
						+ "AND day <= " + calEnd.get(Calendar.DAY_OF_MONTH) + ")"
				);
		
		
		//final ResultSet rs = this.db.executeQuery(
		//		"SELECT * FROM entry_times WHERE crew_id = " + crew.getId() + " AND entry_date >= " + 
		//				startDate.getTime() + " AND entry_date <= " + endDate.getTime() + " ORDER BY entry_date");
		
		try {
			
			while(rs.next()) {
				entry = new EntryTime();
				
				entry.setEntryDate(new Date(rs.getLong("entry_date")));
				entry.setCrewId(crew.getId());
				entry.setComments(rs.getString("comments"));
				entry.setOnPort(rs.getBoolean("is_on_port"));
				entry.parseSchedule(rs.getString("schedule"));
				
				entryList.add(entry);
			}
			
		} catch (Exception e) {
			log.error("Error executing EntryTimeDAO.getByYearMonthAndCrew(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return entryList;
	}
	
	public int updateEntry(EntryTime entry) {
		
		int count = db.executeUpdate("UPDATE entry_times SET schedule = ?, is_on_port = ?, work_in_24_hours = ?, " +
				"rest_in_24_hours = ?, comments = ?, entry_date = ? WHERE id = ?",
				new String[] {
					ScheduleTemplate.getScheduleString(entry.getSchedule()),
					entry.isOnPort() == true ? "1" : "0",
					Float.toString(entry.getWorkIn24Hours()),
					Float.toString(entry.getRestIn24Hours()),
					entry.getComments(),
					Long.toString(entry.getEntryDate().getTime()),
					Integer.toString(entry.getId())
				});
		
		
		if(log.isDebugEnabled()) {
			if(count > 0)
				log.debug("Resting hours entry has been updated: " + entry);
		}
		
		return count;
	}
	
	public int addUpdateEntry(EntryTime entry) {
		
		int count = db.executeUpdate("UPDATE entry_times SET schedule = ?, is_on_port = ?, work_in_24_hours = ?, " +
				"rest_in_24_hours = ?, comments = ? WHERE crew_id = ? AND day = ? AND month = ? AND year = ?",
				new String[] {
					ScheduleTemplate.getScheduleString(entry.getSchedule()),
					entry.isOnPort() == true ? "1" : "0",
					Float.toString(entry.getWorkIn24Hours()),
					Float.toString(entry.getRestIn24Hours()),
					entry.getComments(),
					Integer.toString(entry.getCrewId()),
					Long.toString(entry.getEntryCalendar().get(Calendar.DAY_OF_MONTH)),
					Long.toString(entry.getEntryCalendar().get(Calendar.MONTH)),
					Long.toString(entry.getEntryCalendar().get(Calendar.YEAR))
				});
		
		if(count <= 0) {
			
			count = db.executeInsert("INSERT INTO entry_times(crew_id, entry_date, schedule, " +
					"is_on_port, work_in_24_hours, " +
					"rest_in_24_hours, comments, day, month, year) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", 
					
					new String[] { Integer.toString(entry.getCrewId()), 
					Long.toString(entry.getEntryDate().getTime()),
							ScheduleTemplate.getScheduleString(entry.getSchedule()), 
							entry.isOnPort() == true ? "1" : "0",
							Float.toString(entry.getWorkIn24Hours()),
							Float.toString(entry.getRestIn24Hours()),
							entry.getComments(),
							Long.toString(entry.getEntryCalendar().get(Calendar.DAY_OF_MONTH)),
							Long.toString(entry.getEntryCalendar().get(Calendar.MONTH)),
							Long.toString(entry.getEntryCalendar().get(Calendar.YEAR))
						});			
		}
		
		
		if(log.isDebugEnabled()) {
			if(count > 0)
				log.debug("Resting hours entry has been added or updated: " + entry);
		}
		
		return count;
	}
	
	public List<EntryTime> getAll() {
		
		List<EntryTime> list = new ArrayList<EntryTime>();
		EntryTime entry = null;
		final ResultSet rs = this.db.executeQuery("SELECT * FROM entry_times");
		
		try {
			while(rs.next()) {
				entry = new EntryTime();
				
				entry.setId(rs.getInt("id"));
				entry.setEntryDate(new Date(rs.getLong("entry_date")));
				entry.setCrewId(rs.getInt("crew_id"));
				entry.setComments(rs.getString("comments"));
				entry.setOnPort(rs.getBoolean("is_on_port"));
				entry.parseSchedule(rs.getString("schedule"));
				
				list.add(entry);
			}
		} catch (Exception e) {
			log.error("Error executing EntryTimeDAO.getAll(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return list;
	}
}