package com.echoss.opencv310.util;

import java.awt.*;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TaImage extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// constant 
	public static int XMARGIN = 0;
	public static int YMARGIN = 0;	
	
	// variables
	private double scale = 1.0;
	
	// image
	private BufferedImage image = null;

	// contrutor 
	public TaImage() {
		super();
	}
	
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
    	
		if( image != null ) {
	    	int maxW = (int)((getWidth() - XMARGIN*2)*scale);
	    	int maxH = (int)((getHeight() - YMARGIN*2)*scale);
	    	
    		int w = (int)(image.getWidth());
    		int h = (int)(image.getHeight());
    		
    		double r = 1;
    		if( w > maxW ) r = (double)maxW/w;
    		if( h > maxH ) r = Math.min( r, (double)maxH/h );
    		w *= r * scale;
    		h *= r * scale;
    		
    		g.drawImage(image, XMARGIN, YMARGIN, w, h, null);
		}
	}
}
