package fr.unice.polytech.si3.qgl.qualituriers.entity.boat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cette classe represente un marin qui pourra donner des ordres a différents protagonistes present sur le bateau
 *
 * @author williamdandrea
 * @author CLODONG Yann
 */
public class Marin {

    private final int id;
    private final int x;
    private final int y;
    private final String name;

    public Marin(@JsonProperty("id")int id, @JsonProperty("x")int x, @JsonProperty("y")int y, @JsonProperty("name")String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Marin{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Marin)) return false;
        var castedObj = (Marin)obj;
        return castedObj.id == id;
    }
}
