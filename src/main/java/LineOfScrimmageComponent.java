import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;


public class LineOfScrimmageComponent extends Component {
    //line of scrimmage component is meant to prevent the qb from throwing after he crosses the line (as per football rules)
    // and to also tell the defense to attack the quarterback when he crosses that line
    @Override
    public void onAdded(){
        //adds hit box to the component
        entity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(5,567)));
    }

}