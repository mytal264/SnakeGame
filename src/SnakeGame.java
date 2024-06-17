import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class SnakeGame extends JFrame {
    private static final String HOME_PANEL = "Home";
    private static final String GAME_PANEL = "Game";
    public static final int B_WIDTH = 300;
    public static final int B_HEIGHT = 300;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int highScore;
    private JLabel highScoreLabel;

    public SnakeGame() {
        loadHighScore();
        initUI();
    }

    public int getHighScore() {
        return highScore;
    }

    private void initUI() {
        setTitle("Snake Game");
        setSize(B_WIDTH, B_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/logo.jpg")));
        Image backgroundImage = icon.getImage();

        JPanel homePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JButton startButton = new JButton("התחל משחק");
        startButton.setBounds(150, 5, 110, 30);
        homePanel.add(startButton);
        JButton instructionsButton = new JButton("הוראות משחק");
        instructionsButton.setBounds(20, 5, 110, 30);
        homePanel.add(instructionsButton);
        highScoreLabel = new JLabel("Highest Score: " + highScore);
        highScoreLabel.setBounds(5, 230, 200, 30);
        homePanel.add(highScoreLabel);

        GameBoard gamePanel = new GameBoard(this);
        mainPanel.add(homePanel, HOME_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        add(mainPanel);

        startButton.addActionListener(event -> {
            playSound("resources/click.wav");
            setSize(B_WIDTH, B_HEIGHT);
            setLocationRelativeTo(null);
            cardLayout.show(mainPanel, GAME_PANEL);
            gamePanel.requestFocusInWindow();
            gamePanel.startGame();
        });

        instructionsButton.addActionListener(event -> {
            playSound("resources/click.wav");
            showInstructions();
        });
    }

    private void showInstructions() {
        JFrame instructionsFrame = new JFrame("Game Instructions");
        instructionsFrame.setSize(B_WIDTH, B_HEIGHT-80);
        instructionsFrame.setLocationRelativeTo(null);
        instructionsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        instructionsFrame.setResizable(false);

        JTextArea instructionsArea = getjTextArea();
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        instructionsFrame.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("סגור");
        closeButton.addActionListener(e -> {
            playSound("resources/click.wav");
            instructionsFrame.dispose();
        });
        instructionsFrame.add(closeButton, BorderLayout.SOUTH);
        instructionsFrame.setVisible(true);
    }

    private static JTextArea getjTextArea() {
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setText("""
                                               :הוראות
                
                1. השתמש במקשי החצים כדי להזיז את הנחש
                2. תאכל תפוחים כדי לגדול
                3. הימנע מהתנגשויות בקירות ובגוף הנחש
                4. לעצירת/המשך המשחק P לחץ
                5. נסו להשיג את הניקוד הגבוה ביותר""");
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setLineWrap(true);
        instructionsArea.setMargin(new Insets(10, 30, 10, 10));
        return instructionsArea;
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public void saveHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/highscore.txt"))) {
                writer.write(Integer.toString(score));
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }

    public void showHome() {
        setSize(B_WIDTH, B_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, HOME_PANEL);
    }

    public static void playSound(String soundFile) {
        try {
            InputStream audioSrc = SnakeGame.class.getResourceAsStream(soundFile);
            if (audioSrc == null) {
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}