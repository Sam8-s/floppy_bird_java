package floppy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdJava extends JPanel implements ActionListener, KeyListener {

    // Game constants
    final int WIDTH = 400, HEIGHT = 600;
    final int BIRD_RADIUS = 15;
    final int PIPE_WIDTH = 70;
    final int PIPE_GAP = 200;
    final int PIPE_SPEED = 3;

    // Game state
    int birdY = HEIGHT / 2;
    int birdVelocity = 0;
    int gravity = 1;
    int jumpStrength = -10;
    int score = 0;
    boolean gameRunning = false;

    Timer timer;
    ArrayList<Pipe> pipes = new ArrayList<>();
    Random rand = new Random();

    public FlappyBirdJava() {
        JFrame frame = new JFrame("Flappy Bird Java");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addKeyListener(this);

        timer = new Timer(20, this); // ~60 FPS
        startScreen();
    }

    public void startScreen() {
        int result = JOptionPane.showConfirmDialog(this,
                "🎮 Welcome to Flappy Bird!\n\n" +
                        "🕹️ Press SPACE to fly\n🚫 Avoid pipes\n📏 Don't hit top/bottom\n\n" +
                        "⭐ Try to score high!\n\nStart Game?",
                "Flappy Bird", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    public void startGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes.clear();
        score = 0;
        gameRunning = true;
        timer.start();
    }

    public void gameOver() {
        timer.stop();
        gameRunning = false;
        int result = JOptionPane.showConfirmDialog(this,
                "Game Over!\nYour score: " + score + "\nPlay Again?",
                "Game Over", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    public void actionPerformed(ActionEvent e) {
        birdVelocity += gravity;
        birdY += birdVelocity;

        if (pipes.size() == 0 || pipes.get(pipes.size() - 1).x < WIDTH - 200) {
            int centerY = rand.nextInt(200) + 150;
            pipes.add(new Pipe(WIDTH, centerY - PIPE_GAP / 2, centerY + PIPE_GAP / 2));
        }

        ArrayList<Pipe> toRemove = new ArrayList<>();
        for (Pipe pipe : pipes) {
            pipe.x -= PIPE_SPEED;
            if (!pipe.passed && pipe.x + PIPE_WIDTH < 100) {
                score++;
                pipe.passed = true;
            }
            if (pipe.x + PIPE_WIDTH < 0) {
                toRemove.add(pipe);
            }

            // Collision detection
            if (100 + BIRD_RADIUS > pipe.x && 100 - BIRD_RADIUS < pipe.x + PIPE_WIDTH) {
                if (birdY - BIRD_RADIUS < pipe.top || birdY + BIRD_RADIUS > pipe.bottom) {
                    gameOver();
                    return;
                }
            }
        }

        pipes.removeAll(toRemove);

        // Check out-of-bounds
        if (birdY + BIRD_RADIUS > HEIGHT || birdY < 0) {
            gameOver();
        }

        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(135, 206, 250)); // Sky blue

        // Bird
        g.setColor(Color.BLACK);
        g.fillOval(100 - BIRD_RADIUS, birdY - BIRD_RADIUS, BIRD_RADIUS * 2, BIRD_RADIUS * 2);

        // Pipes
        g.setColor(Color.GREEN);
        for (Pipe pipe : pipes) {
            g.fillRect(pipe.x, 0, PIPE_WIDTH, pipe.top);
            g.fillRect(pipe.x, pipe.bottom, PIPE_WIDTH, HEIGHT - pipe.bottom);
        }

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameRunning) {
            birdVelocity = jumpStrength;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    class Pipe {
        int x, top, bottom;
        boolean passed = false;

        Pipe(int x, int top, int bottom) {
            this.x = x;
            this.top = top;
            this.bottom = bottom;
        }
    }

    public static void main(String[] args) {
        new FlappyBirdJava();
    }
}
