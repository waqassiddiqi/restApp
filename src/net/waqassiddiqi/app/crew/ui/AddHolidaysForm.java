package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.SwingConstants;

import com.alee.extended.date.WebDateField;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;

public class AddHolidaysForm extends BaseForm implements ActionListener {
	private int id = -1;
	
	private WebTextField txtHolidayName;
	private WebDateField txtValidFrom;
	private WebDateField txtValidTo;

	private WebTabbedPane tabPan;
	
	private int defaultTabIndex = 0;
	
	public AddHolidaysForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddHolidaysForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
	}
	
	public void setDefaultTabIndex(int index) {
		this.defaultTabIndex = index;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Overtime / Holidays") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component prepareView() {
		
		tabPan = new WebTabbedPane () {
		
			@Override
			public void setSelectedIndex(int newIndex) {
				super.setSelectedIndex(newIndex);
			}
			
		};
		
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        
        WebScrollPane scrollPane = new WebScrollPane(new GroupPanel(false, new WebLabel("List of days")).setMargin(5));
        
        tabPan.addTab("  Holiday Details   ", getForm());
        tabPan.addTab("  Holiday Dates ", scrollPane);
        
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
		
		txtValidFrom = new WebDateField ();
		txtValidFrom.setInputPrompt ("Select date...");
		txtValidFrom.setDate(new Date());
		txtValidFrom.setInputPromptPosition (SwingConstants.CENTER);
		
		txtValidTo = new WebDateField ();
		txtValidTo.setInputPrompt ("Select date...");
		txtValidTo.setDate(new Date());
		txtValidTo.setInputPromptPosition (SwingConstants.CENTER);
		
		
		txtHolidayName = new WebTextField(15);

		content.add(new WebLabel("Holiday Name", WebLabel.TRAILING), "0,1");
		content.add(txtHolidayName, "1,1");
		
		content.add(new WebLabel("Valid From", WebLabel.TRAILING), "0,2");
		content.add(txtValidFrom, "1,2");
		
		content.add(new WebLabel("Valid Till", WebLabel.TRAILING), "0,3");
		content.add(txtValidTo, "1,3");
		
		return content;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("new")) {
			
			save();
			
			this.id = -1;
			
			txtHolidayName.setText("");
			txtValidFrom.setText("");
			txtValidTo.setText("");
									
		} else if(btnSource.getClientProperty("command").equals("save")) {
			save();
		}
	}
	
	private boolean save() {
		return false;
	}
}