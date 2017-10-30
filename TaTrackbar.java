package com.echoss.opencv310.util;

/*
 * TaTrackbar is a Simple implementation of Highgui functions
 * which is not supported in Java OpenCV now
 * 
 * - createTrackbar
 * - setTrackbarPos
 * - getTrackbarPos
 * 
 * @author Firstwave
 * 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TaTrackbar implements ChangeListener, MouseListener {

	/**
	 * 
	 */

	private String mTrackbarName;
	
	private JPanel mPanel;
	private JSlider mSlider;
	private JLabel mLabel;
	private TrackbarCallback trackbarCallback;
	private Object trackbarUserdata;
	private int prev_pos;

	/* 
	 * <pre>
	 * trackbar name getter
	 * </pre>
	 * 
	 * @return 트랙바 이름
	 *  
	 */
	public String getTrackbarName() {
		return mTrackbarName;
	}
	
	/* 
	 * <pre>
	 * trackbar panel getter
	 * </pre>
	 * 
	 * @return 트랙바 panel
	 *  
	 */
	public JPanel getTrackbarPanel() {
		return mPanel;
	}

	/* 
	 * <pre>
	 * trackbar constructor
	 * </pre>
	 * 
	 * @param trackbarname 	name of trackbar 
	 * @param value			initial value
	 * @param count			max value (min value is fixed as 0)
	 * @param onChange		TrackbarCallback interface callback function
	 * @param userdata		any user data for callback function. This data is re-inputed to callback function parameter (option)
	 * 
	 * @return 
	 *  
	 */
	public TaTrackbar(final String trackbarName, int value, int count, TrackbarCallback onChange, Object userdata ) {
		mTrackbarName = trackbarName;
		trackbarCallback = onChange;
		trackbarUserdata = userdata;
		
		mPanel = new JPanel();
		mPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		BoxLayout layout = new BoxLayout(mPanel, BoxLayout.LINE_AXIS);
		mPanel.setLayout( layout );
		
		int minW = 60 + 240;
//		int minW = 200;
		
		double posValue = trackbarCallback.getPosValue(value, trackbarUserdata);
		mLabel = new JLabel(trackbarName + " " + posValue);
		
		mLabel.setPreferredSize( new Dimension( (int)(minW*0.4), TaNamedWindow.SLIDER_H));
		mPanel.add( mLabel ); 

		mSlider = new JSlider(0, count, value);
		prev_pos = value;
		
		mSlider.setLabelTable(mSlider.createStandardLabels(count));
		mSlider.setPaintLabels(true);

		mSlider.setPreferredSize( new Dimension((int)(minW*0.6), TaNamedWindow.SLIDER_H));
		mSlider.setVisible(true);
		mSlider.addChangeListener( new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				int cur_pos = mSlider.getValue();
				if( cur_pos != prev_pos ) {
					double posValue = trackbarCallback.getPosValue(cur_pos, trackbarUserdata);
					mLabel.setText(trackbarName+ " " + posValue );
					prev_pos = cur_pos;
				}
			}
		});
		mSlider.addMouseListener(this);
		mPanel.add(mSlider);
		
	}
	
	/* 
	 * <pre>
	 * trackbar poisition getter 
	 * 
	 * </pre>
	 * 
	 *  
	 * @return trackbar current posistion
	 *  
	 */
	public int getTrackbarPos() {
		return mSlider.getValue();
	}
	
	/* 
	 * <pre>
	 * trackbar position setter 
	 * 
	 * </pre>
	 * 
	 * @param pos 	new position Value
	 *  
	 * @return 
	 *  
	 */
	public void setTrackbarPos(int pos) {
		mSlider.setValue(pos);
	}

	public interface TrackbarCallback {
		public abstract void onChange(int pos, Object userdata );
		public abstract double getPosValue(int pos, Object userdata );
	}
	
	/* 
	 * <pre>
	 * trackbar callback function setter
	 * 
	 * </pre>
	 * 
	 * @param userdata			optional parameter for callback  function 
	 *  
	 * @return 
	 *  
	 */
	public void setTrackbarCallback(TrackbarCallback onChange, Object userdata ) {
		trackbarCallback = onChange;
		trackbarUserdata = userdata;
	}
	
	public void stateChanged(ChangeEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if( trackbarCallback != null ) {
			trackbarCallback.onChange( mSlider.getValue(), trackbarUserdata);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}
