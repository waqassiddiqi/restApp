package net.waqassiddiqi.app.crew.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.waqassiddiqi.app.crew.db.ConnectionManager;
import net.waqassiddiqi.app.crew.style.skin.DefaultSkin;
import net.waqassiddiqi.app.crew.ui.control.RibbonbarTabControl;
import net.waqassiddiqi.app.crew.ui.menu.MainFrameMenu;

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.desktoppane.WebDesktopPane;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.style.StyleManager;
import com.alee.utils.SwingUtils;
import com.alee.utils.ThreadUtils;

public class MainFrame extends WebFrame implements ActionListener, SwingConstants {
	private static final long serialVersionUID = 1L;
	private static MainFrame instance = null;
		
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
	
    public Component getPreview ()
    {


    	final WebTabbedPane tabbedPane2 = new WebTabbedPane ();
        
        setupTabbedPane ( tabbedPane2, "Ribbon bar" );
        
        //tabbedPane2.setTabbedPaneStyle ( TabbedPaneStyle.attached );
        GroupPanel p = new GroupPanel(tabbedPane2);
        tabbedPane2.setFocusable(false);
        return tabbedPane2;
    }

    private static void setupTabbedPane ( final JTabbedPane tabbedPane, final String text )
    {
    	
    	
    	GroupPanel gpanelV = new GroupPanel(4, createVesselsPanel());
    	
    	tabbedPane.addTab ( "Vessel", gpanelV );

    	
    	final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        

    	
        
        
    	GroupPanel gpanel = new GroupPanel(4, createFirstPanel(), separator, createSecondPanel());
    	//panel.setf
    	
        tabbedPane.addTab ( "Crew", gpanel );
        
        GroupPanel gpanelR = new GroupPanel(4, createReportPanel());
    	
    	tabbedPane.addTab ( "Vessel", gpanelV );
    	
        tabbedPane.addTab ( "Reports", gpanelR );
        //tabbedPane.addTab ( "About", createContent ( text ) );
        
        
    }
    
