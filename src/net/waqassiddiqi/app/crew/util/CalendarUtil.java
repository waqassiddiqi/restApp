package net.waqassiddiqi.app.crew.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}