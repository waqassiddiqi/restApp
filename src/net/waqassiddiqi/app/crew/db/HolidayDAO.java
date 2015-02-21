package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Holiday;
import net.waqassiddiqi.app.crew.model.HolidayList;

import org.apache.log4j.Logger;

public class HolidayDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public HolidayDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public void removeHolidaysFromHolidayList(int holidayListId) {
		db.executeUpdate("DELETE FROM holiday WHERE holiday_id =" + holidayListId);
	}
	
	public int addHoliday(Holiday holiday) {
		
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(holiday.getHolidayDate());
		
		int generatedId = db.executeInsert("INSERT INTO holiday(holiday_id, description, day, month, year) " +
				"VALUES(?, ?, ?, ?, ? );", 
				
				new String[] { 
						Long.toString(holiday.getHolidayListId()),
						holiday.getDescription(),
						Long.toString(calDate.get(Calendar.DAY_OF_MONTH)),
						Long.toString(calDate.get(Calendar.MONTH)),
						Long.toString(calDate.get(Calendar.YEAR))
					});
		
		
		if(log.isDebugEnabled()) {
			if(generatedId > 0)
				log.debug("Holiday has been added");
		}
		
		return generatedId;
	}
	
	public List<Holiday> getHolidayByListId(int listId) {
		
		List<Holiday> holidays = new ArrayList<Holiday>();
		Holiday entry = null;
		final ResultSet rs = this.db.executeQuery("SELECT * FROM holiday WHERE holiday_id = " + listId);
		
		try {
			while(rs.next()) {
				entry = new Holiday();
				
				entry.setId(rs.getInt("id"));
				entry.setHolidayListId(listId);
				
				Calendar calDate = Calendar.getInstance();
				calDate.set(Calendar.YEAR, rs.getInt("year"));
				calDate.set(Calendar.MONTH, rs.getInt("month"));
				calDate.set(Calendar.DAY_OF_MONTH, rs.getInt("day"));
				entry.setHolidayDate(calDate.getTime());
				
				entry.setDescription(rs.getString("description"));
				
				holidays.add(entry);
			}
		} catch (Exception e) {
			log.error("Error executing HolidayDAO.getAll(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return holidays;
	}
	
	public int addHolidayList(HolidayList entry) {
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(entry.getFrom());
		
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(entry.getTo());
		
		int generatedId = db.executeInsert("INSERT INTO holiday_list(name, from_day, from_month, " +
				"from_year, to_day, " +
				"to_month, to_year) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?);", 
				
				new String[] { 
						entry.getName(),
						Long.toString(calFrom.get(Calendar.DAY_OF_MONTH)),
						Long.toString(calFrom.get(Calendar.MONTH)),
						Long.toString(calFrom.get(Calendar.YEAR)),
						Long.toString(calTo.get(Calendar.DAY_OF_MONTH)),
						Long.toString(calTo.get(Calendar.MONTH)),
						Long.toString(calTo.get(Calendar.YEAR))
					});
		
		
		if(log.isDebugEnabled()) {
			if(generatedId > 0)
				log.debug("Holiday list has been added");
		}
		
		return generatedId;
	}
	
	public int updateHolidayList(HolidayList entry) {
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(entry.getFrom());
		
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(entry.getTo());
		
		int rowsUpdated = db.executeUpdate("UPDATE holiday_list SET name = ?, from_day = ?, from_month = ?, " +
				"from_year = ?, to_day = ?, " +
				"to_month = ?, to_year = ? WHERE id = ?",
				
				new String[] { 
						entry.getName(),
						Long.toString(calFrom.get(Calendar.DAY_OF_MONTH)),
						Long.toString(calFrom.get(Calendar.MONTH)),
						Long.toString(calFrom.get(Calendar.YEAR)),
						Long.toString(calTo.get(Calendar.DAY_OF_MONTH)),
						Long.toString(calTo.get(Calendar.MONTH)),
						Long.toString(calTo.get(Calendar.YEAR)),
						Long.toString(entry.getId())
					});
		
		
		if(log.isDebugEnabled()) {
			if(rowsUpdated > 0)
				log.debug("Holiday list has been update");
		}
		
		return rowsUpdated;
	}
	
	public List<HolidayList> getAllHolidayList() {
		
		List<HolidayList> list = new ArrayList<HolidayList>();
		HolidayList entry = null;
		final ResultSet rs = this.db.executeQuery("SELECT * FROM holiday_list order by id");
		
		try {
			while(rs.next()) {
				entry = new HolidayList();
				
				entry.setId(rs.getInt("id"));
				
				Calendar calFromDate = Calendar.getInstance();
				calFromDate.set(Calendar.YEAR, rs.getInt("from_year"));
				calFromDate.set(Calendar.MONTH, rs.getInt("from_month"));
				calFromDate.set(Calendar.DAY_OF_MONTH, rs.getInt("from_day"));
				entry.setFrom(calFromDate.getTime());
				
				Calendar calToDate = Calendar.getInstance();
				calToDate.set(Calendar.YEAR, rs.getInt("to_year"));
				calToDate.set(Calendar.MONTH, rs.getInt("to_month"));
				calToDate.set(Calendar.DAY_OF_MONTH, rs.getInt("to_day"));
				entry.setTo(calToDate.getTime());
				
				entry.setName(rs.getString("name"));
				
				list.add(entry);
			}
		} catch (Exception e) {
			log.error("Error executing HolidayDAO.getAll(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return list;
	}
	
	public HolidayList getHolidayList(int id) {
		
		HolidayList entry = null;
		final ResultSet rs = this.db.executeQuery("SELECT * FROM holiday_list WHERE id = " + id);
		
		try {
			if(rs.next()) {
				entry = new HolidayList();
				
				entry.setId(rs.getInt("id"));
				
				Calendar calFromDate = Calendar.getInstance();
				calFromDate.set(Calendar.YEAR, rs.getInt("from_year"));
				calFromDate.set(Calendar.MONTH, rs.getInt("from_month"));
				calFromDate.set(Calendar.DAY_OF_MONTH, rs.getInt("from_day"));
				entry.setFrom(calFromDate.getTime());
				
				Calendar calToDate = Calendar.getInstance();
				calToDate.set(Calendar.YEAR, rs.getInt("to_year"));
				calToDate.set(Calendar.MONTH, rs.getInt("to_month"));
				calToDate.set(Calendar.DAY_OF_MONTH, rs.getInt("to_day"));
				entry.setTo(calToDate.getTime());
				
				entry.setName(rs.getString("name"));
			}
		} catch (Exception e) {
			log.error("Error executing HolidayDAO.getAll(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return entry;
	}
}