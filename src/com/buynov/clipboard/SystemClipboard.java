package com.buynov.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This class wraps the {@link java.awt.datatransfer.Clipboard} instance of 
 * the system clipboard and provides ways to manipulate it - including getting
 * and setting the contents of the system clipboard, resolving the type of the
 * clipboard's content, etc.
 * 
 * @author Stefan Buynov
 *
 */
public class SystemClipboard {

	/** The single instance of the system clipboard wrapper. */
	private static SystemClipboard instance = null;

	/** Static instance of the default plain text data flavor. */
	private static DataFlavor plainTextFlavor;
	/** Static instance of the default formatted (HTML) text data flavor. */
	private static DataFlavor formattedTextFlavor;

	final static DataFlavor[] plainTextFlavors;
	final static DataFlavor[] formattedTextFlavors;
	
	/** Plain text data flavor MIME type. */
	private static final String PLAIN_TEXT_MIME = 
		"text/plain; class=java.lang.String; charset=Unicode";
	/** Formatted (HTML) text data flavor MIME type. */
	private static final String FORMATTED_TEXT_MIME = 
		"text/html; class=java.lang.String; charset=Unicode";

	static {
		try {
			formattedTextFlavor = new DataFlavor(FORMATTED_TEXT_MIME);
		} catch (ClassNotFoundException e) {
			formattedTextFlavor = null;
			e.printStackTrace();
			//formattedTextFlavor = null;
		}

		try {
			plainTextFlavor = new DataFlavor(PLAIN_TEXT_MIME);
		} catch (ClassNotFoundException e) {
			plainTextFlavor = null;
			e.printStackTrace();
			//formattedTextFlavor = null;
		}
		
		plainTextFlavors = new DataFlavor[1];
		plainTextFlavors[0] = plainTextFlavor;
		
		formattedTextFlavors = new DataFlavor[2];
		formattedTextFlavors[0] = formattedTextFlavor;
		formattedTextFlavors[1] = plainTextFlavor;
		
	}

	
	/**
	 * Get the instance of the system {@link java.awt.datatransfer.Clipboard} 
	 * object.
	 * 
	 * @return the system {@link java.awt.datatransfer.Clipboard} object
	 */
	public final Clipboard getSystemClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		return clipboard;
	}

	/**
	 * Get the preferred {@link java.awt.datatransfer.DataFlavor} instance of 
	 * the system clipboard content. 
	 * 
	 * @return the preferred {@link java.awt.datatransfer.DataFlavor}
	 */
	public final DataFlavor getPreferredFlavor() {
		Clipboard clipboard = getSystemClipboard();
		Transferable trans = clipboard.getContents(null);
		if (trans == null) {
			return null;
		}

		DataFlavor[] flavors = trans.getTransferDataFlavors();

//		dumpFlavors(flavors);

		DataFlavor result = null;

		DataFlavor bestFlavour = DataFlavor.selectBestTextFlavor(flavors);
		if (bestFlavour == null) {
			return null;
		}

		if (formattedTextFlavor.isMimeTypeEqual(bestFlavour)) {
			result = formattedTextFlavor;
		} else if (plainTextFlavor.isMimeTypeEqual(bestFlavour)) {
			result = plainTextFlavor;
		} else {
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor flavor = flavors[i];
				if (formattedTextFlavor.isMimeTypeEqual(flavor)) {
					result = formattedTextFlavor;
					break;
				}
				if (plainTextFlavor.isMimeTypeEqual(flavor)) {
					result = plainTextFlavor;
				}

			}
		}

		return result;
	}
	
	public final String getPreferredContents() {
		return getContents(getPreferredFlavor());
	}

	
	/**
	 * Get the contents of the system clipboard in the format of the given 
	 * {@link java.awt.datatransfer.DataFlavor} type.
	 * 
	 * @param flavor {@link java.awt.datatransfer.DataFlavor} type
	 * @return the system clipboard content as {@link java.lang.String}
	 */
	public final String getContents(final DataFlavor flavor) {
		if (flavor == null) {
			return null;
		}

		Clipboard clipboard = getSystemClipboard();

		try {
			Reader transferData = flavor.getReaderForText(clipboard
					.getContents(null));

			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(transferData);

			String line = null;
			do {
				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line != null) {
					sb.append(line);
					sb.append("\n");
				}
			} while (line != null);

			return sb.toString();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Set the content of the system clipboard to the given {@link String}.
	 * 
	 * @param s the new content of the system clipboard
	 * @param df the appropriate {@link java.awt.datatransfer.DataFlavor}
	 */
	public final void setContents(final String[] s, final DataFlavor[] dfs) {
		TransferableProxy tp = new TransferableProxy(s, dfs);
		getSystemClipboard().setContents(tp, tp);
	}

	/**
	 * Test method.
	 * 
	 * @param flavors flavors
	 */
	private void dumpFlavors(final DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			System.out.println("Flavor[" + i + "] = " + flavor.getMimeType());
			try {
				System.out.println("\tData: "
						+ getSystemClipboard().getData(flavor).getClass());
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the single instance of this class. New instance will be created if 
	 * necessary.
	 * 
	 * @return the single instance of this class
	 */
	public static SystemClipboard getInstance() {
		if (instance == null) {
			instance = new SystemClipboard();
		}

		return instance;
	}

	/**
	 * Get the plainTextFlavor instance. 
	 * 
	 * @return the plainTextFlavor instance
	 */
	public static DataFlavor getPlainTextFlavor() {
		return plainTextFlavor;
	}

	/**
	 * Get the formattedTextFlavor instance.
	 * 
	 * @return the formattedTextFlavor instance
	 */
	public static DataFlavor getFormattedTextFlavor() {
		return formattedTextFlavor;
	}

	
	/**
	 * Get the PLAIN_TEXT_MIME instance.
	 * 
	 * @return the PLAIN_TEXT_MIME instance
	 */
	public static String getPlainTextMIME() {
		return PLAIN_TEXT_MIME;
	}

	/**
	 * Get the FORMATTED_TEXT_MIME instance.
	 * 
	 * @return the FORMATTED_TEXT_MIME instance
	 */
	public static String getFormattedTextMIME() {
		return FORMATTED_TEXT_MIME;
	}

}
