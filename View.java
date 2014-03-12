/**
 * CS349 Winter 2014
 * Assignment 3 Demo Code
 * Jeff Avery & Michael Terry
 */
import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * View of the main play area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class View extends JPanel implements ModelListener {
    private Model model;
    private final MouseDrag drag;
    Graphics graphics;
    private boolean gameOver = false;
    private JPanel gameOverPanel;
    private JButton resetButton;
    private int score;

    // Constructor
    View (Model m) {
        model = m;
        model.addObserver(this);

        setBackground(Color.WHITE);
        View v = this;
        setLayout(new BoxLayout(v, BoxLayout.X_AXIS));
        
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel scoreLabel = new JLabel();
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        
        resetButton = new JButton();
        resetButton.setText("Play Again");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ActionListener resetListener = new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		model.setReset(true);
        		model.resetInstance();
        	}
        };
        
        resetButton.addActionListener(resetListener);
        
        gameOverPanel = new JPanel();        
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.add(gameOverLabel);
        gameOverPanel.add(scoreLabel);
        gameOverPanel.add(resetButton);
        gameOverPanel.setVisible(false);
        gameOverPanel.setBackground(Color.GREEN);
        gameOverPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        this.add(Box.createHorizontalGlue());
        this.add(gameOverPanel);
        this.add(Box.createHorizontalGlue());

        // drag represents the last drag performed, which we will need to calculate the angle of the slice
        drag = new MouseDrag();
        // add mouse listeners
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    // Update fired from model
    @Override
    public void update() {
        this.repaint();
    }

    public void displayGameOver(int points) {
    	score = points;
    	gameOver = true;
    }
    
    public boolean getGameOver() {
    	return gameOver;
    }
    
    // Panel size
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500,400);
    }

    // Paint this panel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        graphics = g;

        // draw all pieces of fruit
        // note that fruit is responsible for figuring out where and how to draw itself
        for (Fruit f : model.getShapes()) {
            f.draw(g2);
        }
        
        // draw all slices
        FruitSlicer currentSlice = model.getCurrentSlice();
        
        if (currentSlice != null) {
        	model.getCurrentSlice().draw(g2, Color.RED);
        }
        
        for (FruitSlicer s: model.getSlices()) {
        	s.draw(g2, Color.BLACK);
        }
        
        if (gameOver) {
        	JLabel label = (JLabel) gameOverPanel.getComponent(1);
        	label.setText("Score: " + score);
        	gameOverPanel.setVisible(true);
        }
    }

    // Mouse handler
    // This does most of the work: capturing mouse movement, and determining if we intersect a shape
    // Fruit is responsible for determining if it's been sliced and drawing itself, but we still
    // need to figure out what fruit we've intersected.
    private MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            drag.start(e.getPoint());
            
            model.createSlice(drag.getStart());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            drag.stop(e.getPoint());
        
            model.updateSlice(drag.getEnd());
            model.addSlice();
            
            // find intersected shapes
            int offset = 0; // Used to offset new fruits
            for (Fruit s : model.getShapes()) {
                if (s.intersects(drag.getStart(), drag.getEnd())) {                    
                    model.updateScore();
                    s.setFillColor(Color.RED);
                    try {
                        Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

                        // add offset so we can see them split - this is used for demo purposes only!
                        // you should change so that new pieces appear close to the same position as the original piece
                        for (Fruit f : newFruits) {
                            f.translate(offset, offset);
                            model.add(f);
                            offset += 20;
                        }
                    } catch (Exception ex) {
                        System.err.println("Caught error: " + ex.getMessage());
                    }
                } else {
                    s.setFillColor(Color.BLUE);
                }
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
        	super.mouseDragged(e);
        	
        	model.updateSlice(e.getPoint());
        }
    };

    /*
     * Track starting and ending positions for the drag operation
     * Needed to calculate angle of the slice
     */
    private class MouseDrag {
        private Point2D start;
        private Point2D end;

        MouseDrag() { }

        protected void start(Point2D start) { this.start = start; }
        protected void stop(Point2D end) { this.end = end; }

        protected Point2D getStart() { return start; }
        protected Point2D getEnd() { return end; }

    }

	public void reset() {
		gameOver = false;
		gameOverPanel.setVisible(false);
	}
}
