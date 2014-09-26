package net.waqassiddiqi.app.crew.util;

import net.waqassiddiqi.app.crew.db.VesselDAO;

public class ConfigurationUtil {

	public static boolean isVesselConfigured() {
		if(new VesselDAO().getAll().size() == 0)
			return false;
		
		return true;
	}
}
