package net.waqassiddiqi.app.crew.ui.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	
	public TimeSheet() {
		this.selectedBlockType = BlockType.WORK;
	}
	
	public Component getView() {
		
		
		
		final GroupPanel legendPanel = new GroupPanel(1, false, getLegendPanel()); 
		final GroupPanel titlePanel = new GroupPanel ( GroupingType.fillFirst, 5, legendPanel);
		
		return new GroupPanel ( GroupingType.fillLast, 10, false, getGrid(2), titlePanel.setMargin(6) ).setMargin ( 10 );
	}
	
	private Component getLegendPanel() {;;
        
        TimeBlock workBlock = new TimeBlock(25, 25, BlockType.WORK);
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
		
		TimeBlock restBlock = new TimeBlock(25, 25, BlockType.REST);
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
		final WebPanel groupPanel = new WebPanel();
		groupPanel.putClientProperty(SwingUtils.HANDLES_ENABLE_STATE, true);
		groupPanel.setOpaque(true);
    	
		int size = rowsCount;
		
		final int rowsAmount = size > 1 ? size * 2 - 1 : 1;
        final double[] rows = new double[ 6 + rowsAmount ];
        rows[ 0 ] = TableLayout.FILL;
        rows[ 1 ] = 20;
        rows[ 2 ] = TableLayout.PREFERRED;
        for ( int i = 3; i < rows.length - 3; i++ )
        {
            rows[ i ] = TableLayout.PREFERRED;
        }
        rows[ rows.length - 3 ] = TableLayout.PREFERRED;
        rows[ rows.length - 2 ] = 20;
        rows[ rows.length - 1 ] = TableLayout.FILL;

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
        
        for(int i=1; i<size; i++) {        	        	
        	//groupPanel.add(getLabel(sdf.format(cal.getTime())), 0 + "," + row);        	
        	//cal.add(Calendar.DAY_OF_MONTH, 1);
        	
        	
        	for(int j=2; j<26; j++) {
        		//WebToggleButton b1 = new WebToggleButton(loadIcon("buttons/spacerL.JPG"));    		
        		//b1.setSelectedIcon(loadIcon("buttons/gray.png"));
        		
        		//WebToggleButton b2 = new WebToggleButton(loadIcon("buttons/spacerL.JPG"));    		
        		//b2.setSelectedIcon(loadIcon("buttons/gray.png"));
        		
        		
                //iconsGroup.setButtonsDrawFocus (false);
        		TimeBlock x321 = new TimeBlock ( 25, 25 );
        		
        		x321.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						if(((TimeBlock) e.getComponent()).getBlockType() == BlockType.REST) {
							TimeSheet.this.selectedBlockType = BlockType.WORK;
						} else {
							TimeSheet.this.selectedBlockType = BlockType.REST;
						}
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
        		
        		TimeBlock x322 = new TimeBlock ( 25, 25 );
        		x322.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						if(((TimeBlock) e.getComponent()).getBlockType() == BlockType.REST) {
							TimeSheet.this.selectedBlockType = BlockType.WORK;
						} else {
							TimeSheet.this.selectedBlockType = BlockType.REST;
						}
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});

        		
        		
        		new TimeBlockDragDropTargetListener(x321);
        		new TimeBlockDragDropTargetListener(x322);
        		
        		//WebButtonGroup dropGroup = new WebButtonGroup ( false, x321, x322 );
        		
        		GroupPanel dropGroup = new GroupPanel ( 0, x321, x322);
        		
                groupPanel.add(dropGroup, j + "," + row);
        	}
        	
        	
        	if ( row > 3 ) {
            //    groupPanel.add ( createHorizontalSeparator () ,
            //           "0," + ( row - 1 ) + "," + ( columns.length - 1 ) + "," + ( row - 1 ), 0 );
            }
        	
        	row += 1;
        }
		
        return groupPanel;
	}
	
	private static WebSeparator createVerticalSeparator() {
		final WebSeparator separator = new WebSeparator(WebSeparator.VERTICAL);
		separator.setDrawSideLines(true);
		separator.setDrawTrailingLine(true);

		separator.setBackground(Color.black);

		return separator;
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
			}
		}
		
	}
}
