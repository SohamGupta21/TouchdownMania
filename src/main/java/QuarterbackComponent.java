import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class QuarterbackComponent extends Component {

    private int horizSpeed, vertSpeed = 0;

    private final AnimatedTexture texture;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animUp;
    private final AnimationChannel animDown;
    private final AnimationChannel animIdleUp;
    private final AnimationChannel animIdleDown;
    private final double[] playStartPosition = new double[2];
    private boolean sacked = false;

    //creates the textures for the movement
    public QuarterbackComponent(boolean isOffense) {
        animIdle = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("footballSpritesLeft.png"), 4, 66, 63, Duration.seconds(1), 0, 3);
        animUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleUp = new AnimationChannel(FXGL.image("footballSpritesUP.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        animDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 3);
        animIdleDown = new AnimationChannel(FXGL.image("footballSpritesDown.png"), 4, 66, 62, Duration.seconds(1), 0, 0);
        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        //creates the texture and hit box
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(65, 65)));
        //ntoes the starting position
        playStartPosition[0] = entity.getX();
        playStartPosition[1] = entity.getY();
    }

    @Override
    public void onUpdate(double tpf) {
        //moves the player according to the arow keys as long as they have not been sacked
        if (!sacked) {
            updateHorizontally(tpf);
            updateVertically(tpf);
        }
        moveInbounds();
    }

    public AnimatedTexture getTexture(){
        return texture;
    }
    public void reset(){
        //resets everything for a new play
        horizSpeed = 0;
        vertSpeed = 0;
        sacked = false;
        entity.setRotation(0);
        entity.setScaleX(1);
    }

    public void sacked() {
        // a quarterback is tackled, allows a new play to start
        sacked = true;
        texture.stop();
        entity.setRotation(90);
    }

    public void updateHorizontally(double tpf) {
        //moves in x direction
        entity.translateX(horizSpeed * tpf);

        if (horizSpeed != 0) {
            //siwtches the texture if starting to pick up speed
            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }
            //aakes the sprite gradually slow down
            horizSpeed = (int) (horizSpeed * 0.9);
            //switches the texture if too slow
            if (FXGLMath.abs(horizSpeed) < 1) {
                horizSpeed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    private void updateVertically(double tpf) {
        //moves in vertical direction
        entity.translateY(vertSpeed * tpf);

        if (vertSpeed != 0) {
            //switches the sprite if moving up or down fast enough, starts motion
            if (texture.getAnimationChannel() == animIdle) {
                if (vertSpeed < 0) {
                    texture.loopAnimationChannel(animUp);
                } else {
                    texture.loopAnimationChannel(animDown);
                }
            }
            //switched from up to down sprite if needed
            if (texture.getAnimationChannel() == animDown) {
                if (vertSpeed < 0) {
                    texture.loopAnimationChannel(animUp);
                }
            }
            //switches sprtie from up to down if needed
            if (texture.getAnimationChannel() == animUp) {
                if (vertSpeed > 0) {
                    texture.loopAnimationChannel(animDown);
                }
            }
            //aakes the sprite gradually slow down
            vertSpeed = (int) (vertSpeed * 0.9);
            //makes the animation stop if the speed is slowed
            if (FXGLMath.abs(vertSpeed) < 1) {
                vertSpeed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void moveRight() {
        //sets speed variable and flips animation if needed
        horizSpeed = 150;

        getEntity().setScaleX(-1);
    }

    public void moveLeft() {
        //sets the speed and changes texture if needed
        horizSpeed = -150;

        getEntity().setScaleX(1);
    }

    public void moveUp() {
        //sets vertical speed
        vertSpeed = -150;
    }

    public void moveDown() {
        //sets the vertical speed
        vertSpeed = 150;
    }

    public void moveInbounds(){
        //keeps the thing in the bounds of the screen
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