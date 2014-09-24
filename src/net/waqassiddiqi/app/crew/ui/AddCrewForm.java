package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;
import net.waqassiddiqi.app.crew.util.InputValidator;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.WebDateField;
import com.alee.extended.image.WebImageDrop;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.GlobalConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
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
	private WebTabbedPane tabPan;
	private Crew currentCrew = null;
	private WebFileChooser fileChooser = null;
	private List<Rank> listRanks;
	private TimeSheet timeSheet;
	
	public AddCrewForm(MainFrame owner) {
		super(owner);
	}
	
	public AddCrewForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
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
        
        tabPan.addTab("  Crew Details   ", getForm());
        tabPan.addTab("  Schedule Template  ", 
        		new GroupPanel(false, 
        				new WebLabel("Rest Hours") {{ setDrawShade(true); }}, 
        				timeSheet.getView()).setMargin(10));
        
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
		
		timeSheet = new TimeSheet(25);
		
		refreshData();
		
		cmbRank = new WebComboBox();
		cmbRank.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScheduleTemplate template = new ScheduleTemplateDAO().getByRank(
						listRanks.get(cmbRank.getSelectedIndex()));
				
				if(template != null) {
					timeSheet.setSchedule(template.getSchedule());
				}
				
			}
		});
		
		txtSignonDate = new WebDateField ();
		txtSignonDate.setInputPrompt ("Enter date...");
		txtSignonDate.setDate(new Date());
		txtSignonDate.setInputPromptPosition (SwingConstants.CENTER);
		
		chkWatchkeeper = new WebCheckBox("Is watchkeeper?");
		
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
		content.add(new GroupPanel(GroupingType.fillFirst, cmbRank, new WebButton(
				new IconsHelper().loadIcon(IconsHelper.class,
						"common/restore_16x16.png"), new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						refreshData();
						bindData();						
					}
				})), "1,1");
		
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
		
		return content;
	}

	private void refreshData() {
		listRanks = new RankDAO().getAll();
	}
	
	@SuppressWarnings("unchecked")
	private void bindData() {
		for(Rank r : listRanks) {
			cmbRank.addItem(r.getRank());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("new")) {
			this.id = -1;
			this.currentCrew = null;
			
			txtFirstName.setText("");
			txtLastName.setText("");
			txtNationality.setText("");
			txtPassport.setText("");
			txtSignonDate.setText("");
			
		} else if(btnSource.getClientProperty("command").equals("save")) {
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
						});
				return;
			}
			
			if(listRanks.size() <= 0) {
				NotificationManager.showPopup(getOwner(), cmbRank, new String[] { "Please add a rank first from Rank Options above" });
				return;
			}
			
			ScheduleTemplateDAO scheduleDao = new ScheduleTemplateDAO();
			
			if(InputValidator.validateInput(getOwner(), new JTextField[] { txtFirstName, txtLastName, txtNationality, txtPassport, txtSignonDate }, 
					"This field cannot be empty")) {
				
				Crew crew = new Crew() {{ 
					setVesselId(listVessel.get(0).getId());
					setFirstName(txtFirstName.getText().trim());
					setLastName(txtLastName.getText().trim());
					setRank(cmbRank.getSelectedItem().toString());
					setNationality(txtNationality.getText().trim());
					setPassportNumber(txtPassport.getText().trim());
					setSignOnDate(txtSignonDate.getDate());
					setWatchKeeper(chkWatchkeeper.isSelected());
				}};
				
				Boolean[] scheduleArray = this.timeSheet.getSchedule();
				
				if(currentCrew == null) {
					
					this.id = new CrewDAO().addCrew(crew);
					
					if(this.id > -1) {
						crew.setId(id);
						this.currentCrew = crew;
						
						ScheduleTemplate templateOnSea = new ScheduleTemplate();
						templateOnSea.setSchedule(scheduleArray);
						
						int scheduleId = scheduleDao.addScheduleTemplate(templateOnSea);
						
						templateOnSea.setId(scheduleId);
						
						this.currentCrew.setScheduleTemplate(templateOnSea);
						
						if(scheduleId > 0) {
							scheduleDao.associateScheduleTemplate(currentCrew, templateOnSea);
						}
						
						NotificationManager.showNotification("New crew has been added");
						
					} else {
						NotificationManager.showNotification("An error has occured while adding new rank");
					}
					
				} else {
					currentCrew.setFirstName(txtFirstName.getText().trim());
					currentCrew.setLastName(txtLastName.getText().trim());
					currentCrew.setRank(cmbRank.getSelectedItem().toString());
					currentCrew.setNationality(txtNationality.getText().trim());
					currentCrew.setPassportNumber(txtPassport.getText().trim());
					currentCrew.setSignOnDate(txtSignonDate.getDate());
					currentCrew.setWatchKeeper(chkWatchkeeper.isSelected());
					
					new CrewDAO().updateCrew(currentCrew);
					
					currentCrew.getScheduleTemplate().setSchedule(scheduleArray);
					scheduleDao.updateScheduleTemplate(currentCrew.getScheduleTemplate());
					
					NotificationManager.showNotification("Crew has been updated.");
				}
			}
		}
	}
}