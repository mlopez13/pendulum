
// PENDULUM MOTION with GRAPHICS

import java.awt.Frame;
import java.awt.Panel;
import java.awt.Button;

import java.awt.GridLayout;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;
import java.awt.Toolkit;

class Pendulum extends Canvas implements Runnable {
		
	// INSTANCE VARIABLES
	double damping;
	double driveAmp, driveFreq;
	double theta = 30*Math.PI/180;
	
	// GRAPHICS PARAMETERS
	// canvas size
	int WIDTH = 400, LENGTH = 400;
	// pivot position and width
	int pivotX = WIDTH/2, pivotY = LENGTH/2, pivotW = 10;
	// rod length
	int L = 150;
	// bob width
	int bobW = 40;
	
	// CONTROL PANEL OBJECTS
	// labelscroll
	LabelScroll dampingScroll = new LabelScroll("Damping constant: ", 0, 1, 0.01, 0.5);
	LabelScroll driveAmpScroll = new LabelScroll("Drive amplitude constant: ", 0, 2, 0.02, 0.5);
	LabelScroll driveFreqScroll = new LabelScroll("Drive frequency constant: ", 0, 1, 0.01, 2./3.);
	
	// RUNNING SWITCH
	boolean running = false;
	
	// - - -
	// - - -
	// - - -
	
	// CONSTRUCTOR METHOD
	Pendulum() {
		
		// setSize is a method of Canvas that is inherited by Pendulum
		setSize(WIDTH, LENGTH);
		setBackground(Color.white);
		
		Frame pictureFrame = new Frame("Driven, damped pendulum.");
		
		pictureFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// CANVAS PANEL
		Panel canvasPanel = new Panel();
		// "this" makes reference to the object Pendulum itself
		canvasPanel.add(this);
		pictureFrame.add(canvasPanel);
		
		// CONTROL PANEL
		Panel controlPanel = new Panel();
		controlPanel.setLayout(new GridLayout(0, 1));
		
		controlPanel.add(dampingScroll);
		controlPanel.add(driveAmpScroll);
		controlPanel.add(driveFreqScroll);
		
		// button
		Button staStoButton = new Button("Start.");
		
		staStoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = !running;
				staStoButton.setLabel(running ? "Stop." : "Start.");
			}
		});
		
		controlPanel.add(staStoButton);
		
		pictureFrame.add(controlPanel, BorderLayout.SOUTH);
		
		// FRAME PACKING
		pictureFrame.pack();
		pictureFrame.setVisible(true);
		
		// RUN SIMULATION!
		// calling .start() on this thread calls the run method
		Thread myThread = new Thread(this);
		myThread.start();
	}
	
	// - - -
	// - - -
	// - - -
	
	// METHODS
	
	// bob position, X and Y
	int bobX(double theta) {
		return WIDTH/2 + (int) Math.round(L*Math.sin(theta));
	}
	int bobY(double theta) {
		return LENGTH/2 + (int) Math.round(L*Math.cos(theta));
	}
	
	// method to compute the angular acceleration in the Euler-Richardson algorithm in the launch method
	double torque(double theta, double omega, double t) {
		return - Math.sin(theta) - damping*omega + driveAmp*Math.sin(driveFreq*t);
	}
	
	// this method has to be written, as Pendulum implements Runnable
	public void run() {
		// INITIALISATION of VARIABLES
		double dt = 0.002, t = 0;
		double omega = 0, alpha;
		// variables needed for the Euler-Richardson algorithm
		double thetaMid, omegaMid, alphaMid;
		
		// EULER-RICHARDSON ALGORITHM
		while (true) {
			
			if (running) {

				// update driveAmp from scrollbar:
				damping = dampingScroll.getValue();
				driveAmp = driveAmpScroll.getValue();
				driveFreq = driveFreqScroll.getValue();
				
				// do 0.1/dt iterations before painting
				for (int i = 0; i < 0.1/dt; i++) {
					alpha = torque(theta, omega, t);
					
					// middle point (Richardson)
					thetaMid = theta + omega*0.5*dt;
					omegaMid = omega + alpha*0.5*dt;
					alphaMid = torque(thetaMid, omegaMid, t+0.5*dt);
					
					theta += omegaMid*dt;
					omega += alphaMid*dt;
					
					// Increment of time
					t += dt;
				}
				
				// now paint:
				repaint();

			}
			
			// and slow down the calculations:
			try {Thread.sleep(10);} catch (InterruptedException e) {}
			
		}
	}
	
	// - - -
	// - - -
	// - - -
	
	// PAINT METHOD
	public void paint(Graphics g) {
		// bob
		g.setColor(Color.red);
		g.fillOval(bobX(theta) - bobW/2, bobY(theta) - bobW/2, bobW, bobW);
		// pivot
		g.setColor(Color.black);
		g.fillOval(pivotX - pivotW/2, pivotY - pivotW/2, pivotW, pivotW);
		// rod
		g.drawLine(pivotX, pivotY, bobX(theta), bobY(theta));
		
		// to avoid animation stuttering:
		Toolkit.getDefaultToolkit().sync();
	}
	
	// - - -
	// - - -
	// - - -
	
	// MAIN METHOD
	public static void main(String[] arg) {
		
		new Pendulum();
		
		// the calculations of the pendulum (and its infinite loop) are run in a separate thread (myThread), so any
		// code below these lines will also be executed:
		
		System.out.println("Hello, world!");
		
	}
	
}
