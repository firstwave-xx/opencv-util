package com.echoss.opencv310.util;

/*
 * TaNamedWindow is a Simple implementation of Highgui functions
 * which is not supported in Java OpenCV now
 * 
 * - namedWindow
 * - moveWindow
 * - ResizeWindow
 * - imshow
 * - createTrackbar
 * - setTrackbarPos
 * - getTrackbarPos
 * - setMouseCallback
 * - waitKey
 * - updateWindow
 * - destroyWindow
 * - destroyAllWindow 
 * 
 * @author Firstwave
 * 
 */

import java.awt.BorderLayout;
//import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
//import org.opencv.core.Size;

import com.echoss.opencv310.util.TaTrackbar.TrackbarCallback;
//OpenCV 2.x => 3.x (Java)
//read write function Highgui.* => Imgcodecs.*
//drawing fucntion Core.circle, Core.line => Imgproc.circle, Imgproc.line
import static org.opencv.imgcodecs.Imgcodecs.*;
//import static org.opencv.imgproc.Imgproc.*;

public class TaNamedWindow implements MouseListener {
	
	// constant
	static final double SCREEN_W = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static final double SCREEN_H = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	static final int SLIDER_H = 40;
	static final int MARGIN_W = 80;
	static final int MARGIN_H = 20;
	static final int MAX_W = (int)(SCREEN_W - MARGIN_W*2);
	static final int MAX_H = (int)(SCREEN_H - MARGIN_H*2);
	

	// variable
	private String mWiname;
	private JFrame mWindow;
	
	private JPanel mSliderPanel;	// horizontal layered sliders 
	private ArrayList<TaTrackbar> trackbarList = new ArrayList<TaTrackbar>(); 
	
	private TaImage mImagePanel;
	private int mHeight, mWidth;	// imagePanel size (not window size)
	private BufferedImage bufImage = null;
	private MatOfByte mMatOfByte;
	private double r_width = 1, r_height = 1;	// rate of image/image panel by width and height 

	private int mFlags;				// 0: normal , 1: autosize
	
	private MouseCallback mouseCallback;
	private Object mouseUserdata;
	
	
	public Object getWindowName() {
		return mWiname;
	}
	
