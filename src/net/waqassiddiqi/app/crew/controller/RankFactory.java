package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.AddRankForm;
import net.waqassiddiqi.app.crew.ui.ListRankForm;

public class RankFactory extends BaseFactory {

	private static RankFactory instance = null;
	
	private RankFactory() { }
	
	public static RankFactory getInstance() {
		if(instance == null) {
			instance = new RankFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		return new ListRankForm(getOwner()).getView();
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
		return new AddRankForm(getOwner()).getView();
	}
}
