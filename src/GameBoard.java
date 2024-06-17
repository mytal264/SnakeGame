import javax.swing.*;
import java.awt.*;

public class GameBoard extends JPanel implements Runnable {
    private final SnakeGame GAME;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int[] X = new int[ALL_DOTS];
    private final int[] Y = new int[ALL_DOTS];
    private int dots;
    private int apple_x;
    private int apple_y;
    private int score;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean paused;
    private boolean newRecordPlayed = false;
    private Thread gameThread;
    private Image ball;
    private Image apple;
    private Image head;

    private JButton replayButton;
    private JButton homeButton;

    public void setPaused() {
        paused = !paused;
    }

    public GameBoard(SnakeGame game) {
        this.GAME = game;
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter(this));
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(SnakeGame.B_WIDTH, SnakeGame.B_HEIGHT));
        loadImages();
        initGame();

        setLayout(null);
        replayButton = new JButton("שחק שוב");
        replayButton.setBounds(SnakeGame.B_WIDTH / 2 - 53, SnakeGame.B_HEIGHT / 2, 100, 30);
        replayButton.addActionListener(e -> {
            SnakeGame.playSound("resources/click.wav");
            GAME.saveHighScore(score);
            resetGame();
            removeButtons();
            requestFocusInWindow();
            startGame();
        });
        add(replayButton);
        replayButton.setVisible(false);

        homeButton = new JButton("חזרה לדף הבית");
        homeButton.setBounds(SnakeGame.B_WIDTH / 2 - 63, SnakeGame.B_HEIGHT / 2 + 40, 120, 30);
        homeButton.addActionListener(e -> {
            SnakeGame.playSound("resources/click.wav");
            GAME.saveHighScore(score);
            GAME.showHome();
            stopGame();
        });
        add(homeButton);
        homeButton.setVisible(false);
    }

    private void removeButtons() {
        replayButton.setVisible(false);
        homeButton.setVisible(false);
    }

    private void loadImages() {
        ball = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/dot.png"));
        apple = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/apple.png"));
        head = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/head.png"));
    }

    private void initGame() {
        dots = 3;
        score = 0;
        newRecordPlayed = false;
        paused = false;
        inGame = true;
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        for (int z = 0; z < dots; z++) {
            X[z] = 50 - z * 10;
            Y[z] = 50;
        }
        locateApple();
    }

    public void startGame() {
        if (gameThread != null && gameThread.isAlive()) {
            stopGame();
        }
        resetGame();
        removeButtons();
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void stopGame() {
        inGame = false;
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
    }

    private void resetGame() {
        initGame();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, X[z], Y[z], this);
                } else {
                    g.drawImage(ball, X[z], Y[z], this);
                }
            }
            g.setColor(Color.white);
            g.drawString("Score: " + score, 5, 15);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        SnakeGame.playSound("/resources/gameOver.wav");
        String msg = "Game Over";
        String scoreMsg = "Score: " + score;
        Font small = new Font("Dialog", Font.BOLD, 22);
        FontMetrics metr = getFontMetrics(small);
        g.setColor(new Color(57, 255, 20));
        g.setFont(small);
        g.drawString(msg, (SnakeGame.B_WIDTH - metr.stringWidth(msg)) / 2 - 5, SnakeGame.B_HEIGHT / 2 - 70);
        g.drawString(scoreMsg, (SnakeGame.B_WIDTH - metr.stringWidth(scoreMsg)) / 2 - 5, SnakeGame.B_HEIGHT / 2 - 20);

        replayButton.setVisible(true);
        homeButton.setVisible(true);
        if (score > GAME.getHighScore()) {
            GAME.saveHighScore(score);
        }
    }

    private void checkApple() {
        if ((X[0] == apple_x) && (Y[0] == apple_y)) {
            dots++;
            score++;
            SnakeGame.playSound("resources/eat.wav");
            checkNewRecord();
            locateApple();
        }
    }

    private void move() {
        if (!paused) {
            for (int z = dots; z > 0; z--) {
                X[z] = X[z - 1];
                Y[z] = Y[z - 1];
            }
            if (leftDirection) {
                X[0] -= DOT_SIZE;
            } else if (rightDirection) {
                X[0] += DOT_SIZE;
            } else if (upDirection) {
                Y[0] -= DOT_SIZE;
            } else if (downDirection) {
                Y[0] += DOT_SIZE;
            }
        }
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (X[0] == X[z]) && (Y[0] == Y[z])) {
                inGame = false;
                break;
            }
        }
        if (Y[0] >= (SnakeGame.B_HEIGHT - 49) || Y[0] < -10 || X[0] >= (SnakeGame.B_WIDTH - 25) || X[0] < -10) {
            inGame = false;
        }
        if (!inGame) {
            stopGame();
            replayButton.setVisible(true);
            homeButton.setVisible(true);
        }
    }

    private void locateApple() {
        int r = (int) (Math.random() * (SnakeGame.B_WIDTH / DOT_SIZE - 5));
        apple_x = ((r * DOT_SIZE));
        r = (int) (Math.random() * (SnakeGame.B_HEIGHT / DOT_SIZE - 5));
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void run() {
        while (inGame) {
            move();
            repaint();
            checkApple();
            checkCollision();
            try {
                int DELAY = 140;
                //noinspection BusyWait
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public boolean isRightDirection() {
        return rightDirection;
    }
    public boolean isLeftDirection() {
        return leftDirection;
    }
    public boolean isUpDirection() {
        return upDirection;
    }
    public boolean isDownDirection() {
        return downDirection;
    }
    public void setRightDirection(boolean rightDirection) {
        this.rightDirection = rightDirection;
    }
    public void setLeftDirection(boolean leftDirection) {
        this.leftDirection = leftDirection;
    }
    public void setUpDirection(boolean upDirection) {
        this.upDirection = upDirection;
    }
    public void setDownDirection(boolean downDirection) {
        this.downDirection = downDirection;
    }

    private void checkNewRecord() {
        if (score > GAME.getHighScore() && !newRecordPlayed) {
            SnakeGame.playSound("/resources/newRecord.wav");
            newRecordPlayed = true;
        }
    }
}