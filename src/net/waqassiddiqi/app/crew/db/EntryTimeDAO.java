package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		
		final ResultSet rs = this.db.executeQuery(
				"SELECT * FROM entry_times WHERE crew_id = " + crew.getId() + " AND entry_date = " + date.getTime());
		
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
		
		final ResultSet rs = this.db.executeQuery(
				"SELECT * FROM entry_times WHERE crew_id = " + crew.getId() + " AND entry_date >= " + 
						startDate.getTime() + " AND entry_date <= " + endDate.getTime() + " ORDER BY entry_date");
		
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
	
	public int addUpdateEntry(EntryTime entry) {
		
		
		int count = db.executeUpdate("UPDATE entry_times SET schedule = ?, is_on_port = ?, work_in_24_hours = ?, " +
				"rest_in_24_hours = ?, comments = ? WHERE crew_id = ? AND entry_date = ?",
				new String[] {
					ScheduleTemplate.getScheduleString(entry.getSchedule()),
					entry.isOnPort() == true ? "1" : "0",
					Float.toString(entry.getWorkIn24Hours()),
					Float.toString(entry.getRestIn24Hours()),
					entry.getComments(),
					Integer.toString(entry.getCrewId()),
					Long.toString(entry.getEntryDate().getTime())
				});
		
		if(count <= 0) {
			
			count = db.executeInsert("INSERT INTO entry_times(crew_id, entry_date, schedule, " +
					"is_on_port, work_in_24_hours, " +
					"rest_in_24_hours, comments) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?);", 
					
					new String[] { Integer.toString(entry.getCrewId()), 
					Long.toString(entry.getEntryDate().getTime()),
							ScheduleTemplate.getScheduleString(entry.getSchedule()), 
							entry.isOnPort() == true ? "1" : "0",
							Float.toString(entry.getWorkIn24Hours()),
							Float.toString(entry.getRestIn24Hours()),
							entry.getComments(),
						});			
		}
		
		
		if(log.isDebugEnabled()) {
			if(count > 0)
				log.debug("Resting hours entry has been added or updated: " + entry);
		}
		
		return count;
	}
	
	public List<Crew> getAll() {
		
		List<Crew> list = new ArrayList<Crew>();
		final ResultSet rs = this.db.executeQuery("SELECT * FROM crews");
		
		try {
			while (rs.next()) {
				list.add(new Crew() { { 
					setId(rs.getInt("id"));
					setVesselId(rs.getInt("vessel_id"));
					setFirstName(rs.getString("first_name"));
					setLastName(rs.getString("last_name"));
					setRank(rs.getString("rank"));
					setNationality(rs.getString("nationality"));
					setPassportNumber(rs.getString("book_number_or_passport"));
					setSignOnDate(new Date(rs.getLong("signon_date")));
					setWatchKeeper(rs.getBoolean("is_watch_keeper"));
				} });
			}
		} catch (Exception e) {
			log.error("Error executing CrewDAO.getAll(): " + e.getMessage(), e);
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