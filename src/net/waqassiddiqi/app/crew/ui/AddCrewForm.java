package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.RankDAO;
import net.waqassiddiqi.app.crew.db.ScheduleTemplateDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Rank;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet;
import net.waqassiddiqi.app.crew.util.InputValidator;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.WebDateField;
import com.alee.extended.image.WebImageDrop;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.global.GlobalConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;
import com.alee.utils.ImageUtils;

public class AddCrewForm extends BaseForm implements ActionListener {
	private int id = -1;
	
	private WebComboBox cmbRank;
	private WebTextField txtFirstName;
	private WebTextField txtLastName;
	private WebTextField txtNationality;
	private WebTextField txtPassport;
	private WebDateField txtSignonDate;
	private WebCheckBox chkWatchkeeper;
	private WebCheckBox chkActive;
	private WebTabbedPane tabPan;
	private Crew currentCrew = null;
	private WebFileChooser fileChooser = null;
	private List<Rank> listRanks;
	private ScheduleTemplateDAO scheduleDao;
	
	private TimeSheet timeSheetOnSeaWatchkeeping;
	private TimeSheet timeSheetOnSeaNonWatchkeeping;
	
	private TimeSheet timeSheetOnPortWatchKeeping;
	private TimeSheet timeSheetOnPortNonWatchKeeping;
	
	protected Boolean[] defaultScheduleList = new Boolean[48];
	
	private int defaultTabIndex = 0;
	
