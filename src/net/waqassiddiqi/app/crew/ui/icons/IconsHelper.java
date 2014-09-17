package net.waqassiddiqi.app.crew.ui.icons;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class IconsHelper {

	private static final Map<String, ImageIcon> iconsCache = new HashMap<String, ImageIcon>();
	
	public ImageIcon loadIcon(final String path) {
		return loadIcon(getClass(), path);
	}

	@SuppressWarnings("rawtypes")
	public ImageIcon loadIcon(final Class nearClass, final String path) {
		final String key = nearClass.getCanonicalName() + ":" + path;
		if (!iconsCache.containsKey(key)) {
			iconsCache.put(key,
					new ImageIcon(nearClass.getResource(path)));
		}
		return iconsCache.get(key);
	}
	
}
