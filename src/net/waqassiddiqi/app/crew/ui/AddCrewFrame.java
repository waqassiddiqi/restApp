package net.waqassiddiqi.app.crew.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.WebDateField;
import com.alee.extended.image.WebImageDrop;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.GlobalConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.utils.ImageUtils;
import com.alee.utils.SwingUtils;

public class AddCrewFrame extends BaseChildFrame {
	private static final long serialVersionUID = 1L;
	private WebComboBox cmbVessel;
	private WebTextField txtFirstName;
	private WebTextField txtLastName;
	private WebTextField txtRank;
	private WebTextField txtNationality;
	private WebTextField txtPassport;
	private WebDateField txtSignonDate;
	private WebCheckBox chkWatchkeeper;
	private List<Vessel> listVessel;
	private MainFrame owner;
	private WebFileChooser fileChooser = null;
	
	@SuppressWarnings("serial")
	public AddCrewFrame(final MainFrame owner, String title, boolean resizeable, boolean closeable,
			boolean maximizeable, boolean iconfiable) {
		
		super(owner, title, resizeable, closeable, maximizeable, iconfiable);
		
		setDefaultCloseOperation(WebDialog.DISPOSE_ON_CLOSE);

		this.owner = owner;
		
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
		
		cmbVessel = new WebComboBox();
		
		txtSignonDate = new WebDateField ();
		txtSignonDate.setInputPrompt ("Enter date...");
		txtSignonDate.setInputPromptPosition (SwingConstants.CENTER);
		
		chkWatchkeeper = new WebCheckBox("Is watchkeeper?");
		
		txtFirstName = new WebTextField(15);
		txtLastName = new WebTextField(15);
		txtRank = new WebTextField(15);
		txtNationality = new WebTextField(15);
		txtPassport = new WebTextField(15);

		addInputField(txtFirstName, txtLastName, txtRank, txtNationality, txtPassport, txtSignonDate, chkWatchkeeper);
		
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
					if (fileChooser.showOpenDialog(owner) == WebFileChooser.APPROVE_OPTION) {
						for (File file : fileChooser.getSelectedFiles()) {
							((WebImageDrop) e.getSource()).setImage(ImageUtils.getBufferedImage(new ImageIcon(file.getAbsolutePath())));
							break;
						}
					}
				}
			}
		};
		
		content.add(new WebImageDrop(64, 64) { { setToolTipText("Double click to select crew photo to add"); addMouseListener(ma);  } }, "1,0,LEFT,CENTER");

		content.add(new WebLabel("Vessel", WebLabel.TRAILING), "0,1");
		content.add(new GroupPanel(GroupingType.fillFirst, cmbVessel, new WebButton(
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
		
		content.add(new WebLabel("Rank", WebLabel.TRAILING), "0,4");
		content.add(txtRank, "1,4");
		
		content.add(new WebLabel("Nationality", WebLabel.TRAILING), "0,5");
		content.add(txtNationality, "1,5");
		
		content.add(new WebLabel("Passport / Book Number", WebLabel.TRAILING), "0,6");
		content.add(txtPassport, "1,6");
		
		content.add(new WebLabel("Sign On Date", WebLabel.TRAILING), "0,7");
		content.add(txtSignonDate, "1,7");
		
		content.add(new WebLabel("", WebLabel.TRAILING), "0,8");
		content.add(chkWatchkeeper, "1,8");

		final WebButton login = new WebButton("Save");
		WebButton cancel = new WebButton("Cancel");
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ((WebButton) e.getSource()) == login ) {
					
					if( !validateInput(new JTextField[] { txtFirstName, txtLastName, txtRank, txtNationality, txtPassport, txtSignonDate }, "This field is required") )
						return;
					
					Crew crew = new Crew() {{ 
						setVesselId( listVessel.get(cmbVessel.getSelectedIndex()).getId() );
						setFirstName(txtFirstName.getText().trim());
						setLastName(txtLastName.getText().trim());
						setRank(txtRank.getText().trim());
						setNationality(txtNationality.getText().trim());
						setPassportNumber(txtPassport.getText().trim());
						setSignOnDate(txtSignonDate.getDate());
						setWatchKeeper(chkWatchkeeper.isSelected());
					}};
					
					if(new CrewDAO().addCrew(crew)) {
						NotificationManager.showNotification("New crew has been added");						
						close();
					}
				} else {
					close();
				}
				
			}
		};
		
		login.addActionListener(listener);
		cancel.addActionListener(listener);
		content.add(new CenterPanel(new GroupPanel(5, login, cancel)), "0,9,1,9");
		SwingUtils.equalizeComponentsWidths(login, cancel);

		add(content);
		
		attachListener();
		
		HotkeyManager.registerHotkey(this, cancel, Hotkey.ESCAPE);
		HotkeyManager.registerHotkey(this, login, Hotkey.ENTER);
	}

	
	
	private void refreshData() {
		listVessel = new VesselDAO().getAll();
	}
	
	private void bindData() {
		for(Vessel v : listVessel) {
			cmbVessel.addItem(v.getName());
		}
	}
	
	private void attachListener() {
		this.addInternalFrameListener(new InternalFrameListener() {
			
			@Override
			public void internalFrameOpened(InternalFrameEvent arg0) {
				
			}
			
			@Override
			public void internalFrameIconified(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void internalFrameDeactivated(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void internalFrameClosing(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void internalFrameActivated(InternalFrameEvent arg0) {	
				
				if(listVessel.size() == 0) {
					NotificationManager.showPopup(owner, cmbVessel, "No Vessel Found",
							new String[] { 
								"In order to add crew, a vessel should be added first",
								"Click on the button below to add new vessel"
							}, 
								"Add new vessel", 
							new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									owner.openDesktopChild((WebInternalFrame) VesselFactory.getInstance().getAdd(), false);
								}
							});
				}
			}
		});
	}
}