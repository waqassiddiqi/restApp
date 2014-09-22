package net.waqassiddiqi.app.crew.model;

import java.util.Date;

public class Crew {
	private int id;
	private int vesselId;
	private String firstName;
	private String lastName;
	private String rank;
	private String nationality;
	private String passportNumber;
	private Date signOnDate;
	private boolean isWatchKeeper;
	private ScheduleTemplate scheduleTemplate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getVesselId() {
		return vesselId;
	}
	public void setVesselId(int vesselId) {
		this.vesselId = vesselId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
	public String getPassportNumber() {
		return passportNumber;
	}
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}
	
	public Date getSignOnDate() {
		return signOnDate;
	}
	public void setSignOnDate(Date signOnDate) {
		this.signOnDate = signOnDate;
	}
	
	public boolean isWatchKeeper() {
		return isWatchKeeper;
	}
	public void setWatchKeeper(boolean isWatchKeeper) {
		this.isWatchKeeper = isWatchKeeper;
	}
	
	public ScheduleTemplate getScheduleTemplate() {
		return scheduleTemplate;
	}
	public void setScheduleTemplate(ScheduleTemplate scheduleTemplate) {
		this.scheduleTemplate = scheduleTemplate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + (isWatchKeeper ? 1231 : 1237);
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((nationality == null) ? 0 : nationality.hashCode());
		result = prime * result
				+ ((passportNumber == null) ? 0 : passportNumber.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result
				+ ((signOnDate == null) ? 0 : signOnDate.hashCode());
		result = prime * result + vesselId;
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
		Crew other = (Crew) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (isWatchKeeper != other.isWatchKeeper)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (nationality == null) {
			if (other.nationality != null)
				return false;
		} else if (!nationality.equals(other.nationality))
			return false;
		if (passportNumber == null) {
			if (other.passportNumber != null)
				return false;
		} else if (!passportNumber.equals(other.passportNumber))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (signOnDate == null) {
			if (other.signOnDate != null)
				return false;
		} else if (!signOnDate.equals(other.signOnDate))
			return false;
		if (vesselId != other.vesselId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return lastName + ", " + firstName;
	}
}