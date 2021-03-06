package generator;

import data.GlobalSettings;
import gui.MainCanvasPanel;
import gui.SideBarShapes;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Implementation of a simple generator that creates a blue circle for a given
 * height and width.
 *
 * @author BenGe47
 */
public class TestShapes extends AGenerator {

	private SideBarShapes guiSideBar;

	/**
	 * Constructor.
	 *
	 * @param mainCanvas Inject MainCanvasPanel
	 * @param name Name for this generator
	 */
	public TestShapes(MainCanvasPanel mainCanvas, String name) {
		this.setName(name);
		this.setMnemonicChar('S');
		this.setMainCanvas(mainCanvas);
		initSideBarPanel();
		this.setGenType(GlobalSettings.GeneratorType.TEST);

		guiSideBar = new SideBarShapes(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateStatus(GlobalSettings.Status.READY);
			}
		});

		createSideBarGUI();
	}

	/**
	 * Compose the GUI for the sidebar card.
	 *
	 */
	@Override
	public void createSideBarGUI() {
		this.addToSidebar(guiSideBar.getSideBarPnl());
	}

	private void drawShape(Graphics2D g2d, BufferedImage image) {
		g2d.setColor(guiSideBar.getColor());

		String chosenShape;
		chosenShape = guiSideBar.getShape();

		switch (chosenShape) {
		case "Ellipse":
			g2d.fill(new Ellipse2D.Float(0, 0, image.getWidth(), image.getHeight()));
			break;
		case "Rectangle":
			g2d.fill(new Rectangle2D.Float(5, 5, image.getWidth() - 10, image.getHeight() - 10));
			break;
		case "RoundRectangle":
			g2d.fill(new RoundRectangle2D.Float(5, 5, image.getWidth() - 10, image.getHeight() - 10, 25, 25));
			break;
		case "Circle":
			int max = (image.getWidth() < image.getHeight()) ? image.getWidth() : image.getHeight();
			g2d.fillOval(0, 0, max, max);
			break;

		default:
			break;
		}

	}

	@Override
	public void run() {
		startCalcTime();
		updateStatus(GlobalSettings.Status.CALCULATING);
		guiSideBar.setButtonsCalculating();

		try {
			Thread.sleep(50);
		} catch (Exception e) {

		}

		BufferedImage image = new BufferedImage(guiSideBar.getWidth(), guiSideBar.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		g2d.setColor(guiSideBar.getBGColor());
		g2d.fillRect(0, 0, guiSideBar.getWidth(), guiSideBar.getHeight());

		drawShape(g2d, image);

		g2d.dispose();

		this.setMainCanvasToImage(image);
		endCalcTime();
		guiSideBar.setButtonsReady();
		updateStatus(GlobalSettings.Status.FINISHED);
	}

	@Override
	public void stopGenerator() {
		guiSideBar.setStopped();
		setGenStatus(GlobalSettings.Status.STOP);
	}

}
