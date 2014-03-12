/**
 * CS349 Winter 2014
 * Assignment 3 Demo Code
 * Jeff Avery & Michael Terry
 */
import javax.swing.*;

import java.awt.*;

/*
 * View to display the Title, and Score
 * Score currently just increments every time we get an update
 * from the model (i.e. a new fruit is added).
 */
public class TitleView extends JPanel implements ModelListener {
  private Model model;
  private JLabel title, score, time, miss, highScore;
  private int points = 0;
  private int missed = 0;
  private int bestScore = 0;
  private int elapsedTime = 0;

  // Constructor requires model reference
  TitleView (Model model) {
    // register with model so that we get updates
    this.model = model;
    this.model.addObserver(this);

    // draw something
    setBorder(BorderFactory.createLineBorder(Color.black));
    setBackground(Color.YELLOW);
    // You may want a better name for this game!
    title = new JLabel(" Way of the Fruit Bushido");
    highScore = new JLabel();
    miss = new JLabel();
    score = new JLabel();
    time = new JLabel();

    JPanel scoreInfo = new JPanel();
    scoreInfo.setLayout(new BorderLayout());
    scoreInfo.setOpaque(false);
    scoreInfo.add(miss, BorderLayout.WEST);
    scoreInfo.add(score, BorderLayout.CENTER);
    scoreInfo.add(time, BorderLayout.EAST);
    
    // use border layout so that we can position labels on the left and right
    this.setLayout(new BorderLayout());
    this.add(title, BorderLayout.WEST);
    this.add(highScore, BorderLayout.CENTER);
    this.add(scoreInfo, BorderLayout.EAST);
  }

  // Panel size
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500,35);
  }

  // Update from model
  // This is ONLY really useful for testing that the view notifications work
  // You likely want something more meaningful here.
  @Override
  public void update() {
    paint(getGraphics());
  }

  // Paint method
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    highScore.setText("            Best Score: " + bestScore);
    miss.setText("Missed: " + missed + "  ");
    score.setText("Points: " + points + "  ");
    
	int seconds = (elapsedTime/1000 < 60)? elapsedTime/1000: (elapsedTime/1000) % 60;
	int minutes = elapsedTime/60000;
		
	if (seconds < 10) {
		time.setText("Time: " + minutes + ":0" + seconds + "  ");
	} else {
		time.setText("Time: " + minutes + ":" + seconds + "  ");
	}
  }
  
  public void updatePoints() {
	 points += 10;
	 score.repaint();
  }
  
  public int getPoints() {
	  return points;
  }
  
  public void updateMissed() {
	 missed++;
	 
	 miss.repaint();
	 
	 if (missed == 5) {
		 model.gameOver(points);
	 }
  }
  
  public void updateTime(int refreshDelay) {
	  elapsedTime += refreshDelay;
	  time.repaint();
  }

  public void reset() {
	  bestScore = points > bestScore? points: bestScore;
	  points = 0;
	  missed = 0;
	  elapsedTime = 0;
  }
}
