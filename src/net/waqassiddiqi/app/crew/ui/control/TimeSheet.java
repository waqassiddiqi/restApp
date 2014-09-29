package net.waqassiddiqi.app.crew.ui.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import net.waqassiddiqi.app.crew.ui.control.TimeBlock.BlockType;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.separator.WebSeparator;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.SwingUtils;

public class TimeSheet {
	
	protected BlockType selectedBlockType;
	protected Date[] dates;
	protected Date startDate = null;
	protected int rows = 1;
	protected Boolean[] scheduleList = new Boolean[48];
	protected TimeBlock[] timeBlocks = new TimeBlock[48];
	private boolean showLegend = true;
	private int blockSize = 25;
	private Component view;
	private float totalRest = 0.0f;
	private float totalWork = 24.0f;
	private String title = "";
	
	private ChangeListener changeListener = null;
	
	public TimeSheet(int blockSize) {
		this(blockSize, BlockType.WORK, "", true, 1, new Date());
	}
	
	public TimeSheet(int blockSize, boolean showLegend, String title) {
		this(blockSize, BlockType.WORK, title, showLegend, 1, new Date());
	}
	
	public TimeSheet(int blockSize, BlockType blockType, String title, boolean showLegend, int days, Date startDate) {
		this.selectedBlockType = blockType;
		rows = days;
		this.startDate = startDate;
		this.showLegend = showLegend;
		this.blockSize = blockSize;
		this.title = title;
		
		initView();
	}
	
	public Boolean[] getSchedule() {
		return scheduleList;
	}
	
	@SuppressWarnings("serial")
	private void initView() {
		if(this.showLegend) {
			final GroupPanel legendPanel = new GroupPanel(1, false, getLegendPanel()); 
			final GroupPanel titlePanel = new GroupPanel ( GroupingType.fillFirst, 5, legendPanel);
			
			if(this.title != null && title.trim().length() > 0)
				view = new GroupPanel(GroupingType.fillLast, 0, false, new WebLabel(title) {{ setDrawShade(true); }}, getGrid(rows), titlePanel.setMargin(6)).setMargin(10);
			else
				view = new GroupPanel(GroupingType.fillLast, 0, false, getGrid(rows), titlePanel.setMargin(6)).setMargin(10);
		} else {
			if(this.title != null && title.trim().length() > 0)
				view = new GroupPanel(GroupingType.fillLast, 0, false, new WebLabel(title) {{ setDrawShade(true); }}, getGrid(rows)).setMargin(10);
			else
				view = new GroupPanel(GroupingType.fillLast, 0, false, getGrid(rows)).setMargin(10);
		}
	}
	
	public Component getView() {
		if(view == null)
			initView();
		
		return view;
	}
	
