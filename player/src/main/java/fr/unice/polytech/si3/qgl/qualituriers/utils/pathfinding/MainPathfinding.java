package fr.unice.polytech.si3.qgl.qualituriers.utils.pathfinding;

import fr.unice.polytech.si3.qgl.qualituriers.utils.CheckPoint;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Circle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainPathfinding {
    private final List<IPathfinder> pathfinders;

    public MainPathfinding() {
        pathfinders = new ArrayList<>();

        pathfinders.add(new AvoidObstacles());
        //pathfinders.add(new OrderedCheckpoints());
        // Add your pathfinders here

        pathfinders.sort(Comparator.comparingInt(IPathfinder::getPriorityRank));
    }

    /**
     * Retourne la prochaine étape afin de rejoindre le point le plus rapidement possible
     * @param context: Les paramètres de recherche
     * @return Checkpoint de la prochaine étape
     */
    public CheckPoint getNextCheckpoint(PathfindingContext context) {

        for(var pathfinder : pathfinders) {
            var cp = pathfinder.getNextCheckpoint(context);
            context.setToReach(cp);
        }
        return  context.getToReach();
    }
}
