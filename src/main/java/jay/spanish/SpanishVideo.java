package jay.spanish;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpanishVideo extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {
    private BufferedImage background;
    private final java.util.List<Stick> sticks = new ArrayList<>();
    private final java.util.List<Stick> sceneSticks = new ArrayList<>();
    private java.util.List<Stick> savedSticks = new ArrayList<>();
    private Stick selectedStick = null;
    private Point dragOffset = null;
    private long stopWatchTime;

    private boolean fadeInActive = false;
    private long fadeInStartTime;
//    private static final long FADE_IN_DURATION_NS = 60 * 2;

    private static final long FADE_IN_DURATION_NS = 52_000_000_000L;

    private boolean sceneMode = false;
    private Timer timer;

    // Fade-in scene variables
    private boolean fadeSceneActive = false;
    private long fadeStartTime = 0;
    private static final long FADE_DURATION_NS = 52_000_000_000L; // 52 seconds in nanoseconds

    private boolean isThirdScene = false; // find the 5 shuar
    private boolean isFourthScene = false; // talk
    private boolean isFifthScene = false; // emilia
    private boolean isSixthScene = false; // hallucinated
    private boolean isSeventhScene = false;
    private boolean isEighthScene = false;
    private boolean isNinethScene = false;

    private Stick ignacio, jose;

    // Credits text list
    private final String[] credits = new String[] {
            "Ruben & Jayden PROYECTO FINAL - el cancion El encuentro",
            "Editor: Ruben O",
            "Animator (I guess): Jayden H",
            "Music inspiration: Hotel California by Eagles",
            "Special Thanks: Ruben for giving me the idea :)",
            "Graphics Sandbox: made by Jayden ",
            "SOURCE CODE: https://github.com/JaydenHuang555/SpanishProject"
    };

    private final String fadeInText[] = new String[] {
            "Ruben & Jayden PROYECTO FINAL - el cancion El encuentro",
            "SOURCE CODE: https://github.com/JaydenHuang555/SpanishProject",
            "El fin"
    };

    public SpanishVideo() {
        try {
            background = ImageIO.read(new File("forest.png"));
            stopWatchTime = System.nanoTime();
        } catch (IOException e) {
            System.out.println("Could not load forest.png");
        }

//        sticks.add(new Stick(100, 200, "happy"));
//        sticks.add(new Stick(300, 200, "sad"));

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        timer = new Timer(30, this);
    }

    private void updateStopWatch(Graphics2D g2) {
        long elapesed = (System.nanoTime() - stopWatchTime) / 1_000_000_000;
        long min = elapesed / 60, sec = elapesed % 60;

        String timeText = String.format("Time: %02d:%02d", min, sec);

        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2;
        g2.drawString(timeText, x, y);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

//        updateStopWatch(g2);

        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        if (sceneMode || isThirdScene || fadeInActive) {
            for (Stick s : sceneSticks) {
                s.draw(g2);
            }
        } else {
            for (Stick s : sticks) {
                if (s.visible) s.draw(g2);
            }
        }

        if (fadeSceneActive) {
            long elapsed = System.nanoTime() - fadeStartTime;
            float alpha = 1.0f - Math.min(1.0f, (float) elapsed / FADE_DURATION_NS);
            if (alpha < 0) alpha = 0;

            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(original);

            // Draw credits text above bottom, white, semi-transparent
            g2.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));

            // Calculate which credit line to show based on elapsed time
            // We have 6 lines, divide 52 sec into 6 segments (~8.66s each)
            int totalCredits = credits.length;
            long segmentDuration = FADE_DURATION_NS / totalCredits;
            int index = (int) Math.min(totalCredits - 1, elapsed / segmentDuration);

            String text = credits[index];

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - 50;

            g2.drawString(text, x, y);

            if (elapsed >= FADE_DURATION_NS) {
                fadeSceneActive = false;
                repaint();
            }
        }
        if (fadeInActive) {
            System.out.println("fading ");
            long elapsed = System.nanoTime() - fadeInStartTime;
            System.out.println("elapsed: "+ elapsed);
            float alpha = Math.min(1.0f, (float) elapsed / FADE_IN_DURATION_NS);

            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 32));

            int index = (int) Math.min(fadeInText.length - 1, elapsed / (FADE_IN_DURATION_NS / fadeInText.length));

            String text = fadeInText[index];

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - 50;

            g2.drawString(text, x, y);
            g2.setComposite(original);

            if (elapsed >= FADE_IN_DURATION_NS) {
                fadeInActive = false;
                timer.stop(); // optional, if nothing else animating
            }

            repaint(); // continuously repaint during fade-in
        }
    }

    public void mousePressed(MouseEvent e) {
        if (sceneMode || fadeSceneActive) return;

        Point p = e.getPoint();

        if (SwingUtilities.isRightMouseButton(e)) {
            for (int i = sticks.size() - 1; i >= 0; i--) {
                Stick s = sticks.get(i);
                if (s.visible && s.contains(p)) {
                    s.visible = !s.visible;
                    repaint();
                    return;
                }
            }
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = sticks.size() - 1; i >= 0; i--) {
                Stick s = sticks.get(i);
                if (s.visible && s.contains(p)) {
                    selectedStick = s;
                    dragOffset = new Point(p.x - s.x, p.y - s.y);
                    return;
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (sceneMode || fadeSceneActive || selectedStick == null) return;
        selectedStick.x = e.getX() - dragOffset.x;
        selectedStick.y = e.getY() - dragOffset.y;
        repaint();
    }

    public void mouseReleased(MouseEvent e) { selectedStick = null; }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
        System.out.println("x: "+e.getX()+", y: "+e.getY());
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_C) {
            sticks.clear();
        }

        if(key == KeyEvent.VK_ESCAPE) {
            isThirdScene = false;
            isFifthScene = false;
            isFourthScene = false;
            isSixthScene = false;
            isSeventhScene = false;
            isEighthScene = false;
            isNinethScene = false;
            sceneMode = false;
            fadeSceneActive = false;
            savedSticks.clear();
            for(Stick stick : sceneSticks) {
                savedSticks.add(stick);
            }
            sticks.clear();

            for(Stick saved : savedSticks) sticks.add(saved);

            timer.stop();
            repaint();
        }

        if(key == KeyEvent.VK_ESCAPE && isThirdScene) {
            timer.stop();
            isThirdScene = false;
            repaint();
        }

        if (key == KeyEvent.VK_ESCAPE && sceneMode) {
            sceneMode = false;
            sticks.clear();
            for(Stick stick : sceneSticks) sticks.add(stick.cancelMoving());
//            savedSticks.clear();
            repaint();
            return;
        }

        if(key == KeyEvent.VK_ESCAPE && fadeSceneActive) {
            fadeSceneActive = false;
            timer.stop();
            repaint();
            return;
        }

        if (fadeSceneActive) return;

        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point screenPoint = pointer.getLocation();
        SwingUtilities.convertPointFromScreen(screenPoint, this);

        if (key == KeyEvent.VK_H) {
            sticks.add(new Stick(screenPoint.x, screenPoint.y, "happy"));
        } else if (key == KeyEvent.VK_S) {
            sticks.add(new Stick(screenPoint.x, screenPoint.y, "sad"));
        } else if (key == KeyEvent.VK_2) {
            launchInfiniteMovingScene();
        } else if (key == KeyEvent.VK_1) {
            startFadeScene();
        }
        else if(key == KeyEvent.VK_3) {
            startThirdScene();
        }
        else if(key == KeyEvent.VK_4) {
            startFourthScene();
        }

        else if(key == KeyEvent.VK_5) {
            startFifthScene();
        }

        else if(key == KeyEvent.VK_6)
            startSixthScene();
        else if(key == KeyEvent.VK_7)
            startSeventhScene();
        else if(key == KeyEvent.VK_8)
            startEigthScene();
        else if(key == KeyEvent.VK_9)
            startNinenthScene();
        else if(key == KeyEvent.VK_0)
            startZerothScene();
        else if(key == KeyEvent.VK_Q)
            startFadeIn();

        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    private void startFadeIn() {
        fadeInStartTime = System.nanoTime();
        fadeInActive = true;
        timer.start();
    }

    private void launchInfiniteMovingScene() {
        sceneMode = true;
        sceneSticks.clear();
        savedSticks.clear();
        for(Stick s : savedSticks) savedSticks.add(s);
        int floorY = getHeight() - 150;
        int width = getWidth();
        jose = new Stick(width - 100, floorY, "sad", true, -2);
        ignacio = new Stick(width - 30, floorY, "sad", true, -2);
        sceneSticks.add(jose);
        sceneSticks.add(ignacio);
        timer.start();
    }

    public void startThirdScene() {
        isThirdScene = true;
        int floorY = getHeight() - 150;
        int width = getWidth();
        if(!sceneSticks.isEmpty()) {
            ArrayList<Stick> next = new ArrayList<>();
            for(Stick stick : sceneSticks) {
                stick.moving = false;
                stick.wantBob = false;
                next.add(stick);
            }
            sceneSticks.clear();
            for(Stick stick : next) sceneSticks.add(stick);
        }
        sticks.clear();

        int delta = 70; // 70
        for(int i = 0; i < 5; i++) {
            Stick stick = new Stick(i * delta, floorY, "happy");
            sceneSticks.add(stick);
        }

        isThirdScene = true;
        timer.start();
    }

    public void startFourthScene() {
        isFourthScene = true;
        if(!sceneSticks.isEmpty()) {
            sceneSticks.get(sceneSticks.size() - 1).wantBob = true;
        }

        timer.start();
    }

    public void startFifthScene() {
        sceneSticks.clear();
        isFifthScene = true;
        jose.mood = "happy";
        sceneSticks.add(jose);
        jose.mood = "happy";
        sceneSticks.add(new Stick(339, 183, "happy"));
        timer.start();

    }

    public void startSixthScene() {
        isSixthScene = true;
        sceneSticks.clear();
        jose.mood = "sad";

        sceneSticks.add(jose);
        sceneSticks.add(ignacio);

        int floorY = getHeight() - 150;
        int delta = 70; // 70
        for(int i = 0; i < 5; i++) {
            Stick stick = new Stick(i * delta, floorY, "happy");
            sceneSticks.add(stick);
        }
        sceneSticks.get(sceneSticks.size() - 1).wantBob = true;
    }

    public void startSeventhScene() {
        isSeventhScene = true;
        if(!sceneSticks.isEmpty()) {
            for(Stick stick : sceneSticks) {
                stick.moving = true;
                stick.speed = -2;
            }
        }
        timer.start();
    }

    public void startEigthScene() {
        if(!sceneSticks.isEmpty()) {
            for(Stick stick : sceneSticks) {
                stick.moving = false;
                stick.mood = "happy";
                stick.wantBob = false;
                stick.speed = 0;
            }
        }
        Stick talkingShuar = sceneSticks.get(sceneSticks.size() - 1);
        talkingShuar.wantBob = true;
        timer.start();
    }

    public void startNinenthScene() {
        isNinethScene = true;
        ignacio.speed = 2;
        ignacio.moving = true;
        jose.speed = 2;
        jose.moving = true;
        timer.start();
    }

    public void startZerothScene() {
        sceneSticks.clear();
        sticks.clear();
        sceneSticks.add(ignacio);
        sceneSticks.add(jose);
        timer.start();
    }

    private void startFadeScene() {
        fadeSceneActive = true;
        fadeStartTime = System.nanoTime();
        sceneMode = false;
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (1 == 1) {
            for (Stick s : sceneSticks) {
                if (s.moving) {
                    s.x += s.speed;
                    if (s.speed > 0 && s.x > getWidth()) s.x = -60;
                    else if (s.speed < 0 && s.x < -60)
                        s.x = getWidth();
                }

                if(s.wantBob || s.moving) {
                    // Bobbing logic
                    s.bobPhase += s.bobSpeed;
                    s.y = s.originalY + (int) (Math.sin(s.bobPhase) * s.bobAmplitude);
                }
            }
            repaint();
        }

        if(fadeSceneActive) {
            long elapsed = System.nanoTime() - fadeStartTime;
            if(elapsed >= FADE_DURATION_NS) {
                fadeSceneActive = false;
                timer.stop();
            }
            repaint();
        }

        if(fadeInActive) {
            long elapsed = System.nanoTime() - fadeInStartTime;
            if(elapsed >= FADE_IN_DURATION_NS) {
                fadeInActive = false;
                timer.stop();
            }
            repaint();
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graphic Sandbox we made for Spanish :)");
        SpanishVideo panel = new SpanishVideo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}
