package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.util.Map;

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
		Component c = new ListRankForm(getOwner()).getView();
		c.setName("listRank");
		return c;
	}

	@Override
	public Component getById(String id) {
		return null;
	}

	@Override
	public Component getEdit(String id) {
		Component c = new AddRankForm(getOwner(), Integer.parseInt(id)).getView();
		c.setName("getRank");
		
		return c;
	}

	@Override
	public Component getAdd() {
		Component c = new AddRankForm(getOwner()).getView();
		c.setName("getRank");
		return c;
	}

	@Override
	public Component getEdit(String id, Map<String, Object> params) {
		AddRankForm f = new AddRankForm(getOwner(), Integer.parseInt(id));
		
		if(params.containsKey("defaultView"))
			f.setDefaultTabIndex((int) params.get("defaultView"));
		
		Component c = f.getView();
		c.setName("getRank");
		
		return c;
	}
}
