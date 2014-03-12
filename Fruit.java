/**
 * CS349 Winter 2014
 * Assignment 3 Demo Code
 * Jeff Avery & Michael Terry
 */
import java.awt.*;
import java.awt.geom.*;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit implements FruitInterface {
    public static final double windowX = 800;
    public static final double windowY = 600;
    public static final double spacing = 100;
    public static final double gravity = 0.005;
    public static final double minVelocityX = 0.2;
    public static final double maxVelocityX = 0.5;
    public static final double minVelocityY = 2;
    public static final double maxVelocityY = 2.5;

    private Area            fruitShape   = null;
    private Color           fillColor    = Color.RED;
    private Color           outlineColor = Color.BLACK;
    private AffineTransform transform    = new AffineTransform();
    private double          outlineWidth = 5;
    private boolean         isCuttable   = true;
    private boolean         isSplit      = false;
    private double          velocityX, velocityY;
    
    // for intersection
    double m, y_intercept;
    double a, b, c, discriminant;

    /**o
     * A fruit is represented using any arbitrary geometric shape.
     */
    Fruit (Area fruitShape, boolean isSplit, boolean isLeftSide) {
        this.fruitShape = (Area)fruitShape.clone();
        this.isSplit = isSplit;
        this.setFillColor(Color.BLUE);

        if (!isSplit) {
            double x = spacing + Math.random() * (windowX - 2*spacing);

            velocityX = minVelocityX + Math.random() * (maxVelocityX - minVelocityX);
            velocityX = (x > windowX/2)? -velocityX: velocityX;

            velocityY = minVelocityY + Math.random() * (maxVelocityY - minVelocityY);

            this.translate(x, windowY);    
        } else {
        	velocityX = isLeftSide? -minVelocityX: minVelocityX;
        	velocityY = 0;
            isCuttable = false;
        }
    }

    /**
     * The color used to paint the interior of the Fruit.
     */
    public Color getFillColor() {
        return fillColor;
    }
    /**
     * The color used to paint the interior of the Fruit.
     */
    public void setFillColor(Color color) {
        fillColor = color;
    }
    /**
     * The color used to paint the outline of the Fruit.
     */
    public Color getOutlineColor() {
        return outlineColor;
    }
    /**
     * The color used to paint the outline of the Fruit.
     */
    public void setOutlineColor(Color color) {
        outlineColor = color;
    }
    
    /**
     * Gets the width of the outline stroke used when painting.
     */
    public double getOutlineWidth() {
        return outlineWidth;
    }

    /**
     * Sets the width of the outline stroke used when painting.
     */
    public void setOutlineWidth(double newWidth) {
        outlineWidth = newWidth;
    }

    /**
     * Concatenates a rotation transform to the Fruit's affine transform
     */
    public void rotate(double theta) {
        transform.rotate(theta);
    }

    /**
     * Concatenates a scale transform to the Fruit's affine transform
     */
    public void scale(double x, double y) {
        transform.scale(x, y);
    }

    /**
     * Concatenates a translation transform to the Fruit's affine transform
     */
    public void translate(double tx, double ty) {
        transform.translate(tx, ty);
    }

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public AffineTransform getTransform() {
        return (AffineTransform)transform.clone();
    }

    /**
     * Creates a transformed version of the fruit. Used for painting
     * and intersection testing.
     */
    public Area getTransformedShape() {
        return fruitShape.createTransformedArea(transform);
    }

    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Graphics2D g2) {
        if (isCuttable || isSplit) {
        	g2.setColor(fillColor);
            g2.fill(getTransformedShape());
        }
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    public boolean intersects(Point2D p1, Point2D p2) {
        Rectangle r = getTransformedShape().getBounds();
    	
        double originX = r.x + r.width/2;
        double originY = r.y + r.height/2;
        double radius = r.width/2;
        
    	if (p1.distance(originX, originY) < radius || p2.distance(originX, originY) < radius) {
    		return false;
    	}
    	
    	// get intersection points of a circle
        // http://math.stackexchange.com/questions/228841/how-do-i-calculate-the-intersections-of-a-straight-line-and-a-circle
        m = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
        y_intercept = p1.getY() - m*p1.getX();
        
        a = Math.pow(m, 2) + 1;
        b = 2*(m*y_intercept - m*originY - originX);
        c = Math.pow(originY, 2) - Math.pow(radius, 2) + Math.pow(originX, 2) - 2*y_intercept*originY + Math.pow(y_intercept, 2);
        
        discriminant = Math.pow(b, 2) - 4*a*c;
        
        return isCuttable && discriminant > 0;
    }

    /**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(Point2D p1) {
        return this.getTransformedShape().contains(p1);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(Point2D p1, Point2D p2) throws NoninvertibleTransformException {
        Area topArea = null;
        Area bottomArea = null;        
        isCuttable = false;
                
        // calculate intersection points
    	double x1 = (-b + Math.sqrt(discriminant))/(2*a);
    	double x2 = (-b - Math.sqrt(discriminant))/(2*a);
    	double y1 = m * x1 + y_intercept;
    	double y2 = m * x2 + y_intercept;
        
        //calculate angle of intersection relative to a horizontal axis
        double radians = -Math.atan((y2-y1)/(x2-x1));
        
        // create transform to shift slices to origin centered on midpoint of cut
        AffineTransform t = new AffineTransform();
        t.rotate(radians);
        t.translate(-x1,-y1);
        
        // create top/bottom cuts
        topArea = (Area) getTransformedShape().clone();
        bottomArea = (Area) getTransformedShape().clone();
        topArea.transform(t);
        bottomArea.transform(t);
        
        Rectangle topRect = new Rectangle(-100, -100, 200, 100);
        Rectangle bottomRect = new Rectangle(-100, 0, 200, 100);
        topArea.intersect(new Area(topRect));
        bottomArea.intersect(new Area(bottomRect));
        
        // move back to original position
        AffineTransform inverse = t.createInverse();
        topArea.transform(inverse);
        bottomArea.transform(inverse);
        
        if (topArea != null && bottomArea != null) {
        	Fruit topFruit, bottomFruit;
        	
        	if (radians < 0) {
        		topFruit = new Fruit(topArea, true, false);
        		bottomFruit = new Fruit(bottomArea, true, true);
        	} else {
        		topFruit = new Fruit(topArea, true, true);
                bottomFruit = new Fruit(bottomArea, true, false);
        	}            
            
            return new Fruit[] { topFruit, bottomFruit };
        }
            
        return new Fruit[0];
     }

     public void updatePosition() {
        if (isCuttable || isSplit) {
            velocityY = velocityY - gravity;
            translate(velocityX, -velocityY);   
        }
     }
     
     public boolean getCuttable() {
    	 return isCuttable;
     }
     
     public boolean getSplit() {
    	 return isSplit;
     }
}
