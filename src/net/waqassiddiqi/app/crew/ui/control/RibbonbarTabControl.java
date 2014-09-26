package net.waqassiddiqi.app.crew.ui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JTabbedPane;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.controller.ReportingFactory;
import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;

import org.apache.log4j.Logger;

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class RibbonbarTabControl {
	
	private IconsHelper iconsHelper;
	private MainFrame owner;
	private Logger log = Logger.getLogger(getClass().getName());
	private int margin = 0;
	private final WebTabbedPane ribbonBarPan;
	private List<WebButton> buttonList = new ArrayList<WebButton>();
	
	public RibbonbarTabControl(MainFrame owner, int margin) {
		
		log.info("Initializing RibbonbarTabControl");
		
		this.margin = margin;
		this.iconsHelper = new IconsHelper();
		this.owner = owner;
		
		ribbonBarPan = new WebTabbedPane();
	}
	
	@SuppressWarnings("serial")
	public Component getComponent() {
		
		
		ribbonBarPan.setBackground(new Color(221, 211, 238));
		
		setupHomeTab(ribbonBarPan);
		setupCrewTab(ribbonBarPan);
		setupReportingTab(ribbonBarPan);
		setupSettingsTab(ribbonBarPan);
		
		ribbonBarPan.setSelectedIndex(1);
		
		ribbonBarPan.setFocusable(false);
		
		return new GroupPanel(
				GroupingType.fillAll, 
				ribbonBarPan) { { setBackground(new Color(252, 248, 252)); setOpaque(true); setFocusable(false); } }.setMargin(this.margin);
	}
	
	public void setupSettingsTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Settings  ", new GroupPanel(4,
				createApplicationSettingsPanel()));
	}
	
	private WebPanel createApplicationSettingsPanel() {

		WebButton btnRestHourReport = new WebButton("Vessel Details",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/vessel_view_32x32.png"));

		btnRestHourReport.setRolloverDecoratedOnly(true);
		btnRestHourReport.setDrawFocus(false);
		btnRestHourReport.setHorizontalTextPosition(AbstractButton.CENTER);
		btnRestHourReport.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnRestHourReport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
			}
		});

		buttonList.add(btnRestHourReport);
		
		GroupPanel gpanel = new GroupPanel(4, new WebLabel(" "), btnRestHourReport);

		WebPanel panel = getRibbonPanel("Application Settings");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	public void setupReportingTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Reports  ", new GroupPanel(4,
				createRestingHourReportingPanel()));
	}
	
	private WebPanel createRestingHourReportingPanel() {

		WebButton btnRestHourReport = new WebButton("Rest Hour Report",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/view_report_32x32.png"));

		btnRestHourReport.setRolloverDecoratedOnly(true);
		btnRestHourReport.setDrawFocus(false);
		btnRestHourReport.setHorizontalTextPosition(AbstractButton.CENTER);
		btnRestHourReport.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnRestHourReport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(ReportingFactory.getInstance().getById("rest"));				
			}
		});

		buttonList.add(btnRestHourReport);
		
		WebButton btnErrorReport = new WebButton("Error Report",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/view_report_32x32.png"));

		btnErrorReport.setRolloverDecoratedOnly(true);
		btnErrorReport.setDrawFocus(false);
		btnErrorReport.setHorizontalTextPosition(AbstractButton.CENTER);
		btnErrorReport.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnErrorReport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(ReportingFactory.getInstance().getById("error"));				
			}
		});

		buttonList.add(btnErrorReport);
		
		GroupPanel gpanel = new GroupPanel(4, btnRestHourReport, btnErrorReport);

		WebPanel panel = getRibbonPanel("Manage Crew");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	public void setupCrewTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Crew  ", new GroupPanel(4,
				createManageCrewPanel(), getVerticalSeparator(), createManageRanksPanel(),
				getVerticalSeparator(), createCrewReportingPanel()));
	}
	
	private WebPanel createManageCrewPanel() {

		WebButton btnAddCrew = new WebButton("Add Crew",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/crew_add_32x32.png"));
		
		btnAddCrew.setRolloverDecoratedOnly(true);
		btnAddCrew.setDrawFocus(false);
		btnAddCrew.setHorizontalTextPosition(AbstractButton.CENTER);
		btnAddCrew.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnAddCrew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(CrewFactory.getInstance().getAdd());				
			}
		});

		WebButton btnListCrew = new WebButton("Edit Crew",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/crew_edit_32x32.png"));

		btnListCrew.setRolloverDecoratedOnly(true);
		btnListCrew.setDrawFocus(false);
		btnListCrew.setHorizontalTextPosition(AbstractButton.CENTER);
		btnListCrew.setVerticalTextPosition(AbstractButton.BOTTOM);
		
		btnListCrew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(CrewFactory.getInstance().get());
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

		buttonList.add(btnAddCrew);
		buttonList.add(btnListCrew);
		buttonList.add(btnDeleteCrew);
		
		GroupPanel gpanel = new GroupPanel(4, btnAddCrew, btnListCrew, btnDeleteCrew);

		WebPanel panel = getRibbonPanel("Manage Crew");
		panel.add(gpanel, BorderLayout.CENTER);

		
		return panel;
	}
	
	private WebPanel createManageRanksPanel() {

		WebButton btnRank = new WebButton("Add Rank",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/rank_32x32.png"));

		btnRank.setRolloverDecoratedOnly(true);
		btnRank.setDrawFocus(false);
		btnRank.setHorizontalTextPosition(AbstractButton.CENTER);
		btnRank.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnRank.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(RankFactory.getInstance().getAdd());
			}
		});

		WebButton btnListRanks = new WebButton("List Ranks",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/list_32x32.png"));

		btnListRanks.setRolloverDecoratedOnly(true);
		btnListRanks.setDrawFocus(false);
		btnListRanks.setHorizontalTextPosition(AbstractButton.CENTER);
		btnListRanks.setVerticalTextPosition(AbstractButton.BOTTOM);
		
		btnListRanks.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(RankFactory.getInstance().get());
			}
		});

		buttonList.add(btnRank);
		buttonList.add(btnListRanks);
		
		GroupPanel gpanel = new GroupPanel(4, btnRank, btnListRanks);

		WebPanel panel = getRibbonPanel("Manage Ranks");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	private WebPanel createCrewReportingPanel() {

		WebButton btnAddRestHour = new WebButton("Enter Hours",
				this.iconsHelper.loadIcon(getClass(), "ribbonbar/add_rest_hours_32x32.png"));

		btnAddRestHour.setRolloverDecoratedOnly(true);
		btnAddRestHour.setDrawFocus(false);
		btnAddRestHour.setHorizontalTextPosition(AbstractButton.CENTER);
		btnAddRestHour.setVerticalTextPosition(AbstractButton.BOTTOM);

		btnAddRestHour.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.addContent(ReportingFactory.getInstance().getAdd());
			}
		});

		buttonList.add(btnAddRestHour);
		
		GroupPanel gpanel = new GroupPanel(4, btnAddRestHour);

		WebPanel panel = getRibbonPanel("Reporting");
		panel.add(gpanel, BorderLayout.CENTER);

		return panel;
	}
	
	public void setupHomeTab(final JTabbedPane tabbedPane) {
		tabbedPane.addTab("  Home  ", new GroupPanel(4, new WebLabel()));
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
	
	public void setEnabled(boolean enable) {
		for(WebButton btn : buttonList) {
			btn.setEnabled(enable);
		}
	}
}