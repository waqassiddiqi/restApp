package net.waqassiddiqi.app.crew.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.ConnectionManager;
import net.waqassiddiqi.app.crew.license.LicenseManager;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.style.skin.DefaultSkin;
import net.waqassiddiqi.app.crew.ui.AddVesselForm.ChangeListener;
import net.waqassiddiqi.app.crew.ui.control.RibbonbarTabControl;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;
import net.waqassiddiqi.app.crew.util.CalendarUtil;
import net.waqassiddiqi.app.crew.util.ConfigurationUtil;

import org.apache.log4j.Logger;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.rootpane.WebFrame;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.style.StyleManager;
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
	
	public MainFrame() {
		super();
		
		//mProgressDialog.setProgress(25);
		
		try {
			ConnectionManager.getInstance().setupDatabase();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		//mProgressDialog.setProgress(50);
		
		setTitle("Crew");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        initFactories();
        
        //mProgressDialog.setProgress(75);
        
		contentPane = new WebPanel();		
		setLayout (new BorderLayout());		
		contentPane.add (createStatusBar(), BorderLayout.SOUTH);              
        getContentPane().setBackground(new Color(252, 248, 252));
        contentPane.setOpaque(false);
        contentPane.add(createRibbonBar(), BorderLayout.NORTH);
        
		add(contentPane, BorderLayout.CENTER);
		
		//mProgressDialog.setProgress(90);
		
		ThreadUtils.sleepSafely(500);
		pack();
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		
		
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
	
	public void addContent(Component view) {
		
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
		final WebStatusBar statusBar = new WebStatusBar();
		statusBar.addSpacing();

		RegistrationSetting settings = LicenseManager.getRegistationDetail();
		
		if(!settings.isRegistered())
			statusBar.add(new WebLabel("This is an evaluation version and will expire on " + 
					CalendarUtil.format("dd MMM yyyy", settings.getExpiry())));
		
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
