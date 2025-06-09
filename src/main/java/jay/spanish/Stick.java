package jay.spanish;


import java.awt.*;
import java.awt.geom.Ellipse2D;

class Stick {
    int x, y, originalY;
    String mood;
    boolean visible = true;
    boolean moving = false;
    boolean wantBob = false;
    int speed = 2;

    double bobPhase = 0;       // Current phase in the sine wave
    double bobSpeed = 0.15;     // How fast the stick bobs
    int bobAmplitude = 5;

    public Stick(int x, int y, String mood) {
        this(x, y, mood, false, 2);
    }

    public Stick(int x, int y, String mood, boolean moving) {
        this(x, y, mood, moving, 2);
    }

    public Stick(int x, int y, String mood, boolean moving, int speed) {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.mood = mood;
        this.moving = moving;
        this.speed = speed;
    }

    public Stick cancelMoving() {
        moving = false;
        return this;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(210, 180, 140));
        g2.fillRoundRect(x, y, 40, 150, 20, 20);

        g2.setColor(Color.WHITE);
        g2.fillOval(x - 10, y - 60, 60, 60);
        g2.setColor(Color.BLACK);
        g2.drawOval(x - 10, y - 60, 60, 60);

        // Eyes
        g2.fillOval(x + 5, y - 45, 8, 8);
        g2.fillOval(x + 25, y - 45, 8, 8);

        // Mouth
        if ("happy".equals(mood)) {
            g2.drawArc(x + 5, y - 35, 30, 15, 0, -180);
        } else {
            g2.drawArc(x + 5, y - 20, 30, 15, 0, 180);
        }
    }

    public boolean contains(Point p) {
        Rectangle body = new Rectangle(x, y, 40, 150);
        Ellipse2D face = new Ellipse2D.Double(x - 10, y - 60, 60, 60);
        return body.contains(p) || face.contains(p);
    }
}
