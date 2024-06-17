import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TAdapter extends KeyAdapter {
    private final GameBoard GAME_BOARD;

    public TAdapter(GameBoard gameBoard) {
        this.GAME_BOARD = gameBoard;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT) && (!GAME_BOARD.isRightDirection())) {
            GAME_BOARD.setLeftDirection(true);
            GAME_BOARD.setUpDirection(false);
            GAME_BOARD.setDownDirection(false);
        }

        if ((key == KeyEvent.VK_RIGHT) && (!GAME_BOARD.isLeftDirection())) {
            GAME_BOARD.setRightDirection(true);
            GAME_BOARD.setUpDirection(false);
            GAME_BOARD.setDownDirection(false);
        }

        if ((key == KeyEvent.VK_UP) && (!GAME_BOARD.isDownDirection())) {
            GAME_BOARD.setUpDirection(true);
            GAME_BOARD.setRightDirection(false);
            GAME_BOARD.setLeftDirection(false);
        }

        if ((key == KeyEvent.VK_DOWN) && (!GAME_BOARD.isUpDirection())) {
            GAME_BOARD.setDownDirection(true);
            GAME_BOARD.setRightDirection(false);
            GAME_BOARD.setLeftDirection(false);
        }

        if (key == KeyEvent.VK_P) {
            GAME_BOARD.setPaused();
        }
    }
}