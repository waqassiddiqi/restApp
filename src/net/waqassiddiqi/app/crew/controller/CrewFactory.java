package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.AddCrewForm;
import net.waqassiddiqi.app.crew.ui.ListCrewForm;

public class CrewFactory extends BaseFactory {

	private static CrewFactory instance = null;
	
	private CrewFactory() { }
	
	public static CrewFactory getInstance() {
		if(instance == null) {
			instance = new CrewFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		Component c = new ListCrewForm(getOwner()).getView();
		c.setName("listCrew");
		
		return c;
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
		Component c = new AddCrewForm(getOwner()).getView();
		c.setName("addCrew");
		
		return c;
		
	}
}