	public TaNamedWindow(String winname, int flags) {
		mWiname = winname;
		mFlags = flags;

		mWindow = new JFrame();
		mWindow.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Tagui.destroyWindow(mWiname);
            }			
		});
		
		mSliderPanel = new JPanel();
		BoxLayout layout = new BoxLayout(mSliderPanel, BoxLayout.Y_AXIS);
		mSliderPanel.setLayout(layout);
		
		mMatOfByte = new MatOfByte();
		mImagePanel = new TaImage();
		
		mWindow.getContentPane().add(BorderLayout.NORTH, mSliderPanel);
		mWindow.getContentPane().add(BorderLayout.CENTER, mImagePanel);
	    
		mWidth = MAX_W/2;
		mHeight = MAX_H/2;
		mImagePanel.setPreferredSize( new Dimension(mWidth, mHeight ) );

		if( flags == Tagui.WINDOW_AUTOSIZE ) {
 			mWindow.setResizable(false);
		}
		else {
 			mWindow.setResizable(true);
		}

		mWindow.setSize( mWidth, mHeight);
		mWindow.setVisible(true);
		mWindow.setTitle(mWiname);
		mWindow.setFocusable(true);

		// mouse call on mWindow ? mImagePanel ?
		mImagePanel.addMouseListener(this);
	}

	public void destroyWindow() {
		mWindow.setVisible(false);
		mMatOfByte = null;
		mHeight = mWidth = 0;
		mFlags = 0;
		
		System.out.println( mWindow.getTitle() + " disposed." );
		mWindow.dispose();
	}

	public TaNamedWindow moveWindow(int x, int y) {
		mWindow.setLocation(x, y);
		
		return this;
	}
	
	public TaNamedWindow resizeWindow(int width, int height) {
		if( height != mHeight || height != mWidth ) {
			mHeight = height;
			mWidth = width;

			if( bufImage != null ) {
				r_width = (double)mHeight/bufImage.getWidth();
				r_height = (double)mWidth/bufImage.getHeight();
			}
			
			try {
				mImagePanel.setPreferredSize( new Dimension(mWidth, mHeight) );
				mWindow.pack();
				
				mImagePanel.invalidate();
				mImagePanel.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return this;
	}
	
	public interface MouseCallback {
		public abstract void onMouse(int event, int x, int y, Object userdata );
	}
	
	public void setMouseCallback(MouseCallback onMouse, Object userdata ) {
		mouseCallback = onMouse;
		mouseUserdata = userdata;
	}
	
	public void mouseClicked(MouseEvent e) {
		if( mouseCallback != null ) {
			mouseCallback.onMouse(e.getID(), e.getX(), e.getY(), mouseUserdata);
		}
	}

	public void mousePressed(MouseEvent e) {
		if( mouseCallback != null ) {
			System.out.println("("+e.getX()+","+e.getY()+") => ("+(int)(e.getX() / r_width)+","+(int)(e.getY() / r_height)+")");
			mouseCallback.onMouse(e.getID(), (int)(e.getX() / r_width), (int)(e.getY() / r_height), mouseUserdata);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if( mouseCallback != null ) {
			System.out.println("("+e.getX()+","+e.getY()+") => ("+(int)(e.getX() / r_width)+","+(int)(e.getY() / r_height)+")");
			mouseCallback.onMouse(e.getID(), (int)(e.getX() / r_width), (int)(e.getY() / r_height), mouseUserdata);
		}
	}

	public void mouseEntered(MouseEvent e) {
		if( mouseCallback != null ) {
			System.out.println("("+e.getX()+","+e.getY()+") => ("+(int)(e.getX() / r_width)+","+(int)(e.getY() / r_height)+")");
			mouseCallback.onMouse(e.getID(),(int)(e.getX() / r_width), (int)(e.getY() / r_height), mouseUserdata);
		}
	}

	public void mouseExited(MouseEvent e) {
		if( mouseCallback != null ) {
			System.out.println("("+e.getX()+","+e.getY()+") => ("+(int)(e.getX() / r_width)+","+(int)(e.getY() / r_height)+")");
			mouseCallback.onMouse(e.getID(),(int)(e.getX() / r_width), (int)(e.getY() / r_height), mouseUserdata);
		}
	}
	

	protected int findTrackbarIndex(final String trackbarname) {
		int index = -1;
		for(int i=0 ; i < trackbarList.size(); i++ ) {
			if( trackbarname.equals(trackbarList.get(i).getTrackbarName()) ) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	public TaNamedWindow createTrackbar(final String trackbarname, int value, int count, TrackbarCallback onChange, Object userdata ) {
		int index = findTrackbarIndex(trackbarname);
		if( index == -1 ) {
			TaTrackbar tb = new TaTrackbar( trackbarname, value, count, onChange, userdata );
			trackbarList.add( tb );
			mSliderPanel.add( tb.getTrackbarPanel() );
			mWindow.pack();
			mSliderPanel.invalidate();
			mSliderPanel.repaint();
		}
		
		return this;
	}
	
	public TaNamedWindow setTrackbarPos(String trackbarname, int pos) {
		int index = findTrackbarIndex(trackbarname);
		if( index >= 0 ) {
			trackbarList.get(index).setTrackbarPos(pos);
		}
		
		return this;
	}

	public int getTrackbarPos(final String trackbarname ) {
		int index = findTrackbarIndex(trackbarname);
		if( index >= 0 ) {
			return trackbarList.get(index).getTrackbarPos();
		}
		
		return -1;
	}
	
	public TaNamedWindow imshow(Mat img, String title) {
		if( title != null ) {
			mWindow.setTitle(mWiname + " - " + title );
		}
		
		return imshow(img);
	}

	public TaNamedWindow imshow(Mat img) {
		
		
		try {
			imencode(".png", img, mMatOfByte);
			byte[] byteArray = mMatOfByte.toArray();
			InputStream in = new ByteArrayInputStream(byteArray);
			BufferedImage srcImage = ImageIO.read(in);
			
			if (mFlags == Tagui.WINDOW_NORMAL ) {
				int w = mImagePanel.getPreferredSize().width;
				int h = mImagePanel.getPreferredSize().height;

				r_width = (double)w/img.width();
				r_height = (double)h/img.height();
				
				bufImage = new BufferedImage(w, h, srcImage.getType());
				Graphics2D g = bufImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(srcImage, 0,0, w, h, 0, 0, srcImage.getWidth(), srcImage.getHeight(), null);
				
				mImagePanel.setImage(bufImage);
	    	}
			else {
	    		int w = (int)(img.width() * Tagui.scale);
	    		int h = (int)(img.height() * Tagui.scale);
	    		
	    		double r = 1;
	    		if( w > MAX_W ) r = (double)MAX_W/w;
	    		if( h > MAX_H ) r = Math.min( r, (double)MAX_H/h );
	    		w *= r;
	    		h *= r;
	    		
				r_width = (double)w/img.width();
				r_height = (double)h/img.height();

				bufImage = new BufferedImage(w, h, srcImage.getType());
				Graphics2D g = bufImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.drawImage(srcImage, 0,0, w, h, 0, 0, srcImage.getWidth(), srcImage.getHeight(), null);

	    		mImagePanel.setPreferredSize(new Dimension(w, h));
	    		mImagePanel.setImage(bufImage);
	  
				mWindow.pack();
	    	}
			mImagePanel.invalidate();
			mImagePanel.repaint();
		} catch(Exception e ) {
			e.printStackTrace();
		}
			
		return this;
	}

	public TaNamedWindow updateWindow() {
		mWindow.invalidate();
		mWindow.repaint();
		
		return this;
	}
}
