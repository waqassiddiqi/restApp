package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.waqassiddiqi.app.crew.model.Vessel;

import org.apache.log4j.Logger;

public class VesselDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public VesselDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addVessel(Vessel vessel) {
		
		int generatedId = db.executeInsert("INSERT INTO vessels(name, imo, flag) VALUES(?, ?, ?);", 
				new String[] { vessel.getName(), vessel.getImo(), vessel.getFlag() });
		
		if(log.isDebugEnabled()) {
			log.debug("Generated ID: " + generatedId);
		}
		
		return generatedId;
	}
	
	public int updateVessel(Vessel vessel) {
		
		int rowsUpdated = db.executeUpdate("UPDATE vessels SET name = ?, imo = ?, flag = ? where id = ?;", 
				new String[] { vessel.getName(), vessel.getImo(), vessel.getFlag(), Integer.toString(vessel.getId()) });
		
		if(log.isDebugEnabled()) {
			log.debug("Rows updated: " + rowsUpdated);
		}
		
		return rowsUpdated;
	}
	
	public List<Vessel> getAll() {
		
		List<Vessel> list = new ArrayList<Vessel>();
		final ResultSet rs = this.db.executeQuery("SELECT * FROM vessels");
		
		try {
			while (rs.next()) {
				list.add(new Vessel() { { 
					setId(rs.getInt("id"));
					setName(rs.getString("name"));
					setImo(rs.getString("imo"));
					setFlag(rs.getString("flag"));
				} });
			}
		} catch (Exception e) {
			log.error("Error executing VesselDAO.getAll(): " + e.getMessage(), e);
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