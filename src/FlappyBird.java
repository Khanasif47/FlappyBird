import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    int velocityY = 0;
    int gravity = 1;

    // Pipes
    class Pipe {
        int x, y, width, height;
        Image img;
        boolean passed = false;

        Pipe(Image img, int x, int y, int width, int height) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    ArrayList<Pipe> pipes;
    Random random = new Random();

    int pipeWidth = 64;
    int pipeHeight = 512;
    int pipeGap = 150;
    int pipeVelocityX = -4;

    Timer gameLoop;
    Timer placePipeTimer;

    boolean gameOver = false;
    int score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        pipes = new ArrayList<>();

        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomY = -pipeHeight / 4 - random.nextInt(pipeHeight / 2);

        Pipe topPipe = new Pipe(topPipeImg, boardWidth, randomY, pipeWidth, pipeHeight);
        Pipe bottomPipe = new Pipe(
                bottomPipeImg,
                boardWidth,
                randomY + pipeHeight + pipeGap,
                pipeWidth,
                pipeHeight
        );

        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        if (gameOver) {
            g.drawString("GAME OVER", boardWidth / 2 - 70, boardHeight / 2);
        }
    }

    void move() {
        velocityY += gravity;
        birdY += velocityY;

        for (Pipe pipe : pipes) {
            pipe.x += pipeVelocityX;

            if (!pipe.passed && birdX > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 1;
            }

            if (collision(pipe)) {
                gameOver = true;
            }
        }

        if (birdY > boardHeight || birdY < 0) {
            gameOver = true;
        }
    }

    boolean collision(Pipe pipe) {
        Rectangle birdRect = new Rectangle(birdX, birdY, birdWidth, birdHeight);
        Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
        return birdRect.intersects(pipeRect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            placePipeTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                birdY = boardHeight / 2;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
