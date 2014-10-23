package net.waqassiddiqi.app.crew.util;

import net.waqassiddiqi.app.crew.model.ApplicationSetting.ApplicationMode;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class ApplicationUtil {
	
	public static ApplicationMode getApplicationMode() {
		try {
			
			String mode = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, 
					"Software\\SHIP IP\\Rest Hours Validator Client\\Settings", "Mode");
			
			if(mode != null && !mode.isEmpty() && mode.equals("standalone")) {
				return ApplicationMode.Standalone;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ApplicationMode.Client;
	}
}