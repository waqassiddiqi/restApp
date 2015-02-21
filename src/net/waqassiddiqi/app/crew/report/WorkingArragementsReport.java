package net.waqassiddiqi.app.crew.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.waqassiddiqi.app.crew.db.ReportDAO;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

public class WorkingArragementsReport {
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public List<String[]> generateReport() {
		ReportDAO rptDao = new ReportDAO();	
		
		Map<Integer, String[]> templateSet = new HashMap<Integer, String[]>();
		
		String[] data = null;
		
		ResultSet rs = null;
		
		try {
			EntryTime entry = null;
			
			rs = rptDao.getWorkingArragements();
			while(rs.next()) {
				
				
				if(templateSet.containsKey(rs.getInt("CREW_ID")) == false) {
					
					data = new String[] { "", "", "", "", "", "", "", "" };
					
					data[0] = rs.getString("CREW");
					
					templateSet.put(rs.getInt("CREW_ID"), data);
				} else {
					data = templateSet.get(rs.getInt("CREW_ID"));
				}
				
				entry = new EntryTime();
				entry.parseSchedule(rs.getString("SCHEDULE"));
				
				Calendar cal = Calendar.getInstance();
				CalendarUtil.toBeginningOfTheDay(cal);
				
				
				boolean currentValue = entry.getSchedule()[0];
				
				String startHour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
				String endHour = "";
				String formattedHours = "";
				Map<Boolean, List<String>> timeMap = new HashMap<Boolean, List<String>>();
				
				timeMap.put(true, new ArrayList<String>());
				timeMap.put(false, new ArrayList<String>());
				
				
				for(int i=1; i<entry.getSchedule().length; i++) {
					
					cal.add(Calendar.MINUTE, 30);
					
					//if(startHour.isEmpty()) {
					//	startHour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
					//}
					
					if(currentValue != entry.getSchedule()[i]) {
						endHour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
						
						formattedHours = startHour + " - " + endHour;
						
						timeMap.get(currentValue).add(formattedHours);
						
						startHour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
					}
					
					currentValue = entry.getSchedule()[i];					
				}
				
				endHour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
				formattedHours = startHour + " - " + endHour;
				
				timeMap.get(currentValue).add(formattedHours);
				
				
				List<String> workHours = timeMap.get(true);
				StringBuilder sb = new StringBuilder();
				
				for(String w : workHours) {
					sb.append(w);
					sb.append("<br/>");
				}
				
				if(rs.getBoolean("IS_ON_PORT")) {					
					data[7] = Double.toString(entry.getTotalRestHours());	
					
					if(rs.getBoolean("IS_WATCH_KEEPING")) {
						if(sb.toString().trim().length() > 0)
							data[3] = sb.toString();
					} else {
						if(sb.toString().trim().length() > 0)
							data[4] = sb.toString();
					}
					
				} else {					
					data[6] = Double.toString(entry.getTotalRestHours());
					
					if(rs.getBoolean("IS_WATCH_KEEPING")) {
						if(sb.toString().trim().length() > 0)
							data[1] = sb.toString();
					} else {
						if(sb.toString().trim().length() > 0)
							data[2] = sb.toString();
					}
				}
				
			}
			
		} catch (Exception e) {
			log.error("Error executing ReportDAO.getWorkingArragements(): " + e.getMessage(), e);
		} finally {
			if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					log.warn("Error closing db resource", e);
				}
		}
		
		return new ArrayList<String[]>(templateSet.values());		
	}
}