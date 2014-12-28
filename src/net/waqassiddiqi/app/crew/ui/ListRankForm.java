package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.waqassiddiqi.app.crew.controller.RankFactory;
import net.waqassiddiqi.app.crew.db.RankDAO;
import net.waqassiddiqi.app.crew.model.Rank;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;

public class ListRankForm extends BaseForm implements ActionListener {
	
	private RanksTableModel tableModel;
	private WebTabbedPane tabPan;
	private WebTable table;
	
	private Object[][] data;
	private String[] columnNames = { "S.No.", "Rank" };
	
	List<Rank> listRank;
	
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
		            JPopupMenu popup = getActionMenu(listRank.get(rowindex).getId(), rowindex);
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
		
		return scrollPane;
	}
	
	private WebPopupMenu getActionMenu(int rankId, int selectedRowIndex) {
		final WebPopupMenu popupMenu = new WebPopupMenu ();
		popupMenu.add(getPopupMeunuItem("Edit details", Integer.toString(rankId), selectedRowIndex));
		popupMenu.add(getPopupMeunuItem("Edit rest hour template", Integer.toString(rankId), selectedRowIndex));
        
        return popupMenu;
	}
	
	private WebMenuItem getPopupMeunuItem(String name, String rankId, int rowIndex) {
		WebMenuItem item = new WebMenuItem (name);
		item.addActionListener(ListRankForm.this);
		item.putClientProperty("rankId", rankId);
		item.putClientProperty("rowIndex", rowIndex);
		
		return item;
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
		
		listRank = new RankDAO().getAll();
		this.data = new Object[listRank.size()][columnNames.length];
		
		for(int i=0; i<listRank.size(); i++) {
			data[i][0] = i+1;
			data[i][1] = listRank.get(i).getRank();
		}
		
		return this.data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof WebMenuItem) {
			WebMenuItem item = (WebMenuItem) e.getSource();
			
			if(item.getActionCommand().equals("Edit details")) {
				getOwner().addContent(RankFactory.getInstance().getEdit((String) item.getClientProperty("rankId")));
			} else if(item.getActionCommand().equals("Edit rest hour template")) {
				Map<String, Object> params = new HashMap<String , Object>();
				params.put("defaultView", 1);
				
				getOwner().addContent(RankFactory.getInstance().getEdit((String) item.getClientProperty("rankId"), params));
			}
		} else if(e.getSource() instanceof WebButton) {
			WebButton btnSource = (WebButton) e.getSource();
			
			if(btnSource.getClientProperty("command").equals("save")) {
				RankDAO rankDao = new RankDAO();
				
				for(int i=0; i<listRank.size(); i++)
					rankDao.updateRank(listRank.get(i));
				
				NotificationManager.showNotification("Rank data has been saved");
			}
		}
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
			
			if(col == 1) {
				for(int i=0; i<listRank.size(); i++) {
					if(i != row) {
						if(listRank.get(i).getRank().equals(value.toString())) {
							
							Component comp = table.getDefaultRenderer(tableModel.getColumnClass(col)).getTableCellRendererComponent(table, longValues[col], false,
											false, 0, col);
							
							NotificationManager.showPopup(getOwner(), comp, new String[] { "This passport number is already associated with other member of crew" });
							return;
						}
					}
				}
			}
			
			data[row][col] = value;
			
			listRank.get(row).setRank(value.toString());
			
			fireTableCellUpdated(row, col);
		}
	}
}