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
}
