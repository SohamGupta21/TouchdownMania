import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;

public class OffensiveLineComponent extends Component {
    //represents an offensive lineman that protects the quarterback
    private final AnimatedTexture texture;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animUp;
    private final AnimationChannel animDown;
    private final AnimationChannel animIdleUp;
    private final AnimationChannel animIdleDown;
    private final double[] playStartPosition = new double[2];
    private final Entity playerToBlock;
    private ArrayList<Entity> fullOffensiveLine;
    private boolean TimerRunning = false;
    private boolean playRunning = false;
    //assgins the textures needed for movement
    public OffensiveLineComponent(boolean isOffense, Entity blockAssignment) {
        animIdle = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 0, 3);
        animUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        animDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        texture = new AnimatedTexture(animIdle);
        texture.loop();
        playerToBlock = blockAssignment;

        //fullOffensiveLine = offensiveLine;
    }

    @Override
    public void onAdded() {
        //adds the texture and the hit box
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(65, 65)));
        //notes where the player started
        playStartPosition[0] = entity.getX();
        playStartPosition[1] = entity.getY();
    }

    @Override
    public void onUpdate(double tpf) {
        if(playRunning) {
            //as long as the play is runnning
            if (entity.getX() < playerToBlock.getX() + 5) {
                //makes sure that he is not too close to the defender so that they dont overlap
                entity.setX(playerToBlock.getX() + 5);
            }
            if (!TimerRunning) {
                //translates according to a vector every second
                //uses a timer boolean because it allows me to run asynchronously (kind of) without threads messing it up
                TimerRunning = true;
                FXGL.getGameTimer().runOnceAfter(() -> {
                    entity.translate(calculateVectorToMoveIn(playerToBlock));
                    TimerRunning = false;
                }, Duration.seconds(1));
            }
            moveInbounds();
        }
    }

    public void reset(){
        //resets variables when a new play starts
        texture.stop();
        playRunning = false;
    }

    public void setPlayRunning(boolean input){
        //runs the play
        if(input){
            texture.loop();
            playRunning = true;
        }
    }
    public Point2D calculateVectorToMoveIn(Entity playerToFollow) {
        //calculates a vector in the direction of the player that they are guarding
        int xVectorVal = 0;
        int yVectorVal = 0;
        //if the player is too close to the defender then it moves it back
        if (Math.abs(entity.getX() - playerToFollow.getX()) < 5 && Math.abs(entity.getY() - playerToFollow.getY()) < 5) {
            return new Point2D(xVectorVal, yVectorVal);
        }
        //uses the relative x location to figure out which way to move
        if (entity.getX() > playerToFollow.getX()) {
            getEntity().setScaleX(1);
            xVectorVal = -1;
        }
        if (entity.getX() < playerToFollow.getX()) {
            getEntity().setScaleX(-1);
            xVectorVal = 1;
        }
        //does the same thing as above for the y coordinate
        if (entity.getY() > playerToFollow.getY()) {
            yVectorVal = -1;
        }
        if (entity.getY() < playerToFollow.getY()) {
            yVectorVal = 1;
        }
        return new Point2D(xVectorVal, yVectorVal);
    }

    public void moveInbounds(){
        //makes sure that it is in the position of the field
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