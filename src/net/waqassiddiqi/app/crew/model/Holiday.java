package net.waqassiddiqi.app.crew.model;

import java.util.Date;

public class Holiday {
	private int id;
	private int holidayListId;
	private String description;
	private Date holidayDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getHolidayListId() {
		return holidayListId;
	}
	public void setHolidayListId(int holidayListId) {
		this.holidayListId = holidayListId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((holidayDate == null) ? 0 : holidayDate.hashCode());
		result = prime * result + holidayListId;
		result = prime * result + id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holiday other = (Holiday) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (holidayDate == null) {
			if (other.holidayDate != null)
				return false;
		} else if (!holidayDate.equals(other.holidayDate))
			return false;
		if (holidayListId != other.holidayListId)
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
