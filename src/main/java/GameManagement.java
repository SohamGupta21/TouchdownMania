import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameManagement {
    //this class managees the game overall, helps with cleaner code rather than having everything in the game.java
    public void createRectangle(ArrayList<Node> screenObjects) {
        //creates the rectangle for the background of the route screen
        Rectangle routeOptionRect = new Rectangle(getAppWidth(), getAppHeight());
        routeOptionRect.setFill(Color.BLACK);
        screenObjects.add(routeOptionRect);
        addUINode(routeOptionRect, 0, 0);
    }

    public void createText(ArrayList<Node> screenObjects) {
        //makes three text fields for the route choosing screen
        Text routeTitle = new Text("Choose Routes");
        routeTitle.setFill(Color.WHITE);
        screenObjects.add(routeTitle);
        addUINode(routeTitle, getAppWidth() / 2 - 50, 20);

        Text WR1Title = new Text("WR1");
        WR1Title.setFill(Color.WHITE);
        screenObjects.add(WR1Title);
        addUINode(WR1Title, getAppWidth() / 4, 60);

        Text WR2Title = new Text("WR2");
        WR2Title.setFill(Color.WHITE);
        screenObjects.add(WR2Title);
        addUINode(WR2Title, getAppWidth() * 3 / 4, 60);
    }

    public void createRouteImages(ArrayList<Node> screenObjects) {
        //runs the route image functions for the different route combinations
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/inAndUp.jpeg", getAppWidth() / 4 - 60, getAppHeight() / 4 - 29, screenObjects);
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/outAndUp.jpeg", 3 * getAppWidth() / 4 - 60, getAppHeight() / 4 - 29,screenObjects);
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/deepIn.jpeg", getAppWidth() / 4 - 60, 2 * getAppHeight() / 4 - 29,screenObjects);
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/deepOut.jpeg", 3 * getAppWidth() / 4 - 60, 2 * getAppHeight() / 4 - 29, screenObjects);
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/streak.jpeg", getAppWidth() / 4 - 60, 3 * getAppHeight() / 4 - 29, screenObjects);
        makeRouteImage("/Users/gupta/Desktop/Java Projects/Madden Game/src/main/resources/assets/textures/streak.jpeg", 3 * getAppWidth() / 4 - 60, 3 * getAppHeight() / 4 - 29, screenObjects);
    }

    public void makeRouteImage(String path, double x, double y, ArrayList<Node> screenObjects) {
        //makes an image view and assigns the image that was passed in
        ImageView routeImage = new ImageView();
        routeImage.setFitWidth(120);
        routeImage.setFitHeight(58);
        //try catch to prevent file lnot found errors
        try {
            routeImage.setImage(new Image(new FileInputStream(path)));
            //adds the btn to an array and the acreen
            screenObjects.add(routeImage);
            addUINode(routeImage, x, y);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void makeRouteButtons(ArrayList<Entity> receivers, ArrayList<Node> screenObjects) {
        //creates the buttons for the routes
        createRouteBtn("IN AND UP", getAppWidth() / 4 - 100, getAppHeight() / 4 - 15 + 63, receivers.get(0).getComponent(WideReceiverComponent.class), inAndUp(), screenObjects);
        createRouteBtn("OUT AND UP", 3 * getAppWidth() / 4 - 100, getAppHeight() / 4 - 15 + 63, receivers.get(1).getComponent(WideReceiverComponent.class), outAndUp(),screenObjects);
        createRouteBtn("DEEP IN", getAppWidth() / 4 - 100, 2 * getAppHeight() / 4 - 15 + 63, receivers.get(0).getComponent(WideReceiverComponent.class), deepIn(),screenObjects);
        createRouteBtn("DEEP OUT", 3 * getAppWidth() / 4 - 100, 2 * getAppHeight() / 4 - 15 + 63, receivers.get(1).getComponent(WideReceiverComponent.class), deepOut(),screenObjects);
        createRouteBtn("STREAK", getAppWidth() / 4 - 100, 3 * getAppHeight() / 4 - 15 + 63, receivers.get(0).getComponent(WideReceiverComponent.class), streak(),screenObjects);
        createRouteBtn("STREAK", 3 * getAppWidth() / 4 - 100, 3 * getAppHeight() / 4 - 15 + 63, receivers.get(1).getComponent(WideReceiverComponent.class), streak(),screenObjects);
    }

    public void createRouteBtn(String text, double x, double y, WideReceiverComponent recComp, ArrayList<Point2D> routeToAdd, ArrayList<Node> screenObjects) {
        //creates a new route and assigns a function to the button (this function basically adds the route to a receiver component)
        Button buttonMade = getUIFactoryService().newButton(text);
        buttonMade.setOnAction((e) -> {
            recComp.setRoute(routeToAdd);
        });
        //adds the button to screen
        buttonMade.setPrefWidth(200);
        buttonMade.setPrefHeight(30);
        screenObjects.add(buttonMade);
        addUINode(buttonMade, x, y);
    }

    public ArrayList<Point2D> inAndUp() {
        //defines a in and up route
        ArrayList<Point2D> route = new ArrayList<>();

        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(0, 1));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));

        return route;
    }

    public ArrayList<Point2D> outAndUp() {
        //defiens an out and up route
        ArrayList<Point2D> route = new ArrayList<>();

        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(0, -1));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));

        return route;
    }

    public ArrayList<Point2D> deepIn() {
        //defines a deep in route
        ArrayList<Point2D> route = new ArrayList<>();

        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(0, 1));
        route.add(new Point2D(0, 1));
        route.add(new Point2D(0, 1));
        System.out.println("deep in : " + route);
        return route;
    }

    public ArrayList<Point2D> deepOut() {
        //defines a deep out route
        ArrayList<Point2D> route = new ArrayList<>();

        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(0, -1));
        route.add(new Point2D(0, -1));
        route.add(new Point2D(0, -1));

        return route;
    }

    public ArrayList<Point2D> streak() {
        //defines a streak route
        ArrayList<Point2D> route = new ArrayList<>();

        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));
        route.add(new Point2D(-1, 0));

        return route;
    }

    public void showOrHideRouteScreen(boolean input, ArrayList<Node> screenObjects) {
        //mkaes the route screen visible or invisible
        for (Node n : screenObjects) {
            n.setVisible(input);
        }
    }

    public void makeDefenseAttackBall(Entity holder,Entity safety, ArrayList<Entity> defensiveBacks, ArrayList<Entity> defensiveLinemen) {
        //makes all of the defense follow a certain player to tackle them
        safety.getComponent(DefensiveComponent.class).setPersonToFollow(holder);

        for (Entity db : defensiveBacks) {
            db.getComponent(DefensiveComponent.class).setPersonToFollow(holder);
        }

        for (Entity dl : defensiveLinemen) {
            dl.getComponent(DefensiveComponent.class).setPersonToFollow(holder);
        }
    }

    public void startNewPlay(int ballLoc, int quarterbackX , int quarterbackY,Entity football, Entity quarterback, ArrayList<Entity> offensiveLinemen, ArrayList<Entity> receivers, Entity safety, ArrayList<Entity> defensiveBacks, ArrayList<Entity> defensiveLinemen, ArrayList<Node> screenObjects) {
        //stops motion
        stopPlay(receivers, safety, defensiveBacks, defensiveLinemen,offensiveLinemen, quarterback);
        //moves players where they need to be
        resetObjectPositions(ballLoc, quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen);
        //shows the route screen
        showOrHideRouteScreen(true, screenObjects);
        //allows the football to be thrown
        football.getComponent(FootballComponent.class).setThrowable(true);
    }

    public void stopPlay(ArrayList<Entity> receivers, Entity safety, ArrayList<Entity> defensiveBacks, ArrayList<Entity> defensiveLinemen,ArrayList<Entity> offensiveLinemen, Entity quarterback) {
        //runs the reset function for every component
        quarterback.getComponent(QuarterbackComponent.class).reset();
        safety.getComponent(DefensiveComponent.class).reset();

        for (Entity wr : receivers) {
            wr.getComponent(WideReceiverComponent.class).reset();
        }

        for (Entity db : defensiveBacks) {
            db.getComponent(DefensiveComponent.class).reset();
        }

        for (Entity dl : defensiveLinemen) {
            dl.getComponent(DefensiveComponent.class).reset();
        }
        for (Entity ol : offensiveLinemen) {
            ol.getComponent(OffensiveLineComponent.class).reset();
        }
    }

    public void restartPlay(ArrayList<Entity> receivers){
        //runs the start function for all the players
        for(Entity r: receivers){
            r.getComponent(WideReceiverComponent.class).start();
        }
        for(int i = 0; i <2 ; i++){
            //makes the defensive backs follow their wide receiver
            FXGL.getGameWorld().getEntitiesByType(EntityType.DB).get(i).getComponent(DefensiveComponent.class).setPersonToFollow(receivers.get(i));
            FXGL.getGameWorld().getEntitiesByType(EntityType.DB).get(i).getComponent(DefensiveComponent.class).start();
        }
        for(Entity dl : new ArrayList<Entity>()){
            dl.getComponent(DefensiveComponent.class).start();
        }
        FXGL.getGameWorld().getEntitiesByType(EntityType.SAF).get(0).getComponent(DefensiveComponent.class).start();
    }

    public void resetObjectPositions(int ballLoc, int quarterbackX , int quarterbackY,Entity football, Entity quarterback, ArrayList<Entity> offensiveLinemen, ArrayList<Entity> receivers, Entity safety, ArrayList<Entity> defensiveBacks, ArrayList<Entity> defensiveLinemen) {
        //sets all of the object positions relative to the quarterback position
        quarterbackX = ballLoc;

        quarterback.setPosition(quarterbackX,quarterbackY);

        safety.setPosition(quarterbackX - 500, quarterbackY);

        defensiveLinemen.get(0).setPosition(quarterbackX - 200, quarterbackY - 80);
        defensiveLinemen.get(1).setPosition(quarterbackX - 200, quarterbackY - 10);
        defensiveLinemen.get(2).setPosition(quarterbackX - 200, quarterbackY + 60);

        offensiveLinemen.get(0).setPosition(quarterbackX - 100, quarterbackY - 80);
        offensiveLinemen.get(1).setPosition(quarterbackX - 100, quarterbackY - 10);
        offensiveLinemen.get(2).setPosition(quarterbackX - 100, quarterbackY + 60);

        receivers.get(0).setPosition(quarterbackX - 100, quarterbackY - 250);
        receivers.get(1).setPosition(quarterbackX - 100, quarterbackY + 250);
        System.out.println(receivers.get(0).getX());
        defensiveBacks.get(0).setPosition(quarterbackX - 200, quarterbackY - 250);
        defensiveBacks.get(1).setPosition(quarterbackX - 200, quarterbackY + 250);
        FXGL.getGameWorld().getEntitiesByType(EntityType.LINE).get(0).setX(quarterbackX - 100);

        quarterback.setPosition(quarterbackX, quarterbackY);

        for(Entity obj : getGameWorld().getEntities()){
            if(obj.getX() < 0) obj.setX(5);
        }

        football.getComponent(FootballComponent.class).setPlayerToFollow(quarterback);
        football.setPosition(quarterback.getX(), quarterback.getY() + 20);

    }

    public void endGame(){
        //shows the game over screen and resets a new game
        showMessage("Game Over: " + getWorldProperties().getInt("score"), () -> {//add your message
            getGameController().startNewGame();//add what you want to do after message
        });
    }
}
