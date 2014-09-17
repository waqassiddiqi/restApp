package net.waqassiddiqi.app.crew.ui.menu;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.alee.laf.menu.WebMenuBar;

public abstract class BaseMenu {
	
	private static final Map<String, ImageIcon> iconsCache = new HashMap<String, ImageIcon>();
	
	public ImageIcon loadIcon(final String path) {
		return loadIcon(getClass(), path);
	}

	@SuppressWarnings("rawtypes")
	public ImageIcon loadIcon(final Class nearClass, final String path) {
		final String key = nearClass.getCanonicalName() + ":" + path;
		if (!iconsCache.containsKey(key)) {
			iconsCache.put(key,
					new ImageIcon(nearClass.getResource("icons/" + path)));
		}
		return iconsCache.get(key);
	}
	
	public abstract void setUpMenuBar(WebMenuBar menuBar);
}
