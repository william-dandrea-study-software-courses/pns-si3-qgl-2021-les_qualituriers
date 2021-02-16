package fr.unice.polytech.si3.qgl.qualituriers.render;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.qualituriers.entity.boat.turnboat.TurnBoat;
import fr.unice.polytech.si3.qgl.qualituriers.game.GameInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.RoundInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.goal.RegattaGoal;
import fr.unice.polytech.si3.qgl.qualituriers.utils.CheckPoint;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Collisions;
import fr.unice.polytech.si3.qgl.qualituriers.utils.PositionableShape;
import fr.unice.polytech.si3.qgl.qualituriers.utils.action.Action;
import fr.unice.polytech.si3.qgl.qualituriers.utils.action.Turn;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThirdRender extends Render{



    private CheckPoint[] listCheckPoint;
    private CheckPoint currentCheckPoint;
    private ObjectMapper om;

    public ThirdRender(GameInfo gameInfo) {
        super(gameInfo);

        om = new ObjectMapper();
        listCheckPoint = ((RegattaGoal) gameInfo.getGoal()).getCheckPoints();
        currentCheckPoint = listCheckPoint[0];
    }

    @Override
    public String nextRound(RoundInfo round) throws JsonProcessingException {

        List<Action> finalsActions = new ArrayList<>();

        PositionableShape<Shape> checkpointsShape = new PositionableShape<>(currentCheckPoint.getShape(), currentCheckPoint.getPosition());
        PositionableShape<Shape> boatShape = new PositionableShape<>(gameInfo.getShip().getShape(), gameInfo.getShip().getTransform());

        if (!Collisions.isColliding(checkpointsShape, boatShape)) {


            // Calculer l'angle
            double angle = gameInfo.getShip().getTransform().getAngleToSee(currentCheckPoint.getPosition());

            if (angle == 0.0) {
                //finalsActions = gameInfo.getShip().moveBoatToAPoint(currentCheckPoint.getPosition());
                finalsActions = gameInfo.getShip().moveBoatInLine();
            } else {
                finalsActions = gameInfo.getShip().turnBoat(angle);
            }
            // verifier si on a atteint le checkpoint : si oui : si ya plus de checpoints apres s'arreter, sinon prendre le nouveau checkpoint

        }







        return om.writeValueAsString(finalsActions);
    }
}
