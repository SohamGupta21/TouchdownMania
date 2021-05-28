import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;


public class Game extends GameApplication {
    private final String font = "Sans Serif";
    private final ArrayList<Node> screenObjects = new ArrayList<>();
    private boolean playRunning = false;
    private Entity quarterback, football, safety,endzone, mainPlayer, timer, line;
    private final ArrayList<Entity> receivers = new ArrayList<>();
    private final ArrayList<Entity> defensiveBacks = new ArrayList<>();
    private final ArrayList<Entity> offensiveLinemen = new ArrayList<>();
    private final ArrayList<Entity> defensiveLinemen = new ArrayList<>();
    private GameManagement game = new GameManagement();


    private int quarterbackX = 1000;
    private final int quarterbackY = 350;

    @Override
    protected void initSettings(GameSettings settings) {
        //creates the main screen with the main menu and all
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Touchdown Mania");
        settings.setMainMenuEnabled(true);
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        //creates all the entities from the entity factory
        getGameWorld().addEntityFactory(new ObjectFactory());

        spawn("background", 0, 73);

        line = spawn("lineOfScrimmage", quarterbackX-100, 73);

        quarterback = null;
        quarterback = spawn("quarterback", quarterbackX, quarterbackY);

        mainPlayer = null;
        mainPlayer = quarterback;

        safety = null;
        safety = spawn("safety", new SpawnData(quarterbackX - 500, quarterbackY).put("player", quarterback));

        defensiveLinemen.clear();
        defensiveLinemen.add(spawn("defensive lineman", new SpawnData(quarterbackX - 200, quarterbackY - 80).put("quarterback", quarterback)));
        defensiveLinemen.add(spawn("defensive lineman", new SpawnData(quarterbackX - 200, quarterbackY - 10).put("quarterback", quarterback)));
        defensiveLinemen.add(spawn("defensive lineman", new SpawnData(quarterbackX - 200, quarterbackY + 60).put("quarterback", quarterback)));

        offensiveLinemen.clear();
        offensiveLinemen.add(spawn("offensive lineman", new SpawnData(quarterbackX - 100, quarterbackY - 80).put("playerToBlock", defensiveLinemen.get(0))));
        offensiveLinemen.add(spawn("offensive lineman", new SpawnData(quarterbackX - 100, quarterbackY - 10).put("playerToBlock", defensiveLinemen.get(1))));
        offensiveLinemen.add(spawn("offensive lineman", new SpawnData(quarterbackX - 100, quarterbackY + 60).put("playerToBlock", defensiveLinemen.get(2))));

        receivers.clear();
        receivers.add(spawn("wide receiver", quarterbackX - 100, quarterbackY - 250));
        receivers.add(spawn("wide receiver", quarterbackX - 100, quarterbackY + 250));

        defensiveBacks.clear();
        defensiveBacks.add(spawn("defensive back", new SpawnData(quarterbackX - 200, quarterbackY - 250).put("player", receivers.get(0))));
        defensiveBacks.add(spawn("defensive back", new SpawnData(quarterbackX - 200, quarterbackY + 250).put("player", receivers.get(1))));

        football = null;
        football = spawn("football", new SpawnData(quarterbackX, quarterbackY + 20).put("player", quarterback).put("gameManager", game).put("screenObj", screenObjects));


        endzone = null;
        endzone = spawn("endzone", 0, 75);

        timer = spawn("timer", new SpawnData(100,50).put("GameManager", game));

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //creates a game timer and a score variable
        vars.put("score", 0);
        vars.put("timer", 120);
    }

    @Override
    protected void initUI() {
        //creates the ui including the background, title, game variables etc.
        screenObjects.clear();
        getGameScene().setBackgroundColor(Color.GREEN);
        var scoreTxt = getUIFactoryService().newText("", 24);
        scoreTxt.textProperty().bind(getip("score").asString("Score: %d"));
        addUINode(scoreTxt, 60, 60);

        var timerTxt = getUIFactoryService().newText("", 24);
        timerTxt.textProperty().bind(getip("timer").asString("Timer: %d"));
        addUINode(timerTxt, 200, 60);

        var Title = getUIFactoryService().newText("TOUCHDOWN MANIA", Color.PURPLE, FontType.GAME, 50);
        addUINode(Title, 500,60);

        //creates screen which allows you to choose the plays for the wide receivers
        game.createRectangle(screenObjects);
        game.createText(screenObjects);
        createDoneButton();
        game.createRouteImages(screenObjects);
        game.makeRouteButtons(receivers,screenObjects);
    }

