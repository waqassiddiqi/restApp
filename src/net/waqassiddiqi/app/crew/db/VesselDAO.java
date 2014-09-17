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
	
	public boolean addVessel(Vessel vessel) {
		
		int rowCount = db.executeUpdate("INSERT INTO vessels(name, imo, flag) VALUES(?, ?, ?);", 
				new String[] { vessel.getName(), vessel.getImo(), vessel.getFlag() });
		
		if(log.isDebugEnabled()) {
			log.debug("Rows affected: " + rowCount);
		}
		
		if(rowCount > 0) 
			return true;
		
		return false;
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