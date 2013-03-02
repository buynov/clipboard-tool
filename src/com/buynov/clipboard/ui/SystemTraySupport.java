package com.buynov.clipboard.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.buynov.clipboard.ClipboardHandler;
import com.buynov.clipboard.SystemClipboard;
import com.buynov.clipboard.TrayIconMouseListener;


public class SystemTraySupport {

	private static final String CLIPBOARD_ICON_EMPTY = "clipboard_icon_empty.png";
	private static final String CLIPBOARD_ICON = "clipboard_icon.png";
	
	private static SystemTraySupport instance = null;
	private static final Image imgClipboard;// = Toolkit.getDefaultToolkit().getImage("clipboard_icon.png");
	private static final Image imgClipboardEmpty;// = Toolkit.getDefaultToolkit().getImage("clipboard_icon_empty.png");
	
	static {
		URL imgURL = ClassLoader.getSystemResource(CLIPBOARD_ICON);
		if (imgURL == null) {
			imgClipboard = Toolkit.getDefaultToolkit().getImage(CLIPBOARD_ICON);
		} else {
			imgClipboard = Toolkit.getDefaultToolkit().getImage(imgURL);
		}

		imgURL = ClassLoader.getSystemResource(CLIPBOARD_ICON_EMPTY);
		if (imgURL == null) {
			imgClipboardEmpty = Toolkit.getDefaultToolkit().getImage(CLIPBOARD_ICON_EMPTY);
		} else {
			imgClipboardEmpty = Toolkit.getDefaultToolkit().getImage(imgURL);
		}
	}
	
	TrayIcon trayIcon = null;
	MenuItem miCyrToLat = null;
	MenuItem miClearFormatting = null;

	public SystemTraySupport() {
	     if (SystemTray.isSupported()) {
	         // get the SystemTray instance
	         SystemTray tray = SystemTray.getSystemTray();
	         
	         // create a action listener to listen for default action executed on the tray icon
	         ActionListener listener = new ClipboardHandler();
	         // create a popup menu
	         PopupMenu popup = new PopupMenu();
	         // create menu item for the default action
	         
	         // Cyrillic to Latinic menu item
	         miCyrToLat = new MenuItem("Cyr -> Latin");
	         miCyrToLat.addActionListener(listener);
	         miCyrToLat.setActionCommand(Constants.CMD_CYR_TO_LAT);
	         popup.add(miCyrToLat);
	         
	         miClearFormatting = new MenuItem("Clear formatting");
	         miClearFormatting.addActionListener(listener);
	         miClearFormatting.setActionCommand(Constants.CMD_CLEAR_FORMATTING);
	         popup.add(miClearFormatting);
	         
	         popup.addSeparator();
	         
	         // Exit menu item
	         MenuItem menuItem = new MenuItem("Exit");
	         menuItem.addActionListener(listener);
	         menuItem.setActionCommand(Constants.CMD_EXIT);
	         popup.add(menuItem);
	         
	         
	         /// ... add other items
	         // construct a TrayIcon
	         String preferredContents = SystemClipboard.getInstance().getPreferredContents();
System.out.println(preferredContents);
	         trayIcon = new TrayIcon(imgClipboard, null, popup);
	         // set the TrayIcon properties
	         trayIcon.addActionListener(listener);
	         trayIcon.setImageAutoSize(true);
	         trayIcon.addMouseMotionListener(new TrayIconMouseListener(trayIcon));

	         // add the tray image
	         try {
	             tray.add(trayIcon);
	         } catch (AWTException e) {
	             System.err.println(e);
	         }
	     } else {
	    	 JOptionPane.showMessageDialog(null, "System Tray is not supported!", "Clipboard Tool", JOptionPane.ERROR_MESSAGE);
	     }
	     // ...
//	     // some time later
//	     // the application state has changed - update the image
//	     if (trayIcon != null) {
//	         trayIcon.setImage(updatedImage);
//	     }
	     // ...	}
	}
	
	public void showMessage(String text, MessageType type) {
		trayIcon.displayMessage("Clipboard Tool", text, type);
	}
	
	public void setState (String state) {
		if (Constants.STATE_UNSUPPORTED.equals(state)) {
			trayIcon.setImage(imgClipboardEmpty);
			miClearFormatting.setEnabled(false);
			miCyrToLat.setEnabled(false);
		}
		else {
			trayIcon.setImage(imgClipboard);
			miClearFormatting.setEnabled(true);
			miCyrToLat.setEnabled(true);
		}
	}
	
	public static SystemTraySupport getInstance () {
		if (instance == null) {
			instance = new SystemTraySupport();
		}
		
		return instance;
	}

	public static void main(String[] args) {
		Logger logger = Logger.getLogger("net.sourceforge.clipboard");
		logger.setLevel(Level.SEVERE);
		Handler[] handler = logger.getHandlers();
System.out.println(handler.length);
		for (int i = 0; i < handler.length; i++) {
			System.out.println(handler[i]);
		}
		logger.info("test");
		
		getInstance();
	}
}
