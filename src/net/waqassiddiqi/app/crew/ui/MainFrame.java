package net.waqassiddiqi.app.crew.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.controller.ReportingFactory;
import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.ConnectionManager;
import net.waqassiddiqi.app.crew.style.skin.DefaultSkin;
import net.waqassiddiqi.app.crew.ui.control.RibbonbarTabControl;
import net.waqassiddiqi.app.crew.util.PrefsUtil;

import org.apache.log4j.Logger;

import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.desktoppane.WebDesktopPane;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.style.StyleManager;
import com.alee.utils.ThreadUtils;

public class MainFrame extends WebFrame {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass().getName());
	private static MainFrame instance = null;
	
	private final WebPanel contentPane;
	private WebMemoryBar memoryBar;
	private final WebDesktopPane desktopPane;
	
	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}
	
	public Component createRibbonBar() {
		return new RibbonbarTabControl(MainFrame.this, 5).getComponent();
	}
	
	private void initFactories() {
		RankFactory.getInstance().setOwner(this);
		CrewFactory.getInstance().setOwner(this);
	}
	
	public MainFrame() {
		super();
		
		setTitle("Crew");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        initFactories();
        
		contentPane = new WebPanel();		
		setLayout (new BorderLayout());		
		contentPane.add (createStatusBar(), BorderLayout.SOUTH);
		
		desktopPane = new WebDesktopPane();        
        desktopPane.setOpaque (true);
        desktopPane.setBackground(new Color(252, 248, 252));        
        
        getContentPane().setBackground(new Color(252, 248, 252));
        contentPane.setOpaque(false);
        
        
        //desktopPane.add();
        
        
        contentPane.add(createRibbonBar(), BorderLayout.NORTH);
        
		add(contentPane, BorderLayout.CENTER);
		
		this.addContent(ReportingFactory.getInstance().getById("rest"));
		
		ThreadUtils.sleepSafely(500);
		pack();
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		PrefsUtil.setBoolean(PrefsUtil.PREF_VESSEL_ADDED, true);
		
		if(!PrefsUtil.getBoolean(PrefsUtil.PREF_VESSEL_ADDED, false)) {
			net.waqassiddiqi.app.crew.util.NotificationManager.showPopup(MainFrame.this, MainFrame.this, "No Vessel Found",
					new String[] { 
						"In order to continue, vessel details should be entered first",
						"Click on the button below to enter vessel details"
					}, 
						"Add new vessel", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							openDesktopChild((WebInternalFrame) VesselFactory.getInstance().getAdd(), false);
							//NotificationManager.showNotification("Vessel details has been added, you can always edit them from Vessels options");
						}
					});
		}
		
	}
	
	public void addContent(Component view) {
		
		if(activeComponent == null)
			contentPane.add(view, BorderLayout.CENTER);
		else {
			removeContent(activeComponent);
			contentPane.add(view, BorderLayout.CENTER);
		}
		
		activeComponent = view;
	}
	
	private Component activeComponent = null; 
	
	public void removeContent(Component view) {
		contentPane.remove(view);
		contentPane.revalidate();
	}
	
	public void openDesktopChild(WebInternalFrame childFrame, boolean openMaximum) {
		if(childFrame == null)
			return;
		
		childFrame.pack();
		
		if(childFrame.isVisible() == false) {
			this.desktopPane.add(childFrame, 0);
			this.desktopPane.getDesktopManager().activateFrame(childFrame);
			childFrame.setVisible(true);
		} else {
			this.desktopPane.getDesktopManager().activateFrame(childFrame);
		}
		
		if(openMaximum) {
			try {
				childFrame.setMaximum(true);
			} catch (PropertyVetoException e1) {
				log.warn(e1.getMessage(), e1);
			}
		}
	}
	
	@Deprecated
	public void addChildDesktopContent(WebInternalFrame childFrame) {
		this.desktopPane.add(childFrame);
		this.desktopPane.getDesktopManager().activateFrame(childFrame);
	}
	
	@Deprecated
	public void activateChildDesktopFrame(WebInternalFrame childFrame) {
		this.desktopPane.getDesktopManager().activateFrame(childFrame);
	}
	
	private WebStatusBar createStatusBar() {
		final WebStatusBar statusBar = new WebStatusBar();
		statusBar.addSpacing();

		statusBar.add(new WebLabel("This is an evaluation version"));
		
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
				setVisible(true);
			}
		});
	}
	
	public static void runApplication() throws SQLException {
		
		ConnectionManager.getInstance().setupDatabase();
		
		StyleManager.setDefaultSkin(DefaultSkin.class.getCanonicalName());		
		WebLookAndFeel.install();
		MainFrame.getInstance().display();

	}
	
	public static void main(String[] args) throws SQLException {
		runApplication(); 
	}
}
