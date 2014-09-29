package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import net.waqassiddiqi.app.crew.db.RankDAO;
import net.waqassiddiqi.app.crew.db.ScheduleTemplateDAO;
import net.waqassiddiqi.app.crew.model.Rank;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet;
import net.waqassiddiqi.app.crew.util.InputValidator;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;

public class AddRankForm extends BaseForm {
	
	private MainFrame owner;
	private int id = -1;
	private Component thisComponent;
	private WebTextField txtRankName;
	private WebTextField txtRankId;
	
	private TimeSheet timeSheetOnSeaWatchkeeping;
	private TimeSheet timeSheetOnSeaNonWatchkeeping;
	
	private TimeSheet timeSheetOnPortWatchKeeping;
	private TimeSheet timeSheetOnPortNonWatchKeeping;
	
	private WebTabbedPane tabPan;
	private Rank currentRank = null;
	private WebButton btnCancel;
	
	public AddRankForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddRankForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Manage Ranks") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component prepareView() {
        
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  Rank Details  ", getForm());
        
        timeSheetOnSeaWatchkeeping = new TimeSheet(25, false, "Watch keeping Hours: ");
        timeSheetOnSeaNonWatchkeeping = new TimeSheet(25, false, "Non Watchkeeping Hours: ");
        
        timeSheetOnPortWatchKeeping = new TimeSheet(25, false, "Watch keeping Hours: ");
        timeSheetOnPortNonWatchKeeping = new TimeSheet(25, true, "Non Watch keeping Hours: ");
        
        WebScrollPane scrollPane = new WebScrollPane(new GroupPanel(false, 
				new WebLabel("Sea Hours") {{ setDrawShade(true); }}, 
				timeSheetOnSeaWatchkeeping.getView(), timeSheetOnSeaNonWatchkeeping.getView(), WebSeparator.createHorizontal(), 
				new GroupPanel(false, 
						new WebLabel("Port Hours") {{ setDrawShade(true); }},  
						timeSheetOnPortWatchKeeping.getView(), 
						timeSheetOnPortNonWatchKeeping.getView())).setMargin(5));
        
        tabPan.addTab("  Schedule Template ", scrollPane);
        
        tabPan.setContentInsets(new Insets(10, 10, 10, 10));
        
        return tabPan;
	}
	
	private Component getForm() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);

		txtRankId = new WebTextField(15);
		txtRankId.setEnabled(false);
		
		txtRankId.setVisible(false);
		
		txtRankName = new WebTextField(15);
		
		//content.add(new WebLabel("Rank ID", WebLabel.TRAILING), "0,0");		
		//content.add(txtRankId, "1,0");

		content.add(new WebLabel("Rank", WebLabel.TRAILING), "0,1");
		content.add(txtRankName, "1,1");
		
		thisComponent = content;
		
		return content;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			owner.removeContent(thisComponent);
		} else if(btnSource.getClientProperty("command").equals("new")) {
			this.id = -1;
			this.currentRank = null;
			
			txtRankId.setText("");
			txtRankName.setText("");
			
		} else if(btnSource.getClientProperty("command").equals("save")) {
			
			if(txtRankName.getText().trim().isEmpty()) {
				tabPan.setSelectedIndex(0);
			}
			
			Rank rank = new Rank();
			rank.setRank(txtRankName.getText().trim());
			
			RankDAO rankDao = new RankDAO();			
			
			if( (currentRank != null) && currentRank.getRank().equals(rank.getRank()) == false &&
					rankDao.isExists(rank))  {
				
				NotificationManager.showPopup(owner, 
						txtRankName, new String[] { txtRankName.getText().trim() + " already exists" });
				return;
				
			} else if(rankDao.isExists(rank) && currentRank == null) {
				
				NotificationManager.showPopup(owner, 
						txtRankName, new String[] { txtRankName.getText().trim() + " already exists" });
				
				return;
			}
			
			ScheduleTemplateDAO scheduleDao = new ScheduleTemplateDAO();
			
			if(InputValidator.validateInput(owner, new JTextField[] { txtRankName }, "Rank cannot be empty")) {
				
				Boolean[] scheduleArrayOnSeaWatchkeeping = this.timeSheetOnSeaWatchkeeping.getSchedule();
				Boolean[] scheduleArrayOnSeaNonWatchkeeping = this.timeSheetOnSeaNonWatchkeeping.getSchedule();
				Boolean[] scheduleArrayOnPortWatchkeeping = this.timeSheetOnPortWatchKeeping.getSchedule();
				Boolean[] scheduleArrayOnPortNonWatchkeeping = this.timeSheetOnPortNonWatchKeeping.getSchedule();
				
				if(currentRank == null) {
					this.id = rankDao.addRank(rank);
					
					if(this.id > 0) {
						currentRank = new Rank();
						currentRank.setId(id);
						currentRank.setRank(rank.getRank());
						
						txtRankId.setText(Integer.toString(this.id));
						
						
						
						ScheduleTemplate template = new ScheduleTemplate();
						template.setSchedule(scheduleArrayOnSeaWatchkeeping);
						template.setOnPort(false);
						template.setWatchKeeping(true);
						
						int scheduleId = scheduleDao.addScheduleTemplate(template);
						
						template.setId(scheduleId);
						
						if(scheduleId > 0) {
							scheduleDao.associateScheduleTemplate(currentRank, template);
						}
						
						template = new ScheduleTemplate();
						template.setSchedule(scheduleArrayOnSeaNonWatchkeeping);
						template.setOnPort(false);
						template.setWatchKeeping(false);
						
						scheduleId = scheduleDao.addScheduleTemplate(template);
						
						template.setId(scheduleId);
						
						if(scheduleId > 0) {
							scheduleDao.associateScheduleTemplate(currentRank, template);
						}
						
						template = new ScheduleTemplate();
						template.setSchedule(scheduleArrayOnPortWatchkeeping);
						template.setOnPort(true);
						template.setWatchKeeping(true);
						
						scheduleId = scheduleDao.addScheduleTemplate(template);
						
						template.setId(scheduleId);
						
						if(scheduleId > 0) {
							scheduleDao.associateScheduleTemplate(currentRank, template);
						}
						
						template = new ScheduleTemplate();
						template.setSchedule(scheduleArrayOnPortNonWatchkeeping);
						template.setOnPort(true);
						template.setWatchKeeping(false);
						
						scheduleId = scheduleDao.addScheduleTemplate(template);
						
						template.setId(scheduleId);
						
						if(scheduleId > 0) {
							scheduleDao.associateScheduleTemplate(currentRank, template);
						}
						
						//ScheduleTemplate template = new ScheduleTemplate();
						//template.setSchedule(scheduleArray);
						
						//int scheduleId = scheduleDao.addScheduleTemplate(template);
						
						
						
						//currentRank.setScheduleTemplate(template);
						
						NotificationManager.showNotification("New rank has been added.");
						
					} else {
						NotificationManager.showNotification("An error has occured while adding new rank");
					}
				} else {
					currentRank.setRank(rank.getRank());
					rankDao.updateRank(currentRank);
					
					NotificationManager.showNotification("Rank has been updated.");
				}
			}
		}
	}
}