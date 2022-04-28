package imageReconstruction;

import java.awt.*;

public class TriangleFragment {
    Point p1;
    Point p2;
    Point p3;
    Color color;

    public TriangleFragment(Point p1, Point p2, Point p3, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.color = color;
    }

    public void moveP1(int x, int y) {
        this.p1 = new Point(x, y);
    }

    public void moveP2(int x, int y) {
        this.p2 = new Point(x, y);
    }

    public void moveP3(int x, int y) {
        this.p3 = new Point(x, y);
    }

    public void changeColor(Color color) {
        this.color = color;
    }

}
