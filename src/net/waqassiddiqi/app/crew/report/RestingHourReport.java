package net.waqassiddiqi.app.crew.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.waqassiddiqi.app.crew.db.EntryTimeDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

import com.alee.utils.TimeUtils;

public class RestingHourReport {
	private Crew crew;
	private int month;
	private int year;
	private List<EntryTime> lstEntryTimes;
	private EntryTime previousDay = null;
	private List<EntryTime> last7Day = null;
	private EntryTimeDAO entryTimeDao = null;
	
	public RestingHourReport(Crew c, Vessel v, int month, int year) {
		this.crew = c;
		this.month = month;
		this.year = year;
		entryTimeDao = new EntryTimeDAO();
	}
	
	public void generateReport() {
		
		last7Day = new ArrayList<EntryTime>();
		
		final Calendar calStart = Calendar.getInstance();
		final Calendar calEnd = Calendar.getInstance();
		
		calStart.set(Calendar.YEAR, year);
		calStart.set(Calendar.MONTH, month);
		
		calEnd.set(Calendar.YEAR, year);
		calEnd.set(Calendar.MONTH, month);
		
		calStart.setTime(CalendarUtil.getFirstDayOfMonth(calStart.getTime()));
		calEnd.setTime(CalendarUtil.getLastDayOfMonth(calEnd.getTime()));
		
		CalendarUtil.toBeginningOfTheDay(calStart);
		CalendarUtil.toBeginningOfTheDay(calEnd);
		
		final Calendar previousCal = Calendar.getInstance();
		previousCal.setTime(calStart.getTime());
		previousCal.add(Calendar.DAY_OF_MONTH, -1);
		
		EntryTimeDAO entryTimeDao = new EntryTimeDAO();
		
		lstEntryTimes = entryTimeDao.getByYearMonthAndCrew(calStart.getTime(), calEnd.getTime(), crew);
		
		if(lstEntryTimes != null) {
			
			previousDay = entryTimeDao.getByDateAndCrew(previousCal.getTime(), crew);
			if(previousDay != null) {
				this.last7Day.add(this.previousDay);
				EntryTime time = null;
				
				for (int m = 0; m < 6; m++) {
					previousCal.add(Calendar.DAY_OF_MONTH, -1);
					time = entryTimeDao.getByDateAndCrew(previousCal.getTime(), crew);
					if(time == null) {
						time = new EntryTime() {{ setCrewId(crew.getId()); setEntryDate(previousCal.getTime()); }};
					}
						
					this.last7Day.add(0, time);
				}
			}
			
			if(lstEntryTimes.size() < calStart.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			
				for(int i=0; calStart.getTime().getTime() <= calEnd.getTime().getTime(); i++) {
					EntryTime time = new EntryTime() {{ setCrewId(crew.getId()); setEntryDate(calStart.getTime()); }};
					
					if(lstEntryTimes.contains(time) == false) {
						lstEntryTimes.add(i, time);
					}
					
					calStart.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
			
			
			
			Collections.sort(lstEntryTimes, new Comparator<EntryTime>() {

				@Override
				public int compare(EntryTime e1, EntryTime e2) {
					return Integer.valueOf(e1.getEntryCalendar().get(Calendar.DAY_OF_MONTH)).compareTo(
							e2.getEntryCalendar().get(Calendar.DAY_OF_MONTH));
				}				
			});
		}
		
	}
	
	public double get24HourRestHours(int paramInt) {
		paramInt--;
		
		return Math.min(count24HSectionA(paramInt), Math.min(
				count24HSectionB(paramInt), count24HSectionC(paramInt)));
	}

	public double get7DayRestHours(int paramInt) {
		paramInt--;
		return count7DayRestHours(paramInt);
	}

	private double count7DayRestHours(int paramInt) {
		double d = 0.0D;
		int i;
		if ((paramInt < 6) && this.last7Day.size() > 0) {
			for (i = paramInt + 1; i < this.last7Day.size(); i++) {
				d += ((EntryTime) this.last7Day.get(i)).getTotalRestHours();
			}
			for (i = 0; i < paramInt + 1; i++) {
				d += ((EntryTime) this.lstEntryTimes.get(i))
						.getTotalRestHours();
			}
		} else if (paramInt >= 6) {
			for (i = paramInt - 6; i < paramInt + 1; i++) {
				d += ((EntryTime) this.lstEntryTimes.get(i))
						.getTotalRestHours();
			}
		}
		return d;
	}

	private double count24HSectionA(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d += this.previousDay.getPreviousDaySectionA();
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionA();
		} else if (paramInt > 0) {
			d += ((EntryTime) this.lstEntryTimes.get(paramInt - 1))
					.getPreviousDaySectionA();
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionA();
		}
		return d;
	}

	private double count24HSectionB(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d += this.previousDay.getPreviousDaySectionB();
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionB();
		} else if (paramInt > 0) {
			d += ((EntryTime) this.lstEntryTimes.get(paramInt - 1))
					.getPreviousDaySectionB();	
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionB();
		}
		return d;
	}

	private double count24HSectionC(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d += this.previousDay.getPreviousDaySectionC();
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionC();
		} else if (paramInt > 0) {
			d += ((EntryTime) this.lstEntryTimes.get(paramInt - 1))
					.getPreviousDaySectionC();
			d += ((EntryTime) this.lstEntryTimes.get(paramInt))
					.getTodaySectionC();
		}
		return d;
	}
	
	public List<EntryTime> getEntryTimeList() {
		return this.lstEntryTimes;
	}
	
	public String getFormattedMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		
		return monthNames[month];
	}
	
	public int getYear() {
		return this.year;
	}
	
	public boolean isFilled(EntryTime entry, int index) {
		
		if(TimeUtils.isSameDay(crew.getSignOnDate(), entry.getEntryDate())) {
			return entry.getSchedule()[index];
		}
		
		if(crew.getSignOnDate().after(entry.getEntryDate())) {
			return false;
		}
		
		if(entryTimeDao.getByDateAndCrew(entry.getEntryDate(), crew) == null)
			return false;
		
		return entry.getSchedule()[index];
	}
	
	public boolean isBeforeSignOnDateOrNoEntry(EntryTime entry) {
		
		if(TimeUtils.isSameDay(crew.getSignOnDate(), entry.getEntryDate())) {
			return false;
		}
		
		if(crew.getSignOnDate().after(entry.getEntryDate())) {
			return true;
		}
		
		if(entryTimeDao.getByDateAndCrew(entry.getEntryDate(), crew) == null)
			return true;
		
		return false;
	}
}