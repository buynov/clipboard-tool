package com.buynov.clipboard;

import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import com.buynov.clipboard.ui.Constants;
import com.buynov.clipboard.ui.SystemTraySupport;


/**
 * This class serves as a controller between the system clipboard and the 
 * associated UI.
 * 
 * @author Stefan Buynov
 *
 */
public class ClipboardHandler implements FlavorListener, ActionListener {

	/**
	 * Creates an instance of this class.
	 */
	public ClipboardHandler() {
		super();
		SystemClipboard.getInstance().getSystemClipboard().addFlavorListener(this);
	}

	public final void flavorsChanged(final FlavorEvent e) {
		DataFlavor df = SystemClipboard.getInstance().getPreferredFlavor();
		if (df == null) {
			SystemTraySupport.getInstance().setState(Constants.STATE_UNSUPPORTED);
		} else {
			SystemTraySupport.getInstance().setState(Constants.STATE_FULL_FEATURES);
		}
	}

	public final void actionPerformed(final ActionEvent e) {
		String cmd = e.getActionCommand();
		Object src = e.getSource();
		
		if (cmd != null) {
			if (Constants.CMD_CYR_TO_LAT.equals(cmd)) {
				cyrillicToLatinic();
				SystemTraySupport.getInstance().showMessage("Conversion complete!", MessageType.INFO);
			} 
			else if (Constants.CMD_CLEAR_FORMATTING.equals(cmd)) {
				clearFormatting();
				SystemTraySupport.getInstance().showMessage("Formatting cleared!", MessageType.INFO);
			}
			else if (Constants.CMD_EXIT.equals(cmd)) {
				System.exit(0);
			}
		}
		else if (src != null) {
			// Check if the event comes from the TrayIcon
			if (src instanceof java.awt.TrayIcon) {
System.out.println("\tTray Event: " + e.paramString());
			}
		}
	}

	public final String cyrillicToLatinic() {
		DataFlavor flavor = SystemClipboard.getInstance().getPreferredFlavor();
System.out.println(flavor.getMimeType());
		
		DataFlavor[] dfs;
		if (SystemClipboard.getFormattedTextFlavor().equals(flavor)) {
			dfs = SystemClipboard.formattedTextFlavors;
		}
		else {
			dfs = SystemClipboard.plainTextFlavors;
		}
		
		String[] ss = new String[dfs.length];
		for (int i = 0; i < ss.length; i++) {
			String transferData = SystemClipboard.getInstance().getContents(dfs[i]);
System.out.println(transferData);
			String s = replaceAccordingly(new StringBuffer(transferData));

			if (transferData == null) {
				continue;
			}

			System.out.println(s);
			
			ss[i] = s;
		}
		
		SystemClipboard.getInstance().setContents(ss, dfs);
		return null;
	}
	
	public final String clearFormatting() {
		String data = SystemClipboard.getInstance().getContents(SystemClipboard.getPlainTextFlavor());
System.out.println(data);
		if (data == null) {
			return null;
		}

		System.out.println(data);
		String[] ss = new String[1];
		ss[0] = data;

		SystemClipboard.getInstance().setContents(ss, SystemClipboard.plainTextFlavors);
		return null;
	}
	
	public final String replaceAccordingly(final StringBuffer sb) {
		Collection<String> keys = MappingHandler.getCharactersForReplacement();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String replacement = MappingHandler.getReplacementForCharacter(key);
			int index = sb.indexOf(key);
			while (index >= 0) {
				sb.replace(index, index + key.length(), replacement);
				index = sb.indexOf(key);
			}
		}
		
		return sb.toString();
	}
	
}
