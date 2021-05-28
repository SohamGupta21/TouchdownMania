import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.PhysicsComponent;

public class ObjectFactory implements EntityFactory {
    //makes all of the entities
    //quarterback entity, uses collidable (in the library) and quarterback component (mine)
    @Spawns("quarterback")
    public Entity newQB(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.QB)
                .with(new QuarterbackComponent(true))
                .with(new CollidableComponent(true))
                .build();

    }
    //wide receiver, uses collidable and wide receiver component
    @Spawns("wide receiver")
    public Entity newWR(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.WR)
                .with(new WideReceiverComponent(true))
                .with(new CollidableComponent(true))
                .build();
    }
    //offensive line, uses offensive lineman and collideable component
    @Spawns("offensive lineman")
    public Entity newOL(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.OL)
                .with(new OffensiveLineComponent(true, data.get("playerToBlock")))
                .with(new CollidableComponent(true))
                .build();

    }
    //defensive linemen, uses defensive component and is collidable
    @Spawns("defensive lineman")
    public Entity newDL(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.DL)
                .with(new DefensiveComponent(false, data.get("quarterback"), true, 1))
                .with(new CollidableComponent(true))
                .build();
    }
    //defensive back, uses defensive component as well and collidable
    @Spawns("defensive back")
    public Entity newDB(SpawnData data) {

        return FXGL.entityBuilder(data)
                .type(EntityType.DB)
                .with(new DefensiveComponent(false, data.get("player"), true, 0.08))
                .with(new CollidableComponent(true))
                .build();
    }
    //safety, uses defensive component as well as collidable
    @Spawns("safety")
    public Entity newSAF(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.SAF)
                .with(new DefensiveComponent(false, data.get("player"), true, 0.08))
                .with(new CollidableComponent(true))
                .build();

    }
    //makes the background simply as an image
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .from(data)
                .view("fieldBackground.jpg")
                .build();

    }
    //makes the line of scrimmage which is not visible
    @Spawns("lineOfScrimmage")
    public Entity newLineOfScrimmage(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.LINE)
                .with(new CollidableComponent(true))
                .with(new LineOfScrimmageComponent())
                .build();
    }
    //makes the football using the football and collidable component
    @Spawns("football")
    public Entity newFootball(SpawnData data) {
        var texture = FXGL.texture("football.png",20,20);

        return FXGL.entityBuilder(data)
                .type(EntityType.BALL)
                .viewWithBBox(texture)
                .with(new CollidableComponent(true))
                .with(new FootballComponent(data.get("player"), data.get("gameManager"), data.get("screenObj")))
                .build();

    }
    //makes the endzone which is invisible since the endzone is drawn in the background
    @Spawns("endzone")
    public Entity newEndZone(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.ENDZONE)
                .with(new CollidableComponent(true))
                .with(new EndZoneComponent())
                .build();
    }
    //makes the timer which is not for the screen but for keeping a round short
    @Spawns("timer")
    public Entity newTimer(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(EntityType.TIMER)
                .with(new TimeClockComponent(data.get("GameManager")))
                .build();
    }
}
