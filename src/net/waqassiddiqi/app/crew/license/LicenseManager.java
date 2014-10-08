package net.waqassiddiqi.app.crew.license;

import java.util.Calendar;
import java.util.Date;

import net.waqassiddiqi.app.crew.db.RegistrationSettingDAO;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;

import com.alee.utils.TimeUtils;

public class LicenseManager {
	
	public static RegistrationSetting getRegistationDetail() {
		
		RegistrationSettingDAO regDao = new RegistrationSettingDAO();
		
		RegistrationSetting settings = regDao.get();
		if(settings == null) {
			
			settings = new RegistrationSetting();
			
			Date today = TimeUtils.getStartOfDay(new Date());
			
			Calendar expiryCal = Calendar.getInstance();
			expiryCal.setTime(today);
			expiryCal.add(Calendar.DAY_OF_YEAR, 30);
			
			settings.setExpiry(expiryCal.getTime());
			settings.setRegistered(false);
			settings.setProductKey("");
			settings.setUsed(0d);
			settings.setUsername("");
			
			regDao.addRegistrationSetting(settings);
		}
		
		return settings;
	}
}