package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.security.InvalidParameterException;

import net.waqassiddiqi.app.crew.ui.AddCrewFrame;
import net.waqassiddiqi.app.crew.ui.ListCrewFrame;
import net.waqassiddiqi.app.crew.ui.MainFrame;

public class CrewFactory extends BaseFactory {

	private static CrewFactory instance = null;
	private AddCrewFrame addCrewFrame = null;
	private ListCrewFrame listCrewFrame = null;
	
	private MainFrame owner = null;
	
	private CrewFactory() { }
	
	public static CrewFactory getInstance() {
		if(instance == null) {
			instance = new CrewFactory();
		}
		
		return instance;
	}
	
	public void setOwner(MainFrame owner) {
		if(owner != null)
			this.owner = owner;
		else
			throw new InvalidParameterException("Owner cannot be set more than once");
	}
	
	@Override
	public Component get() {
		if(listCrewFrame == null) {
			listCrewFrame = new ListCrewFrame(owner, "List all crew members", 
					true, true, true, true);
		}
		
		return listCrewFrame;
	}

	@Override
	public Component getById(String id) {
		return null;
	}

	@Override
	public Component getEdit(String id) {
		return null;
	}

	@Override
	public Component getAdd() {
		if(addCrewFrame == null) {
			addCrewFrame = new AddCrewFrame(owner, "Add new crew", false, true, false, true);
		}
		
		return addCrewFrame;
	}
	
}
