package net.waqassiddiqi.app.crew.ui.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.waqassiddiqi.app.crew.ui.handler.TimeBlockDragDropHandler;

import com.alee.utils.GraphicsUtils;
import com.alee.utils.ImageUtils;
import com.alee.utils.SwingUtils;

public class TimeBlock extends JComponent {
	private static final long serialVersionUID = 1L;

	public enum BlockType {
		REST, WORK
	};

	protected BlockType blockType = BlockType.WORK;

	/**
	 * Preview image corners rounding.
	 */
	protected int round;

	/**
	 * Preview image area width.
	 */
	protected int width;

	/**
	 * Preview image area height.
	 */
	protected int height;

	/**
	 * Actual image placed into WebImageDrop component.
	 */
	protected BufferedImage actualImage;

	/**
	 * Preview image.
	 */
	protected BufferedImage image;

	/**
	 * Constructs new TimeBlock component of size 25x25
	 */
	public TimeBlock() {
		this(25, 25);
	}

	/**
	 * Constructs new TimeBlock component with the specified size and block type
	 * 
	 * @param width
	 *            preview image area width
	 * @param height
	 *            preview image area height
	 */
	public TimeBlock(final int width, final int height,
			final BlockType blockType) {
		this(width, height, null, blockType);
	}

	/**
	 * Constructs new WebImageDrop component with the specified preview image
	 * area size.
	 * 
	 * @param width
	 *            preview image area width
	 * @param height
	 *            preview image area height
	 */
	public TimeBlock(final int width, final int height) {
		this(width, height, null, BlockType.REST);
	}

	/**
	 * Constructs new WebImageDrop component with the specified preview image
	 * area size and actual image.
	 * 
	 * @param width
	 *            preview image area width
	 * @param height
	 *            preview image area height
	 * @param image
	 *            actual image
	 */
	@SuppressWarnings("serial")
	public TimeBlock(final int width, final int height,
			final BufferedImage image, final BlockType blockType) {
		super();

		this.blockType = blockType;

		this.width = width;
		this.height = height;

		this.round = 0;

		this.actualImage = image;

		this.image = image;
		updatePreview();

		SwingUtils.setOrientation(this);

		// Image drop handler
		setTransferHandler(new TimeBlockDragDropHandler(this, null) {
			@Override
			protected boolean imagesImported(final List<ImageIcon> images) {
				for (final ImageIcon image : images) {
					try {
						setImage(ImageUtils.getBufferedImage(image));
						return true;
					} catch (final Throwable e) {
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns actual image.
	 * 
	 * @return actual image
	 */
	public BufferedImage getImage() {
		return actualImage;
	}

	/**
	 * Returns preview image.
	 * 
	 * @return preview image
	 */
	public BufferedImage getThumbnail() {
		return image;
	}

	/**
	 * Returns preview image corners rounding.
	 * 
	 * @return preview image corners rounding
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Sets preview image corners rounding.
	 * 
	 * @param round
	 *            new preview image corners rounding
	 */
	public void setRound(final int round) {
		this.round = round;
		updatePreview();
	}

	/**
	 * Returns preview image area width.
	 * 
	 * @return preview image area width
	 */
	public int getImageWidth() {
		return width;
	}

	/**
	 * Sets preview image area width.
	 * 
	 * @param width
	 *            preview image area width
	 */
	public void setImageWidth(final int width) {
		this.width = width;
		updatePreview();
	}

	/**
	 * Returns preview image area height.
	 * 
	 * @return preview image area height
	 */
	public int getImageHeight() {
		return height;
	}

	/**
	 * Sets preview image area height.
	 * 
	 * @param height
	 *            new preview image area height
	 */
	public void setImageHeight(final int height) {
		this.height = height;
		updatePreview();
	}

	/**
	 * Sets new displayed image. This forces a new preview image to be generated
	 * so be aware that this call does some heavy work.
	 * 
	 * @param image
	 *            new displayed image
	 */
	public void setImage(final BufferedImage image) {
		this.actualImage = image;
		this.image = image;
		updatePreview();
		repaint();
	}

	/**
	 * Updates image preview.
	 */
	protected void updatePreview() {
		/*
		 * final Graphics2D g2d = (Graphics2D) getGraphics(); final Object aa =
		 * GraphicsUtils.setupAntialias(g2d);
		 * 
		 * final Shape border = new RoundRectangle2D.Double(getWidth() / 2 -
		 * width / 2 + 1, getHeight() / 2 - height / 2 + 1, width - (image ==
		 * null ? 3 : 1), height - (image == null ? 3 : 1), 0 * 2, 0 * 2);
		 * 
		 * if(isFilled) g2d.setPaint(Color.GRAY); g2d.fill(border);
		 * 
		 * g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
		 * BasicStroke.CAP_SQUARE, 1f, null, 0)); g2d.setPaint(Color.BLACK);
		 * g2d.draw(border);
		 * 
		 * GraphicsUtils.restoreAntialias(g2d, aa);
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2d = (Graphics2D) g;
		final Object aa = GraphicsUtils.setupAntialias(g2d);

		final Shape border = new RoundRectangle2D.Double(getWidth() / 2 - width
				/ 2 + 1, getHeight() / 2 - height / 2 + 1, width
				- (image == null ? 3 : 1), height - (image == null ? 3 : 1),
				0 * 2, 0 * 2);

		if (blockType == BlockType.WORK)
			g2d.setPaint(Color.LIGHT_GRAY);
		else
			g2d.setPaint(Color.white);
		g2d.fill(border);

		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
				BasicStroke.CAP_SQUARE, 1f, null, 0));
		g2d.setPaint(Color.BLACK);
		g2d.draw(border);

		GraphicsUtils.restoreAntialias(g2d, aa);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width - 2, height - 1);
	}

	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
	}

	public BlockType getBlockType() {
		return this.blockType;
	}
}
