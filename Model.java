/**
 * CS349 Winter 2014
 * Assignment 3 Demo Code
 * Jeff Avery & Michael Terry
 */
import java.awt.geom.Point2D;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Class the contains a list of fruit to display.
 * Follows MVC pattern, with methods to add observers,
 * and notify them when the fruit list changes.
 */
public class Model {
  // Observer list
  private Vector<ModelListener> views = new Vector();

  private ModelListener scoreView;
  private ModelListener mainView;
  
  // Fruit that we want to display
  private CopyOnWriteArrayList<Fruit> shapes = new CopyOnWriteArrayList();
  
  // Slices to display
  private CopyOnWriteArrayList<FruitSlicer> slices = new CopyOnWriteArrayList();
  
  private FruitSlicer currentSlice;
  private boolean isReset = false;
  
  // Constructor
  Model() {
    shapes.clear();
  }

  // MVC methods
  // These likely don't need to change, they're just an implementation of the
  // basic MVC methods to bind view and model together.
  public void addObserver(ModelListener view) {
	if (view instanceof TitleView) {
	    scoreView = view;
	} else {		
		views.add(view);
		
		if (view instanceof View) {
			mainView = view;
		}
	}
  }

  public void notifyObservers() {
    for (ModelListener v : views) {
      v.update();
    }
  }
  
  // Model methods
  // You may need to add more methods here, depending on required functionality.
  // For instance, this sample makes to effort to discard fruit from the list.
  public void add(Fruit s) {
    shapes.add(s);
    notifyObservers();
  }

  public CopyOnWriteArrayList<Fruit> getShapes() {
      return (CopyOnWriteArrayList<Fruit>)shapes.clone();
  }

  public void updatePositions() {
	  for (Fruit f: shapes) {
		  f.updatePosition();
	
		  if (f.getTransformedShape().getBounds().getY() > 700 && f.getCuttable()) {
			  updateMissed();
		  }
		  
		  if (f.getTransformedShape().getBounds().getY() > 700 || (!f.getCuttable() && !f.getSplit())) {
			  shapes.remove(f);
		  }
	  }
    
	  notifyObservers();
  }
  
  public void updateScore() {
	  ((TitleView) scoreView).updatePoints();
  }
  
  public int getScore() {
	  return ((TitleView) scoreView).getPoints();
  }
  
  public void updateMissed() {
	  ((TitleView) scoreView).updateMissed();
  }
  
  public void gameOver(int points) {
	  ((View) mainView).displayGameOver(points);
  }

  public boolean isGameOver() {
	  return ((View) mainView).getGameOver();
  }
  
  public void updateTime(int refreshDelay) {
	  ((TitleView) scoreView).updateTime(refreshDelay);
  }
  
  public void createSlice(Point2D p) {
	  currentSlice = new FruitSlicer(p.getX(), p.getY(), p.getX(), p.getY());
  }
  
  public FruitSlicer getCurrentSlice() {
	  return currentSlice;
  }
  
  public void updateSlice(Point2D p) {
	  currentSlice.updateCoords(p.getX(), p.getY());
  }
  
  public void addSlice() {
	  slices.add(currentSlice);
	  currentSlice = null;
  }
  
  public CopyOnWriteArrayList<FruitSlicer> getSlices() {
	  return slices;
  }
  
  public void removeOldSlices() {
	  for (FruitSlicer s: slices) {
		  s.decrementLifetime();
		  
		  if (s.getDestroy()) {
			  slices.remove(s);
		  }
	  }
  }

  public void setReset(boolean isReset) {
	  this.isReset = isReset;
  }
  
  public boolean isReset() {
	  return isReset;
  }
  
  public void resetInstance() {
	  currentSlice = null;
	  slices.clear();
	  shapes.clear();
	  
	  ((TitleView) scoreView).reset();
	  ((View) mainView).reset();
  }
}
