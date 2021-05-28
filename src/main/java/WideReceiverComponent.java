import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;

public class WideReceiverComponent extends Component {


    public boolean movingWithInput = false;
    private int horizSpeed, vertSpeed = 0;
    private final AnimatedTexture texture;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animUp;
    private final AnimationChannel animDown;
    private final AnimationChannel animIdleUp;
    private final AnimationChannel animIdleDown;
    private boolean playRunning, tackled = false;
    private boolean timerRunning = false;
    private int routeIndex = 0;
    private int routeCounter = 0;
    private int playCounter = 1;
    public TimerAction routeTimer;
    private ArrayList<Point2D> route = new ArrayList<>();
    //assigns the textures for the component
    public WideReceiverComponent(boolean isOffense) {
        animIdle = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 0, 3);
        animUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        animDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 0);

        texture = new AnimatedTexture(animWalk);

        texture.loop();

    }

    @Override
    public void onAdded() {
        //creates the texture and hitbox
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(65, 65)));
    }

    @Override
    public void onUpdate(double tpf) {
        if(playRunning){
            //as long as the play is running
            ///decides whether to move in a route or by the arrow keys
            if (route != null && !tackled) {
                routeMovement(route);
            }
            if (movingWithInput) {
                updateHorizontally(tpf);
                updateVertically(tpf);
            }
            moveInbounds();
        }
    }

    public void reset(){
        //resets variables when a new play starts
        texture.stop();
        movingWithInput = false;
        horizSpeed = 0;
        vertSpeed = 0;
        entity.setScaleX(1);
        entity.setRotation(0);
        playRunning = false;
        tackled = false;
        timerRunning = false;
        routeIndex = -1;
        routeCounter = 0;
        //FXGL.getGameTimer().clear();
        //entity.translate(new Point2D(0,0).multiply(0));
        //route = new ArrayList<>();
    }

    public void start(){
        //sets variables to start a new play
        playRunning = true;
        texture.loop();
        routeIndex = 0;
        playCounter ++;
        texture.loopAnimationChannel(animWalk);
        texture.setScaleX(1);
    }

    public void setPlayRunning(boolean input) {
        //allows to set the play running
        playRunning = input;
        timerRunning = false;
    }

    public void tackled() {
        //runs when teh player gets tackled by the defense
        tackled = true;
        movingWithInput = false;
        texture.stop();
        entity.setRotation(90);
    }

    public void setMovingWithInput(boolean b) {
        //means that user controls the player
        movingWithInput = b;
    }

    public void updateHorizontally(double tpf) {
        //moves in case of arrow key movement left and right
        entity.translateX(horizSpeed * tpf);

        if (horizSpeed != 0) {
            //switches the texture to a walking motion if it is picking up speed
            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }
            //aakes the sprite gradually slow down
            horizSpeed = (int) (horizSpeed * 0.9);
            //makes the entity show no movement if it slows down
            if (FXGLMath.abs(horizSpeed) < 1) {
                horizSpeed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    private void updateVertically(double tpf) {
        //moves vertically
        entity.translateY(vertSpeed * tpf);

        if (vertSpeed != 0) {
            //if speed picks up then sets either the up or down animation
            if (texture.getAnimationChannel() == animIdle) {
                if (vertSpeed < 0) {
                    texture.loopAnimationChannel(animUp);
                } else {
                    texture.loopAnimationChannel(animDown);
                }
            }
            //if switched from down to up movement
            if (texture.getAnimationChannel() == animDown) {
                if (vertSpeed < 0) {
                    texture.loopAnimationChannel(animUp);
                }
            }
            //if switched from up to down motion
            if (texture.getAnimationChannel() == animUp) {
                if (vertSpeed > 0) {
                    texture.loopAnimationChannel(animDown);
                }
            }
            //aakes the sprite gradually slow down
            vertSpeed = (int) (vertSpeed * 0.9);
            //if the sprite slows down then it shows static animation
            if (FXGLMath.abs(vertSpeed) < 1) {
                vertSpeed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void moveRight() {
        //sets horizontal speed and flips the aniomation if needed
        horizSpeed = 150;

        getEntity().setScaleX(-1);
    }

    public void moveLeft() {
        //sets horizontal speed and flips if needed
        horizSpeed = -150;
        getEntity().setScaleX(1);
    }

    public void moveUp() {
        //sets vertical speed
        vertSpeed = -150;
    }

    public void moveDown() {
        //sets vertical speed
        vertSpeed = 150;
    }

    public void setRoute(ArrayList<Point2D> inputRoute) {
        //sets a new route for the receiver
        if (route != null) route.clear();

        route.addAll(inputRoute);
        //route = inputRoute;
    }

    public void routeMovement(ArrayList<Point2D> routeInfo) {
        //moves in teh shape of aroute if play is just starting
        if (!timerRunning && routeIndex>-1) {
            timerRunning = true;
            //uses timer running to keep it make this run asynchronously (not on top of each other)
            routeTimer = FXGL.getGameTimer().runAtInterval(() -> {
                if (routeIndex < routeInfo.size() && routeIndex > -1) {
                    //moves according to the vector that is in the route array
                    entity.translate(routeInfo.get(routeIndex).multiply(0.5/playCounter));
                    timerRunning = false;
                    routeCounter++;
                }
            }, Duration.seconds(0.08));
        }
        //determines which vector in the route array that is being iterated through
        if (routeCounter >= 125 * playCounter) {
            routeIndex++;
            routeCounter = 0;
        }
    }

    public void moveInbounds(){
        //keeps the player on the field
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