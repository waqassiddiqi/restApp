package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Crew;

import org.apache.log4j.Logger;

public class CrewDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public CrewDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addCrew(Crew crew) {
		
		int generatedId = db.executeInsert("INSERT INTO crews(vessel_id, first_name, last_name, " +
				"rank, nationality, " +
				"book_number_or_passport, signon_date, is_watch_keeper) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?);", 
				
				new String[] { Integer.toString(crew.getVesselId()), crew.getFirstName(), crew.getLastName(),
						crew.getRank(), crew.getNationality(), crew.getPassportNumber(),
						Long.toString(crew.getSignOnDate().getTime()), 
						crew.isWatchKeeper() == true ? "1" : "0"
					});
		
		if(log.isDebugEnabled()) {
			log.debug("Generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public int updateCrew(Crew crew) {
		
		int generatedId = db.executeUpdate("UPDATE crews SET first_name = ?, last_name = ?, " +
				"rank = ?, nationality = ?, " +
				"book_number_or_passport = ?, signon_date = ?, is_watch_keeper = ? WHERE id = ?",
					new String[] { crew.getFirstName(), crew.getLastName(),
						crew.getRank(), crew.getNationality(), crew.getPassportNumber(),
						Long.toString(crew.getSignOnDate().getTime()), 
						crew.isWatchKeeper() == true ? "1" : "0", Integer.toString(crew.getId())
					});
		
		if(log.isDebugEnabled()) {
			log.debug("Generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public List<Crew> getAll() {
		
		List<Crew> list = new ArrayList<Crew>();
		final ResultSet rs = this.db.executeQuery("SELECT * FROM crews ORDER BY id");
		
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
	

	public boolean isPassportExists(String passportNumber) {
		final ResultSet rs = this.db.executeQuery("SELECT * FROM crews where book_number_or_passport = '" 
				+ passportNumber + "'");
		
		try {			
			
			if(rs.next()) {
				return true;	
			}
				
		} catch (Exception e) {
			log.error("Error executing CrewDAO.isPassportExists(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public void delete(final int id) {
		
		try {			
			
			this.db.executeUpdate("DELETE FROM crews where id = " + id);
			new ScheduleTemplateDAO().removeScheduleTemplateByCrew(new Crew() {{ setId(id); }});
			
				
		} catch (Exception e) {
			log.error("Error executing CrewDAO.delete(): " + e.getMessage(), e);
		}
	}
	
	public Crew getById(int id) {
		
		Crew c = null;
		final ResultSet rs = this.db.executeQuery("SELECT * FROM crews where id = " + id);
		
		try {			
			
			if(rs.next()) {
				c = new Crew() {{ setId(rs.getInt("id"));
						setVesselId(rs.getInt("vessel_id"));
						setFirstName(rs.getString("first_name"));
						setLastName(rs.getString("last_name"));
						setRank(rs.getString("rank"));
						setNationality(rs.getString("nationality"));
						setPassportNumber(rs.getString("book_number_or_passport"));
						setSignOnDate(new Date(rs.getLong("signon_date")));
						setWatchKeeper(rs.getBoolean("is_watch_keeper")); 
					}};	
			}
				
		} catch (Exception e) {
			log.error("Error executing CrewDAO.getById(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return c;
	}
}