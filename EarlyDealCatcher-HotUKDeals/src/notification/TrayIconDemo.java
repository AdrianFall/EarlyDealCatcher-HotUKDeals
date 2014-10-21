package notification;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.ImageIcon;

public class TrayIconDemo {
 
 
 
    public void displayTray(String message) throws AWTException{
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        //Creating a tray icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/hot.jpg"));
        Image image = icon.getImage();
        //System.out.println(image);
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("New Hot Deals");
        tray.add(trayIcon);
        trayIcon.displayMessage("New Deals!", message, MessageType.INFO);
    }
}