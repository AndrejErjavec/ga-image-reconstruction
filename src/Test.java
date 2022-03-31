import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 40, 40);
        g.dispose();

        File output = new File("image.jpg");
        try {
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(output.getAbsolutePath());

        JFrame frame = new JFrame();
        Canvas canvas = new Canvas();

        canvas.setPreferredSize(new Dimension(300, 300));
        canvas.setMaximumSize(new Dimension(300, 300));
        canvas.setMinimumSize(new Dimension(300, 300));
        canvas.setBackground(Color.white);

        frame.setSize(300, 300);
        frame.setTitle("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(canvas);
        frame.pack();

        BufferStrategy bs = canvas.getBufferStrategy();
        if(bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        Graphics gs = bs.getDrawGraphics();

        gs.drawImage(image, 0, 0, null);
        bs.show();
        bs.dispose();

        System.out.println(new Color(image.getRGB(10, 10)).getGreen());
    }
}
