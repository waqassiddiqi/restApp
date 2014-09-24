package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.AddVesselForm;

public class VesselFactory extends BaseFactory {

	private static VesselFactory instance = null;
	
	private VesselFactory() { }
	
	public static VesselFactory getInstance() {
		if(instance == null) {
			instance = new VesselFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		return null;
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
		return new AddVesselForm(getOwner()).getView();
	}
}
