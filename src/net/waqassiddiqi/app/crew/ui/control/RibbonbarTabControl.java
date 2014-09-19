package net.waqassiddiqi.app.crew.ui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.ui.AddCrewFrame;
import net.waqassiddiqi.app.crew.ui.ListVesselFrame;
import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.ui.ServerSettingsDialog;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;
import net.waqassiddiqi.app.crew.util.PrefsUtil;

import org.apache.log4j.Logger;

import com.alee.extended.button.WebSplitButton;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.menu.WebRadioButtonMenuItem;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class RibbonbarTabControl {
	
	private WebPanel serverSettingsPanel;
	private IconsHelper iconsHelper;
	private MainFrame owner;
	private Logger log = Logger.getLogger(getClass().getName());
	private int margin = 0;
	
	private ListVesselFrame listVesselFrame = null;
	private AddCrewFrame addCrewFrame = null;
	
	public RibbonbarTabControl(MainFrame owner, int margin) {
		this.margin = margin;
		this.iconsHelper = new IconsHelper();
		this.owner = owner;
	}
	
	@SuppressWarnings("serial")
	public Component getComponent() {
		final WebTabbedPane ribbonBarPan = new WebTabbedPane ();
		
		ribbonBarPan.setBackground(new Color(221, 211, 238));
		
		//setupVesselsTab(ribbonBarPan);
		setupCrewTab(ribbonBarPan);
		setupSettingsTab(ribbonBarPan);
		
		ribbonBarPan.setFocusable(false);
		
		return new GroupPanel(
				GroupingType.fillAll, 
				ribbonBarPan) { { setBackground(new Color(252, 248, 252)); setOpaque(true); setFocusable(false); } }.setMargin(this.margin);
	}
	
	public void setupCrewTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Crew  ", new GroupPanel(4,
				manageCrewPanel()));
	}
	
	private WebPanel getRibbonPanel(String panelTitle) {
		final WebPanel panel = new WebPanel();
		panel.setUndecorated(false);
		panel.setLayout(new BorderLayout());
		panel.setWebColoredBackground(false);
		panel.setBackground(new Color(235, 211, 238));
		
		final WebPanel southPanel = new WebPanel();
		southPanel.setPaintSides(true, false, false, false);
		setupPanel(southPanel, panelTitle);
		panel.add(southPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	private WebPanel manageCrewPanel() {
		

		WebButton btnAddVessel = new WebButton("Add Crew",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/crew_add_32x32.png"));

		btnAddVessel.setRolloverDecoratedOnly(true);
		btnAddVessel.setDrawFocus(false);
		btnAddVessel.setHorizontalTextPosition(AbstractButton.CENTER);
		btnAddVessel.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnAddVessel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(addCrewFrame == null) {
					addCrewFrame = new AddCrewFrame(owner, "Add new crew member", false, true, false, true);
				}
				
				openDesktopFrame(addCrewFrame, false);
			}
		});

		WebButton btnListVessel = new WebButton("Edit Crew",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/crew_edit_32x32.png"));

		btnListVessel.setRolloverDecoratedOnly(true);
		btnListVessel.setDrawFocus(false);
		btnListVessel.setHorizontalTextPosition(AbstractButton.CENTER);
		btnListVessel.setVerticalTextPosition(AbstractButton.BOTTOM);
		
		btnListVessel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.openDesktopChild((WebInternalFrame) CrewFactory.getInstance().get(), true);
			}
		});
		
		WebButton btnDeleteCrew = new WebButton("Delete Crew",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/crew_delete_32x32.png"));

		btnDeleteCrew.setRolloverDecoratedOnly(true);
		btnDeleteCrew.setDrawFocus(false);
		btnDeleteCrew.setHorizontalTextPosition(AbstractButton.CENTER);
		btnDeleteCrew.setVerticalTextPosition(AbstractButton.BOTTOM);
		
		btnDeleteCrew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		GroupPanel gpanel = new GroupPanel(4, btnAddVessel, btnListVessel, btnDeleteCrew);

		WebPanel panel = getRibbonPanel("Manage Crew");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	public void setupVesselsTab(final JTabbedPane tabbedPane) {
		
		VesselFactory.getInstance().setOwner(owner);
		
		tabbedPane.addTab("  Vessel  ", new GroupPanel(4,
				createVesselsPanel()));
	}
	
	private WebPanel createVesselsPanel() {
		WebButton btnAddVessel = new WebButton("Add Vessel",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/add_32x32.png"));

		btnAddVessel.setRolloverDecoratedOnly(true);
		btnAddVessel.setDrawFocus(false);
		btnAddVessel.setHorizontalTextPosition(AbstractButton.CENTER);
		btnAddVessel.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnAddVessel.setBackground(new Color(235, 211, 238));
		
		btnAddVessel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				owner.openDesktopChild((WebInternalFrame) VesselFactory.getInstance().getAdd(), false);
			}
		});
		
		final WebSeparator separator = new WebSeparator(WebSeparator.VERTICAL);
		separator.setDrawSideLines(true);
		separator.setDrawTrailingLine(true);

		WebButton btnListVessel = new WebButton("List Vessels",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/vessel_view_32x32.png"));

		btnListVessel.setRolloverDecoratedOnly(true);
		btnListVessel.setDrawFocus(false);
		btnListVessel.setHorizontalTextPosition(AbstractButton.CENTER);
		btnListVessel.setVerticalTextPosition(AbstractButton.BOTTOM);
		
		btnListVessel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(listVesselFrame == null) {
					listVesselFrame = new ListVesselFrame(owner, "List all vessel", 
							true, true, true, true);
				}
				
				openDesktopFrame(listVesselFrame, true);
			}
		});

		GroupPanel gpanel = new GroupPanel(4, btnAddVessel, getVerticalSeparator(), btnListVessel);

		WebPanel panel = getRibbonPanel("Manage Vessel");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	public void setupSettingsTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Settings  ", new GroupPanel(4,
				createGeneralSettingsPanel(), getVerticalSeparator(), createServerSettingsPanel()));
	}
	
	private WebPanel createGeneralSettingsPanel() {
		final WebPanel panel = new WebPanel();
		panel.setUndecorated(true);
		panel.setLayout(new BorderLayout());
		panel.setWebColoredBackground(false);
		panel.setOpaque(false);

		final WebPanel southPanel = new WebPanel();
		southPanel.setPaintSides(true, true, true, true);
		setupPanel(southPanel, "General Settings");
		panel.add(southPanel, BorderLayout.SOUTH);

		WebSplitButton btnApplicationMode = new WebSplitButton("Application Mode", 
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/app_settings_32x32.png"));

		btnApplicationMode.setRolloverDecoratedOnly(true);
		btnApplicationMode.setDrawFocus(false);
		btnApplicationMode.setHorizontalTextPosition(AbstractButton.CENTER);
		btnApplicationMode.setVerticalTextPosition(AbstractButton.BOTTOM);

		final WebPopupMenu popupMenu = new WebPopupMenu ();
		
		
		WebRadioButtonMenuItem serverModeItem = new WebRadioButtonMenuItem ("Server Mode");
		WebRadioButtonMenuItem clientModeItem = new WebRadioButtonMenuItem ("Client Mode");
		
		
		if(PrefsUtil.getBoolean(PrefsUtil.PREF_APP_SERVER_MODE, false)) {
			serverModeItem.setSelected(true);
		} else {
			clientModeItem.setSelected(true);
		}
		
		serverModeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrefsUtil.setBoolean(PrefsUtil.PREF_APP_SERVER_MODE, true);
			}
		});
		
		clientModeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrefsUtil.setBoolean(PrefsUtil.PREF_APP_SERVER_MODE, false);
			}
		});
		
		final ButtonGroup modeButtonGroup = new ButtonGroup ();
		modeButtonGroup.add(serverModeItem);
		modeButtonGroup.add(clientModeItem);
		
		popupMenu.add(serverModeItem);
		popupMenu.add(clientModeItem);
		btnApplicationMode.setPopupMenu(popupMenu);

		GroupPanel gpanel = new GroupPanel(4, btnApplicationMode);

		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	private WebPanel createServerSettingsPanel() {
		serverSettingsPanel = new WebPanel();
		serverSettingsPanel.setUndecorated(true);
		serverSettingsPanel.setLayout(new BorderLayout());
		serverSettingsPanel.setWebColoredBackground(true);

		serverSettingsPanel.setOpaque(false);
		
		final WebPanel southPanel = new WebPanel();
		southPanel.setPaintSides(true, true, true, true);
		setupPanel(southPanel, "Server Settings");
		serverSettingsPanel.add(southPanel, BorderLayout.SOUTH);

		WebButton btnServerSettings = new WebButton("Server Settings", 
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/server_settings_32x32.png"));

		btnServerSettings.setRolloverDecoratedOnly(true);
		btnServerSettings.setDrawFocus(false);
		btnServerSettings.setHorizontalTextPosition(AbstractButton.CENTER);
		btnServerSettings.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnServerSettings.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerSettingsDialog serverSettingsFrame = new ServerSettingsDialog( (Window) owner);
				
				serverSettingsFrame.pack();
				serverSettingsFrame.setLocationRelativeTo(owner);
				serverSettingsFrame.setVisible(true);
			}
		});

		GroupPanel gpanel = new GroupPanel(4, btnServerSettings);

		serverSettingsPanel.add(gpanel, BorderLayout.CENTER);

		return serverSettingsPanel;
	}
	
	private void setupPanel(final WebPanel panel, String text) {
		panel.setUndecorated(false);
		panel.setMargin(new Insets(3, 3, 3, 3));
		panel.setRound(StyleConstants.borderWidth);

		panel.add(new WebLabel(text, WebLabel.CENTER));
	}
	
	private WebSeparator getVerticalSeparator() {
		final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        
        return separator;
	}
	
	private void openDesktopFrame(WebInternalFrame childFrame, boolean openMaximum) {
		
		WebLookAndFeel.setDecorateFrames ( false );
		
		childFrame.pack();
		
		if(childFrame.isVisible() == false) {
			owner.addChildDesktopContent(childFrame);
			childFrame.setVisible(true);
		} else {
			owner.activateChildDesktopFrame(childFrame);
		}
		
		
		if(openMaximum) {
			try {
				childFrame.setMaximum(true);
			} catch (PropertyVetoException e1) {
				log.warn(e1.getMessage(), e1);
			}
		}
		
		WebLookAndFeel.setDecorateFrames(false);
	}
}