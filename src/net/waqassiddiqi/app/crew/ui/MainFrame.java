package net.waqassiddiqi.app.crew.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.waqassiddiqi.app.crew.Constant;
import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.ConnectionManager;
import net.waqassiddiqi.app.crew.db.DatabaseClient;
import net.waqassiddiqi.app.crew.license.LicenseManager;
import net.waqassiddiqi.app.crew.model.ApplicationSetting;
import net.waqassiddiqi.app.crew.model.ApplicationSetting.ApplicationMode;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.style.skin.DefaultSkin;
import net.waqassiddiqi.app.crew.ui.AddVesselForm.ChangeListener;
import net.waqassiddiqi.app.crew.ui.control.RibbonbarTabControl;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;
import net.waqassiddiqi.app.crew.util.CalendarUtil;
import net.waqassiddiqi.app.crew.util.ConfigurationUtil;
import net.waqassiddiqi.app.crew.util.PrefsUtil;

import org.apache.log4j.Logger;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.extended.panel.SingleAlignPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextField;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.style.StyleManager;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.ImageUtils;
import com.alee.utils.ThreadUtils;

public class MainFrame extends WebFrame implements ChangeListener {
	
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass().getName());
	private static MainFrame instance = null;
	private final WebPanel contentPane;
	private WebMemoryBar memoryBar;
	private RibbonbarTabControl ribbonBar;
	private static WebDialog mProgressDialog;
	private static WebProgressBar mProgressBar;
	private IconsHelper iconHelper = null;
	private ApplicationSetting appSettings = null;
	private WebStatusBar statusBar;
	private boolean isConnected = false;
	private WebLabel lblServerStatus;
	
	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}
	
	public Component createRibbonBar() {
		this.ribbonBar = new RibbonbarTabControl(MainFrame.this, 5);
		
		return ribbonBar.getComponent();
	}
	
	private void initFactories() {
		RankFactory.getInstance().setOwner(this);
		CrewFactory.getInstance().setOwner(this);
		VesselFactory.getInstance().setOwner(this);
	}
	
	private void initClient() {
		
		try {
			
						
			if(ApplicationSetting.getApplicationMode() == ApplicationMode.Unknown) {
				
				throw new SQLException("Unable to get application settings from database");
				
			} else if(ApplicationSetting.getApplicationMode() == ApplicationMode.Client) {
				
				ConnectionManager.getInstance().setLocal(false);
				ConnectionManager.getInstance().closeConnection();
				
			}	
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			WebOptionPane.showMessageDialog (
					this, "An application instance is already running or application data has corrupted", 
					"Error", WebOptionPane.ERROR_MESSAGE );
			
			System.exit(1);
		}
	}
	
	public MainFrame() {
		super();
		
		setTitle("SHIP IP LTD - REST HOURS VALIDATOR " + Constant.version);
		
		iconHelper = new IconsHelper();
		
		List<ImageIcon> iconList = new ArrayList<ImageIcon>();
		iconList.add(iconHelper.loadIcon("common/app_icon_128.jpg"));
		iconList.add(iconHelper.loadIcon("common/app_icon_64.jpg"));
		iconList.add(iconHelper.loadIcon("common/app_icon_32.jpg"));
		iconList.add(iconHelper.loadIcon("common/app_icon_16.jpg"));
		
		setIconImages(ImageUtils.toImagesList(iconList));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        lblServerStatus = new WebLabel();
        
        initFactories();
		
        contentPane = new WebPanel();		
		setLayout(new BorderLayout());
		contentPane.add(createRibbonBar(), BorderLayout.NORTH);
		getContentPane().setBackground(new Color(252, 248, 252));
        contentPane.setOpaque(false);
        
		add(contentPane, BorderLayout.CENTER);
		
		ConnectionManager.getInstance().setServerIP(PrefsUtil.getString(PrefsUtil.PREF_SERVER_IP, "127.0.0.1"));
		ConnectionManager.getInstance().setServerPort(PrefsUtil.getString(PrefsUtil.PREF_SERVER_PORT, "9090"));
		
		if(true) {
			
		}
		
		if(ApplicationSetting.getApplicationMode() == ApplicationMode.Client) {
			
			if(new DatabaseClient().isServerAvailable(
					PrefsUtil.getString(PrefsUtil.PREF_SERVER_IP, "127.0.0.1"), 
					PrefsUtil.getString(PrefsUtil.PREF_SERVER_PORT, "9090")) == false) {
				
				this.ribbonBar.setEnabled(false);
				
				showConnectionDialog();
				
			} else {
				isConnected = true;
				if(ConfigurationUtil.isVesselConfigured() == false) {
					
					this.ribbonBar.setEnabled(false);
					
					net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(MainFrame.this, MainFrame.this, "No Vessel Found",
							new String[] { 
								"In order to continue, vessel details should be entered first",
								"Click on the button below to enter vessel details"
							}, 
								"Add new vessel", 
							new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									addContent(VesselFactory.getInstance().getAdd());
								}
							}, false);
				}
				
			}
		} else {
			if(ConfigurationUtil.isVesselConfigured() == false) {
				
				this.ribbonBar.setEnabled(false);
				
				net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(MainFrame.this, MainFrame.this, "No Vessel Found",
						new String[] { 
							"In order to continue, vessel details should be entered first",
							"Click on the button below to enter vessel details"
						}, 
							"Add new vessel", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								addContent(VesselFactory.getInstance().getAdd());
							}
						}, false);
			} else {
				Component c = new ProductActivationForm(this).getView();
				c.setName("license");
				addContent(c);
			}
		}
		
		contentPane.add (createStatusBar(), BorderLayout.SOUTH);
		
		ThreadUtils.sleepSafely(500);
		pack();
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
	}
	
	@SuppressWarnings("serial")
	private void showConnectionDialog() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.FILL, TableLayout.FILL },
				{ TableLayout.FILL, TableLayout.FILL } });
		layout.setHGap(5);
		layout.setVGap(5);
		WebPanel content = new WebPanel(layout);
		content.setOpaque(false);

		final WebTextField txtServerIP = new WebTextField(10) {{ setText(PrefsUtil.getString(PrefsUtil.PREF_SERVER_IP, "127.0.0.1")); }};
		final WebTextField txtServerPort = new WebTextField(10) {{ setText(PrefsUtil.getString(PrefsUtil.PREF_SERVER_PORT, "9090")); }};
		
		content.add(new WebLabel("Server IP"), "0,0");
		content.add(txtServerIP, "1,0");
		
		content.add(new WebLabel("Server Port"), "0,1");
		content.add(txtServerPort, "1,1");
		
		final WebProgressOverlay progressOverlay = new WebProgressOverlay ();
        progressOverlay.setConsumeEvents ( false );
		
		final WebButton button = new WebButton( "Test Connection");
        progressOverlay.setComponent(button);
        
		GroupPanel gp = new GroupPanel(GroupingType.fillLast, 10, false, new WebLabel("<html><div style=\"text-align: center;\">Please verify the connection " +
				"settings to<br/>Rest Hours Validator Server</div></html>"), content, new SingleAlignPanel(progressOverlay, SingleAlignPanel.RIGHT)
				.setMargin(10, 0, 0, 0));
		
		final WebPopOver popOver = net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(MainFrame.this, MainFrame.this, false, false, iconHelper.loadIcon("common/server_16x16.png"), 
				"Server Connection Failed",
				gp);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				progressOverlay.setShowLoad(true);
				
				SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						return new DatabaseClient().isServerAvailable(txtServerIP.getText().trim(), txtServerPort.getText().trim());
					}
					
					@Override
					protected void done() {
						
						try {
							
							if(!get()) {
								net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(MainFrame.this, 
										txtServerIP, new String[] { "Unable to connect to server" });
								
								lblServerStatus.setIcon(iconHelper.loadIcon("common/disconnect_16x61.png"));
								TooltipManager.setTooltip(lblServerStatus, iconHelper.loadIcon("common/server_16x16.png"), "Not conntected to Rest Hours Validator Server");
								
							} else {
								
								/*ConnectionManager.getInstance().closeConnection();
								ConnectionManager.getInstance().setLocal(true);
								
								ApplicationSettingDAO appDao = new ApplicationSettingDAO();
								appSettings = appDao.get();
								
								appSettings.setServerIP(txtServerIP.getText().trim());
								appSettings.setServerPort(txtServerPort.getText().trim());
								
								appDao.updateApplicationSetting(appSettings);
								ConnectionManager.getInstance().closeConnection();*/
								
								PrefsUtil.setString(PrefsUtil.PREF_SERVER_IP, txtServerIP.getText().trim()); 
								PrefsUtil.setString(PrefsUtil.PREF_SERVER_PORT, txtServerPort.getText().trim());
								
								
								isConnected = true;
								ribbonBar.setEnabled(true);
								popOver.dispose();
								
								lblServerStatus.setIcon(iconHelper.loadIcon("common/connect_16x16.png"));
								TooltipManager.setTooltip(lblServerStatus, iconHelper.loadIcon("common/server_16x16.png"), "Connnected to Rest Hours Validator Server" );
							}
							
						} catch (Exception e) {
							
						}
						
						progressOverlay.setShowLoad(false);
					}
				};
				
				worker.execute();
			}
		});
	}
	
	public void addContent(Component view) {
		
		if(ApplicationSetting.getApplicationMode() == ApplicationMode.Client) {
			if(new DatabaseClient().isServerAvailable(
					PrefsUtil.getString(PrefsUtil.PREF_SERVER_IP, "127.0.0.1"), 
					PrefsUtil.getString(PrefsUtil.PREF_SERVER_PORT, "9090")) == false) {
				
				this.ribbonBar.setEnabled(false);
				showConnectionDialog();
				
				activeComponent = view;
				
				return;
				
			}
		}
		
		if(activeComponent == null) {
			contentPane.add(view, BorderLayout.CENTER);
			activeComponent = view;
		} else {
			
			if(!view.getName().equals(activeComponent.getName())) {
				removeContent(activeComponent);
				
				contentPane.add(view, BorderLayout.CENTER);
				
				activeComponent = view;
			}
		}
	}
	
	private Component activeComponent = null; 
	
	public void removeContent(Component view) {
		if(view instanceof WebPanel) {
			BaseForm f = (BaseForm) ((WebPanel) view).getClientProperty("host");
			if(f != null)
				f.unregisterHotKeys();
		}
		
		contentPane.remove(view);
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	private WebStatusBar createStatusBar() {
		statusBar = new WebStatusBar();
		statusBar.addSpacing();
		
		WebLabel lblStatus = new WebLabel();
		
		if(ApplicationSetting.getApplicationMode() == ApplicationMode.Standalone) {
			
			RegistrationSetting settings = LicenseManager.getRegistationDetail();
			
			if(LicenseManager.isExpired()) {
				
				if(!settings.isRegistered()) {
					TooltipManager.setTooltip(lblStatus, "<html><center>Your product evaluation period has expired. In order to activate it, <br/>" +
							"please copy the System ID and send it to sales@shipip.com. <br/>" +
							"You will receive an email containing your product activation within three <br/>" +
							"business days of successful payments.</center></html>");
					
					lblStatus.setText("Product evaluation period has expired");
					
				} else {
					
					TooltipManager.setTooltip(lblStatus, "<html><center>Your product evaluation period has expired. In order to re-activate it, <br/>" +
							"please copy the System ID and send it to sales@shipip.com. <br/>" +
							"You will receive an email containing your product activation within three <br/>" +
							"business days of successful payments.</center></html>");
					
					lblStatus.setText("Product registration has expired");
				}			
				
				statusBar.add(lblStatus);
				this.ribbonBar.setEnabled(false);
				
			} else if(!settings.isRegistered()) {
				
				statusBar.add(new WebLabel("This is an evaluation version and will expire on " + 
						CalendarUtil.format("dd MMM yyyy", settings.getExpiry())));
				
			} else {
				
				if(LicenseManager.validateLicense() == false) {
					statusBar.add(new WebLabel("Invalid license details found, product has been locked"));
					this.ribbonBar.setEnabled(false);
				}
			}
		}
		
		if(ApplicationSetting.getApplicationMode() == ApplicationMode.Client) {
			statusBar.addSeparatorToEnd();
			
			statusBar.addToEnd(lblServerStatus);
			
			if(isConnected) {
				lblServerStatus.setIcon(iconHelper.loadIcon("common/connect_16x16.png"));
				TooltipManager.setTooltip(lblServerStatus, iconHelper.loadIcon("common/server_16x16.png"), "Connnected to Rest Hours Validator Server" );

			} else {
				lblServerStatus.setIcon(iconHelper.loadIcon("common/disconnect_16x61.png"));
				TooltipManager.setTooltip(lblServerStatus, iconHelper.loadIcon("common/server_16x16.png"), "Not conntected to Rest Hours Validator Server");
			}
		}
		
		statusBar.addSeparatorToEnd();
		
		memoryBar = new WebMemoryBar();
		memoryBar.setShowMaximumMemory(false);
		memoryBar.setPreferredWidth(memoryBar.getPreferredSize().width + 20);
		statusBar.addToEnd(memoryBar);

		NotificationManager.setMargin(0, 0, statusBar.getPreferredSize().height, 0);
		return statusBar;
	}
	
	public void display() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				mProgressDialog.dispose();
				
				setVisible(true);
			}
		});
	}
	
	public static void runApplication() throws SQLException {
		StyleManager.setDefaultSkin(DefaultSkin.class.getCanonicalName());		
		WebLookAndFeel.install();
		
		mProgressDialog = getWebProgressDialog();
		mProgressDialog.setVisible(true);
		
		setFont();
		
		MainFrame.getInstance().display();
	}
	
	private static void setFont() {
		
		try {
			URL l = ClassLoader.class.getResource("/resource/template/CarroisGothic-Regular.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, l.openStream());
	        font = font.deriveFont(Font.PLAIN, 13);
	        GraphicsEnvironment ge =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(font);
	        
	        UIManager.put("Button.font", font);
			UIManager.put("ToggleButton.font", font);
			UIManager.put("RadioButton.font", font);
			UIManager.put("CheckBox.font", font);
			UIManager.put("ColorChooser.font", font);
			UIManager.put("ComboBox.font", font);
			UIManager.put("Label.font", font);
			UIManager.put("List.font", font);
			UIManager.put("MenuBar.font", font);
			UIManager.put("MenuItem.font", font);
			UIManager.put("RadioButtonMenuItem.font", font);
			UIManager.put("CheckBoxMenuItem.font", font);
			UIManager.put("Menu.font", font);
			UIManager.put("PopupMenu.font", font);
			UIManager.put("OptionPane.font", font);
			UIManager.put("Panel.font", font);
			UIManager.put("ProgressBar.font", font);
			UIManager.put("ScrollPane.font", font);
			UIManager.put("Viewport.font", font);
			UIManager.put("TabbedPane.font", font);
			UIManager.put("Table.font", font);
			UIManager.put("TableHeader.font", font);
			UIManager.put("TextField.font", font);
			UIManager.put("PasswordField.font", font);
			UIManager.put("TextArea.font", font);
			UIManager.put("TextPane.font", font);
			UIManager.put("EditorPane.font", font);
			UIManager.put("TitledBorder.font", font);
			UIManager.put("ToolBar.font", font);
			UIManager.put("ToolTip.font", font);
			UIManager.put("Tree.font", font);
	        
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void main(String[] args) throws SQLException {
		runApplication();
	}
	
	@Override
	public void added(Vessel vessel) {
		this.ribbonBar.setEnabled(true);
	}
	
	public static WebDialog getWebProgressDialog() {
		
		mProgressDialog = new WebDialog((Frame) null);
		mProgressDialog.setModal(false);
		mProgressDialog.setUndecorated(true);
		
		WebPanel contentPanel = new WebPanel(new BorderLayout());
		
		WebDecoratedImage background = new WebDecoratedImage ( new IconsHelper().loadIcon("common/splash.jpg") );
		background.setShadeWidth ( 5, false );
		background.setRound ( 0 );
		
		contentPanel.add(background, BorderLayout.CENTER);
        mProgressBar = new WebProgressBar();
        
        mProgressBar.setRound(0);
        mProgressBar.setIndeterminate(true);
        
        GroupPanel p = new GroupPanel(0,  false, background, mProgressBar);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(p);
        
        mProgressDialog.add(contentPanel);
        
        mProgressDialog.pack();
        mProgressDialog.setLocationRelativeTo(null);
        mProgressDialog.setVisible(true);
		
		
		return mProgressDialog;
	}
}