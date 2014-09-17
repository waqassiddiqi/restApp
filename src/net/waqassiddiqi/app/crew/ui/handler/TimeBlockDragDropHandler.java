package net.waqassiddiqi.app.crew.ui.handler;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.alee.utils.DragUtils;

public class TimeBlockDragDropHandler extends TransferHandler {
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	public TimeBlockDragDropHandler(final JComponent component, BufferedImage image) {
		super();

		this.image = image;

		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					exportAsDrag(component, e, getSourceActions(component));
				}
			}
		});
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	public boolean canImport(final TransferHandler.TransferSupport info) {
		return isDropEnabled();
	}

	@Override
	public boolean importData(final TransferHandler.TransferSupport info) {
		return info.isDrop() && importData(info.getTransferable());
	}

	public boolean importData(final Transferable t) {
		if (isDropEnabled()) {
			final List<ImageIcon> images = new ArrayList<ImageIcon>();

			// Check imported files
			final List<File> files = DragUtils.getImportedFiles(t);
			if (files != null) {
				for (final File file : files) {
					images.add(new ImageIcon(file.getAbsolutePath()));
				}
			}

			// Check imported raw image
			final Image image = DragUtils.getImportedImage(t);
			if (image != null) {
				images.add(new ImageIcon(image));
			}

			return isDropEnabled() && imagesImported(images);
		} else {
			return false;
		}
	}

	protected boolean isDropEnabled() {
		return true;
	}

	protected boolean imagesImported(final List<ImageIcon> images) {
		return true;
	}

	private static final DataFlavor[] imageFlavor = new DataFlavor[] { DataFlavor.imageFlavor };

	@Override
	protected Transferable createTransferable(JComponent c) {
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return imageFlavor;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals(DataFlavor.imageFlavor);
			}

			@Override
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor)) {
					return image;
				} else {
					return null;
				}
			}
		};
	}
}
