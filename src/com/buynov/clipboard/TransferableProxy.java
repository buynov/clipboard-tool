package com.buynov.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.activation.DataHandler;

public class TransferableProxy implements Transferable, ClipboardOwner {

	private HashMap<String, DataHandler> handlersMap = new HashMap<String, DataHandler>();
	
	public TransferableProxy() {
		
	}
	
	public TransferableProxy(String[] s, DataFlavor[] dfs) {
		if (s.length != dfs.length) {
			throw new IllegalArgumentException("Illegal arguments provided!");
		}
			
		for (int i = 0; i < dfs.length; i++) {
			String mimeType = dfs[i].getMimeType();
			DataHandler dh = new DataHandler(s[i], mimeType);
			handlersMap.put(mimeType, dh);
		}
	}
	
	public void addDataFlavor(String s, DataFlavor df) {
		String mimeType = df.getMimeType();
		DataHandler dh = new DataHandler(s, mimeType);
		handlersMap.put(mimeType, dh);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		
		DataHandler dh = handlersMap.get(flavor.getMimeType());
		if (dh == null) {
			throw new UnsupportedFlavorException(flavor);
		}
		
		return dh.getTransferData(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] result = new DataFlavor[handlersMap.size()];
		int i = 0;
		for (Iterator<DataHandler> iterator = handlersMap.values().iterator(); iterator.hasNext();) {
			DataHandler dh = (DataHandler) iterator.next();
			result[i] = dh.getTransferDataFlavors()[0];
			i++;
		}
		
		return result;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataHandler dh = handlersMap.get(flavor.getMimeType());
		
		return (dh != null);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

}
