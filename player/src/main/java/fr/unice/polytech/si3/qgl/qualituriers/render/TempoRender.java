package fr.unice.polytech.si3.qgl.qualituriers.render;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.qualituriers.game.GameInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.RoundInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.goal.RegattaGoal;
import fr.unice.polytech.si3.qgl.qualituriers.utils.CheckPoint;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Collisions;
import fr.unice.polytech.si3.qgl.qualituriers.utils.action.Action;
import fr.unice.polytech.si3.qgl.qualituriers.utils.logger.ILogger;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Shape;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TempoRender extends Render {

    private CheckPoint currentCheckPoint;
    private int checkPointCounter = 0;

    public TempoRender(GameInfo gameInfo, ILogger logger) {
        super(gameInfo, logger);
        CheckPoint[] listCheckPoint = ((RegattaGoal) gameInfo.getGoal()).getCheckPoints();
        currentCheckPoint = listCheckPoint[0];
    }

    @Override
    public List<Action> nextRound(RoundInfo round) throws JsonProcessingException {


        int numberOfCheckPoints = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints().length;

        gameInfo.getShip().setPosition(round.getShip().getPosition());
        gameInfo.getShip().setEntities(round.getShip().getEntities());

        gameInfo.getShip().setSailors(Arrays.asList(gameInfo.getSailors()));



        PositionableShape<? extends Shape> checkpointsShape = currentCheckPoint.getPositionableShape();
        PositionableShape<? extends Shape> boatShape = gameInfo.getShip().getPositionableShape();


        double distanceRestanteX = currentCheckPoint.getPosition().getX() - gameInfo.getShip().getPosition().getX();
        double distanceRestanteY = currentCheckPoint.getPosition().getY() - gameInfo.getShip().getPosition().getY();


        double distanceRestante = Math.sqrt(distanceRestanteX * distanceRestanteX + distanceRestanteY * distanceRestanteY);
        System.out.println("======================================================================================================");
        System.out.println(distanceRestanteX);
        System.out.println(distanceRestanteY);
        System.out.println("Distance restante : " + distanceRestante);
        System.out.println("======================================================================================================");


        System.out.println("WIND     : " + gameInfo.getWind());
        System.out.println("ENTITIES : " + gameInfo.getSeaEntities());


        if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter == numberOfCheckPoints - 1) {
            return new ArrayList<>();
        }

        if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter < numberOfCheckPoints-1) {
            checkPointCounter++;
            currentCheckPoint = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints()[checkPointCounter];
        }



        List<Action> actions = gameInfo.getShip().moveBoatDistanceStrategy2(currentCheckPoint.getPosition(), this.logger);
        System.out.println(actions);
        return actions;
    }
}
