package fr.unice.polytech.si3.qgl.qualituriers.utils.pathfinding;

import fr.unice.polytech.si3.qgl.qualituriers.Config;
import fr.unice.polytech.si3.qgl.qualituriers.entity.boat.Boat;
import fr.unice.polytech.si3.qgl.qualituriers.render.TempoRender;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Collisions;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Point;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Transform;
import fr.unice.polytech.si3.qgl.qualituriers.utils.logger.SeaDrawer;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Circle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Segment;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Shape;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableCircle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionablePolygon;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableShape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class PathfindingProblem {
    private final List<PositionablePolygon>  polygons = new ArrayList<>();
    private final List<PositionablePolygon>  enlargedPolygons = new ArrayList<>();
    private final List<PathfindingNode> nodes = new ArrayList<>();
    private final PathfindingNode startPosition;
    private final PathfindingNode goal;
    private double currentMinimalValidPath = Double.MAX_VALUE;


    PathfindingProblem(PathfindingNode startPosition, PathfindingNode goal) {
        this.startPosition = startPosition;
        this.goal = goal;
    }

    void addPolygon(PositionablePolygon polygon) {
        polygons.add(polygon);
        var enlarged = polygon.enlargeOf(Config.BOAT_MARGIN * 4);
        enlargedPolygons.add(enlarged);

        SeaDrawer.drawPolygon(enlarged, Color.magenta);
    }

    private boolean canNavigateOn(PathfindingNode start, PathfindingNode end) {
        return !Collisions.raycastPolygon(new Segment(start.getPosition(), end.getPosition()), Config.BOAT_MARGIN * 2, polygons.stream());
    }

    private PathfindingNode getNearestOutsideLimitNode(PathfindingNode node) {
        node = new PathfindingNode(node.getPosition(), null);
        var poly = Collisions.getCollidingPolygon(node.toPositionableCircle(), enlargedPolygons.stream());
        while(poly != null) {
            Point pt = node.getPosition();
            var dir = pt.substract(poly.getTransform().getPoint());
            var dist = dir.length();
            dir = dir.normalized();

            for(double d = dist; Collisions.isColliding(node.toPositionableCircle(), poly); d += 50)
                node.setPosition(poly.getTransform().getPoint().add(dir.scalar(d)));

            poly = Collisions.getCollidingPolygon(node.toPositionableCircle(), enlargedPolygons.stream());
        }
        return node;
    }

    private void generateNodes() {
        for(var poly : enlargedPolygons) {
            nodes.addAll(PathfindingNode.createFrom(poly));
        }
    }

    private void generateRoads() {
        for(var n1 : nodes) {
            for(var n2 : nodes) {
                if(n1 != n2)
                    PathfindingRoad.createIfPraticable(n1, n2, Config.BOAT_MARGIN * 3, polygons.stream());
            }
        }
    }

    PathfindingResult solve() {

        var pseudoStart = getNearestOutsideLimitNode(startPosition);
        var pseudoGoal = getNearestOutsideLimitNode(goal);

        generateNodes();

        nodes.add(pseudoGoal);
        nodes.add(pseudoStart);

        generateRoads();
        //PathfindingRoad.draw();

        SeaDrawer.drawPin(pseudoGoal.getPosition(), Color.YELLOW);
        SeaDrawer.drawPin(pseudoStart.getPosition(), Color.YELLOW);

        var path = searchPath(pseudoStart, pseudoGoal, new PathfindingResult() {{ addNode(startPosition); addNode(pseudoStart); }});
        if(path == null) throw new RuntimeException("No path founded !");
        path.addNode(goal);

        return path;
    }

    private PathfindingResult searchPath(PathfindingNode from, PathfindingNode to, PathfindingResult currentPath) {
        List<PathfindingNode> connectedNodes = new ArrayList<>();
        from.getRoads().stream()
                .map(r -> r.getArriving(from))
                .filter(n -> !currentPath.contains(n))
                .sorted(Comparator.comparingDouble(r -> r.calculateHeuristic(to)))
                .forEach(connectedNodes::add);

        PathfindingResult bestPath = null;
        for(var n : connectedNodes) {

            if(currentPath.contains(n))
                continue;

            var evaluationPath = currentPath.copy();
            evaluationPath.addNode(n);
            if(evaluationPath.length() > currentMinimalValidPath) break;

            if(n.equals(to)) {
                currentMinimalValidPath = evaluationPath.length();
                bestPath = evaluationPath;
                continue;
            }

            evaluationPath = searchPath(n, to, evaluationPath);
            if(evaluationPath != null) bestPath = evaluationPath;
        }

        return bestPath;
    }
}
