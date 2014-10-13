package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import net.waqassiddiqi.app.crew.controller.CrewFactory;
import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.RankDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Rank;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import org.apache.commons.lang.ArrayUtils;

import com.alee.extended.date.WebDateField;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.alee.utils.swing.WebDefaultCellEditor;

public class ListCrewForm extends BaseForm implements ActionListener, TableModelListener, KeyListener {
	
	private CrewTableModel tableModel;
	private WebTabbedPane tabPan;
	private WebTable table;
	private CrewDAO crewDao;
	private SimpleDateFormat sdf;
	private Object[][] data;
	private String[] columnNames = { "S.No.", "First Name", "Last Name", "Rank", "Nationality", 
			"Passport", "SignOn Date", "Watch Keeper", "Is Active" };
	private List<Crew> crewList = new ArrayList<Crew>();
	private Boolean[] listModifiedCrewIndex;
	private WebTextField txtNameFilter;
	private WebTextField txtLastNameFilter;
	TableRowSorter<CrewTableModel> mSorter;
	private WebCheckBox chkActive;
	
	public ListCrewForm(MainFrame owner) {
		super(owner);
		crewDao = new CrewDAO();
		sdf = new SimpleDateFormat("dd.MM.yyyy");
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Crew List") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
		
		getToolbar().add(new WebLabel("Filter: ") {{ setMargin(1); }});
		
		txtNameFilter = new WebTextField(15);
		txtNameFilter.addKeyListener(ListCrewForm.this);
		
		txtLastNameFilter = new WebTextField(15);
		txtLastNameFilter.addKeyListener(ListCrewForm.this);
		
		chkActive = new WebCheckBox("Is Active?");
		chkActive.setSelected(true);
		chkActive.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				filter();
			}
		});
		
		GroupPanel gp = new GroupPanel(10, new WebLabel("First Name") {{ setDrawShade(true); }}, 
				txtNameFilter, 
				new WebLabel("Last Name") {{ setDrawShade(true); }}, 
				txtLastNameFilter, chkActive);
		
		getToolbar().add(gp);
	}
	
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  List of Crew  ", getGrid());
        
        return tabPan;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Component getGrid() {
		tableModel = new CrewTableModel(getData(), columnNames);
		tableModel.addTableModelListener(this);
		
		
		mSorter = new TableRowSorter<CrewTableModel>(tableModel);
		
		table = new WebTable(tableModel);
		
		table.setRowSorter(mSorter);
		
		TableColumn column = table.getColumnModel().getColumn(3);
		WebComboBox cmbRank = new WebComboBox ();
		List<Rank> listRanks = new RankDAO().getAll();
		for(Rank r : listRanks) {
			cmbRank.addItem(r.getRank());
		}
		column.setCellEditor (new WebDefaultCellEditor(cmbRank));
		
		
		column = table.getColumnModel().getColumn(6);
		WebDateField dateField = new WebDateField ();
        dateField.setHorizontalAlignment ( SwingConstants.LEFT );
        dateField.setDateFormat(sdf);
		column.setCellEditor (new WebDefaultCellEditor(dateField));
		
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
		            JPopupMenu popup = getActionMenu(crewList.get(rowindex).getId(), rowindex);
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
		
		return scrollPane;
	}
	
	private void filter() {
	    RowFilter<CrewTableModel, Object> rf = null;
	    ArrayList<RowFilter<Object, Object>> andFilters = new ArrayList<RowFilter<Object, Object>>();
	    
	    try {

	    	//if(!txtNameFilter.getText().isEmpty() && !txtLastNameFilter.getText().isEmpty()) {
	    			    		
	    		andFilters.add(RowFilter.regexFilter("^(?i)" + txtNameFilter.getText(), 1));
	    		andFilters.add(RowFilter.regexFilter("^(?i)" + txtLastNameFilter.getText(), 2));
	    		
	    		RowFilter<Object, Object> activeFilter = new RowFilter<Object, Object>() {

	                @Override
	                public boolean include(Entry<? extends Object, ? extends Object> entry) {
	                    Boolean bol = (Boolean) entry.getValue(8);
	                    return bol.booleanValue() == chkActive.isSelected();
	                }
	            };
	            
	            andFilters.add(activeFilter);
	    		
	    	//} else {
	    	//	andFilters.add(RowFilter.regexFilter("(?i)" + value, columnIndex));
	    	//}
	    	
	    	rf = RowFilter.andFilter(andFilters);
	    	
	    } catch (java.util.regex.PatternSyntaxException e) {
	        return;
	    }
	    
	    mSorter.setRowFilter(rf);
	}
	
	private WebPopupMenu getActionMenu(int crewId, int selectedRowIndex) {
		final WebPopupMenu popupMenu = new WebPopupMenu ();
		popupMenu.add(getPopupMeunuItem("Edit details", Integer.toString(crewId), selectedRowIndex));
		popupMenu.add(getPopupMeunuItem("Edit rest hour template", Integer.toString(crewId), selectedRowIndex));
		popupMenu.add(getPopupMeunuItem("Delete", Integer.toString(crewId), selectedRowIndex));
        
        return popupMenu;
	}
	
	private WebMenuItem getPopupMeunuItem(String name, String crewId, int rowIndex) {
		WebMenuItem item = new WebMenuItem (name);
		item.addActionListener(ListCrewForm.this);
		item.putClientProperty("crewId", crewId);
		item.putClientProperty("rowIndex", rowIndex);
		
		return item;
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
		listModifiedCrewIndex = new Boolean[crewList.size()];
		Arrays.fill(listModifiedCrewIndex, Boolean.FALSE);
		
		this.data = new Object[crewList.size()][columnNames.length];
		
		
		for(int i=0; i<crewList.size(); i++) {
			data[i][0] = i + 1;
			data[i][1] = crewList.get(i).getFirstName();
			data[i][2] = crewList.get(i).getLastName();
			data[i][3] = crewList.get(i).getRank();
			data[i][4] = crewList.get(i).getNationality();
			data[i][5] = crewList.get(i).getPassportNumber();
			data[i][6] = sdf.format(crewList.get(i).getSignOnDate());
			data[i][7] = crewList.get(i).isWatchKeeper();
			data[i][8] = crewList.get(i).isActive();
		}
		
		return this.data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof WebMenuItem) {
			WebMenuItem item = (WebMenuItem) e.getSource();
			
			if(item.getActionCommand().equals("Edit details")) {
				getOwner().addContent(CrewFactory.getInstance().getEdit((String) item.getClientProperty("crewId")));
			} else if(item.getActionCommand().equals("Edit rest hour template")) {
				Map<String, Object> params = new HashMap<String , Object>();
				params.put("defaultView", 1);
				
				getOwner().addContent(CrewFactory.getInstance().getEdit((String) item.getClientProperty("crewId"), params));
			} else {
				int returnCode = WebOptionPane.showConfirmDialog(getOwner(), "Are you sure to delete select crew?", "Confirm", 
						WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);
				
				if(returnCode == 0) {
					crewDao.delete(Integer.parseInt(item.getClientProperty("crewId").toString()));
					
					NotificationManager.showNotification("Crew has been deleted");
					tableModel.deleteRow(Integer.parseInt(item.getClientProperty("rowIndex").toString()));
				}
			}
			
			
		} else if(e.getSource() instanceof WebButton) {
			WebButton btnSource = (WebButton) e.getSource();
			
			if(btnSource.getClientProperty("command").equals("save")) {
				for(int i=0; i<listModifiedCrewIndex.length; i++) {
					if(listModifiedCrewIndex[i]) {
						crewDao.updateCrew(crewList.get(i));
					}
				}
				
				NotificationManager.showNotification("Crew data has been saved");
			}
		}
	}
	
	public class CrewTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public final Object[] longValues = { 0, "", "", "", "", "", "", Boolean.FALSE, Boolean.TRUE };
		
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

		public void deleteRow(int row) {
			data = (Object[][]) ArrayUtils.remove(data, row);
			
			fireTableRowsDeleted(row, row);
		}
		
		public <T> List<T> twoDArrayToList(T[][] twoDArray) {
		    List<T> list = new ArrayList<T>();
		    for (T[] array : twoDArray) {
		        list.addAll(Arrays.asList(array));
		    }
		    return list;
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
			
			
			Crew c = crewList.get(row);
			
			if(col == 5) {
				
				Component comp = table.getDefaultRenderer(tableModel.getColumnClass(col))
						.getTableCellRendererComponent(table, longValues[col], false,
								false, 0, col);
				
				if(crewDao.isPassportExists(value.toString().trim().trim(), c.getId())) {
					NotificationManager.showPopup(getOwner(), comp, new String[] { "This passport number is already associated with other member of crew" });
					return;
				}
			}
			
			data[row][col] = value;
			
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
				try {
					c.setSignOnDate(sdf.parse((String) value));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if(col == 7) {
				c.setWatchKeeper((boolean) value);
			} else if(col == 8) {
				c.setActive((boolean) value);
			}
			
			listModifiedCrewIndex[row] = true;
			
			fireTableCellUpdated(row, col);
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();    
        
        Crew c = crewList.get(row);
        if(crewDao.updateCrew(c) > 0) {
        	listModifiedCrewIndex[row] = false;
        }
        
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		filter();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}