package gui;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import notification.TrayIconDemo;
import observer.AdvancedObserverThread;

public class MainGUI {

	final static DefaultListModel model = new DefaultListModel();
	private static JTextArea consoleLog = new JTextArea();
	private static JList keywordList = new JList(model);
	private static JTextField keywordField = new JTextField();
	private static JButton keywordButton = new JButton();
	private static JLabel countdownLabel = new JLabel();
	private static int countNewItems = 0;
	private static MainGUI gui;
	private static JFrame mainWindow;

	public static int getCountNewItems() {
		return countNewItems;
	}

	public MainGUI() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		gui = new MainGUI();

		mainWindow = gui.setUpTheMainWindow();

		gui.printToConsole("Program Running...");
		gui.printToConsole("http://jsoup.org/download");

		AdvancedObserverThread observer = new AdvancedObserverThread(
				"http://www.hotukdeals.com/all/deals/hot");
		Thread thread = new Thread(observer);
		thread.start();
		
		ComponentListener cL = new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	            super.componentResized(e);
	            System.out.println("componentShown = "+e.getComponent().isDisplayable());
	        }
	    };
		    
		   mainWindow.addComponentListener(cL);

		// Set the main window to visible
		mainWindow.setVisible(true);

	
		
	}

	public static void printToConsole(String message) {
		// TODO Auto-generated method stub
		consoleLog.setText(consoleLog.getText() + message + "\n");
	}

	private JFrame setUpTheMainWindow() {
		// Instantiate JFrame
		JFrame mainWindow = new JFrame();
		// Set the size of main window
		mainWindow.setSize(800, 600);

		// Instantiate a JPanel
		JPanel panel = new JPanel();

		// Set the layout of JPanel to not managed
		panel.setLayout(null);

		// Countdown Label
		countdownLabel.setSize(150, 80);
		countdownLabel.setLocation(50, 10);
		countdownLabel.setText("Countdown...");
		panel.add(countdownLabel);

		// Console LOG - Instantiates and positions console log text area
		consoleLog.setSize(300, 550);
		consoleLog.setLocation(499, 0);
		consoleLog.setEnabled(false);
		panel.add(consoleLog);
		// END Console LOG

		// Keyword JList
		keywordList.setSize(420, 380);
		keywordList.setLocation(20, 80);
		panel.add(keywordList);

		// add an action Listener when double click go to the default browser an
		// open the link

		keywordList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {

				if (evt.getClickCount() == 2) {
					int index = getIndexOfSelectedItem();
					System.out.println(index);
					try {

						ArrayList<String> tempHref = AdvancedObserverThread
								.getHrefs();
						Desktop.getDesktop().browse(
								java.net.URI.create(tempHref.get(index)));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		// END Keyword JList

		// END Keyword Text Field

		// Add the panel to the main window
		mainWindow.add(panel);

		return mainWindow;
	}

	@SuppressWarnings("unchecked")
	public static void addElementToList(String title, int rating) {
		boolean repeatedItem = false;
		int indexFoundAt = -1;
		// loop over every item in model (checking for repetition)
		for (int i = 0; i < model.size(); i++) {
			String tempItem = (String) model.get(0);
			if (tempItem.contains(title)) {
				System.err
						.println("Element list already contains the item with same title...");
				// Remove the element (in case the rating has changed)
				model.remove(i);
				
				
				// CHECK WHETHER THE USER HAS SEEN THE JFRAME BEFORE MAKING THE ITEM NOT NEW
				mainWindow.isShowing();
				
				repeatedItem = true;
				break;

			} 

		} // END for every item in model

		// Add the element to the list model
		if (!repeatedItem) {
			countNewItems++;
			model.addElement("NEW! Rating: " + rating + ". Item: " + title);

		} else {
			model.addElement("Rating: " + rating + ". Item: " + title);
		}

	}

	public static int getIndexOfSelectedItem() {
		return AdvancedObserverThread.titles.indexOf(keywordList
				.getSelectedValue().toString().split("Item: ")[1]);

	}

	// method to get the selected item
	public static int selectedItemInJList() {
		return keywordList.getSelectedIndex();
	}

	public static void checkForExistenceAndRemoveElementFromList(String title) {
		// Loop for every item in model
		for (int i = 0; i < model.size(); i++) {
			String currentItem = (String) model.get(i);
			if (currentItem.contains(title)) {
				if (model.get(i).toString().contains("NEW!"))
					countNewItems--;
				model.remove(i);
				printToConsole("Removed item (" + i + ") - " + title);
			}
		}
	}

	public static void setCountdownLabelText(String countdownNumber) {
		countdownLabel.setText(countdownNumber);

	}

	public static void displayNotification() {
		if (countNewItems != 0 && countNewItems > 0) {
			TrayIconDemo td = new TrayIconDemo();
			try {
				td.displayTray("There are " + countNewItems + " new items.");
				countNewItems = 0;
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

}
