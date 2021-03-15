package fr.unice.polytech.si3.qgl.qualituriers.utils;

import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Circle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Rectangle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableCircle;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionablePolygon;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.positionable.PositionableShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Note: pas de test dans le cas où "project[1] = project2[0]" car pas réussi à trouver de cas
public class CollisionsTest {

    @Test
    void TestCirclesCollisions() {
        PositionableShape<Circle> circle1 = new PositionableCircle(new Circle(5), new Transform(0, 0, 0));
        PositionableShape<Circle> circle2 = new PositionableCircle(new Circle(10), new Transform(14, 0, 0));
        PositionableShape<Circle> circle3 = new PositionableCircle(new Circle(10), new Transform(15, 0, 0));

        assertTrue(Collisions.isColliding(circle1, circle2));
        assertFalse(Collisions.isColliding(circle1, circle3));
    }

    @Test
    void RectangleColliding() {
        PositionablePolygon rect1 = new PositionablePolygon(new Rectangle(3, 3, 0), new Transform(0, 0, 0));
        PositionablePolygon rect2 = new PositionablePolygon(new Rectangle(3, 3, 0), new Transform(2, 2, 0));

        assertTrue(Collisions.isColliding(rect1, rect2));
    }

    @Test
    void OrientedRectangleColliding() {
        PositionablePolygon rect1 = new PositionablePolygon(new Rectangle(10, 10, 0), new Transform(0, 0, 0));
        PositionablePolygon rect2 = new PositionablePolygon(new Rectangle(10, 10, 0), new Transform(0, 11, Math.PI / 4));

        assertTrue(Collisions.isColliding(rect1, rect2));
    }

    @Test
    void RectangleDontCollide() {
        PositionablePolygon rect1 = new PositionablePolygon(new Rectangle(3, 3, 0), new Transform(0, 0, 0));
        PositionablePolygon rect2 = new PositionablePolygon(new Rectangle(3, 3, 0), new Transform(0, 9, Math.PI / 4));
        assertFalse(Collisions.isColliding(rect1, rect2));
    }

    @Test
    void RectangleCircleDontCollide() {
        PositionablePolygon rect = new PositionablePolygon(new Rectangle(5, 3, 0), new Transform(105.88, -340.16, -2.1));
        PositionableCircle circle = new PositionableCircle(new Circle(85), new Transform(10, -350, 0));
        assertFalse(Collisions.isColliding(circle, rect));
    }

    @Test
    void RectangleCircleDontCollide2() {
        PositionablePolygon rect = new PositionablePolygon(new Rectangle(5, 3, 0), new Transform(-316.04, 417.15, 1.047));
        PositionableCircle circle = new PositionableCircle(new Circle(85), new Transform(-150, 410, 0));
        assertFalse(Collisions.isColliding(circle, rect));
    }


    @Test
    void RaycastBasicTest() {
        assertTrue(
                Collisions.raycast(
                        Point.ZERO, new Point(1, 0),
                        new PositionableCircle(new Circle(4.5), new Transform(new Point(5, 0), 0)), 0)
        );
        assertFalse(
                Collisions.raycast(
                        Point.ZERO, new Point(1, 0),
                        new PositionableCircle(new Circle(1), new Transform(new Point(5, 0), 0)), 0)
        );
    }

    @Test
    void RaycastInsideTest() {
        assertTrue(
                Collisions.raycast(
                        Point.ZERO, new Point(1, 0),
                        new PositionableCircle(new Circle(10), new Transform(new Point(5, 0), 0)), 0)
        );
    }


}
