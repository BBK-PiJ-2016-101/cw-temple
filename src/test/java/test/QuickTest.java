import org.junit.*;
import static org.junit.Assert.*;
import game.GameState;

public class QuickTest {
    int score = 0;
    @Before
    public void runTheGame() {
	score = GameState.runNewGame(1050, false);
    }
    @Test
    public void testQuickTest() {
	assertEquals(1,score);
    }
}
