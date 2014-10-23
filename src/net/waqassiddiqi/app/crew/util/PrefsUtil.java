package net.waqassiddiqi.app.crew.util;

import java.util.prefs.Preferences;

public class PrefsUtil {
	
	public static final String PREF_SERVER_IP = "pref_server_ip";
	public static final String PREF_SERVER_PORT = "pref_server_port";
	public static final String PREF_VESSEL_ADDED = "pref_vessel_added";
	
	public static final String PREF_FIRST_RUN = "is_first_run";
	public static final String PREF_APPLICATION_MODE = "app_mode";
	
	public static String getString(String key, String defaultValue) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		return prefs.get(key, defaultValue);
	}
	
	public static void setString(String key, String value) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		prefs.put(key, value);
	}
	
	public static int getInt(String key, int defaultValue) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		return prefs.getInt(key, defaultValue);
	}
	
	public static void setInt(String key, int value) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		prefs.putInt(key, value);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		return prefs.getBoolean(key, defaultValue);
	}
	
	public static void setBoolean(String key, boolean value) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		prefs.putBoolean(key, value);
	}
	
	public static long getLong(String key, long defaultValue) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		return prefs.getLong(key, defaultValue);
	}
	
	public static void setLong(String key, long value) {
		Preferences prefs = Preferences.userNodeForPackage(net.waqassiddiqi.app.crew.ui.MainFrame.class);
		prefs.putLong(key, value);
	}
}