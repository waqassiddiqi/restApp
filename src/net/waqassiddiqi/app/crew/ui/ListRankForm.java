package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.waqassiddiqi.app.crew.db.RankDAO;
import net.waqassiddiqi.app.crew.model.Rank;

import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;

public class ListRankForm extends BaseForm implements ActionListener {
	
	private RanksTableModel tableModel;
	private WebTabbedPane tabPan;
	private WebTable table;
	
	private Object[][] data;
	private String[] columnNames = { "S.No.", "Rank" };
	
	public ListRankForm(MainFrame owner) {
		super(owner);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("List of Ranks") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  List of Ranks  ", getGrid());
        
        return tabPan;
	}
	
	private Component getGrid() {
		tableModel = new RanksTableModel(getData(), columnNames);
		table = new WebTable(tableModel);
		
		WebScrollPane scrollPane = new WebScrollPane(table);
		
		initColumnSizes(table);
		
		table.setFocusable(false);
		
		return scrollPane;
	}
	
	private void initColumnSizes(JTable table) {
		RanksTableModel model = (RanksTableModel) table.getModel();
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
		
		List<Rank> list = new RankDAO().getAll();
		this.data = new Object[list.size()][columnNames.length];
		
		for(int i=0; i<list.size(); i++) {
			data[i][0] = i+1;
			data[i][1] = list.get(i).getRank();
		}
		
		return this.data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
	public class RanksTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public final Object[] longValues = { 0, "" };
		
		public RanksTableModel(Object[][] data, Object[] columnNames) {
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
			fireTableCellUpdated(row, col);
		}
	}
}