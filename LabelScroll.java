
// LABEL + SCROLLBAR

import java.text.DecimalFormat;

import java.awt.Panel;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.Dimension;

import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

class LabelScroll extends Panel implements AdjustmentListener {
	
	// NUMBER FORMATTING
	DecimalFormat myFormat = new DecimalFormat("0.00");
	
	// INSTANCE VARIABLES
	Label myLabel;
	Scrollbar myScrollbar;
	
	String myText;
	double myValue;
	double myScale;
	
	LabelScroll(String text, double min, double max, double step, double ini) {
		
		myText = text;
		myValue = ini;
		
		// LABEL
		myLabel = new Label(text + myFormat.format(ini));
		
		// SCROLLBAR
		
		// 1. find decimalPlaces of step
		String stepString = String.valueOf(step);
		int decimalPlaces = stepString.length() - stepString.indexOf('.') - 1;
		
		myScale = Math.pow(10, decimalPlaces);
		
		// 2. scale min, max, ini
		int scrollMin = (int) (myScale*min);
		int scrollMax = (int) (myScale*max);
		int scrollIni = (int) (myScale*ini);
		int scrollWid = (int) (10*myScale*step);
		
		myScrollbar = new Scrollbar(Scrollbar.HORIZONTAL, scrollIni, scrollWid, scrollMin, scrollMax +
			scrollWid) {
			public Dimension getPreferredSize() {
				return new Dimension(100, 15);
			}};
		
		myScrollbar.addAdjustmentListener(this);
		
		add(myLabel);
		add(myScrollbar);
		
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int scrollValue = myScrollbar.getValue();
		myValue = scrollValue/myScale;
		myLabel.setText(myText + myFormat.format(myValue));
	}
	
	public double getValue() {
		return myValue;
	}
	
}
