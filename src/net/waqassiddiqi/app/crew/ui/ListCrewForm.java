package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;

public class ListCrewForm extends BaseForm implements ActionListener {
	
	private CrewTableModel tableModel;
	private WebTabbedPane tabPan;
	private WebTable table;
	private CrewDAO crewDao;
	
	private Object[][] data;
	private String[] columnNames = { "ID", "First Name", "Last Name", "Rank", "Nationality", 
			"Passport", "SignOn Date", "Watch Keeper" };
	private List<Crew> crewList = new ArrayList<Crew>();
	
	
	public ListCrewForm(MainFrame owner) {
		super(owner);
		crewDao = new CrewDAO();
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Crew List") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  List of Crew  ", getGrid());
        
        return tabPan;
	}
	
	private Component getGrid() {
		tableModel = new CrewTableModel(getData(), columnNames);
		table = new WebTable(tableModel);
		
		WebScrollPane scrollPane = new WebScrollPane(table);
		
		initColumnSizes(table);
		
		table.setFocusable(false);
		
		return scrollPane;
	}
	
	private void initColumnSizes(JTable table) {
		CrewTableModel model = (CrewTableModel) table.getModel();
		TableColumn column;
		Component comp;
		int headerWidth;
		int cellWidth;
		Object[] longValues = model.longValues;
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < model.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null,
					column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i))
					.getTableCellRendererComponent(table, longValues[i], false,
							false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}
	
	private Object[][] getData() {
		this.data = null;
		
		crewList = crewDao.getAll();
		this.data = new Object[crewList.size()][columnNames.length];
		
		for(int i=0; i<crewList.size(); i++) {
			data[i][0] = crewList.get(i).getId();
			data[i][1] = crewList.get(i).getFirstName();
			data[i][2] = crewList.get(i).getLastName();
			data[i][3] = crewList.get(i).getRank();
			data[i][4] = crewList.get(i).getNationality();
			data[i][5] = crewList.get(i).getPassportNumber();
			data[i][6] = crewList.get(i).getSignOnDate();
			data[i][7] = crewList.get(i).isWatchKeeper();
		}
		
		return this.data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("save")) {
			for(int i=0; i<crewList.size(); i++) {
				crewDao.updateCrew(crewList.get(i));
			}
			
			NotificationManager.showNotification("Crew details has been updated.");
		}
	}
	
	public class CrewTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public final Object[] longValues = { 0, "", "", "", "", "", "", Boolean.FALSE };
		
		public CrewTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int c) {
			return longValues[c].getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col >= 1;
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			
			Crew c = crewList.get(row);
			if(col == 1) {
				c.setFirstName(value.toString().trim());
			} else if(col == 2) {
				c.setLastName(value.toString().trim());
			} else if(col == 3) {
				c.setRank(value.toString().trim());
			} else if(col == 4) {
				c.setNationality(value.toString().trim());
			} else if(col == 5) {
				c.setPassportNumber(value.toString().trim());
			} else if(col == 6) {
				c.setRank(value.toString().trim());
			}  else if(col == 7) {
				c.setWatchKeeper((boolean) value);
			}
			
			fireTableCellUpdated(row, col);
		}
	}
}