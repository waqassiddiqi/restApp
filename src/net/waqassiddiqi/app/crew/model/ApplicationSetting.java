package net.waqassiddiqi.app.crew.model;

import java.awt.image.BufferedImage;


public class ApplicationSetting {
	private int id;
	private boolean isServer;
	private String serverIP;
	private String serverPort;
	private BufferedImage logo;
	private String customText;
	
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
	public String getCustomText() {
		return customText;
	}
	public void setCustomText(String customText) {
		this.customText = customText;
	}
}
