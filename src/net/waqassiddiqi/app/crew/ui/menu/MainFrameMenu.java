package net.waqassiddiqi.app.crew.ui.menu;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import com.alee.extended.menu.DynamicMenuType;
import com.alee.extended.menu.WebDynamicMenu;
import com.alee.extended.menu.WebDynamicMenuItem;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;

public class MainFrameMenu extends BaseMenu {

	ActionListener listener;

	public MainFrameMenu(ActionListener listener) {
		this.listener = listener;
	}
	
	
	@SuppressWarnings("serial")
	@Override
	public void setUpMenuBar(WebMenuBar menuBar) {

		menuBar.add(new WebMenu("File") {
			{

				add(new WebMenuItem("Close", loadIcon("menubar/close.png")) {
					{
						// setEnabled(false);
					}
				});
				addSeparator();
				add(new WebMenuItem("Exit", loadIcon("menubar/file_exit.png")) {
					{
						setAccelerator(Hotkey.ALT_F4);
						setActionCommand("exit");
						addActionListener(listener);
					}
				});
			}
		});
		menuBar.add(new WebMenu("Settings") {
			{
				add(new WebMenuItem("Vessel Details") {
					{
						setActionCommand("vesselDetails");
						addActionListener(listener);
					}
				});
				
				add ( new WebMenu ( "Crew Members")
                {
                    {

                        
                        add ( new WebMenuItem ( "Add Crew", loadIcon ("menubar/crew_add_small.png")) {
        					{
        						setActionCommand("addCrew");
        						addActionListener(listener);
        					}
        				});
                        add ( new WebMenuItem ( "View Crew", loadIcon ("menubar/edit.png" )) );
                    }
                } );
			}
		});

		menuBar.add(new WebMenu("Reports") {
			{
				TooltipManager.setTooltip(this,
						"View, import or export reports");
				add(new WebMenuItem("Resting hour report",
						loadIcon("menubar/reports.png")) {
					{
						TooltipManager.setTooltip(this,
								"View, print or export resting hour report",
								TooltipWay.trailing);
					}
				});
				add(new WebMenuItem("Error Log Report",
						loadIcon("menubar/reports.png")) {
					{
						TooltipManager.setTooltip(this,
								"View, print or export error log report",
								TooltipWay.trailing);
					}
				});
			}
		});

		menuBar.add(new WebMenu("Help") {
			{

				add(new WebMenuItem("Register Software",
						loadIcon("menubar/register.png")) {
					{

					}
				});

				addSeparator();
				add(new WebMenuItem("About", loadIcon("menubar/about.png")) {
					{

					}
				});
			}
		});
	}
	
	public WebDynamicMenu getDynamicQuickMenu() {
		final WebDynamicMenu menu = new WebDynamicMenu();
		menu.setType(DynamicMenuType.shutter);
		menu.setHideType(DynamicMenuType.shutter);
		menu.setRadius(70);
		menu.setStepProgress(0.06f);
		
			ImageIcon icon = loadIcon("menubar/crew_add.png");
			WebDynamicMenuItem item = new WebDynamicMenuItem(icon);
			item.setMargin(new Insets(8, 8, 8, 8));
			item.setDrawBorder(true);
			menu.addItem(item);
			
			icon = loadIcon("menubar/report.png");
			item = new WebDynamicMenuItem(icon);
			item.setMargin(new Insets(8, 8, 8, 8));
			item.setDrawBorder(true);
			menu.addItem(item);
		
			icon = loadIcon("menubar/add_rest_hours.png");
			item = new WebDynamicMenuItem(icon);
			item.setMargin(new Insets(8, 8, 8, 8));
			item.setDrawBorder(true);
			menu.addItem(item);

		return menu;
	}
}
