package net.waqassiddiqi.app.crew.db;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import net.waqassiddiqi.app.crew.model.ApplicationSetting;

import org.apache.log4j.Logger;

public class ApplicationSettingDAO {
	private Logger log = Logger.getLogger(getClass().getName());
	protected ConnectionManager db;
	
	public ApplicationSettingDAO() {
		db = ConnectionManager.getInstance();
	}
	
	public int addApplicationSetting(ApplicationSetting settings) {
		
		int generatedId = db.executeInsert("INSERT INTO app_settings(is_server, server_ip, server_port, " +
				"custom_text_rest_report, custom_text_error_report, custom_text_nc_report, custom_text_working_report) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?);", 
				new String[] { settings.isServer() ? "1" : "0", settings.getServerIP(), settings.getServerPort(), 
						settings.getCustomRestReportText(), settings.getCustomErrorReportText(), settings.getCustomNCReportText(), settings.getCustomWorkingReportText() } );
		
		if(log.isDebugEnabled()) {
			log.debug("New application settings added: " + generatedId);
		}
		
		return generatedId;
	}
	
	public boolean updateApplicationSetting(ApplicationSetting settings) {
		int rows = db.executeUpdate("UPDATE app_settings SET is_server=?, server_ip=?, server_port=?, " +
				"custom_text_rest_report=?, custom_text_error_report=?, custom_text_nc_report=?, custom_text_working_report=?", 
				new String[] { settings.isServer() ? "1" : "0", settings.getServerIP(), settings.getServerPort(), 
						settings.getCustomRestReportText(), settings.getCustomErrorReportText(), settings.getCustomNCReportText(), settings.getCustomWorkingReportText() });
		
		if(rows > 0)
			return true;
		
		return false;
	}
	
	public boolean addLogo(File source) {
		
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		PreparedStatement stmt = null;
		
		try {
			
			baos = new ByteArrayOutputStream();
			
			BufferedImage img = ImageIO.read(source);
			ImageIO.write(img, "png", baos);
			
			baos.close();
			
			bais = new ByteArrayInputStream(baos.toByteArray());
			
			stmt = db.getConnection().prepareStatement("UPDATE app_settings SET logo_image = ?");
			stmt.setBinaryStream(1, bais);
            
            if(stmt.executeUpdate() > 0)
            	return true;
			
		} catch (Exception e) {
			log.error("Error executing ApplicationSettingDAO.addLogo(): " + e.getMessage(), e);
		} finally {
			try {
				if (bais != null) bais.close();
			} catch (IOException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
			
			try {
				if (baos != null) baos.close();
			} catch (IOException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
			
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException ex) {
				log.error("failed to close db resources: " + ex.getMessage(), ex);
			}
		}
		
		return false;
	}
	
	public ApplicationSetting get() {
		
		ApplicationSetting settings = null;
		
		final ResultSet rs = this.db.executeQuery("SELECT * FROM app_settings");
		
		try {
			if(rs.next()) {
				
				settings = new ApplicationSetting() {{ 
					
					setId(rs.getInt("id"));
					setServer(rs.getBoolean("is_server"));
					setServerIP(rs.getString("server_ip"));
					setServerPort(rs.getString("server_port"));
					
					setCustomRestReportText(rs.getString("custom_text_rest_report"));
					setCustomErrorReportText(rs.getString("custom_text_error_report"));
					setCustomNCReportText(rs.getString("custom_text_nc_report"));
					setCustomWorkingReportText(rs.getString("custom_text_working_report"));
					
					
					Blob blob = rs.getBlob("logo_image");
					if(blob != null) {
						BufferedImage img = ImageIO.read(blob.getBinaryStream());
						setLogo(img);
					}
					
				}};
			}
		} catch (Exception e) {
			log.error("Error executing ApplicationSettingDAO.get(): " + e.getMessage(), e);
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