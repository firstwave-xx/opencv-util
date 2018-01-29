package com.echoss.opencv310.util;

/**
 * Tagui is a Simple implementation of Highgui functions
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
 * - destryWindow
 * - destryAllWindow
 * 
 * @author Firstwave
 * 
 */

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.opencv.core.Mat;

import com.echoss.opencv310.util.TaNamedWindow.MouseCallback;
import com.echoss.opencv310.util.TaTrackbar.TrackbarCallback;

public class Tagui {

	// constant
	public final static int WINDOW_NORMAL = 0;
	public final static int WINDOW_AUTOSIZE = 1;
	public static double scale = 1.0;
	
	// attribute
	private static ArrayList<TaNamedWindow> namedWindowList = new ArrayList<TaNamedWindow>();
	private static int keyUserdata[] = new int[2];		// [0] = id [1] = keycode
	private static KeyEventDispatcher dispatcher;

	/** 
	 * create tagui window with flag WINDOW_AUTOSIZE<br> 
	 * Change return type to TaNamedWindow for use sequencial "." operation <br>
	 * It can be use like this:<br>
	 * namedWindow("Source").imshow( src );<br>
	 * 
	 * @param winname name of window
	 * 
	 * @return named window object which is created or already created
	 *  
	 */
	public static TaNamedWindow namedWindow(final String winname) {
		return namedWindow(winname, WINDOW_AUTOSIZE );
	}

	/**
	 * creae named window with flag.  If flags equal 0 means WINDOW_NORMAL , equal 1 means WINDOW_AUTOSIZE<br>
	 * WINDOW_NORMAL let user change the size of window <br>
	 * WINDOW_AUTOSIZE the size is chosen by automatic method<br> 
	 * Change return type to TaNamedWindow for use sequencial "." operation<br> 
	 * It can be use like this:<br>
	 * namedWindow("Source", 0).resizeWindow(100,100).imshow( src );
	 * 
	 * @param winname name of window
	 * @param flags  selection of window sizing method
	 * 
	 * @return named window object which is created or already created
	 *  
	 */
	public static TaNamedWindow namedWindow(final String winname, int flags) {
		int index = findWindowIndex(winname);
		if( index == -1 ) {
			TaNamedWindow win = new TaNamedWindow( winname, flags );
			namedWindowList.add( win );
			return win;
		}
		else {
			return namedWindowList.get(index);
		}
	}

