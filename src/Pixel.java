import java.awt.*;

public class Pixel {
    public int pos_x;
    public int pos_y;
    public Color color;

    public Pixel(int pos_x, int pos_y, Color color) {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.color = color;
    }

    public Pixel(int max_x, int max_y) {
        this.pos_x = (int)(Math.random() * max_x);
        this.pos_y = (int)(Math.random() * max_y);
        int randomColor = (int) (Math.random() * 255);
        this.color = new Color(randomColor, randomColor, randomColor);
    }
}
