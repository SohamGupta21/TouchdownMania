import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;

public class EndZoneComponent extends Component {

    @Override
    public void onAdded(){
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(125,570)));
    }


}
