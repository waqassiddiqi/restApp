package net.waqassiddiqi.app.crew.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.util.CalendarUtil;

import org.apache.log4j.Logger;

public class EntryTime {
	private int id;
	private int crewId;
	private Date entryDate;
	private Boolean[] schedule;
	private boolean isOnPort;
	private float workIn24Hours;
	private float restIn24Hours;
	private String comments;
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public EntryTime() {
		schedule = new Boolean[48];
		Arrays.fill(schedule, Boolean.FALSE);
	}

	public Calendar getEntryCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(entryDate);
		
		return cal;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCrewId() {
		return crewId;
	}

	public void setCrewId(int crewId) {
		this.crewId = crewId;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Boolean[] getSchedule() {
		return schedule;
	}

	public void setSchedule(Boolean[] schedule) {
		this.schedule = schedule;
	}

	public boolean isOnPort() {
		return isOnPort;
	}

	public void setOnPort(boolean isOnPort) {
		this.isOnPort = isOnPort;
	}

	public float getWorkIn24Hours() {
		return workIn24Hours;
	}

	public void setWorkIn24Hours(float workIn24Hours) {
		this.workIn24Hours = workIn24Hours;
	}

	public float getRestIn24Hours() {
		return restIn24Hours;
	}

	public void setRestIn24Hours(float restIn24Hours) {
		this.restIn24Hours = restIn24Hours;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void parseSchedule(String scheduleString) {
		for(int i=0; (i<48 && i<scheduleString.length()); i++) {
			
			if(scheduleString.charAt(i) == '1') {
				this.schedule[i] = true;
				
				this.workIn24Hours += 0.5;
				
			} else {
				this.schedule[i] = false;
				
				this.restIn24Hours += 0.5;
			}
		}
	}

	@Override
	public String toString() {
		return "EntryTime [id=" + id + ", crewId=" + crewId + ", entryDate="
				+ entryDate + ", schedule=" + Arrays.toString(schedule)
				+ ", isOnPort=" + isOnPort + ", workIn24Hours=" + workIn24Hours
				+ ", restIn24Hours=" + restIn24Hours + ", comments=" + comments
				+ "]";
	}
	
	public String getFormattedDate() {
		return CalendarUtil.format("yyyy-MM-dd", this.entryDate);
	}
	
	public String getDayOfWeek() {
		return CalendarUtil.getDayOfWeek(this.entryDate);
	}
	
	public double getTotalRestHours() {
		double d = 0.0D;
		if (this.schedule != null) {
			
			for(int i=0; i<schedule.length; i++) {
				if(!schedule[i]) {
					d += 0.5D;
				}
			}					
		}
		return d;
	}

	public double getPreviousDaySectionA() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = this.schedule.length - 1; i >= schedule.length - 22; i--) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}

	public Boolean[] getPreviousDaySectionAEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			//for (int i = this.schedule.length - 1; i >= schedule.length - 22; i--) {
			//	list.add(schedule[i]);
			//}
			
			for(int i=26; i<this.schedule.length; i++) {
				list.add(schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}
	
	public double getTodaySectionA() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = 0; i < this.schedule.length - 22; i++) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}
	
	public Boolean[] getTodaySectionAEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			for (int i = 0; i < 26; i++) {
				list.add(this.schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}

	public double getPreviousDaySectionB() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = this.schedule.length - 1; i >= this.schedule.length - 12; i--) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}
	
	public Boolean[] getPreviousDaySectionBEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			//for (int i = this.schedule.length - 1; i >= this.schedule.length - 12; i--) {
			//	list.add(schedule[i]);
			//}
			
			for(int i=36; i<this.schedule.length; i++) {
				list.add(schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}

	public double getTodaySectionB() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = 0; i < this.schedule.length - 12; i++) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}

	public Boolean[] getTodaySectionBEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			for (int i = 0; i < 36; i++) {
				list.add(this.schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}
	
	public double getPreviousDaySectionC() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = this.schedule.length - 1; i >= schedule.length - 36; i--) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}
	
	public Boolean[] getPreviousDaySectionCEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			//for (int i = this.schedule.length - 1; i >= schedule.length - 36; i--) {
			//	list.add(schedule[i]);
			//}
			
			for (int i = 12; i < this.schedule.length; i++) {
				list.add(this.schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}

	public double getTodaySectionC() {
		double d = 0.0D;
		if (this.schedule != null) {
			for (int i = 0; i < this.schedule.length - 36; i++) {
				if (!this.schedule[i]) {
					d += 0.5D;
				}
			}
		}
		return d;
	}

	public Boolean[] getTodaySectionCEntries() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (this.schedule != null) {
			for (int i = 0; i < 12; i++) {
				list.add(this.schedule[i]);
			}
		}
		
		return list.toArray(new Boolean[list.size()]);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + crewId;
		result = prime * result
				+ ((entryDate == null) ? 0 : entryDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		EntryTime other = (EntryTime) obj;
		
		if (crewId != other.crewId)
			return false;
		
		if (entryDate == null) {
			
			if (other.entryDate != null)
				return false;
			
		} else {
			Calendar calOther = Calendar.getInstance();
			calOther.setTime(other.entryDate);
			
			if(getEntryCalendar().get(Calendar.DAY_OF_MONTH) == calOther.get(Calendar.DAY_OF_MONTH)
					&& getEntryCalendar().get(Calendar.MONTH) == calOther.get(Calendar.MONTH)
					&& getEntryCalendar().get(Calendar.YEAR) == calOther.get(Calendar.YEAR)) {
				return true;
			} else {
				log.info("Difference: " + getEntryCalendar().getTime().toString() + " AND " + calOther.getTime().toString());
			}
		}
			
		return false;
	}
}