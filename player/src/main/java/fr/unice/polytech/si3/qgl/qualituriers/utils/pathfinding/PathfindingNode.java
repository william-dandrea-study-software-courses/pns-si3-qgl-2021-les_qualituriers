package fr.unice.polytech.si3.qgl.qualituriers.utils.pathfinding;

import fr.unice.polytech.si3.qgl.qualituriers.Config;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Collisions;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Point;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Transform;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Circle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Segment;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableCircle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionablePolygon;

import java.util.ArrayList;
import java.util.List;

public class PathfindingNode {
    private final Point position;
    private final List<PathfindingNode> reachableNodes = new ArrayList<>();
    private final PositionablePolygon owner;

    PathfindingNode(Point position, PositionablePolygon owner) {
        this.position = position;
        this.owner = owner;
    }

    void addReachableNode(PathfindingNode node) {
        reachableNodes.add(node);
    }
    void removeReachableNode(PathfindingNode node) {
        reachableNodes.remove(node);
    }

    List<PathfindingNode> getReachableNodes() {
        return reachableNodes;
    }

    Point getPosition() {
        return position;
    }

    PositionablePolygon getOwner() {
        return owner;
    }

    double calculateHeuristic(PathfindingNode goal) {
        return goal.getPosition().substract(position).length();
    }

    static List<PathfindingNode> createFrom(PositionablePolygon positionablePolygon) {
        var vertices = positionablePolygon.getShape().getVertices(positionablePolygon.getTransform());
        List<PathfindingNode> nodes = new ArrayList<>();

        for (Point vertex : vertices) nodes.add(new PathfindingNode(vertex, positionablePolygon));

        for(int i = 0; i < vertices.length; i++) {
            int indexNeighbourSide1 = i + 1 >= nodes.size() ? 0 : i + 1;
            int indexNeighbourSide2 = i <= 0 ? nodes.size() - 1 : i - 1;
            nodes.get(i).addReachableNode(nodes.get(indexNeighbourSide1));
            nodes.get(i).addReachableNode(nodes.get(indexNeighbourSide2));
        }

        return nodes;
    }

    public void checkRoads(List<PositionablePolygon> obstacles) {
        List<PathfindingNode> unreachable = new ArrayList<>();

        for (var reachable : reachableNodes) {
            if(Collisions.raycastPolygon(new Segment(reachable.getPosition(), getPosition()), 2 * Config.BOAT_MARGIN, obstacles.stream().filter(o -> o != this.owner))) {
                unreachable.add(reachable);
                reachable.removeReachableNode(this);
            }
        }
        //unreachable.forEach(n -> n.removeReachableNode(this));
        unreachable.forEach(this::removeReachableNode);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        PathfindingNode node = (PathfindingNode) obj;

        var pos = this.position.equals(node.position);
        var pol = this.owner.equals(node.owner);
        var rech = this.reachableNodes.equals(node.reachableNodes);
        return pos && pol && rech;
    }

    PositionableCircle toPositionableCircle() {
        return new PositionableCircle(new Circle(100), new Transform(getPosition(), 0));
    }
}
