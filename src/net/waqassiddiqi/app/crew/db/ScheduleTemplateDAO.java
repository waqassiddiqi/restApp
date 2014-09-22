package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Rank;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;

import org.apache.log4j.Logger;

public class ScheduleTemplateDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public ScheduleTemplateDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addScheduleTemplate(ScheduleTemplate template) {
		
		int generatedId = db.executeInsert("INSERT INTO schedule_templates(schedule, is_on_port) VALUES(?, ?);", 
				new String[] { template.getScheduleString(), String.valueOf(template.isOnPort()) });
		
		if(log.isDebugEnabled()) {
			log.debug("New generated template ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public int updateScheduleTemplate(ScheduleTemplate template) {
		
		int rowsAffected = db.executeUpdate("UPDATE schedule_templates " +
				"set schedule = ?, is_on_port = ? WHERE id = ?;", 
				new String[] { template.getScheduleString(), 
					String.valueOf(template.isOnPort()), String.valueOf(template.getId()) });
		
		if(log.isDebugEnabled()) {
			log.debug("Updated: " + rowsAffected);
		}
		
		return rowsAffected;
	}
	
	public int associateScheduleTemplate(Rank rank, ScheduleTemplate schedule) {
		int generatedId = db.executeUpdate("INSERT INTO ranks_schedule_template(rank_id, schedule_id, is_on_port) VALUES(?, ?, ?);", 
				new String[] { String.valueOf(rank.getId()), 
					String.valueOf(schedule.getId()), String.valueOf(schedule.isOnPort()) });
		
		if(log.isDebugEnabled()) {
			log.debug("New generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public int associateScheduleTemplate(Crew crew, ScheduleTemplate schedule) {
		int generatedId = db.executeUpdate("INSERT INTO crew_schedule_template(crew_id, schedule_id, is_on_port) VALUES(?, ?, ?);", 
				new String[] { String.valueOf(crew.getId()), 
					String.valueOf(schedule.getId()), String.valueOf(schedule.isOnPort()) });
		
		if(log.isDebugEnabled()) {
			log.debug("New generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public ScheduleTemplate getByRank(Rank rank) {
		final ResultSet rs = this.db.executeQuery("SELECT s.* FROM schedule_templates s " +
				"INNER JOIN ranks_schedule_template rs " +
				"ON rs.schedule_id = s.id WHERE rs.rank_id = " + rank.getId());
		
		ScheduleTemplate template = null;
		
		try {
			
			if(rs.next()) {
				template = new ScheduleTemplate() {{
						setId(rs.getInt("id"));
						parseSchedule(rs.getString("schedule"));
						setOnPort(rs.getBoolean("is_on_port"));
					}};
			}
			
		} catch (Exception e) {
			log.error("Error executing ScheduleTemplateDAO.getByRank(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return template;
	}
	
	public ScheduleTemplate getByCrew(Crew crew) {
		final ResultSet rs = this.db.executeQuery("SELECT s.* FROM schedule_templates s " +
				"INNER JOIN crew_schedule_template cs " +
				"ON cs.schedule_id = s.id WHERE cs.crew_id = " + crew.getId());
		
		ScheduleTemplate template = null;
		
		try {
			
			if(rs.next()) {
				template = new ScheduleTemplate() {{
						setId(rs.getInt("id"));
						parseSchedule(rs.getString("schedule"));
						setOnPort(rs.getBoolean("is_on_port"));
					}};
			}
			
		} catch (Exception e) {
			log.error("Error executing ScheduleTemplateDAO.getByCrew(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return template;
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