import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;

import java.sql.Time;

public class TimeClockComponent extends Component {
    private boolean TimerRunning;
    private TimerAction gameClock;
    private GameManagement gameManager;
    //allows the component to manage the overall game
    public TimeClockComponent(GameManagement gm){
        gameManager = gm;
    }

    @Override
    public void onUpdate(double tpf) {
        //ends the game if needed
        if(checkGameOver()){
            gameManager.endGame();
            FXGL.getWorldProperties().setValue("timer", 0);
        }
    }

    public void startTimer(){
        //startes the game clock timer
        TimerAction gameClock  = FXGL.getGameTimer().runAtInterval(() -> {
            FXGL.getWorldProperties().increment("timer", -1);
        }, Duration.seconds(1));
    }

    public void reset(){
        //resets the timer
        TimerRunning = false;
        gameClock.expire();
    }

    public boolean checkGameOver(){
        //checks if the game is out of time
        if(FXGL.getWorldProperties().getInt("timer") <= 0){
            return true;
        }
        return false;
    }
}
