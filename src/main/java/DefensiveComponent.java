import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;


public class DefensiveComponent extends Component {

    private final AnimatedTexture texture;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animUp;
    private final AnimationChannel animDown;
    private final AnimationChannel animIdleUp;
    private final AnimationChannel animIdleDown;
    private boolean TimerRunning, playRunning, vectorMover;
    private Entity personToFollow;
    private final double interval;


    public DefensiveComponent(boolean isOffense, Entity quarterback, boolean vM, double inter) {
        animIdle = new AnimationChannel(FXGL.image("footballSpritesRight 2.png"), 4, 66, 63, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("footballSpritesRight 2.png"), 4, 66, 63, Duration.seconds(1), 0, 3);
        animUp = new AnimationChannel(FXGL.image("footballSpritesUP 2.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleUp = new AnimationChannel(FXGL.image("footballSpritesUP 2.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        animDown = new AnimationChannel(FXGL.image("footballSpritesDown 2.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleDown = new AnimationChannel(FXGL.image("footballSpritesDown 2.png"), 4, 66, 62, Duration.seconds(1), 0, 0);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
        personToFollow = quarterback;
        this.vectorMover = vM;
        interval = inter;
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(65, 65)));

    }

    @Override
    public void onUpdate(double tpf) {
        if (playRunning) {
            if (!TimerRunning) {
                TimerRunning = true;
                FXGL.getGameTimer().runOnceAfter(() -> {
                    if (vectorMover) {
                        entity.translate(calculateVectorToMoveIn(personToFollow).multiply(5));
                    }
                    TimerRunning = false;
                }, Duration.seconds(interval));
            }
        }
        moveInbounds();
    }

    public void start(){
        playRunning = true;
        vectorMover = true;
        TimerRunning = false;
    }

    public void reset() {
        TimerRunning = false;
        playRunning = false;
        vectorMover = false;
        entity.setScaleX(1);
    }

    public void setPlayRunning(boolean input) {
        playRunning = input;
    }

    public void setPersonToFollow(Entity player) {
        personToFollow = player;
    }

    public Point2D calculateVectorToMoveIn(Entity playerToFollow) {
        int xVectorVal = 0;
        int yVectorVal = 0;
        if (Math.abs(entity.getX() - playerToFollow.getX()) < 5 && Math.abs(entity.getY() - playerToFollow.getY()) < 5) {
            return new Point2D(xVectorVal, yVectorVal);
        }
        if (entity.getX() > playerToFollow.getX()) {
            getEntity().setScaleX(-1);
            xVectorVal = -1;
        }
        if (entity.getX() < playerToFollow.getX()) {
            getEntity().setScaleX(1);
            xVectorVal = 1;
        }
        if (entity.getY() > playerToFollow.getY()) {
            yVectorVal = -1;
        }
        if (entity.getY() < playerToFollow.getY()) {
            yVectorVal = 1;
        }
        return new Point2D(xVectorVal, yVectorVal);
    }

    public void moveInbounds(){
        if(entity.getX() < 0){
            entity.setX(0);
        }
        if(entity.getX() + 66 > 1280){
            entity.setX(1280 - 66);
        }
        if(entity.getY() < 73){
            entity.setY(73);
        }
        if(entity.getY() +  66 > 647){
            entity.setY(647-66);
        }
    }
}