package net.waqassiddiqi.app.crew.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DatabaseClient {
	private Logger log = Logger.getLogger(getClass().getName());
	
	public boolean isServerAvailable(String serverIP, String port) {
		Connection connection = null;
		String url = "jdbc:h2:tcp://" + serverIP + ":" + port + "/./resource/restdb;IFEXISTS=TRUE";
		
		try {
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection(url, "shipip", "letmeinasadmin");
			
			return true;
		} catch(Exception e) {
			log.error("Server: " + url + " is not available", e);
		} finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Unable to close db resources", e);
				}
		}
		
		return false;
	}
}