    public static WebPanel createReportPanel ()
    {
        final WebPanel panel = new WebPanel ();
        panel.setUndecorated ( false );
        panel.setLayout ( new BorderLayout () );
        panel.setWebColoredBackground ( false );

        final WebPanel southPanel = new WebPanel ();
        southPanel.setPaintSides ( true, false, false, false );
        setupPanel ( southPanel, SOUTH, "Generate Reports" );
        panel.add ( southPanel, BorderLayout.SOUTH );


        MainFrameMenu m = new MainFrameMenu(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
        
        WebButton ib = new WebButton ("Resting Report", m.loadIcon("menubar/report.png"));
    	
    	ib.setRolloverDecoratedOnly ( true );
    	ib.setDrawFocus ( false );
    	ib.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib.setVerticalTextPosition(AbstractButton.BOTTOM);
    	
    	final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        
        WebButton ib1 = new WebButton ("Error Log Report", m.loadIcon("menubar/logs.png"));
    	
    	ib1.setRolloverDecoratedOnly ( true );
    	ib1.setDrawFocus ( false );
    	ib1.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib1.setVerticalTextPosition(AbstractButton.BOTTOM);
        
        GroupPanel gpanel = new GroupPanel(4, ib, separator, ib1);
        
        
        panel.add ( gpanel, BorderLayout.CENTER );

        return panel;
    }
    
    public static WebPanel createVesselsPanel ()
    {
        final WebPanel panel = new WebPanel ();
        panel.setUndecorated ( false );
        panel.setLayout ( new BorderLayout () );
        panel.setWebColoredBackground ( false );

        final WebPanel southPanel = new WebPanel ();
        southPanel.setPaintSides ( true, false, false, false );
        setupPanel ( southPanel, SOUTH, "Vessel Settings" );
        panel.add ( southPanel, BorderLayout.SOUTH );


        MainFrameMenu m = new MainFrameMenu(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
        
        WebButton ib = new WebButton ("Vessel Details", m.loadIcon("menubar/ship.png"));
    	
    	ib.setRolloverDecoratedOnly ( true );
    	ib.setDrawFocus ( false );
    	ib.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib.setVerticalTextPosition(AbstractButton.BOTTOM);
    	
    	final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        
        WebButton ib1 = new WebButton ("View Crew", m.loadIcon("menubar/view_text.png"));
    	
    	ib1.setRolloverDecoratedOnly ( true );
    	ib1.setDrawFocus ( false );
    	ib1.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib1.setVerticalTextPosition(AbstractButton.BOTTOM);
        
        GroupPanel gpanel = new GroupPanel(4, ib);
        
        
        panel.add ( gpanel, BorderLayout.CENTER );

        return panel;
    }
    
    public static WebPanel createFirstPanel ()
    {
        final WebPanel panel = new WebPanel ();
        panel.setUndecorated ( false );
        panel.setLayout ( new BorderLayout () );
        panel.setWebColoredBackground ( false );

        final WebPanel southPanel = new WebPanel ();
        southPanel.setPaintSides ( true, false, false, false );
        setupPanel ( southPanel, SOUTH, "Manage Crew" );
        panel.add ( southPanel, BorderLayout.SOUTH );


        MainFrameMenu m = new MainFrameMenu(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
        
        WebButton ib = new WebButton ("Add Crew Member", m.loadIcon("menubar/crew_add.png"));
    	
    	ib.setRolloverDecoratedOnly ( true );
    	ib.setDrawFocus ( false );
    	ib.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib.setVerticalTextPosition(AbstractButton.BOTTOM);
    	
    	final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        
        WebButton ib1 = new WebButton ("View Crew", m.loadIcon("menubar/view_text.png"));
    	
    	ib1.setRolloverDecoratedOnly ( true );
    	ib1.setDrawFocus ( false );
    	ib1.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib1.setVerticalTextPosition(AbstractButton.BOTTOM);
        
        GroupPanel gpanel = new GroupPanel(4, ib, separator, ib1);
        
        
        panel.add ( gpanel, BorderLayout.CENTER );

        return panel;
    }
    
    public static WebPanel createSecondPanel ()
    {
        final WebPanel panel = new WebPanel ();
        panel.setUndecorated ( false );
        panel.setLayout ( new BorderLayout () );
        panel.setWebColoredBackground ( false );

        final WebPanel southPanel = new WebPanel ();
        southPanel.setPaintSides ( true, false, false, false );
        setupPanel ( southPanel, SOUTH, "Resting Hours" );
        panel.add ( southPanel, BorderLayout.SOUTH );


        MainFrameMenu m = new MainFrameMenu(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
        
        WebButton ib = new WebButton ("Add Resting Hours", m.loadIcon("menubar/rest_hours_add.png"));
    	
    	ib.setRolloverDecoratedOnly ( true );
    	ib.setDrawFocus ( false );
    	ib.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib.setVerticalTextPosition(AbstractButton.BOTTOM);
    	
    	final WebSeparator separator = new WebSeparator ( WebSeparator.VERTICAL );
        separator.setDrawSideLines ( true );
        separator.setDrawTrailingLine(true);
        
        WebButton ib1 = new WebButton ("View Resting Hours", m.loadIcon("menubar/view_text.png"));
    	
    	ib1.setRolloverDecoratedOnly ( true );
    	ib1.setDrawFocus ( false );
    	ib1.setHorizontalTextPosition(AbstractButton.CENTER);
    	ib1.setVerticalTextPosition(AbstractButton.BOTTOM);
        
        GroupPanel gpanel = new GroupPanel(4, ib, separator, ib1);
        
        
        panel.add ( gpanel, BorderLayout.CENTER );

        return panel;
    }
    
    private static void setupPanel ( final WebPanel panel, final int location, String text )
    {
        // Decoration settings
        panel.setUndecorated ( false );
        panel.setMargin ( new Insets ( 3, 3, 3, 3 ) );
        panel.setRound ( StyleConstants.largeRound );

        panel.add ( new WebLabel ( text, WebLabel.CENTER ) );
    }

    private static WebLabel createContent ( final String text )
    {
        final WebLabel label = new WebLabel ( text, WebLabel.CENTER );
        label.setMargin ( 5 );
        return label;
    }
	
	public MainFrame() {
		super();
		
		setTitle("Crew");
		
		WebMenuBar menuBar = new WebMenuBar ();
		final MainFrameMenu mainFramMenu = new MainFrameMenu(this);
		mainFramMenu.setUpMenuBar(menuBar);
        //setJMenuBar(menuBar);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
		final WebPanel contentPane = new WebPanel();		
		setLayout (new BorderLayout());		
		contentPane.add (createStatusBar(), BorderLayout.SOUTH);
		
		desktopPane = new WebDesktopPane();        
        desktopPane.setOpaque (true);
        desktopPane.setBackground(Color.white);
        desktopPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (SwingUtils.isRightMouseButton(e)) {
					mainFramMenu.getDynamicQuickMenu().showMenu(e.getComponent(), e.getPoint());
				}
			}
		});
        
        
        contentPane.add(desktopPane, BorderLayout.CENTER);
        contentPane.add(createRibbonBar(), BorderLayout.NORTH);
        
		add(contentPane, BorderLayout.CENTER);
		ThreadUtils.sleepSafely(500);
		pack();
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	}
	
	public void addChildDesktopContent(WebInternalFrame childFrame) {
		this.desktopPane.add(childFrame);
		this.desktopPane.getDesktopManager().activateFrame(childFrame);
	}
	
	public void activateChildDesktopFrame(WebInternalFrame childFrame) {
		this.desktopPane.getDesktopManager().activateFrame(childFrame);
	}
	
	private WebStatusBar createStatusBar() {
		final WebStatusBar statusBar = new WebStatusBar();
		statusBar.addSpacing();

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
	public void actionPerformed(ActionEvent e) {
		if ("exit".equals(e.getActionCommand())) {
			System.exit(0);
        } else if ("addCrew".equals(e.getActionCommand())) {
        	final WebInternalFrame internalFrame = new WebInternalFrame ( "Add Crew", true, true, true, true );
        	JLabel label = new JLabel ( "Add new crew member from here", JLabel.CENTER );
            label.setOpaque ( false );
            internalFrame.add ( label );
            desktopPane.add ( internalFrame );
            internalFrame.open ();
            internalFrame.setBounds ( 0, 0, 300, 300 );
        } else if ("vesselDetails".equals(e.getActionCommand())) {
        	final WebInternalFrame internalFrame = new WebInternalFrame ( "Vessel Details", true, true, true, true );
        	JLabel label = new JLabel ( "Vessel Details", JLabel.CENTER );
            label.setOpaque ( false );
            internalFrame.add ( label );
            desktopPane.add ( internalFrame );
            internalFrame.open ();
            internalFrame.setBounds ( 0, 0, 300, 300 );
        }
	}
}
