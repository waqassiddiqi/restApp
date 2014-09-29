package net.waqassiddiqi.app.crew.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.controller.VesselFactory;
import net.waqassiddiqi.app.crew.db.ConnectionManager;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.style.skin.DefaultSkin;
import net.waqassiddiqi.app.crew.ui.AddVesselForm.ChangeListener;
import net.waqassiddiqi.app.crew.ui.control.RibbonbarTabControl;
import net.waqassiddiqi.app.crew.util.ConfigurationUtil;

import org.apache.log4j.Logger;

import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
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
		
		setTitle("Crew");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        initFactories();
        
		contentPane = new WebPanel();		
		setLayout (new BorderLayout());		
		contentPane.add (createStatusBar(), BorderLayout.SOUTH);              
        getContentPane().setBackground(new Color(252, 248, 252));
        contentPane.setOpaque(false);
        contentPane.add(createRibbonBar(), BorderLayout.NORTH);
        
		add(contentPane, BorderLayout.CENTER);
		
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

	@Override
	public void added(Vessel vessel) {
		this.ribbonBar.setEnabled(true);
	}
}