	/** 
	 * create named window with width and height as WINDOW_NORMAL<br>
	 * 
	 * width and height mean is not window total size but image panel size<br> 
	 * 
	 * and all image show request is resized to this setting<br> 
	 * this is same as namedWindow("winName",0).resizeWindow(w, h) for the first time only.<br>
	 * 
	 * @param winname 	name of window
	 * @param width  	image panel width pixel
	 * @param height 	image panel height pixel
	 * 
	 * @return TaNamedWindow object created
	 *  
	 */
	public static TaNamedWindow namedWindow(final String winname, int w, int h) {
		int index = findWindowIndex(winname);
		if( index == -1 ) {
			TaNamedWindow win = new TaNamedWindow( winname, WINDOW_NORMAL ).resizeWindow(w, h);
			namedWindowList.add( win );
			return win;
		}
		else {
			return namedWindowList.get(index);
		}
	}
	
	
	/** 
	 * create named window with (width, height) and (x,y) postion also as WINDOW_NORMAL<br>
	 * 
	 * width and height mean is not window total size but image panel size <br>
	 * and all image show request is resized to this setting <br>
	 * this is same as namedWindow("winName",0).resizeWindow(w, h)<br> 
	 * 
	 * x, y is position from full screen <br>
	 * this is same as namedWindow("winName",0).resizeWindow(w, h).moveWindow(x,y) for the first time only. <br>
	 * repeat call ignore the resize and move <br>
	 * 
	 * @param winname 	name of window 
	 * @param width  	image panel width pixel
	 * @param height 	image panel height pixel
	 * @param x  		x position of window top left from screen
	 * @param y 		y position of window top left from screen
	 * 
	 * @return TaNamedWindow object created
	 *  
	 */
	public static TaNamedWindow namedWindow(final String winname, int w, int h, int x, int y) {
		int index = findWindowIndex(winname);
		if( index == -1 ) {
			TaNamedWindow win = new TaNamedWindow( winname, WINDOW_NORMAL ).resizeWindow(w, h).moveWindow(x, y);
			namedWindowList.add( win );
			return win;
		}
		else {
			return namedWindowList.get(index);
		}
	}
	
	
	/** 
	 * draw Mat as image and support description at title 
	 * 
	 * @param winname 	name of window 
	 * @param mat  		opencv Mat
	 * @param title 	description. This show the window title as  winame - title . 
	 * 
	 * @return 
	 *  
	 */
	public static void imshow(final String winname, Mat mat, String title) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			TaNamedWindow win = new TaNamedWindow( winname, WINDOW_NORMAL );
			namedWindowList.add( win );
			index = namedWindowList.size() - 1;
		}
		
		namedWindowList.get(index).imshow(mat, title);
	}
	
	/** 
	 * draw Mat
	 * 
	 * @param winname 	name of window 
	 * @param mat  		opencv Mat
	 * 
	 * @return 
	 *  
	 */
	public static void imshow(final String winname, Mat mat) {
		imshow(winname, mat, null);
	}
	
	/** 
	 * destroy window<br>
	 * 
	 * called by program or automatically called by pressing close button <br>
	 * if this is the last window in screen, close program <br>
	 * 
	 * @param winname 	name of window
	 * 
	 * @return 
	 *  
	 */
	public static void destroyWindow(final String winname) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return;
		}
		
		namedWindowList.get(index).destroyWindow();
		namedWindowList.remove(index );
		
		checkEmptyWindow();
	}
	
	/** 
	 * destory all windows <br>
	 * and close program too.
	 * 
	 * @param 
	 * 
	 * @return 
	 *  
	 */
	public static void destroyAllWindows() {
		for( int i=namedWindowList.size()-1 ; i>=0 ; i-- ) {
			namedWindowList.get(i).destroyWindow();
			namedWindowList.remove(i);
		}
		
		checkEmptyWindow();
	}
	
	/**
	 * move window
	 * 
	 * @param winname 	name of window
	 * @param x  		x position of window top left from screen
	 * @param y 		y position of window top left from screen
	 *
	 * @return 
	 *  
	 */
	public static TaNamedWindow moveWindow(final String winname, int x, int y) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return null;
		}
		
		return namedWindowList.get(index).moveWindow( x, y);
	}

	/** 
	 * resize window 
	 * 
	 * @param winname 	name of window
	 * @param width  	image panel width pixel
	 * @param height 	image panel height pixel
	 *  
	 * @return 
	 *  
	 */
	public static TaNamedWindow resizeWindow(final String winname, int width, int heigth) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return null;
		}
		
		return namedWindowList.get(index).resizeWindow( width, heigth );
	}
	
	/** 
	 * update UI<br>  
	 * This is not used.
	 * 
	 * @param winname 	name of window
	 *  
	 * @return 
	 *  
	 */
	public static void updateWindow(final String winname) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return;
		}
		
		namedWindowList.get(index).updateWindow();
		
	}

	
	/** 
	 * mouse callback function setter 
	 * 
	 * @param winname 	name of window
	 * @param onMouse	MouseCallback interface callback function
	 * @param userdata	optional userdata for callback function 
	 *  
	 * @return 
	 *  
	 */
	public static void setMouseCallback(final String winname, MouseCallback onMouse, Object userdata) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return;
		}
		
		namedWindowList.get(index).setMouseCallback(onMouse, userdata);		
	}
	
	/** 
	 * create trackbar  
	 * 
	 * @param trackbarname 	name of trackbar
	 * @param winname 		name of window
	 * @param value			initial value
	 * @param count			max value (min value is fixed as 0)
	 * @param onChange		TrackbarCallback interface callback function
	 * @param userdata		optional userdata for callback function 
	 *  
	 * @return 
	 *  
	 */
	public static int createTrackbar(final String trackbarname, final String winname,  int value, int count, TrackbarCallback onChange, Object userdata ) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return -1;
		}
		
		namedWindowList.get(index).createTrackbar(trackbarname, value, count, onChange, userdata );		
		
		return namedWindowList.get(index).findTrackbarIndex(trackbarname);
	}
	
	/** 
	 * trackbar position setter 
	 * 
	 * @param trackbarname 	name of trackbar
	 * @param winname 		name of window
	 * @param pos			new position value
	 *  
	 * @return 
	 *  
	 */
	public static void setTrackbarPos(final String trackbarname, final String winname, int pos) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return;
		}
		
		namedWindowList.get(index).setTrackbarPos(trackbarname, pos);		
	}
	
	/**
	 * trackbar position getter 
	 * 
	 * @param trackbarname 	name of trackbar
	 * @param winname 		name of window
	 *  
	 * @return trackbar current pos
	 *  
	 */
	public int getTrackbarPos(final String trackbarname, final String winname ) {
		int index = findWindowIndex(winname);
		
		if( index == -1 ) {
			return -1;
		}
		
		return namedWindowList.get(index).getTrackbarPos(trackbarname);		
	}
	
	/**
	 * wait a keyboard press 
	 * 
	 * @param timeout		mili second(1/1000 second) timeout value. 
	 * 		if larger than zero,  wait key input or timeout is under zero
	 *      if equal 0 , wait until key input 
	 *                      
	 * @return key			input keycode, or -1 when timeout
	 *  
	 */
	private static Object syncObject = new Object();
	private static int lastKey = 0;
	
	public static int waitKey(int delay) {
		try {
			if(delay==0) {
				synchronized (syncObject) {
					syncObject.wait();
				}
			}
			Thread.sleep(delay);
		} catch(Exception e) {
			
		}
		int ret = -1;
		if( keyUserdata[1] != lastKey ) {
			ret = keyUserdata[1];
			lastKey = ret;
		}
		return ret;
	}
	
	
	
	private static void checkEmptyWindow() {
		if( namedWindowList.isEmpty() ) {
			System.out.println( "Tagui has no window now and exit.");
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
			System.exit(1);
		}
	}
	
	private static int findWindowIndex(final String winame) {
		int index = -1;
		int nsize = namedWindowList.size();
		for( int i=0; i<nsize; i++ ) {
			if( namedWindowList.get(i).getWindowName().equals(winame) ) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
	static {
		KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
		    	if( e.getID() == KeyEvent.KEY_PRESSED ) {
					synchronized (syncObject) {
						keyUserdata[0] = e.getID();
						keyUserdata[1] = e.getKeyCode();	// waitKey value
						syncObject.notifyAll();
					}
		    	}
		    	return false;
			}
		};
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		  .addKeyEventDispatcher(dispatcher);
	}
	

	
}
