import org.junit.*;
import static org.junit.Assert.*;
import game.GameState;

public class runNewGameTest {
    int score = -1;
    @Before
    public void runTheGame() {
	score = GameState.runNewGame(1050, false);
    }
    @Test
    public void runNewGameTest() {
	// the score should not be negative, if the game ran successfully
	assertNotEquals(-1,score);
    }
    @Test
    public void pickUpSomeGoldTest() {
	// the score should not be zero, if some gold was picked up
	assertNotEquals(0,score);
    }
}
