package net.waqassiddiqi.app.crew.model;

import java.awt.image.BufferedImage;

import net.waqassiddiqi.app.crew.db.ApplicationSettingDAO;


public class ApplicationSetting {
	private int id;
	private boolean isServer;
	private String serverIP;
	private String serverPort;
	private BufferedImage logo;
	private String customRestReportText;
	private String customErrorReportText;
	private String customNCReportText;
	private String customWorkingReportText;
	
	public enum ApplicationMode { Unknown, Server, Client, Standalone };
	
	public ApplicationSetting() {
		customRestReportText = "";
		customErrorReportText = "";
		customNCReportText = "";
		customWorkingReportText = "";
		serverIP = "";
		serverPort = "";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isServer() {
		return isServer;
	}
	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public BufferedImage getLogo() {
		return logo;
	}
	public void setLogo(BufferedImage logo) {
		this.logo = logo;
	}
	
	public String getCustomRestReportText() {
		return customRestReportText;
	}
	public void setCustomRestReportText(String customRestReportText) {
		this.customRestReportText = customRestReportText;
	}
	
	public String getCustomErrorReportText() {
		return customErrorReportText;
	}
	public void setCustomErrorReportText(String customErrorReportText) {
		this.customErrorReportText = customErrorReportText;
	}
	
	public String getCustomNCReportText() {
		return customNCReportText;
	}
	public void setCustomNCReportText(String customNCReportText) {
		this.customNCReportText = customNCReportText;
	}
	
	public String getCustomWorkingReportText() {
		return customWorkingReportText;
	}
	public void setCustomWorkingReportText(String customWorkingReportText) {
		this.customWorkingReportText = customWorkingReportText;
	}
	
	private static ApplicationSetting appInstance = null;
	
	public static ApplicationSetting getGlobalApplicationSettings() {
		if(appInstance == null)
			appInstance = new ApplicationSettingDAO().get();
		
		return appInstance;
	}
	
	public static ApplicationMode getApplicationMode() {
		
		if(appInstance == null)
			appInstance = new ApplicationSettingDAO().get();
		
		if(appInstance == null)
			return ApplicationMode.Unknown;
		
		if(appInstance.isServer == true) {
			return ApplicationMode.Server;
		} else if(appInstance.getServerIP().trim().isEmpty()) {
			return ApplicationMode.Standalone;
		} else {
			return ApplicationMode.Client;
		}
	}
}