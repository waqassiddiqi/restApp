package net.waqassiddiqi.app.crew.style.skin;

import java.util.List;

import javax.swing.JComponent;

import com.alee.managers.style.SupportedComponent;
import com.alee.managers.style.data.ComponentStyle;
import com.alee.managers.style.data.SkinInfo;
import com.alee.managers.style.skin.WebLafSkin;
import com.alee.utils.XmlUtils;

public class CustomSkin extends WebLafSkin {

	/**
	 * Theme information. Contains complete information about this theme.
	 */
	protected SkinInfo skinInfo;

	/**
	 * Constructs new custom theme.
	 * 
	 * @param location
	 *            skin info XML location relative to this class
	 */
	public CustomSkin(final String location) {
		super();
		this.skinInfo = XmlUtils.fromXML(this.getClass().getResource(location));
	}

	/**
	 * Constructs new custom theme.
	 * 
	 * @param skinInfo
	 *            theme information
	 */
	public CustomSkin(final SkinInfo skinInfo) {
		super();
		this.skinInfo = skinInfo;
	}

	/**
	 * Returns theme information.
	 * 
	 * @return theme information
	 */
	public SkinInfo getSkinInfo() {
		return skinInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return skinInfo.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return skinInfo.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return skinInfo.getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthor() {
		return skinInfo.getAuthor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getSupportedSystems() {
		return skinInfo.getSupportedSystemsList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSkinClass() {
		return skinInfo.getSkinClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentStyle getComponentStyle(final JComponent component,
			final SupportedComponent type) {
		return skinInfo.getStyle(component, type);
	}
}
