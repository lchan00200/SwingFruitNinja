/**
 * CS349 Winter 2014
 * Assignment 3 Demo Code
 * Jeff Avery & Michael Terry
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {
  static private Model model;
  static private View view;
  static private TitleView title;
  static private int refreshDelay;
  static private int startingDelay, spawnDelay, minDelay;
  static private int timerDelay;

  /*
   * Main entry point for the application
   */
  public static void main(String[] args) {
    // instantiate your model and views
    // add any new views here
    model = new Model();
    title = new TitleView(model);
    view = new View(model);
    refreshDelay = 3;
    startingDelay = 2500;
    minDelay = 500;
    spawnDelay = startingDelay;
    timerDelay = 10;

    // customize the title and any other top-level settings
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.add(title, BorderLayout.NORTH);
    frame.add(view, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
    frame.setSize(800,600);
    
    ActionListener refreshView = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        model.updatePositions();
      }
    };
   
    ActionListener fruitSpawner = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Fruit f = new Fruit(new Area(new Ellipse2D.Double(0, 0, 50, 50)), false, false);
        model.add(f);
      }
    };

    ActionListener sliceListener = new ActionListener() {
    	public void actionPerformed(ActionEvent evt) {
    		model.removeOldSlices();
    	}
    };
    
    ActionListener timeListener = new ActionListener() {
    	public void actionPerformed(ActionEvent evt) {
            model.updateTime(timerDelay);
            
            int newDelay = startingDelay - model.getScore() * 10;
            spawnDelay = newDelay > minDelay? newDelay: minDelay;
    	}
    };
    
    // re-draw the screen elements
    Timer refreshTimer = new Timer(refreshDelay , refreshView);
    refreshTimer.start();
  
    // spawn a fruit every second
    Timer spawnTimer = new Timer(spawnDelay, fruitSpawner);
    spawnTimer.start();
    
    Timer sliceTimer = new Timer(timerDelay, sliceListener);
    sliceTimer.start();
    
    // score timer
    Timer scoreTimer = new Timer(timerDelay, timeListener);
    scoreTimer.start();
    
    // checks for when to end game
    while (true) {
    	spawnTimer.setDelay(spawnDelay);
    	
    	if (model.isGameOver()) {
    		refreshTimer.stop();
    		spawnTimer.stop();
    		sliceTimer.stop();
    		scoreTimer.stop();
    	}
    	
		if (model.isReset()) {
			refreshTimer.start();
			spawnTimer.start();
			sliceTimer.start();
			scoreTimer.start();
			model.setReset(false);
		}
    }
  }
}