    public void createDoneButton() {
        //this defines the button that happens when the user chooses what play to assign to the receivers
        Button btnNext = getUIFactoryService().newButton("Done");
        btnNext.setOnAction((e) -> {
            playRunning = true;
            game.showOrHideRouteScreen(false, screenObjects);
            for (Entity wr : receivers) {
                wr.getComponent(WideReceiverComponent.class).setPlayRunning(true);
            }
            safety.getComponent(DefensiveComponent.class).setPlayRunning(true);

            for (Entity db : defensiveBacks) {
                db.getComponent(DefensiveComponent.class).setPlayRunning(true);
            }

            for (Entity dl : defensiveLinemen) {
                dl.getComponent(DefensiveComponent.class).setPlayRunning(true);
            }
            for (Entity ol : offensiveLinemen) {
                ol.getComponent(OffensiveLineComponent.class).setPlayRunning(true);
            }
            if(getWorldProperties().getInt("timer") == 120){
                timer.getComponent(TimeClockComponent.class).startTimer();
            }
            quarterback.setY(quarterbackY);
            mainPlayer = quarterback;
            game.restartPlay(receivers);
            football.getComponent(FootballComponent.class).setPlayerToFollow(quarterback);
        });
        btnNext.setPrefWidth(160);
        btnNext.setPrefHeight(60);
        screenObjects.add(btnNext);
        addUINode(btnNext, getAppWidth() / 2 - 30, getAppHeight() - 110);
    }

