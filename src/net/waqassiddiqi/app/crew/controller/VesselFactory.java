package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.security.InvalidParameterException;

import net.waqassiddiqi.app.crew.ui.AddVesselFrame;
import net.waqassiddiqi.app.crew.ui.MainFrame;

public class VesselFactory extends BaseFactory {

	private static VesselFactory instance = null;
	private AddVesselFrame addVesselFrame = null;
	
	private MainFrame owner = null;
	
	private VesselFactory() { }
	
	public static VesselFactory getInstance() {
		if(instance == null) {
			instance = new VesselFactory();
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
		if(addVesselFrame == null) {
			addVesselFrame = new AddVesselFrame(owner, "Add new vessel", false, true, false, true);
		}
		
		return addVesselFrame;
	}
	
}
