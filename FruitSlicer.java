import java.awt.*;

public class FruitSlicer {
	private int x1, y1, x2, y2;
	private int lifetime = 50;
	
	FruitSlicer(double d, double e, double f, double g) {
		this.x1 = (int) d;
		this.y1 = (int) e;
		this.x2 = (int) f;
		this.y2 = (int) g;
	}
	
	public void draw(Graphics2D g, Color c) {
		g.setColor(c);
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void updateCoords(double x, double y) {
		x2 = (int) x;
		y2 = (int) y;
	}

	public void decrementLifetime() {
		lifetime--;
	}
	
	public boolean getDestroy() {
		return lifetime == 0;
	}
}