	private Component getLegendPanel() {
        
        TimeBlock workBlock = new TimeBlock(blockSize, blockSize, BlockType.WORK);
		TooltipManager.setTooltip ( workBlock, "Double click to select", TooltipWay.down );
		workBlock.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { 
				TimeSheet.this.selectedBlockType = ((TimeBlock) e.getComponent()).getBlockType();
			}
			
			@Override
			public void mouseExited(MouseEvent e) { }
			
			@Override
			public void mouseEntered(MouseEvent e) { }
			
			@Override
			public void mouseClicked(MouseEvent e) { }
		});
		
		TimeBlock restBlock = new TimeBlock(blockSize, blockSize, BlockType.REST);
		TooltipManager.setTooltip ( restBlock, "Double click to select", TooltipWay.down );
		restBlock.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { 
				TimeSheet.this.selectedBlockType = ((TimeBlock) e.getComponent()).getBlockType();
			}
			
			@Override
			public void mouseExited(MouseEvent e) { }
			
			@Override
			public void mouseEntered(MouseEvent e) { }
			
			@Override
			public void mouseClicked(MouseEvent e) { }
		});
		
		return new GroupPanel(4, true, 
				new GroupPanel(2, true, workBlock , new WebLabel("Work")),
				new GroupPanel(2, true, restBlock, new WebLabel("Rest"))
				);
	}
	
	private Component getGrid(int rowsCount) {
		
		Arrays.fill(scheduleList, Boolean.TRUE);
		
		final WebPanel groupPanel = new WebPanel();
		groupPanel.putClientProperty(SwingUtils.HANDLES_ENABLE_STATE, true);
		groupPanel.setOpaque(true);
    	
		dates = new Date[rowsCount];
		
		int size = rowsCount + 1;
		
		final int rowsAmount = size > 1 ? size * 2 - 1 : 1;
        final double[] rows = new double[ 6 + rowsAmount ];
        rows[ 0 ] = TableLayout.FILL;
        rows[ 1 ] = 20;
        rows[ 2 ] = TableLayout.PREFERRED;
		for (int i = 3; i < rows.length - 3; i++) {
			rows[i] = TableLayout.PREFERRED;
		}
		
        //rows[ rows.length - 3 ] = TableLayout.PREFERRED;
        //rows[ rows.length - 2 ] = 20;
        //rows[ rows.length - 1 ] = TableLayout.FILL;

        final double[] columns = new double[26];
        
        for(int i=0; i<26; i++) {
        	columns[i] = TableLayout.PREFERRED;
        }
        
		final TableLayout groupLayout = new TableLayout(new double[][] { columns, rows });
		groupLayout.setHGap(2);
		groupLayout.setVGap(2);
		groupPanel.setLayout(groupLayout);
		
		//groupPanel.add ( createVerticalSeparator() , "1,0,1," + ( rows.length - 1 ) );
		groupPanel.add (createHorizontalSeparator(), "0,2," + ( columns.length - 1 ) + ",2" );
		
		int row = 3;
		
		Calendar cal = Calendar.getInstance();        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for(int i=2; i<26; i++) {
        	groupPanel.add(getLabel("|" + (i-2)), i + ",1");
        }
        
        MouseAdapter ma = new MouseAdapter() {
        	@Override
			public void mousePressed(MouseEvent e) {
				if(((TimeBlock) e.getComponent()).getBlockType() == BlockType.REST) {
					TimeSheet.this.selectedBlockType = BlockType.WORK;
					
					//TimeSheet.this.scheduleList.get(
					//		((TimeBlock) e.getComponent()).getRowId())[((TimeBlock) e.getComponent()).getId()] = true;
					
				} else {
					TimeSheet.this.selectedBlockType = BlockType.REST;
					
					//TimeSheet.this.scheduleList.get(
					//		((TimeBlock) e.getComponent()).getRowId())[((TimeBlock) e.getComponent()).getId()] = false;
				}
				
				
			}
		};
        
        for(int i=1; i<size; i++) {        	        	
        	int blockId = 0;
        	for(int j=2; j<26; j++) {
        		TimeBlock firstBlock = new TimeBlock (this.blockSize, this.blockSize);
        		firstBlock.setId(blockId);
        		firstBlock.setRowId(i-1);
        		firstBlock.addMouseListener(ma);
        		
        		timeBlocks[blockId++] = firstBlock;
        		
        		TimeBlock secondBlock = new TimeBlock(this.blockSize, this.blockSize);
        		secondBlock.setId(blockId);
        		secondBlock.setRowId(i-1);
        		secondBlock.addMouseListener(ma);
        		
        		timeBlocks[blockId++] = secondBlock;
        		
        		new TimeBlockDragDropTargetListener(firstBlock);
        		new TimeBlockDragDropTargetListener(secondBlock);
        		
        		GroupPanel dropGroup = new GroupPanel ( 0, firstBlock, secondBlock);	
                groupPanel.add(dropGroup, j + "," + row);
        	}
        	
        	
        	if ( row > 3 ) {
            //    groupPanel.add ( createHorizontalSeparator () ,
            //           "0," + ( row - 1 ) + "," + ( columns.length - 1 ) + "," + ( row - 1 ), 0 );
            }
        	
        	row += 1;
        }
		
        groupPanel.setOpaque(false);
        
        return groupPanel;
	}
	
	private static WebSeparator createHorizontalSeparator() {
		final WebSeparator separator = new WebSeparator(WebSeparator.HORIZONTAL);
		separator.setDrawSideLines(true);
		separator.setDrawTrailingLine(true);
		return separator;
	}

	private static Component getLabel(final String text) {

		final WebLabel titleLabel = new WebLabel(text);
		titleLabel.setDrawShade(true);

		return titleLabel;
	}
	
	public class TimeBlockDragDropTargetListener extends DropTargetAdapter {
		@SuppressWarnings("unused")
		private DropTarget dropTarget;
		private TimeBlock component;
		
		public TimeBlockDragDropTargetListener(TimeBlock component) {
			this.component = component;
			dropTarget = new DropTarget(component, DnDConstants.ACTION_COPY, this, true, null);
		}
        
		@Override
		public void drop(DropTargetDropEvent dtde) {
		}
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			
			if(component.getBlockType() != selectedBlockType) {
				component.setBlockType(selectedBlockType);
				component.repaint();
				
				if(component.getBlockType() == BlockType.WORK) {
					totalRest -= 0.5;
					totalWork = 24 - totalRest;
				} else {
					totalWork -= 0.5;
					totalRest = 24 - totalWork;
				}
			}
			
			int blockId = component.getId();
			boolean bWork = false;
			
			if(selectedBlockType == BlockType.WORK) {
				bWork = true;
			}
			
			setSchedule(blockId, bWork);
		}	
	}
	
	public void setSchedule(Boolean[] scheduleArray) {
		BlockType type = BlockType.REST;
		
		for(int i=0; i<scheduleArray.length; i++) {
			if(scheduleArray[i]) {
				type = BlockType.WORK;
			} else {
				type = BlockType.REST;
			}
			
			if(timeBlocks[i].getBlockType() != type) {
				timeBlocks[i].setBlockType(type);
				timeBlocks[i].repaint();
				
				if(scheduleArray[i]) {
					totalRest -= 0.5;
					totalWork = 24 - totalRest;
				} else {
					totalWork -= 0.5;
					totalRest = 24 - totalWork;
				}
			}
			
			setSchedule(timeBlocks[i].getId(), scheduleArray[i]);
		}
	}
	
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	
	public float getTotalRest() {
		return this.totalRest;
	}
	
	public float getTotalWork() {
		return this.totalWork;
	}
	
	private void setSchedule(int blockId, boolean isWork) {
		this.scheduleList[blockId] = isWork;
		
		if(this.changeListener != null)
			changeListener.changed(totalRest, totalWork);
	}
	
	public void setChangeListener(ChangeListener listener) {
		this.changeListener = listener;
	}
	
	public interface ChangeListener {
		public void changed(float totalRest, float totalWork);
	}
}