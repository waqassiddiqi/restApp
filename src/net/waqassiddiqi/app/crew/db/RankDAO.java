package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Rank;

import org.apache.log4j.Logger;

public class RankDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public RankDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addRank(Rank rank) {
		
		int generatedId = db.executeInsert("INSERT INTO ranks(rank) VALUES(?);", new String[] { rank.getRank() });
		
		if(log.isDebugEnabled()) {
			log.debug("New generated rank ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public int updateRank(Rank rank) {
		
		int rowsAffected = db.executeUpdate("UPDATE ranks SET rank = ? WHERE id = ?;", new String[] { 
				rank.getRank(), String.valueOf(rank.getId()) });
		
		if(log.isDebugEnabled()) {
			log.debug("Rank has been updated");
		}
		
		return rowsAffected;
	}
	
	public boolean isExists(Rank rank) {
		final ResultSet rs = this.db.executeQuery("SELECT * FROM ranks WHERE rank = '" + 
				rank.getRank().trim() + "'");
		
		try {
			
			if(rs.next())
				return true;
			
		} catch (Exception e) {
			log.error("Error executing RankDAO.isExists(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public Rank getById(int id) {
		
		final ResultSet rs = this.db.executeQuery("SELECT * FROM ranks where id = " + id);
		Rank rank = null;
		
		try {
			if(rs.next()) {
				rank = new Rank(); 
				rank.setId(rs.getInt("id"));
				rank.setRank(rs.getString("rank"));
			}
		} catch (Exception e) {
			log.error("Error executing RankDAO.getById(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return rank;
	}
	
	public List<Rank> getAll() {
		
		List<Rank> list = new ArrayList<Rank>();
		final ResultSet rs = this.db.executeQuery("SELECT * FROM ranks");
		
		try {
			while (rs.next()) {
				list.add(new Rank() { { 
					setId(rs.getInt("id"));
					setRank(rs.getString("rank"));
				} });
			}
		} catch (Exception e) {
			log.error("Error executing RankDAO.getAll(): " + e.getMessage(), e);
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