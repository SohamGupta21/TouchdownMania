import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
//this component manages the football object
public class FootballComponent extends Component {
    private double[] P0 = new double[2];
    private double[] P1 = new double[2];
    private double[] P2 = new double[2];
    //entity that is holding the ball
    private Entity playerToFollow;
    private boolean isBeingThrown, hasBeenThrown = false;
    private boolean throwable = true;
    private double t = 0;
    private boolean scoreAdded = false;
    //allows the football component to change things in the overall game through the gameManagementclass
    private GameManagement gameManagement;
    ArrayList<Node> screenObjects;

    public FootballComponent(Entity player, GameManagement game, ArrayList<Node> so) {
        //the football takes the entity argument because it allows the object to follow the player, as if it is being held
        playerToFollow = player;
        //next two variables give the class access to the main game
        gameManagement = game;
        screenObjects = so;
    }

    @Override
    public void onUpdate(double tpf) {
        //follows the quarterback
        if (entity.getX() < 100 && entity.getY() > 75 && entity.getY() < FXGL.getAppHeight() - 75 && !scoreAdded) {
            FXGL.getWorldProperties().increment("score", 7);
            scoreAdded = true;
        }
        //follows a quardratic bezier curve
        if (isBeingThrown) {
            if (t < 1) {
                t += 0.01;
            }
            entity.setPosition(calculateLocationWithBezier(t)[0], calculateLocationWithBezier(t)[1]);
        }
        //if the football is in the hands of a player then it just follows that player, changes according to position of player
        if (!isBeingThrown) {
            if(playerToFollow.getScaleX() == 1) {
                entity.setPosition(playerToFollow.getPosition().add(25, 35));
            } else if (playerToFollow.getScaleX() == -1){
                entity.setPosition(playerToFollow.getPosition().add(0, 35));
            }
        }
        //checks that if the ball is thrown to the ground, an incomplete pass
        if(Math.round(entity.getX()) == P2[0] && Math.round(entity.getY()) == P2[1] && playerToFollow == null){
            entity.getComponent(FootballComponent.class).setPlayerToFollow(FXGL.getGameWorld().getEntitiesByType(EntityType.QB).get(0));
            P0 = new double[2];
            P1 = new double[2];
            P2 = new double[2];
            isBeingThrown = false;
            gameManagement.startNewPlay((int) Math.round(FXGL.getGameWorld().getEntitiesByType(EntityType.QB).get(0).getX()), (int) Math.round(FXGL.getGameWorld().getEntitiesByType(EntityType.QB).get(0).getX()), (int) Math.round(FXGL.getGameWorld().getEntitiesByType(EntityType.QB).get(0).getY()),entity,  FXGL.getGameWorld().getEntitiesByType(EntityType.QB).get(0), new ArrayList<Entity>(FXGL.getGameWorld().getEntitiesByType(EntityType.OL)), new ArrayList<Entity>(FXGL.getGameWorld().getEntitiesByType(EntityType.WR)),FXGL.getGameWorld().getEntitiesByType(EntityType.SAF).get(0),new ArrayList<Entity>(FXGL.getGameWorld().getEntitiesByType(EntityType.DB)),new ArrayList<Entity>(FXGL.getGameWorld().getEntitiesByType(EntityType.DL)),screenObjects);
        }
        //makes sure that the ball in still in bounds
        moveInbounds();
    }

    public void reset() {
        //this function is called when a new play has to start, it just resets variables
        isBeingThrown = false;
        hasBeenThrown = false;
        throwable = true;
        scoreAdded = false;
        P0 = new double[2];
        P1 = new double[2];
        P2 = new double[2];

    }

    public void setThrowable(boolean input) {
        //allows the ball to be thrown
        throwable = input;
    }

    public Entity getPlayerToFollow() {
        //returns the player who is holding the ball
        return playerToFollow;
    }

    public void setPlayerToFollow(Entity inputPlayer) {
        //sets the player who is holding the ball
        playerToFollow = inputPlayer;
    }

    public void setIsBeingThrown(boolean input) {
        //change change whether the ball is being thrown or not
        isBeingThrown = input;
    }

    public void throwBall(double destinationX, double destinationY) {
        //throws the ball across acruve path
        if(throwable){
            //next couple segments of code just set variables so that the ball can be thrown
            P0[0] = playerToFollow.getX();
            P0[1] = playerToFollow.getY();

            P2[0] = destinationX;
            P2[1] = destinationY;

            P1[0] = (playerToFollow.getX() + destinationX) / 2;
            P1[1] = destinationY - ((playerToFollow.getX() - destinationX) / 3);

            if (P1[1] < 10) {
                P1[1] = 10;
            }
            //changes variable because the state of the ball has changed
            playerToFollow = null;
            isBeingThrown = true;
            hasBeenThrown = true;
        }



    }


    private double[] calculateLocationWithBezier(double t) {
        //uses the bezier formula to move the ball
        //equation : 𝐂(𝑡)=(1−𝑡)2𝐏0+2𝑡(1−𝑡)𝐏1+𝑡2𝐏2
        //the t variable represents the percentage of the curve that the ball has traveled across
        double x = Math.pow(1 - t, 2) * P0[0] + 2 * t * (1 - t) * P1[0] + Math.pow(t, 2) * P2[0];
        double y = Math.pow(1 - t, 2) * P0[1] + 2 * t * (1 - t) * P1[1] + Math.pow(t, 2) * P2[1];

        return new double[]{x, y};
    }

    public void moveInbounds(){
        //keeps the ball inbounds
        //the numbers are specific to the size of the field
        if(entity.getX() < 0){
            entity.setX(0);
        }
        if(entity.getX() + 20 > 1280){
            entity.setX(1280 - 66);
        }
        if(entity.getY() < 73){
            entity.setY(73);
        }
        if(entity.getY() +  20 > 647){
            entity.setY(647-66);
        }
    }
}