    @Override
    protected void initInput() {
        //moves the player that the user has control over
        onKey(KeyCode.LEFT, () -> {
            if (playRunning) {
                if (mainPlayer == quarterback) mainPlayer.getComponent(QuarterbackComponent.class).moveLeft();
                else mainPlayer.getComponent(WideReceiverComponent.class).moveLeft();
            }

        });
        onKey(KeyCode.RIGHT, () -> {
            if (playRunning) {
                if (mainPlayer == quarterback) mainPlayer.getComponent(QuarterbackComponent.class).moveRight();
                else mainPlayer.getComponent(WideReceiverComponent.class).moveRight();
            }

        });
        onKey(KeyCode.UP, () -> {
            if (playRunning) {
                if (mainPlayer == quarterback) mainPlayer.getComponent(QuarterbackComponent.class).moveUp();
                else mainPlayer.getComponent(WideReceiverComponent.class).moveUp();
            }
        });
        onKey(KeyCode.DOWN, () -> {
            if (playRunning) {
                if (mainPlayer == quarterback) mainPlayer.getComponent(QuarterbackComponent.class).moveDown();
                else mainPlayer.getComponent(WideReceiverComponent.class).moveDown();
            }
        });
        //throws the ball based on the location of the mouse click
        getInput().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (playRunning && football.getComponent(FootballComponent.class).getPlayerToFollow() != null) {
                football.getComponent(FootballComponent.class).throwBall(getInput().getMousePositionUI().getX(), getInput().getMousePositionUI().getY());
            }
        });

    }

    @Override
    protected void initPhysics() {
        //manages collisions
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.WR, EntityType.BALL) {
            //this collision represents a receiver catching a ball
            @Override
            protected void onCollisionBegin(Entity receiver, Entity ball) {
                football.getComponent(FootballComponent.class).setPlayerToFollow(receiver);
                football.getComponent(FootballComponent.class).setIsBeingThrown(false);
                receiver.getComponent(WideReceiverComponent.class).setMovingWithInput(true);
                mainPlayer = receiver;
                game.makeDefenseAttackBall(receiver,safety, defensiveBacks, defensiveLinemen);
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.DL, EntityType.QB) {
            //this collision is if the defensive linemen sacks the qb
            @Override
            protected void onCollisionBegin(Entity dl, Entity qb) {
                if (Math.random() < 0.75){
                    qb.getComponent(QuarterbackComponent.class).sacked();
                    getGameTimer().runOnceAfter(() -> {
                        playRunning = false;
                        game.startNewPlay((int) football.getX(), quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                    }, Duration.seconds(1));
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.DL, EntityType.OL) {
            // this manages a battle between the defensive and offensive linemen
            @Override
            protected void onCollisionBegin(Entity dl, Entity ol) {
                if (Math.random() > 0.5) {
                    dl.translateX(-25);
                    ol.translateX(-15);
                } else {
                    dl.translateX(15);
                    ol.translateX(25);
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.DL, EntityType.WR) {
            // this happens if the defensive linemen collides with the receiver (checks if the receiver has the ball and then tackles)
            @Override
            protected void onCollisionBegin(Entity dl, Entity wr) {
                //uses randomness and checks if the ball is in the hands of the receiver
                if (Math.random() < 0.75 && football.getComponent(FootballComponent.class).getPlayerToFollow() == wr) {
                    wr.getComponent(WideReceiverComponent.class).tackled();
                    //runs a timer so that the user can see the tackle
                    getGameTimer().runOnceAfter(() -> {
                        playRunning = false;
                        game.startNewPlay((int)football.getX(), quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                    }, Duration.seconds(1));
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.DB, EntityType.WR) {
            // collision between defensive back and the wide receiver - the db guards the wr
            @Override
            protected void onCollisionBegin(Entity db, Entity wr) {
                //uses randomness and checks if the ball is in the hands of the receiver
                if (Math.random() < 0.75 && football.getComponent(FootballComponent.class).getPlayerToFollow() == wr) {
                    wr.getComponent(WideReceiverComponent.class).tackled();
                    getGameTimer().runOnceAfter(() -> {
                        mainPlayer = quarterback;
                        playRunning = false;
                        game.startNewPlay((int)football.getX(), quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                    }, Duration.seconds(1));
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SAF, EntityType.QB) {
            //manages a collision between the safety and the quarterback
            @Override
            protected void onCollisionBegin(Entity saf, Entity qb) {
                //runs a randomness function and then checks if the quarterback has the ball in his hand
                if (Math.random() < 0.75) qb.getComponent(QuarterbackComponent.class).sacked();
                getGameTimer().runOnceAfter(() -> {
                    playRunning = false;
                    game.startNewPlay((int)football.getX(), quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                }, Duration.seconds(1));
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SAF, EntityType.WR) {
            // runs collision between a safety and wr
            @Override
            protected void onCollisionBegin(Entity saf, Entity wr) {
                //uses randomness and checks if the wr has the ball
                if (Math.random() < 0.75 && football.getComponent(FootballComponent.class).getPlayerToFollow() == wr) {
                    wr.getComponent(WideReceiverComponent.class).tackled();
                    //runs a timer so that we can see the tackle
                    getGameTimer().runOnceAfter(() -> {
                        playRunning = false;
                        game.startNewPlay((int)football.getX(), quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                    }, Duration.seconds(1));
                }
            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.ENDZONE) {
            // checks for a touchdown
            @Override
            protected void onCollisionBegin(Entity ball, Entity endzone) {
                ball.setPosition(500,500);
                //as long as the ball is not just randomly thrown, it should work
                if(ball.getComponent(FootballComponent.class).getPlayerToFollow() != null){
                    getNotificationService().pushNotification("TOUCHDOWN!!!");
                    getWorldProperties().increment("score", 7);
                    playRunning = false;
                    game.startNewPlay(1000, quarterbackX, quarterbackY, football, quarterback, offensiveLinemen, receivers, safety, defensiveBacks, defensiveLinemen, screenObjects);
                }

            }
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.QB, EntityType.LINE) {
            // checks if the quarterback ever crosses the line of scrimmage, meaning that he cant throw and so that everyone attacks him
            @Override
            protected void onCollisionBegin(Entity qb, Entity line) {
                football.getComponent(FootballComponent.class).setThrowable(false);
                game.makeDefenseAttackBall(quarterback, safety, defensiveBacks, defensiveLinemen);
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
