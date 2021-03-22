package fr.unice.polytech.si3.qgl.qualituriers.render;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.qualituriers.entity.deck.visible.StreamVisibleDeckEntity;
import fr.unice.polytech.si3.qgl.qualituriers.entity.deck.visible.VisibleDeckEntities;
import fr.unice.polytech.si3.qgl.qualituriers.entity.deck.visible.VisibleDeckEntity
import fr.unice.polytech.si3.qgl.qualituriers.game.GameInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.RoundInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.goal.RegattaGoal;
import fr.unice.polytech.si3.qgl.qualituriers.utils.CheckPoint;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Collisions;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Point;
import fr.unice.polytech.si3.qgl.qualituriers.utils.action.Action;
import fr.unice.polytech.si3.qgl.qualituriers.utils.logger.ILogger;
import fr.unice.polytech.si3.qgl.qualituriers.utils.pathfinding.MainPathfinding;
import fr.unice.polytech.si3.qgl.qualituriers.utils.pathfinding.PathfindingContext;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Shape;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TempoRender extends Render {

    //private CheckPoint currentCheckPoint;
    //private int checkPointCounter = 0;

    public TempoRender(GameInfo gameInfo, ILogger logger) {
        super(gameInfo, logger);
        CheckPoint[] listCheckPoint = ((RegattaGoal) gameInfo.getGoal()).getCheckPoints();
        //currentCheckPoint = listCheckPoint[0];
    }

    int currentCheckpointIndex = 0;
    CheckPoint intermediareCheckpoint = null;
    public List<Action> nextRoundAlternative(RoundInfo round) {



        // Récupération des checkpoints
        var checkpoints = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints();

        // Est-ce que tout les checkpoints sont complété ?
        if(checkpoints.length <= currentCheckpointIndex) return new ArrayList<>();

        // Mise à jour du gameInfo
        gameInfo.getShip().setPosition(round.getShip().getPosition());
        gameInfo.getShip().setEntities(round.getShip().getEntities());

        gameInfo.getShip().setSailors(Arrays.asList(gameInfo.getSailors()));

        // Vérification si le checkpoint actuel est validé
        var currentCheckpoint = checkpoints[currentCheckpointIndex];
        if(Collisions.isColliding(currentCheckpoint.getPositionableShape(), gameInfo.getShip().getPositionableShape())) {
            //Mise à jour du nouveau checkpoint
            currentCheckpointIndex++;
            if(currentCheckpointIndex >= checkpoints.length) return new ArrayList<>();
            currentCheckpoint = checkpoints[currentCheckpointIndex];
        }

        // Mapping of checkpoints to PositionnalShape
        List<PositionableShape<? extends Shape>> positionableShapes = new ArrayList<>();
        if(gameInfo.getSeaEntities() != null)
            Arrays.stream(gameInfo.getSeaEntities()).map(VisibleDeckEntity::getPositionableShape).forEach(positionableShapes::add);

        // Recherche de l'itinéraire
        if(intermediareCheckpoint == null || Collisions.isColliding(intermediareCheckpoint.getPositionableShape(), gameInfo.getShip().getPositionableShape())) {
            MainPathfinding pathfinding = new MainPathfinding();
            intermediareCheckpoint = pathfinding.getNextCheckpoint(new PathfindingContext(
                    gameInfo.getShip(),
                    positionableShapes,
                    currentCheckpoint
            ));
            int test = 0;
        }

        // Calcul des action a effectuer pour atteindre l'étape
        var actions = gameInfo.getShip().moveBoatDistanceStrategy2(intermediareCheckpoint.getPosition(), this.gameInfo,this.logger);

        return actions;
    }



    @Override
    public List<Action> nextRound(RoundInfo round)  {
        return nextRoundAlternative(round);
        /*

        System.out.println("Round");
        if (gameInfo.getSeaEntities() != null) {
            System.out.println("1");

            gameInfo.getShip().setPosition(round.getShip().getPosition());
            gameInfo.getShip().setEntities(round.getShip().getEntities());

            gameInfo.getShip().setSailors(Arrays.asList(gameInfo.getSailors()));

            int numberOfCheckPoints = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints().length;

            PositionableShape<? extends Shape> checkpointsShape = currentCheckPoint.getPositionableShape();
            PositionableShape<? extends Shape> boatShape = gameInfo.getShip().getPositionableShape();




            double distanceRestanteX = currentCheckPoint.getPosition().getX() - gameInfo.getShip().getPosition().getX();
            double distanceRestanteY = currentCheckPoint.getPosition().getY() - gameInfo.getShip().getPosition().getY();


            double distanceRestante = Math.sqrt(distanceRestanteX * distanceRestanteX + distanceRestanteY * distanceRestanteY);
            System.out.println("======================================================================================================");
            System.out.println("| " + distanceRestanteX);
            System.out.println("| " + distanceRestanteY);
            System.out.println("| " + "Distance restante : " + distanceRestante);
            System.out.println("======================================================================================================");





            List<PositionableShape<? extends Shape>> obstacles = new ArrayList<>();
            for (VisibleDeckEntity entity: gameInfo.getSeaEntities()) {
                obstacles.add(entity.getPositionableShape());
            }

            var checkpoints = Arrays.asList((((RegattaGoal)gameInfo.getGoal()).getCheckPoints())).subList(checkPointCounter, Arrays.asList((((RegattaGoal)gameInfo.getGoal()).getCheckPoints())).size());

            MainPathfinding mainPathfinding = new MainPathfinding();
            PathfindingContext pathfindingContext = new PathfindingContext(gameInfo.getShip(), obstacles.stream(), checkpoints);
            currentCheckPoint = mainPathfinding.getNextCheckpoint(pathfindingContext);


            if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter == numberOfCheckPoints - 1) {
                return new ArrayList<>();
            }

            if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter < numberOfCheckPoints-1) {
                checkPointCounter++;
                currentCheckPoint = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints()[checkPointCounter];
            }


            List<Action> actions = gameInfo.getShip().moveBoatDistanceStrategy2(currentCheckPoint.getPosition(), this.gameInfo,this.logger);
            System.out.println("| " + actions);

            return actions;


        } else {

            System.out.println("2");

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
            System.out.println("| " + distanceRestanteX);
            System.out.println("| " + distanceRestanteY);
            System.out.println("| " + "Distance restante : " + distanceRestante);




            if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter == numberOfCheckPoints - 1) {
                return new ArrayList<>();
            }

            if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter < numberOfCheckPoints-1) {
                checkPointCounter++;
                currentCheckPoint = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints()[checkPointCounter];
            }


            List<Action> actions = gameInfo.getShip().moveBoatDistanceStrategy2(currentCheckPoint.getPosition(), this.gameInfo,this.logger);
            System.out.println("| " + actions);
            System.out.println("======================================================================================================");
            return actions;


        }
        */
    }




    /*
    @Override
    public List<Action> nextRound(RoundInfo round) throws JsonProcessingException {


        if (gameInfo.getSeaEntities() != null) {
            List<PositionableShape<? extends Shape>> obstacles = new ArrayList<>();
            for (VisibleDeckEntity entity: gameInfo.getSeaEntities()) {
                obstacles.add(entity.getPositionableShape());
            }

            var checkpoints = Arrays.asList((((RegattaGoal)gameInfo.getGoal()).getCheckPoints()));

            MainPathfinding mainPathfinding = new MainPathfinding();
            PathfindingContext pathfindingContext = new PathfindingContext(gameInfo.getShip(), obstacles,checkpoints);
            currentCheckPoint = mainPathfinding.getNextCheckpoint(pathfindingContext);
        }


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
        System.out.println("ENTITIES : " + Arrays.toString(gameInfo.getSeaEntities()));


        if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter == numberOfCheckPoints - 1) {
            return new ArrayList<>();
        }

        if (Collisions.isColliding(checkpointsShape, boatShape) && checkPointCounter < numberOfCheckPoints-1) {
            //if (distanceRestante <= checkPointRadius / 2) {
            checkPointCounter++;
            currentCheckPoint = ((RegattaGoal)gameInfo.getGoal()).getCheckPoints()[checkPointCounter];
        }



        List<Action> actions = gameInfo.getShip().moveBoatDistanceStrategy2(currentCheckPoint.getPosition(), this.gameInfo,this.logger);
        System.out.println(actions);
        //System.out.println(Arrays.toString(gameInfo.getSailors()));
        return actions;

        //return gameInfo.getShip().playTurn();



    }
    */
}
