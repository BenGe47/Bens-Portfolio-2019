package gui;

import generator.IGenerator;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Constructs the GUI with menu bars, menu items and panels. Also updates status
 * of the generators in the status bar.
 * 
 * @author BenGe47
 *
 */
public class MainWindow extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar menubar;
	private JPanel statusbarPanel;
	private MainCanvasPanel centerImagePanel;
	private JLabel statusLabel;
	private IGenerator observableGenerator;

	private final SaveMainCanvasToImage imageSaverHelper;
	private final ArrayList<IGenerator> generators;
	private CardLayout card = new CardLayout();

	/**
	 * Constructor.
	 *
	 * @param imageSaver Helper to write displayed images to disk
	 * @param generators List of generators to build the menu items
	 * @param centerImagePanel Main panel to display generated pictures
	 */
	public MainWindow(SaveMainCanvasToImage imageSaver, ArrayList<IGenerator> generators,
			MainCanvasPanel centerImagePanel) {
		this.imageSaverHelper = imageSaver;
		this.centerImagePanel = centerImagePanel;
		this.generators = generators;
		initUI();
		registerGenerators();
	}

	/**
	 * Initialize GUI with menu bar, panels status and center.
	 *
	 */
	private void initUI() {
		setTitle("Ben´s Portfolio");
		setSize(1024, 768);
		setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setLayout(new BorderLayout());

		// Add Statusbar as JPanel
		statusbarPanel = new JPanel();
		statusbarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(statusbarPanel, BorderLayout.SOUTH);
		statusbarPanel.setPreferredSize(new Dimension(this.getWidth(), 32));
		statusbarPanel.setLayout(new BoxLayout(statusbarPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel("");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusbarPanel.add(statusLabel);

		JMenu menuFile;
		JMenu menuSimple;
		JMenu menuCellular;
		JMenu menuLSystem;
		JMenu menuRTree;
		JMenu menuGames;
		JMenu menuHelp;
		JMenuItem menuItem;
		menubar = new JMenuBar();

		// MenuBar File Entry
		menuFile = new JMenu("File");
		menubar.add(menuFile);

		menuItem = new JMenuItem("Save Image as PNG");
		menuItem.addActionListener((ActionEvent ae) -> {
			saveMenuItemClicked(ae);
		});
		menuFile.add(menuItem);

		menuFile.add(new JSeparator());

		menuItem = new JMenuItem("Quit");
		menuItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		menuFile.add(menuItem);

		// MenuBar Generators
		menuSimple = new JMenu("Test");
		menubar.add(menuSimple);
		menuCellular = new JMenu("Cellular Automatons");
		menubar.add(menuCellular);
		menuLSystem = new JMenu("L-Systems");
		menubar.add(menuLSystem);
		menuRTree = new JMenu("Random-Tree");
		menubar.add(menuRTree);
		menuGames = new JMenu("Games");
		menubar.add(menuGames);
		menuHelp = new JMenu("?");
		menubar.add(menuHelp);

		menuItem = new JMenuItem("About");
		menuItem.addActionListener((ActionEvent event) -> {
			showDialogAbout();
		});
		menuHelp.add(menuItem);

		this.setJMenuBar(menubar);

		// Add Left Control Panel
		JPanel container = new JPanel();
		container.setLayout(card);

		container.setPreferredSize(new Dimension(350, this.getHeight()));
		container.setBorder(new BevelBorder(BevelBorder.LOWERED));

		generators.forEach((generator) -> {
			container.add(generator.getSideBarPanel(), generator.getName());
		});

		this.add(container, BorderLayout.WEST);

		// Add Generator Entries from ArrayList
		for (IGenerator generator : generators) {
			menuItem = new JMenuItem(generator.getName());
			menuItem.addActionListener((ActionEvent ae) -> {

				generalStop();
				card.show(container, generator.getName());
				generator.setReady();
				statusLabel.setText(generator.getName() + " Status: " + generator.getGenStatus());

			});

			switch (generator.getGenType()) {
			case TEST:
				menuSimple.add(menuItem);
				break;
			case CELLULAR:
				menuCellular.add(menuItem);
				break;
			case LSYSTEM:
				menuLSystem.add(menuItem);
				break;
			case RTREE:
				menuRTree.add(menuItem);
				break;
			default:
				break;
			}
		}

		// Add Center Panel
		JScrollPane spCenter = new JScrollPane(centerImagePanel);
		spCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(spCenter, BorderLayout.CENTER);
	}

	/**
	 * Register generators with observer so the status in the status bar can be
	 * automatically updated.
	 *
	 */
	private void registerGenerators() {
		generators.forEach((generator) -> {
			generator.addObserver(this);
		});
	}

	/**
	 * React to menu entry click with saving image to drive.
	 *
	 * @param aeMenuSaveClick Click event on menu entry
	 */
	private void saveMenuItemClicked(ActionEvent aeMenuSaveClick) {
		String userhome = System.getProperty("user.home");
		JFileChooser saveFileChooser = new JFileChooser(userhome + "\\Downloads");
		if (saveFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String path = saveFileChooser.getSelectedFile().getAbsolutePath();
			BufferedImage image = GetBufferedImageFromCenterPanel();
			imageSaverHelper.writeBufferedImgToDisk(path, image);
		}
	}

	/**
	 * Returns the center panel as an image. Helper method for
	 * saveMenuItemClicked to save an image to a drive.
	 *
	 * @return a buffered image of the center panel
	 */
	private BufferedImage GetBufferedImageFromCenterPanel() {
		return centerImagePanel.getImage();
	}

	/**
	 * Update the status of the active generator in the status bar. Stops other
	 * generators. Also starts new thread for status ready generators.
	 *
	 * @param obsGenerator active generator
	 * @param o1 Pass through to the method notifyObservers
	 */
	@Override
	public void update(Observable obsGenerator, Object o1) {

		observableGenerator = (IGenerator) obsGenerator;

		System.out.println(observableGenerator.getName() + ": Status = " + observableGenerator.getGenStatus());

		// Update status bar with error message if status is error
		if (observableGenerator.getGenStatus() == IGenerator.Status.ERROR) {
			statusLabel.setText(observableGenerator.getName() + " Status: " + observableGenerator.getGenStatus() + ": "
					+ observableGenerator.getErrorMessage());
			return;
		}

		// Stop all generators, but not active obsGenerator
		generators.stream().map((gen) -> {
			if (!gen.equals(obsGenerator)) {
				boolean bool = gen.getGenStatus() == IGenerator.Status.CALCULATE;
				gen.stopGenerator();
				if (bool) {
					statusLabel
							.setText(observableGenerator.getName() + " Status: " + observableGenerator.getGenStatus());
				}
			}
			return gen;
		}).forEachOrdered((gen) -> {

		});

		// Update status bar with status of observableGenerator
		statusLabel.setText(observableGenerator.getName() + " Status: " + observableGenerator.getGenStatus());

		// In ready status start thread to keep the buttons active
		if (observableGenerator.getGenStatus() == IGenerator.Status.READY) {
			Thread thread = new Thread(observableGenerator);
			thread.start();
		}

	}

	/**
	 * Stops all generators.
	 */
	private void generalStop() {
		generators.forEach((generator) -> {
			generator.stopGenerator();
		});
	}

	/**
	 * Show credits/help pop-up window.
	 */
	private void showDialogAbout() {

		JDialog meinJDialog = new JDialog();
		meinJDialog.setLayout(new GridBagLayout());
		meinJDialog.setTitle("About Ben's Portfolio");
		meinJDialog.setSize(400, 250);
		meinJDialog.setModal(true);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);

		JTextPane taAbout = new JTextPane();
		taAbout.setContentType("text/html");
		taAbout.setText("\n <b>Version 19.037</b> " + "<br><br>Author: BenGe47" + "<br>E-Mail: @googlemail.com"
				+ "<br><br>Copyright 2019");
		taAbout.setOpaque(false);
		taAbout.setEditable(false);
		meinJDialog.add(taAbout, gbc);

		JButton btnClose = new JButton("OK");
		btnClose.setPreferredSize(new Dimension(60, 25));

		btnClose.addActionListener((ActionEvent e) -> {
			meinJDialog.dispose();

		});
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 1;
		gbc.gridy = 1;
		meinJDialog.add(btnClose, gbc);

		meinJDialog.setLocationRelativeTo(null);
		meinJDialog.setVisible(true);
	}
}
