package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;

import com.alee.extended.menu.DynamicMenuType;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.utils.swing.IntTextDocument;

public class ListVesselFrame extends WebInternalFrame {
	private static final long serialVersionUID = 1L;

	private WebComboBox type;
    private WebComboBox hidingType;
    private WebTextField radius;
    private WebTextField itemsAmount;
    private WebCheckBox drawBorder;
	private VesselsTableModel tableModel;
	private WebTable table;
	private IconsHelper iconsHelper = new IconsHelper();
	private Object[][] data;
	private String[] columnNames = { "ID", "Name", "IMO", "Flag" };
	
	public ListVesselFrame(final MainFrame owner, String title, boolean resizeable, boolean closeable,
			boolean maximizeable, boolean iconfiable) {
		
		super(title, resizeable, closeable, maximizeable, iconfiable);
		
		setDefaultCloseOperation(WebInternalFrame.DISPOSE_ON_CLOSE);
		
		add(getView());
	}
	
	private Component getView() {
		type = new WebComboBox(DynamicMenuType.values(),
				DynamicMenuType.shutter);
		final GroupPanel tg = new GroupPanel(5, new WebLabel(
				"Display animation:"), type);

		hidingType = new WebComboBox(DynamicMenuType.values(),
				DynamicMenuType.star);
		final GroupPanel htg = new GroupPanel(5,
				new WebLabel("Hide animation:"), hidingType);

		radius = new WebTextField(new IntTextDocument(), "70", 4);
		final GroupPanel rg = new GroupPanel(5, new WebLabel("Menu radius:"),
				radius);

		itemsAmount = new WebTextField(new IntTextDocument(), "5", 4);
		final GroupPanel iag = new GroupPanel(5, new WebLabel("Items amount:"),
				itemsAmount);

		drawBorder = new WebCheckBox("Show custom border", true);

		
		tableModel = new VesselsTableModel(getData(), columnNames);
		
		table = new WebTable(tableModel);
		
		table.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseReleased(MouseEvent e) {
		        int r = table.rowAtPoint(e.getPoint());
		        if (r >= 0 && r < table.getRowCount()) {
		            table.setRowSelectionInterval(r, r);
		        } else {
		            table.clearSelection();
		        }

		        int rowindex = table.getSelectedRow();
		        if (rowindex < 0)
		            return;
		        if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
		            JPopupMenu popup = getActionMenu();
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
		
		WebScrollPane scrollPane = new WebScrollPane(table);
		
		final WebPanel clickPanel = new WebPanel(true);
		clickPanel.add(scrollPane);

		final WebButton restore = new WebButton("Refresh",
				iconsHelper.loadIcon(IconsHelper.class,
						"common/restore_16x16.png"), new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						refreshData();
					}
				});
		
		final WebButton btnSaveChanges = new WebButton("Save",
				iconsHelper.loadIcon(IconsHelper.class,
						"common/saveall_16x16.png"), new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
					}
				});
		
		
		final GroupPanel titlePanel = new GroupPanel (GroupingType.fillFirst, 5, new WebLabel("<html><b>Vessels List</b></html"), 
				btnSaveChanges, restore);
		
		initColumnSizes (table);
		
		return new GroupPanel(GroupingType.fillLast, 10, false, titlePanel, clickPanel).setMargin(10);
	}
	
	public void refreshData() {
		getData();
		this.tableModel.fireTableDataChanged();
	}
	
	private void initColumnSizes(JTable table) {
		VesselsTableModel model = (VesselsTableModel) table.getModel();
		TableColumn column;
		Component comp;
		int headerWidth;
		int cellWidth;
		Object[] longValues = model.longValues;
		TableCellRenderer headerRenderer = table.getTableHeader()
				.getDefaultRenderer();

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
		
		List<Vessel> list = new VesselDAO().getAll();
		this.data = new Object[list.size()][columnNames.length];
		
		for(int i=0; i<list.size(); i++) {
			data[i][0] = list.get(i).getId();
			data[i][1] = list.get(i).getName();
			data[i][2] = list.get(i).getImo();
			data[i][3] = list.get(i).getFlag();
		}
		
		return this.data;
	}
	
	private WebPopupMenu getActionMenu() {
		final WebPopupMenu popupMenu = new WebPopupMenu ();
        popupMenu.add(new WebMenuItem ("Delete", WebLookAndFeel.getIcon(16), Hotkey.ALT_X));
        popupMenu.addSeparator ();
        popupMenu.add (new WebMenuItem("View Crew"));
        
        return popupMenu;
	}
	
	public class VesselsTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public final Object[] longValues = { 0, "", "", "" };
		
		public VesselsTableModel(Object[][] data, Object[] columnNames) {
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