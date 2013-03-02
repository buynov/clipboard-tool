package com.buynov.clipboard;

import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JToolTip;

public class TrayIconMouseListener implements MouseListener, MouseMotionListener {

	TrayIcon trayIcon;
	JToolTip toolTip;
	
	public TrayIconMouseListener(TrayIcon ti) {
		if (ti == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		this.trayIcon = ti;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("clicked ...");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		trayIcon.setToolTip(SystemClipboard.getInstance().getPreferredContents());
		System.out.println("Tooltip changed");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		trayIcon.setToolTip("Rendering ...");
		System.out.println("exitted ...");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("pressed ...");

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("released ...");

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.println("dragged ...");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		System.out.println("moved ...: " + e);
		JToolTip toolTip = getToolTip();
		//toolTip.setLocation(0,0);
		toolTip.setVisible(true);
		System.out.println(toolTip.getVisibleRect());
	}

	private JToolTip getToolTip() {
		if (toolTip == null) {
			toolTip = new JToolTip();
		}
		toolTip.setTipText(SystemClipboard.getInstance().getPreferredContents());
		
		return toolTip;
	}
}