	public AddCrewForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddCrewForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
		Arrays.fill(defaultScheduleList, Boolean.TRUE);
		scheduleDao = new ScheduleTemplateDAO();
	}
	
	public void setDefaultTabIndex(int index) {
		this.defaultTabIndex = index;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Manage Crew") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
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
        
        tabPan.addTab("  Crew Details   ", getForm());
        tabPan.addTab("  Schedule Template ", scrollPane);
        
        tabPan.setSelectedIndex(defaultTabIndex);
        
        tabPan.setContentInsets(new Insets(10, 10, 10, 10));
        
        return tabPan;
	}
	
	@SuppressWarnings("serial")
	private Component getForm() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);
		
		refreshData();
		
		cmbRank = new WebComboBox();
		cmbRank.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getDefaultScheduleTemplate();				
			}
		});
		
		txtSignonDate = new WebDateField ();
		txtSignonDate.setInputPrompt ("Enter date...");
		txtSignonDate.setDate(new Date());
		txtSignonDate.setInputPromptPosition (SwingConstants.CENTER);
		
		chkActive = new WebCheckBox("Is Active?");
		chkActive.setSelected(true);
		
		chkWatchkeeper = new WebCheckBox("Is watchkeeper?");		
		chkWatchkeeper.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getDefaultScheduleTemplate();
			}
		});
		
		
		txtFirstName = new WebTextField(15);
		txtLastName = new WebTextField(15);
		txtNationality = new WebTextField(15);
		txtPassport = new WebTextField(15);
		
		bindData();
		
		final MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					
					if (fileChooser == null) {
						fileChooser = new WebFileChooser();
						fileChooser.setDialogTitle("Choose an image to add");
						fileChooser.setMultiSelectionEnabled(false);
						fileChooser.setAcceptAllFileFilterUsed(false);
						fileChooser
								.addChoosableFileFilter(GlobalConstants.IMAGES_FILTER);
					}
					if (fileChooser.showOpenDialog(getOwner()) == WebFileChooser.APPROVE_OPTION) {
						for (File file : fileChooser.getSelectedFiles()) {
							((WebImageDrop) e.getSource()).setImage(ImageUtils.getBufferedImage(new ImageIcon(file.getAbsolutePath())));
							break;
						}
					}
				}
			}
		};
		
		content.add(new WebImageDrop(64, 64) { { setToolTipText("Double click to select crew photo to add"); addMouseListener(ma);  } }, "1,0,LEFT,CENTER");

		content.add(new WebLabel("Rank", WebLabel.TRAILING), "0,1");
		content.add(cmbRank, "1,1");
		
		content.add(new WebLabel("First Name", WebLabel.TRAILING), "0,2");
		content.add(txtFirstName, "1,2");
		
		content.add(new WebLabel("Last Name", WebLabel.TRAILING), "0,3");
		content.add(txtLastName, "1,3");
		
		content.add(new WebLabel("Nationality", WebLabel.TRAILING), "0,4");
		content.add(txtNationality, "1,4");
		
		content.add(new WebLabel("Passport / Book Number", WebLabel.TRAILING), "0,5");
		content.add(txtPassport, "1,5");
		
		content.add(new WebLabel("Sign On Date", WebLabel.TRAILING), "0,6");
		content.add(txtSignonDate, "1,6");
		
		content.add(new WebLabel("", WebLabel.TRAILING), "0,7");
		content.add(chkWatchkeeper, "1,7");
		
		content.add(new WebLabel("", WebLabel.TRAILING), "0,8");
		content.add(chkActive, "1,8");
		
		return content;
	}

	private void refreshData() {
		listRanks = new RankDAO().getAll();
	}
	
	@SuppressWarnings("unchecked")
	private void bindData() {
		
		if(this.id > 0) {
			currentCrew = new CrewDAO().getById(id);
			
			if(currentCrew != null) {
				txtFirstName.setText(currentCrew.getFirstName());
				txtLastName.setText(currentCrew.getLastName());
				txtNationality.setText(currentCrew.getNationality());
				txtPassport.setText(currentCrew.getPassportNumber());
				txtSignonDate.setDate(currentCrew.getSignOnDate());
				chkWatchkeeper.setSelected(currentCrew.isWatchKeeper());
				chkActive.setSelected(currentCrew.isActive());
			}
		}
		
		cmbRank.addItem("Select rank");
		
		int i = 0;
		for(Rank r : listRanks) {
			cmbRank.addItem(r);
			
			if(currentCrew != null) {
				if(r.getRank().equals(currentCrew.getRank())) {
					cmbRank.setSelectedIndex(i);
				}
			}
			
			i++;
		}
		
		
		
	}
	
	private void getDefaultScheduleTemplate() {
		
		timeSheetOnPortWatchKeeping.setSchedule(defaultScheduleList);
		timeSheetOnPortNonWatchKeeping.setSchedule(defaultScheduleList);
		timeSheetOnSeaWatchkeeping.setSchedule(defaultScheduleList);
		timeSheetOnSeaNonWatchkeeping.setSchedule(defaultScheduleList);
		
		
		List<ScheduleTemplate> templates = new ArrayList<ScheduleTemplate>(); 
		if(currentCrew != null)
			templates = new ScheduleTemplateDAO().getAllByCrew(currentCrew);
		
		if(templates.size() <= 0 && cmbRank.getSelectedIndex() > 0) {
			int returnCode = WebOptionPane.showConfirmDialog(getOwner(), "Would you like to enter the default template exist for that Rank?", "Confirm", 
					WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);
			
			if(returnCode == 0) {
				templates = new ScheduleTemplateDAO().getAllByRank((Rank) cmbRank.getSelectedItem());
			}
		}
		
		if(templates != null && templates.size() > 0) {
			for(ScheduleTemplate t : templates) {
				if(t.isOnPort() && t.isWatchKeeping()) {
					timeSheetOnPortWatchKeeping.setSchedule(t.getSchedule());
				} else if(t.isOnPort() && !t.isWatchKeeping()) {
					timeSheetOnPortNonWatchKeeping.setSchedule(t.getSchedule());
				} else if(!t.isOnPort() && t.isWatchKeeping()) {
					timeSheetOnSeaWatchkeeping.setSchedule(t.getSchedule());
				} else if(!t.isOnPort() && !t.isWatchKeeping()) {
					timeSheetOnSeaNonWatchkeeping.setSchedule(t.getSchedule());
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("new")) {
			
			save();
			
			this.id = -1;
			this.currentCrew = null;
			
			txtFirstName.setText("");
			txtLastName.setText("");
			txtNationality.setText("");
			txtPassport.setText("");
			txtSignonDate.setText("");
						
			
		} else if(btnSource.getClientProperty("command").equals("save")) {
			save();
		}
	}
	
	private void save() {
		tabPan.setSelectedIndex(0);
		
		final List<Vessel> listVessel = new VesselDAO().getAll();
		if(listVessel.size() <= 0) {
			net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(getOwner(), getOwner(), "No Vessel Found",
					new String[] { 
						"In order to continue, vessel details should be entered first",
						"Click on the button below to enter vessel details"
					}, 
						"Add new vessel", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							getOwner().addContent(VesselFactory.getInstance().getAdd());
						}
					}, true);
			return;
		}
		
		if(listRanks.size() <= 0) {
			NotificationManager.showPopup(getOwner(), cmbRank, new String[] { "Please add a rank first from Rank Options above" });
			return;
		}	
		
		CrewDAO crewDao = new CrewDAO();
		
		if(InputValidator.validateInput(getOwner(), new JTextField[] { txtFirstName, txtLastName, txtNationality, txtPassport, txtSignonDate }, 
				"This field cannot be empty")) {
			
			if(currentCrew == null) {
				
				Crew crew = new Crew() {{ 
					setVesselId(listVessel.get(0).getId());
					setFirstName(txtFirstName.getText().trim());
					setLastName(txtLastName.getText().trim());
					setRank(cmbRank.getSelectedItem().toString());
					setNationality(txtNationality.getText().trim());
					setPassportNumber(txtPassport.getText().trim());
					setSignOnDate(txtSignonDate.getDate());
					setWatchKeeper(chkWatchkeeper.isSelected());
					setActive(chkActive.isSelected());
				}};
				
				if(crewDao.isPassportExists(txtPassport.getText().trim(), 0)) {
					NotificationManager.showPopup(getOwner(), txtPassport, new String[] { "Passport / Book number must be unique" });
					return;
				}
				
				this.id = crewDao.addCrew(crew);
				
				if(this.id > -1) {
					crew.setId(id);
					this.currentCrew = crew;
					
					associateDefaultTemplates();
					
					NotificationManager.showNotification("New crew has been added");
					
				} else {
					NotificationManager.showNotification("An error has occured while adding new rank");
				}
				
			} else {
				
				if(crewDao.isPassportExists(txtPassport.getText().trim(), currentCrew.getId())) {
					NotificationManager.showPopup(getOwner(), txtPassport, new String[] { "This passport number is already associated with other member of crew" });
					return;
				}
				
				currentCrew.setFirstName(txtFirstName.getText().trim());
				currentCrew.setLastName(txtLastName.getText().trim());
				currentCrew.setRank(cmbRank.getSelectedItem().toString());
				currentCrew.setNationality(txtNationality.getText().trim());
				currentCrew.setPassportNumber(txtPassport.getText().trim());
				currentCrew.setSignOnDate(txtSignonDate.getDate());
				currentCrew.setWatchKeeper(chkWatchkeeper.isSelected());
				currentCrew.setActive(chkActive.isSelected());
				
				new CrewDAO().updateCrew(currentCrew);
				scheduleDao.removeScheduleTemplateByCrew(currentCrew);
				
				associateDefaultTemplates();
				
				NotificationManager.showNotification("Crew has been updated.");
			}
		}
	}
	
	private void associateDefaultTemplates() {
		Boolean[] scheduleArrayOnSeaWatchkeeping = this.timeSheetOnSeaWatchkeeping.getSchedule();
		Boolean[] scheduleArrayOnSeaNonWatchkeeping = this.timeSheetOnSeaNonWatchkeeping.getSchedule();
		Boolean[] scheduleArrayOnPortWatchkeeping = this.timeSheetOnPortWatchKeeping.getSchedule();
		Boolean[] scheduleArrayOnPortNonWatchkeeping = this.timeSheetOnPortNonWatchKeeping.getSchedule();
		
		scheduleDao = new ScheduleTemplateDAO();
		
		ScheduleTemplate template = new ScheduleTemplate();
		template.setSchedule(scheduleArrayOnSeaWatchkeeping);
		template.setOnPort(false);
		template.setWatchKeeping(true);
		
		int scheduleId = scheduleDao.addScheduleTemplate(template);
		
		template.setId(scheduleId);
		
		if(scheduleId > 0) {
			scheduleDao.associateScheduleTemplate(currentCrew, template);
		}
		
		template = new ScheduleTemplate();
		template.setSchedule(scheduleArrayOnSeaNonWatchkeeping);
		template.setOnPort(false);
		template.setWatchKeeping(false);
		
		scheduleId = scheduleDao.addScheduleTemplate(template);
		
		template.setId(scheduleId);
		
		if(scheduleId > 0) {
			scheduleDao.associateScheduleTemplate(currentCrew, template);
		}
		
		template = new ScheduleTemplate();
		template.setSchedule(scheduleArrayOnPortWatchkeeping);
		template.setOnPort(true);
		template.setWatchKeeping(true);
		
		scheduleId = scheduleDao.addScheduleTemplate(template);
		
		template.setId(scheduleId);
		
		if(scheduleId > 0) {
			scheduleDao.associateScheduleTemplate(currentCrew, template);
		}
		
		template = new ScheduleTemplate();
		template.setSchedule(scheduleArrayOnPortNonWatchkeeping);
		template.setOnPort(true);
		template.setWatchKeeping(false);
		
		scheduleId = scheduleDao.addScheduleTemplate(template);
		
		template.setId(scheduleId);
		
		if(scheduleId > 0) {
			scheduleDao.associateScheduleTemplate(currentCrew, template);
		}
	}
}