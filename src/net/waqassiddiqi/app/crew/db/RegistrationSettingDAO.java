package net.waqassiddiqi.app.crew.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.waqassiddiqi.app.crew.model.RegistrationSetting;

import org.apache.log4j.Logger;

public class RegistrationSettingDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public RegistrationSettingDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addRegistrationSetting(RegistrationSetting settings) {
		
		int generatedId = db.executeInsert("INSERT INTO reg_settings(username, is_registered, expiry, product_key, app_used, registered_on, system_id, usage_started_on) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?);", 
				new String[] { settings.getUsername(), settings.isRegistered() ? "1" : "0", 
						Long.toString(settings.getExpiry().getTime()), settings.getProductKey(), Double.toString(settings.getUsed()),
						(settings.getRegisteredOn() == null) ? Long.toString(0) : Long.toString(settings.getRegisteredOn().getTime()), settings.getSystemId(), 
								Long.toString(settings.getUsageStartedOn().getTime()) });
		
		if(log.isDebugEnabled()) {
			log.debug("New registration details added: " + generatedId);
		}
		
		return generatedId;
	}
	
	public boolean updateRegistrationSetting(RegistrationSetting settings) {
		int rows = db.executeUpdate("UPDATE reg_settings SET username=?, is_registered=?, expiry=?, product_key=?, app_used=?, registered_on=?, system_id=?, usage_started_on=?", 
				new String[] { settings.getUsername(), settings.isRegistered() ? "1" : "0", 
						Long.toString(settings.getExpiry().getTime()), settings.getProductKey(), Double.toString(settings.getUsed()),
						(settings.getRegisteredOn() == null) ? Long.toString(0) : Long.toString(settings.getRegisteredOn().getTime()), 
								settings.getSystemId(), Long.toString(settings.getUsageStartedOn().getTime()) });
		
		if(rows > 0)
			return true;
		
		return false;
	}
	
	public RegistrationSetting get() {
		
		RegistrationSetting settings = null;
		
		final ResultSet rs = this.db.executeQuery("SELECT * FROM reg_settings");
		
		try {
			if(rs.next()) {
				
				settings = new RegistrationSetting() {{ 
					
					setId(rs.getInt("id"));
					setUsername(rs.getString("username"));
					setRegistered(rs.getBoolean("is_registered"));
					setExpiry(new Date(rs.getLong("expiry")));
					setProductKey(rs.getString("product_key"));
					setUsed(rs.getDouble("app_used"));
					setSystemId(rs.getString("system_id"));
					setUsageStartedOn(new Date(rs.getLong("usage_started_on")));
					
					if(isRegistered()) {
						setRegisteredOn(new Date(rs.getLong("registered_on")));
					}
					
				}};
			}
		} catch (Exception e) {
			log.error("Error executing RegistrationSettingDAO.get(): " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return settings;
	}
}