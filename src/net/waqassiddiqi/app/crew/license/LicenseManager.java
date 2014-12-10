package net.waqassiddiqi.app.crew.license;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.waqassiddiqi.app.crew.db.RegistrationSettingDAO;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;
import net.waqassiddiqi.crewapp.license.KeyValidator;
import net.waqassiddiqi.crewapp.license.KeyValidator.Status;
import net.waqassiddiqi.crewapp.license.util.SystemUtil;

import com.alee.utils.TimeUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class LicenseManager {
	
	public static RegistrationSetting getRegistationDetail() {
		
		RegistrationSettingDAO regDao = new RegistrationSettingDAO();
		
		RegistrationSetting settings = regDao.get();
		if(settings == null) {
			
			settings = new RegistrationSetting();

			Date today = TimeUtils.getStartOfDay(new Date());
			
			try {
				
				String checksum = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, 
						"Software\\SHIP IP\\Rest Hours Validator\\Settings", "Checksum");
				
				if(checksum != null && !checksum.isEmpty()) {
					today = new SimpleDateFormat("ddMMyyyy").parse(checksum);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Calendar expiryCal = Calendar.getInstance();
			expiryCal.setTime(today);
			expiryCal.add(Calendar.DAY_OF_YEAR, 30);
			
			Calendar usageStartedCal = Calendar.getInstance();
			usageStartedCal.setTime(TimeUtils.getStartOfDay(new Date()));
			
			settings.setExpiry(expiryCal.getTime());
			settings.setUsageStartedOn(usageStartedCal.getTime());
			settings.setRegistered(false);
			settings.setProductKey("");
			settings.setUsed(0d);
			settings.setUsername("");
			
			regDao.addRegistrationSetting(settings);
			
		} else {
			Date lastUsage = settings.getUsageStartedOn();
			
			Calendar calUsage = Calendar.getInstance();
			calUsage.setTime(lastUsage);
			
			Calendar calToday = Calendar.getInstance();
			calToday.setTime(TimeUtils.getStartOfDay(new Date()));
			
			if(calUsage.getTimeInMillis() != calToday.getTimeInMillis()) {
				
				if(calToday.getTimeInMillis() < calUsage.getTimeInMillis()) {
					calUsage.add(Calendar.DAY_OF_MONTH, 1);
				} else {
					
					calUsage = calToday;
					
					settings.setUsageStartedOn(calUsage.getTime());
					regDao.updateRegistrationSetting(settings);
				}
			}
			
		}
		
		return settings;
	}
	
	public static boolean registerProduct(String systemId, String serialKey) {
		if(KeyValidator.PKV_CheckKey(serialKey, systemId) == Status.KEY_GOOD) {
			
			int seed = KeyValidator.extractSeed(serialKey);
			
			RegistrationSettingDAO regDao = new RegistrationSettingDAO();
			RegistrationSetting settings = regDao.get();
			
			if(settings.getUsed() >= seed) {
				return false;
			}
			
			settings.setUsed((double) seed);
			
			settings.setRegisteredOn(new Date());
			settings.setRegistered(true);
			
			Date today = TimeUtils.getStartOfDay(new Date());
			
			Calendar expiryCal = Calendar.getInstance();
			expiryCal.setTime(today);
			expiryCal.add(Calendar.YEAR, 1);
			settings.setExpiry(expiryCal.getTime());
			
			settings.setUsageStartedOn(TimeUtils.getStartOfDay(new Date()));
			
			settings.setProductKey(serialKey);
			settings.setSystemId(systemId);
			
			if(regDao.updateRegistrationSetting(settings)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean validateLicense() {
		RegistrationSettingDAO regDao = new RegistrationSettingDAO();
		RegistrationSetting settings = regDao.get();
		
		if(SystemUtil.getRegistrationId().equals(settings.getSystemId())) {
			return true;
		} else {
			return false;
		} 
	}
	
	public static int getDaysToExpire() {
		RegistrationSetting settings = new RegistrationSettingDAO().get();
		
		Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        
        calendar1.setTime(settings.getExpiry());
        calendar2.setTime(settings.getUsageStartedOn());
        
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds1 - milliseconds2;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        return (int) diffDays;
	}
	
	public static boolean isExpired() {
		if(getDaysToExpire() <= 0) {
			return true;
		}
		
		return false;
	}
}