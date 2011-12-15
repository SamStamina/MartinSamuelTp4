package ets.log120.tp4.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ets.log120.tp4.app.ChangeImageCommand;
import ets.log120.tp4.app.Controller;
import ets.log120.tp4.app.Perspective;
import ets.log120.tp4.app.PerspectiveFactory;
import ets.log120.tp4.app.PerspectiveUtil;

public class MainWindow extends JFrame {

	// --------------------------------------------------
	// Constructeur(s)
	// --------------------------------------------------

	public MainWindow() {
		super();
		initLang();
		setLayout(new BorderLayout());

		controller = Controller.getInstance();
		initMenuBar();
		add(getLeftPanel(), BorderLayout.LINE_START);
		add(graphicalView = new GraphicalPerspectiveView(controller), BorderLayout.CENTER);

		setTitle(lang.getProperty("app.title"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		/*
		try {
			perspective = PerspectiveFactory.makePerspective(
					"Image principale", ImageIO.read(new File("vincent.jpg")));
			setPerspective(perspective);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	// --------------------------------------------------
	// Méthode(s)
	// --------------------------------------------------

	private void setPerspective(Perspective p) {
		perspective = p;
		
		textView.setPerspective(perspective);
		graphicalView.setPerspective(perspective);
		
		perspective.imageChanged.addObserver(new Observer() {
			@Override
			public void update(Observable arg0, Object arg1) {
				Perspective p = (Perspective) arg1;
				thumbnailPerspective.setImage(p.getImageName(), p.getImage());
			}
		});
		
		thumbnailPerspective = PerspectiveFactory.makePerspective();
		thumbnailPerspective.imageChanged.addObserver(new ThumbnailPerpectiveChanged());
		thumbnailPerspective.setImage(p.getImageName(), p.getImage());
	}

	/**
	 * Initialise le fichier de propriétés contenant le texte à afficher à
	 * l'utilisateur.
	 */
	private void initLang() {
		final String fileName = "lang.fr";

		try {
			lang = new java.util.Properties();
			lang.load(new java.io.FileInputStream("lang.fr"));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Le fichier de langue " + fileName + " n'existe pas.");
			System.exit(ERROR);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Une erreur est survenue lors du chargement du fichier de langue");
		}
	}

	private JPanel getLeftPanel() {
		final int MARGIN = 5;
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		panel.add(textView = new TextualPerspectiveView());
		panel.add(Box.createVerticalGlue());
		panel.add(thumbnail = new ImageComponent(THUMB_WIDTH, THUMB_HEIGHT));
		
		return panel;
	}

	/**
	 * Initialise la barre de menu.
	 */
	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getFileMenu());
		menuBar.add(getImageMenu());
		setJMenuBar(menuBar);
	}

	/**
	 * Retourne le menu « Fichier » de l'application.
	 */
	private JMenu getFileMenu() {
		JMenu fileMenu = new JMenu(lang.getProperty("app.menu.file"));
		JMenuItem exitItem = new JMenuItem(
				lang.getProperty("app.menu.file.exit"));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		JMenuItem saveItem = new JMenuItem(
				lang.getProperty("app.menu.file.save"));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				serializePerspective();
			}
		});

		fileMenu.add(saveItem);
		JMenuItem openPerspectiveItem = new JMenuItem(
				lang.getProperty("app.menu.file.openPerspective"));
		openPerspectiveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				unSerializePerspective();
			}
		});

		fileMenu.add(openPerspectiveItem);

		return fileMenu;
	}

	private void serializePerspective() {
		try {
			FileOutputStream fichier = new FileOutputStream("perspective.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(perspective);
			oos.flush();
			oos.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	private void unSerializePerspective() {
		try {
			FileInputStream file = new FileInputStream("perspective.ser");
			ObjectInputStream in = new ObjectInputStream(file);
			perspective = (Perspective) in.readObject();

			try {
				perspective.setImage(perspective.getImageName(),
						ImageIO.read(new File(perspective.getImageName())));
				setPerspective(perspective);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le menu « Image » de l'application.
	 */
	private JMenu getImageMenu() {
		JMenu imageMenu = new JMenu(lang.getProperty("app.menu.image"));
		JMenuItem openImageItem = new JMenuItem(lang.getProperty("app.menu.image.change"));
		openImageItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new MyFilter());

				int returnVal = fc.showOpenDialog(getContentPane());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String fileName = fc.getSelectedFile().getAbsolutePath();
					BufferedImage newImage = null;
					try {
						newImage = ImageIO.read(new File(fileName));
						Perspective p = PerspectiveFactory.makePerspective(fileName, newImage);
						setPerspective(p);
					} catch (IOException ex) {
						System.out.println("fail");
					}
				} else {

				}
			}
		});

		imageMenu.add(openImageItem);

		return imageMenu;
	}

	// --------------------------------------------------
	// Attribut(s)
	// --------------------------------------------------

	private java.util.Properties lang;
	private GraphicalPerspectiveView graphicalView;
	private TextualPerspectiveView textView;
	private ImageComponent thumbnail;
	private Perspective perspective;
	private Perspective thumbnailPerspective;
	private Controller controller;
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static final int THUMB_WIDTH = 256;
	private static final int THUMB_HEIGHT = 256;

	// --------------------------------------------------
	// Classe(s) interne(s)
	// --------------------------------------------------
	class MyFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			String filename = file.getName();
			return filename.endsWith(".png") || filename.endsWith(".jpg")
					|| filename.endsWith(".gif") || (file.isDirectory())
					|| filename.endsWith(".jpeg") || filename.endsWith(".tif");
		}

		public String getDescription() {
			return "*.png, *.jpg, *.jpeg *.gif";
		}
	}

	private class ThumbnailPerpectiveChanged implements java.util.Observer {
		@Override
		public void update(Observable arg0, Object arg1) {
			Perspective p = (Perspective) arg1;
			thumbnail.setImage(
					p.getImage(),
					PerspectiveUtil.getZoomToFitDisplay(p, thumbnail.getWidth(), thumbnail.getHeight()),
					p.getPosition());
		}
	}
}
