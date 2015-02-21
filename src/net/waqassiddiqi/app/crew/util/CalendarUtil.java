package net.waqassiddiqi.app.crew.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class providng factory and
 * manipulation methods for {@link Calendar}s.
 *
 * @author Willi Schoenborn
 */
public final class CalendarUtil {
    /**
     * Sets a {@link Calendar} to midnight (00:00:00) at 
     * the date currently selected.
     * 
     * @param calendar the {@link Calendar} which will be set to midnight
     * @throws NullPointerException if calendar is null
     */
    public static void toBeginningOfTheDay(Calendar calendar) {
        if(calendar == null)
        	calendar = Calendar.getInstance();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    
    public static String format(String format, Date date) {
    	
    	if(date == null)
    		date = new Date();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	
    	return sdf.format(date);
    }
    
    public static String getDayOfWeek(Date date) {
    	
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
    }
    
    public static Date getLastDayOfMonth(Date today) {
    	Calendar calendar = Calendar.getInstance();  
        calendar.setTime(today);  

        calendar.add(Calendar.MONTH, 1);  
        calendar.set(Calendar.DAY_OF_MONTH, 1);  
        calendar.add(Calendar.DATE, -1);  

        return calendar.getTime();
    }
    
    public static Date getFirstDayOfMonth(Date today) {
    	Calendar calendar = Calendar.getInstance();  
        calendar.setTime(today);  
  
        calendar.set(Calendar.DAY_OF_MONTH, 1);    

        return calendar.getTime();
    }
}