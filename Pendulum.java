
// PENDULUM MOTION with GRAPHICS

import java.awt.*;
import java.awt.event.*;

class Pendulum extends Canvas implements Runnable {
	
	// INSTANCE VARIABLES
	// set damping = 0.5 for a damped pendulum
	double damping = 0.5;
	// set driveAmp = 0.5, driveFreq = 2.0/3.0 for a drived pendulum
	double driveAmp = 0.5, driveFreq = 2.0/3.0;
	// amplitude of pendulum
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
	
	// method to compute the angular acceleration
	// in the Euler-Richardson algorithm in the launch method
	double torque(double theta, double omega, double t) {
		return - Math.sin(theta) - damping*omega +
			driveAmp*Math.sin(driveFreq*t);
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
			// but slow down the calculations:
			try {Thread.sleep(10);} catch (InterruptedException e) {}
		}
	}
	
	// - - -
	// - - -
	// - - -
	
	// CONSTRUCTOR METHOD
	Pendulum() {
		// setSize is a method of Canvas that is inherited by Pendulum
		setSize(WIDTH, LENGTH);
		
		Frame pictureFrame = new Frame("Driven, damped pendulum.");
		
		pictureFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		Panel canvasPanel = new Panel();
		
		// "this" makes reference to the object Pendulum itself
		canvasPanel.add(this);
		
		pictureFrame.add(canvasPanel);
		pictureFrame.pack();
		pictureFrame.setVisible(true);
		
		// RUN SIMULATION!
		// calling .start() on this thread calls the run method
		Thread myThread = new Thread(this);
		myThread.start();
	}
	
	// PAINT METHOD
	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.fillOval(bobX(theta) - bobW/2, bobY(theta) - bobW/2, bobW,
			bobW);
		g.setColor(Color.black);
		g.fillOval(pivotX - pivotW/2, pivotY - pivotW/2, pivotW,
			pivotW);
		g.drawLine(pivotX, pivotY, bobX(theta), bobY(theta));
	}
	
	// - - -
	// - - -
	// - - -
	
	// MAIN METHOD
	public static void main(String[] arg) {
		
		new Pendulum();
		
		// the calculations of the pendulum (and its infinite loop)
		// are run in a separate thread (myThread), so other stuff can
		// be done here:
		System.out.println("Hello, world! Long thyme, no c.");
	}
	
}
