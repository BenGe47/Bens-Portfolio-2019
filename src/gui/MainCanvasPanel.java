package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * JPanel managing display of generated graphics.
 *
 * @author BenGe47
 */
public class MainCanvasPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage mainCanvas;

	/**
	 * Constructor.
	 *
	 */
	public MainCanvasPanel() {
		super();
	}

	/**
	 * Get mainCanvas JPanel.
	 *
	 * @return mainCanvas main Panel
	 */
	public BufferedImage getImage() {
		return this.mainCanvas;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (mainCanvas != null) {
			g.drawImage(mainCanvas, 0, 0, mainCanvas.getWidth(), mainCanvas.getHeight(), null);
		}
	}

	/**
	 * Draw mainCanvas with input mainCanvas.
	 * 
	 *
	 * @param inGraphics2D new mainCanvas to display
	 */
	public void setImage(BufferedImage inGraphics2D) {
		this.setSize(inGraphics2D.getWidth(), inGraphics2D.getHeight());
		this.mainCanvas = inGraphics2D;
		super.repaint();
	}
}
