package net.waqassiddiqi.app.crew.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.waqassiddiqi.app.crew.db.EntryTimeDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

public class ErrorReport {
	private Crew crew;
	private int month;
	private int year;
	private List<EntryTime> lstEntryTimes;
	private EntryTime previousDay = null;
	private List<EntryTime> last7Day = null;
	
	Map<Integer, Double> sectionAMapping;
	Map<Integer, Double> sectionBMapping;
	Map<Integer, Double> sectionCMapping;
	Map<Integer, Double> sevenDayMapping;
	
	public ErrorReport(Crew c, Vessel v, int month, int year) {
		this.crew = c;
		this.month = month;
		this.year = year;
	}
	
	public void generateReport() {
		
		last7Day = new ArrayList<EntryTime>();
		
		this.sectionAMapping = new HashMap<Integer, Double>();
	    this.sectionBMapping = new HashMap<Integer, Double>();
	    this.sectionCMapping = new HashMap<Integer, Double>();
	    this.sevenDayMapping = new HashMap<Integer, Double>();
		
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
			
			Set<EntryTime> entSet = new HashSet<EntryTime>();
			
			for(int i=0; i<lstEntryTimes.size(); i++) {
				entSet.add(lstEntryTimes.get(i));
			}
			
			lstEntryTimes.clear();
			
			
			
			for(EntryTime e : entSet)
				lstEntryTimes.add(e);
			
			Collections.sort(lstEntryTimes, new Comparator<EntryTime>() {

				@Override
				public int compare(EntryTime e1, EntryTime e2) {
					return Integer.valueOf(
							e1.getEntryCalendar().get(Calendar.DAY_OF_MONTH))
							.compareTo(
									e2.getEntryCalendar().get(
											Calendar.DAY_OF_MONTH));
				}
			});
			
			refresh();
		}
		
	}
	
	public void refresh() {
		if(this.lstEntryTimes == null)
			return;
		
		for (int i = 0; i < this.lstEntryTimes.size(); i++) {
			this.sectionAMapping.put(Integer.valueOf(i), Double.valueOf(count24HSectionA(i)));
			this.sectionBMapping.put(Integer.valueOf(i), Double.valueOf(count24HSectionB(i)));
			this.sectionCMapping.put(Integer.valueOf(i), Double.valueOf(count24HSectionC(i)));
			this.sevenDayMapping.put(Integer.valueOf(i), Double.valueOf(count7DayRestHours(i)));
		}
	}
	
	public double get24HourRestHours(int paramInt) {
		paramInt--;
		return Math.min(((Double) this.sectionAMapping.get(Integer
				.valueOf(paramInt))).doubleValue(), Math.min(
				((Double) this.sectionBMapping.get(Integer.valueOf(paramInt)))
						.doubleValue(), ((Double) this.sectionCMapping
						.get(Integer.valueOf(paramInt))).doubleValue()));
	}

	public double get7DayRestHours(int paramInt) {
		paramInt--;
		return ((Double) this.sevenDayMapping.get(Integer.valueOf(paramInt)))
				.doubleValue();
	}

	public double getLast3DayTotalRestHours(int paramInt) {
		paramInt--;
		double d = 0.0D;
		if ((paramInt < 2) && (this.last7Day.size() > 0)) {
			if (paramInt == 0) {
				d = get24HourRestHours(1)
						+ Math.min(
								count24HSectionA(
										(EntryTime) this.last7Day.get(5),
										(EntryTime) this.last7Day.get(6)),
								Math.min(
										count24HSectionB(
												(EntryTime) this.last7Day
														.get(5),
												(EntryTime) this.last7Day
														.get(6)),
										count24HSectionC(
												(EntryTime) this.last7Day
														.get(5),
												(EntryTime) this.last7Day
														.get(6))))
						+ Math.min(
								count24HSectionA(
										(EntryTime) this.last7Day.get(4),
										(EntryTime) this.last7Day.get(5)),
								Math.min(
										count24HSectionB(
												(EntryTime) this.last7Day
														.get(4),
												(EntryTime) this.last7Day
														.get(5)),
										count24HSectionC(
												(EntryTime) this.last7Day
														.get(4),
												(EntryTime) this.last7Day
														.get(5))));
			} else if (paramInt == 1) {
				d = get24HourRestHours(1)
						+ get24HourRestHours(2)
						+ Math.min(
								count24HSectionA(
										(EntryTime) this.last7Day.get(5),
										(EntryTime) this.last7Day.get(6)),
								Math.min(
										count24HSectionB(
												(EntryTime) this.last7Day
														.get(5),
												(EntryTime) this.last7Day
														.get(6)),
										count24HSectionC(
												(EntryTime) this.last7Day
														.get(5),
												(EntryTime) this.last7Day
														.get(6))));
			}
		} else if (paramInt >= 2) {
			d = get24HourRestHours(paramInt - 1) + get24HourRestHours(paramInt)
					+ get24HourRestHours(paramInt + 1);
		}
		return d;
	}

	public boolean contain6HourContinuousRest(int paramInt) {
		paramInt--;
		
		List<Boolean> a = get24HSectionA(paramInt);
		List<Boolean> b = get24HSectionB(paramInt);
		List<Boolean> c = get24HSectionC(paramInt);
		
		int i = 0;
		
		for(boolean bool : a) {
			if (!bool) {
				i++;
				if (i >= 12) {
					return true;
				}
			} else {
				i = 0;
			}
		}
		
		i = 0;
		
		for(boolean bool : b) {
			if (!bool) {
				i++;
				if (i >= 12) {
					return true;
				}
			} else {
				i = 0;
			}
		}
		
		i = 0;
		
		for(boolean bool : c) {
			if (!bool) {
				i++;
				if (i >= 12) {
					return true;
				}
			} else {
				i = 0;
			}
		}
		
		i = 0;
		
		for(boolean bool : this.lstEntryTimes.get(paramInt).getSchedule()) {
			if (!bool) {
				i++;
				if (i >= 12) {
					return true;
				}
			} else {
				i = 0;
			}
		}
		
		return false;
	}

	public boolean getContainMoreThan2RestPeriods(int paramInt) {
		paramInt--;
		
		List<Boolean> a = get24HSectionA(paramInt);
		List<Boolean> b = get24HSectionB(paramInt);
		List<Boolean> c = get24HSectionC(paramInt);
		
		if(getContainMoreThan2RestPeriods(a) == true)
			return true;
		
		if(getContainMoreThan2RestPeriods(b) == true)
			return true;
		
		if(getContainMoreThan2RestPeriods(c) == true)
			return true;
		
		List<Boolean> d = Arrays.asList(lstEntryTimes.get(paramInt).getSchedule());
		
		if(getContainMoreThan2RestPeriods(d) == true)
			return true;
		
		return false;
	}
	
	private boolean getContainMoreThan2RestPeriods(List<Boolean> list) {
		int restPeriods = 0, workPeriods = 0;
		List<Integer> numberOfRestPeriod = new ArrayList<Integer>();
		List<Integer> numberOfWorkdPeriod = new ArrayList<Integer>();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) == false) {
				
				restPeriods++;
				
				if(workPeriods > 0) {
					numberOfWorkdPeriod.add(workPeriods);
					workPeriods = 0;
				}
				
			} else {
				workPeriods++;
				
				if(restPeriods > 0) {
					numberOfRestPeriod.add(restPeriods);
					restPeriods = 0;
				}
			}
		}
		
		if(numberOfRestPeriod.size() <= 3)
			return false;
		
		boolean bAnyRestPeriodGreateThan1Hour = true;
		
		for(Integer restPeriod : numberOfRestPeriod) {
			if(restPeriod == 1)
				return true;
		}
		
		if(bAnyRestPeriodGreateThan1Hour) {
		
			int numberOfHalfHourWork = 0;
			
			for(Integer workPeriod : numberOfWorkdPeriod) {
				if(workPeriod == 1)
					numberOfHalfHourWork++;
			}
			
			if(numberOfHalfHourWork <=2 )
				return false;
			else 
				return true;
			
		} else {
			return true;
		}
	}
	
	
	public int getRestPeriodCounter(int paramInt) {
		paramInt--;
		int i = 0;
		int j = 0;
		
		List<Boolean> a = get24HSectionA(paramInt);
		List<Boolean> b = get24HSectionB(paramInt);
		List<Boolean> c = get24HSectionC(paramInt);
			
		for(boolean bool : a) {
			if (!bool) {
				if (j == 0) {
					i++;
				}
				j = 1;
			} else {
				j = 0;
			}
		}
		
		j = 0;
		
		for(boolean bool : b) {
			if (!bool) {
				if (j == 0) {
					i++;
				}
				j = 1;
			} else {
				j = 0;
			}
		}
		
		j = 0;
		
		for(boolean bool : c) {
			if (!bool) {
				if (j == 0) {
					i++;
				}
				j = 1;
			} else {
				j = 0;
			}
		}
		
		return i;
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

	private List<Boolean> get24HSectionA(int paramInt) {
		if((paramInt == 0) && (this.previousDay != null)) {
			
			return get24HSectionA(this.previousDay, this.lstEntryTimes.get(paramInt));
			
		} else if (paramInt > 0) {
			
			return get24HSectionA(this.lstEntryTimes.get(paramInt - 1), this.lstEntryTimes.get(paramInt));
			
		}
		
		return new ArrayList<Boolean>();
	}
	
	private List<Boolean> get24HSectionA(EntryTime paramEntryTime1, EntryTime paramEntryTime2) {
		
		Boolean[] b1 = paramEntryTime1.getPreviousDaySectionAEntries();
		Boolean[] b2 = paramEntryTime2.getTodaySectionAEntries();
		
		List<Boolean> l = new ArrayList<Boolean>();
		for(int i=0; i<b1.length; i++)
			l.add(b1[i]);
		
		for(int i=0; i<b2.length; i++)
			l.add(b2[i]);
		
		return l;
	}
	
	private List<Boolean> get24HSectionB(int paramInt) {
		if((paramInt == 0) && (this.previousDay != null)) {
			
			return get24HSectionB(this.previousDay, this.lstEntryTimes.get(paramInt));
			
		} else if (paramInt > 0) {
			
			return get24HSectionB(this.lstEntryTimes.get(paramInt - 1), this.lstEntryTimes.get(paramInt));
			
		}
		
		return new ArrayList<Boolean>();
	}
	
	private List<Boolean> get24HSectionB(EntryTime paramEntryTime1, EntryTime paramEntryTime2) {
		
		Boolean[] b1 = paramEntryTime1.getPreviousDaySectionBEntries();
		Boolean[] b2 = paramEntryTime2.getTodaySectionBEntries();
		
		List<Boolean> l = new ArrayList<Boolean>();
		for(int i=0; i<b1.length; i++)
			l.add(b1[i]);
		
		for(int i=0; i<b2.length; i++)
			l.add(b2[i]);
		
		return l;
	}
	
	private List<Boolean> get24HSectionC(int paramInt) {
		if((paramInt == 0) && (this.previousDay != null)) {
			
			return get24HSectionC(this.previousDay, this.lstEntryTimes.get(paramInt));
			
		} else if (paramInt > 0) {
			
			return get24HSectionC(this.lstEntryTimes.get(paramInt - 1), this.lstEntryTimes.get(paramInt));
			
		}
		
		return new ArrayList<Boolean>();
	}
	
	private List<Boolean> get24HSectionC(EntryTime paramEntryTime1, EntryTime paramEntryTime2) {
		
		Boolean[] b1 = paramEntryTime1.getPreviousDaySectionCEntries();
		Boolean[] b2 = paramEntryTime2.getTodaySectionCEntries();
		
		List<Boolean> l = new ArrayList<Boolean>();
		for(int i=0; i<b1.length; i++)
			l.add(b1[i]);
		
		for(int i=0; i<b2.length; i++)
			l.add(b2[i]);
		
		return l;
	}
	
	private double count24HSectionA(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d = count24HSectionA(this.previousDay,
					(EntryTime) this.lstEntryTimes.get(paramInt));
		} else if (paramInt > 0) {
			d = count24HSectionA(
					(EntryTime) this.lstEntryTimes.get(paramInt - 1),
					(EntryTime) this.lstEntryTimes.get(paramInt));
		}
		return d;
	}

	private double count24HSectionA(EntryTime paramEntryTime1,
			EntryTime paramEntryTime2) {
		return paramEntryTime1.getPreviousDaySectionA()
				+ paramEntryTime2.getTodaySectionA();
	}

	private double count24HSectionB(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d = count24HSectionB(this.previousDay,
					(EntryTime) this.lstEntryTimes.get(paramInt));
		} else if (paramInt > 0) {
			d = count24HSectionB(
					(EntryTime) this.lstEntryTimes.get(paramInt - 1),
					(EntryTime) this.lstEntryTimes.get(paramInt));
		}
		return d;
	}

	private double count24HSectionB(EntryTime paramEntryTime1,
			EntryTime paramEntryTime2) {
		return paramEntryTime1.getPreviousDaySectionB()
				+ paramEntryTime2.getTodaySectionB();
	}

	private double count24HSectionC(int paramInt) {
		double d = 0.0D;
		if ((paramInt == 0) && (this.previousDay != null)) {
			d = count24HSectionC(this.previousDay,
					(EntryTime) this.lstEntryTimes.get(paramInt));
		} else if (paramInt > 0) {
			d = count24HSectionC(
					(EntryTime) this.lstEntryTimes.get(paramInt - 1),
					(EntryTime) this.lstEntryTimes.get(paramInt));
		}
		return d;
	}

	private double count24HSectionC(EntryTime paramEntryTime1,
			EntryTime paramEntryTime2) {
		return paramEntryTime1.getPreviousDaySectionC()
				+ paramEntryTime2.getTodaySectionC();
	}
	
	public List<EntryTime> getEntryTimeList() {
		return this.lstEntryTimes;
	}
	
	public String getFormattedMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		
		//return new SimpleDateFormat("MMM").format(cal.getTime());
		
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		
		return monthNames[month];
	}
	
	public int getYear() {
		return this.year;
	}
	
	public int getMonth() {
		return this.month;
	}
}