package jay.spanish;

import java.awt.*;

public class EmiliaStick extends Stick{


    public EmiliaStick(int x, int y, String mood) {
        super(x, y, mood);
    }

    public EmiliaStick(int x, int y, String mood, boolean moving) {
        super(x, y, mood, moving);
    }

    public EmiliaStick(int x, int y, String mood, boolean moving, int speed) {
        super(x, y, mood, moving, speed);
    }

    @Override
    public void draw(Graphics2D g2) {
        // Body
        g2.setColor(new Color(210, 180, 140)); // tan body color
        g2.fillRoundRect(x, y, 40, 150, 20, 20);

        // Head
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 10, y - 60, 60, 60);
        g2.setColor(Color.BLACK);
        g2.drawOval(x - 10, y - 60, 60, 60);

        // Hair (long brown hair)
        g2.setColor(new Color(139, 69, 19)); // SaddleBrown
        g2.setStroke(new BasicStroke(3));

        int headLeft = x - 10;
        int headRight = x + 50;
        int topY = y - 60;

        // Side strands
        g2.drawLine(headLeft + 5, topY + 5, headLeft + 5, topY + 45);
        g2.drawLine(headRight - 5, topY + 5, headRight - 5, topY + 45);

        // Bottom arc (long hair behind)
        g2.drawArc(headLeft, topY + 25, 60, 40, 0, -180);

        // Fringe/bangs
        g2.drawArc(headLeft, topY - 10, 60, 30, 0, 180);

        // Eyes (light blue)
        g2.setColor(new Color(135, 206, 250)); // sky blue
        g2.fillOval(x + 5, y - 45, 8, 8);
        g2.fillOval(x + 25, y - 45, 8, 8);

        // Mouth
        g2.setColor(Color.BLACK);
        if ("happy".equals(mood)) {
            g2.drawArc(x + 5, y - 35, 30, 15, 0, -180); // smile
        } else {
            g2.drawArc(x + 5, y - 20, 30, 15, 0, 180); // frown
        }
    }

}
