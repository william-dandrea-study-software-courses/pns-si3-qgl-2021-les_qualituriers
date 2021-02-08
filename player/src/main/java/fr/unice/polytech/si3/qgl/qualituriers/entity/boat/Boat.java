package fr.unice.polytech.si3.qgl.qualituriers.entity.boat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.qualituriers.Deck;
import fr.unice.polytech.si3.qgl.qualituriers.entity.boat.boatutils.BabordTribordAngle;
import fr.unice.polytech.si3.qgl.qualituriers.entity.boat.boatutils.HowTurn;
import fr.unice.polytech.si3.qgl.qualituriers.utils.Transform;
import fr.unice.polytech.si3.qgl.qualituriers.utils.action.Moving;
import fr.unice.polytech.si3.qgl.qualituriers.utils.shape.Shape;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cette classe a pour objectif de décrire un bateau, le bateau pourra bouger mais aussi faire plusieurs actions
 * en fonction de l'emplacement des entity
 *
 * @author williamdandrea, Alexandre Arcil
 * @author CLODONG Yann
 * TODO : 28/01/2020 : essayer de revoir le constructeur pour parser des json node plutot que chaques field un par un.
 */
public class Boat {

    private final int life;
    private final Transform transform;
    private final String name;
    private final Deck deck;
    private final BoatEntity[] entities;
    private final Shape shape;

    @JsonCreator
    public Boat(@JsonProperty("life") int life, @JsonProperty("position") Transform position,
                @JsonProperty("name") String name, @JsonProperty("deck") Deck deck,
                @JsonProperty("entities") BoatEntity[] entities, @JsonProperty("shape") Shape shape) {
        this.life = life;
        this.transform = position;
        this.name = name;
        this.deck = deck;
        this.entities = entities;
        this.shape = shape;
    }

    public String getName() {
        return name;
    }
    public int getLife() { return life; }
    public Transform getTransform() { return transform; }
    public Deck getDeck() { return deck; }
    public BoatEntity[] getEntities() { return entities; }

    public Shape getShape() {
        return shape;
    }


    /**
     *Gauche  Droite  Rotation
     * 0	    0	    0
     * 0	    1	    PI/4
     * 0	    2	    PI/2
     * 1	    0	    -PI/4
     * 1	    1	    0
     * 1	    2	    PI/4
     * 2	    0	    -PI/2
     * 2	    1	    -PI/4
     * 2	    2	    0
     *
     * Strategie :
     * - Regarder ou sont les marins
     * -
     * @param finaleOrientationOfTheBoat
     */
    void turnBoat(double finaleOrientationOfTheBoat, Marin sailors[]) {

        // We retrieve the oars (and their positions) who are positioned on the Boat
        List<BoatEntity> listOfOars = Arrays.stream(entities).filter(boatEntity -> boatEntity.type.equals(BoatEntities.OAR) ).collect(Collectors.toList());
        List<BoatEntity> listOfOarsAtLeft = listOfOars.stream().filter(boatEntity -> boatEntity.y == 0).collect(Collectors.toList());
        List<BoatEntity> listOfOarsAtRight = listOfOars.stream().filter(boatEntity -> boatEntity.y == deck.getWidth()-1).collect(Collectors.toList());
        int numberOfOars = listOfOars.size();
        int numberOfLeftOars = listOfOarsAtLeft.size();
        int numberOfRightOars = listOfOarsAtRight.size();

        // We calculate the angle between the actual orientation of the boat and the wanted orientation
        double actualOrientationOfTheBoat = transform.getOrientation();
        double differenceOfAngle = finaleOrientationOfTheBoat - actualOrientationOfTheBoat;

        // We watch if we have sailors positioned on the oar
        List<Marin> sailorsOnOarAtRight = new ArrayList<>();
        List<Marin> sailorsOnOarAtLeft = new ArrayList<>();

        for (Marin sailor: sailors) {
            for(BoatEntity rightEntity : listOfOarsAtRight) {
                if (rightEntity.x == sailor.getX() && rightEntity.y == sailor.getY()) {
                    sailorsOnOarAtRight.add(sailor);
                }
            }
            for(BoatEntity leftEntity : listOfOarsAtLeft) {
                if (leftEntity.x == sailor.getX() && leftEntity.y == sailor.getY()) {
                    sailorsOnOarAtLeft.add(sailor);
                }
            }
        }

        System.out.println("ListOfOars : " + listOfOars.toString());
        Arrays.stream(sailors).forEach(sailor -> System.out.println(sailor.toString()));

        System.out.println("ListOfOarsLeft : " + listOfOarsAtLeft.toString());
        System.out.println("ListOfOarsRight : " + listOfOarsAtRight.toString());

        System.out.println("Number of oars : " + numberOfOars);
        System.out.println("Number of left oars : " + numberOfLeftOars);
        System.out.println("Number of righ oars : " + numberOfRightOars);

        System.out.println("SailorAtLeft : " + sailorsOnOarAtLeft.toString() );
        System.out.println("SailorAtRight : " + sailorsOnOarAtRight.toString() );

        System.out.println("Difference of angle : " + differenceOfAngle);

        // We check if we have a number pair or impair of oar, if it is impair, we have one more possible angle
        int numberOfAnglesPossibles  = (numberOfOars % 2 != 0) ? (numberOfOars / 2) + 1: (numberOfOars / 2);




        // This map represent the angle (Double) we have if we have (Integer - babord) oar left and (Integer - tribort) oar right (of the boat)
        // First integer : number of oar at babord (left)
        // Second integer : number of oar at tribord (right)
        List<BabordTribordAngle> possibleAngles = permutationsOfAngle(numberOfAnglesPossibles, numberOfOars);
        System.out.println(possibleAngles.toString());




        // We try to find the good angles in the possibleAngle list

        double differenceWeAccept = 0.0001;
        HowTurn howTurn = findTheGoodAngle(differenceOfAngle, possibleAngles, differenceWeAccept);


        System.out.println(howTurn.toString());
        //System.out.println(differenceOfAngle);

        // Now we watch if we have the good number of oar from each side


    }


    // ==================================== Methods For turnBoat ==================================== //


    /**
     * This list represent the angle (Double) we have if we have (Integer - babord) oar left and (Integer - tribort) oar right (of the boat)
     * First integer : number of oar at babord (left)
     * Second integer : number of oar at tribord (right)
     * @param numberOfAnglesPossibles
     * @param numberOfOars
     * @return
     */
    private List<BabordTribordAngle> permutationsOfAngle(int numberOfAnglesPossibles, int numberOfOars) {

        List<BabordTribordAngle> possibleAngles = new ArrayList<>();

        for (int bab = 0; bab <= numberOfAnglesPossibles; bab++) {
            for (int tri = 0; tri <= numberOfAnglesPossibles ; tri++) {
                if (bab + tri <= numberOfOars) {
                    //PI * <diff rame tribord - rame bâbord>/<nombre total de rames>
                    int diff = tri - bab;

                    double angle = (diff == 0) ? 0 : Math.PI * diff / numberOfOars ;
                    possibleAngles.add(new BabordTribordAngle(bab,tri,angle));

                    //System.out.println(bab + " " + tri + " " + diff + " " + angle);
                }
            }
        }
        return possibleAngles;
    }


    private HowTurn findTheGoodAngle(double differenceOfAngle, List<BabordTribordAngle> possibleAngles, double differenceWeAccept) {

        List<BabordTribordAngle> rotActionToDoForTurn = new ArrayList<>();


        // Si angle négatif
        if (differenceOfAngle < 0) {

            while (differenceOfAngle <= -Math.PI/2) {

                //System.out.println("difference of angle : " + differenceOfAngle);
                // Cas ou angle < O
                if (differenceOfAngle <= -(Math.PI / 2)) {

                    for (BabordTribordAngle babTriAng : possibleAngles) {

                        if (babTriAng.getAngle() == -(Math.PI / 2)) {

                            rotActionToDoForTurn.add(babTriAng);

                        }

                    }
                    differenceOfAngle = differenceOfAngle + (Math.PI / 2);

                }

            }

            System.out.println("difference of angle : " + differenceOfAngle);


            //System.out.println("Hello");
            for (BabordTribordAngle babTriAng : possibleAngles) {

                double min = differenceOfAngle - differenceWeAccept;
                double max = differenceOfAngle + differenceWeAccept;

                if ( babTriAng.getAngle() >= min && babTriAng.getAngle() <= max) {
                    // TODO : we can optimize here for check what is the good element to do
                    rotActionToDoForTurn.add(babTriAng);
                    System.out.println(babTriAng);
                    differenceOfAngle = differenceOfAngle - babTriAng.getAngle();
                    break;
                }
            }

        } else if (differenceOfAngle > 0){

            while (differenceOfAngle >= Math.PI/2) {

                //System.out.println("difference of angle : " + differenceOfAngle);
                // Cas ou angle > O
                if (differenceOfAngle >= (Math.PI / 2)) {

                    for (BabordTribordAngle babTriAng : possibleAngles) {

                        if (babTriAng.getAngle() == (Math.PI / 2)) {

                            rotActionToDoForTurn.add(babTriAng);

                        }

                    }
                    differenceOfAngle = differenceOfAngle - (Math.PI / 2);

                }

            }



            //System.out.println("Hello");
            for (BabordTribordAngle babTriAng : possibleAngles) {

                double min = differenceOfAngle - differenceWeAccept;
                double max = differenceOfAngle + differenceWeAccept;

                if ( babTriAng.getAngle() >= min && babTriAng.getAngle() <= max) {
                    // TODO : we can optimize here for check what is the good element to do
                    rotActionToDoForTurn.add(babTriAng);
                    System.out.println(babTriAng);
                    differenceOfAngle = differenceOfAngle - babTriAng.getAngle();
                    break;
                }
            }
        }


        System.out.println(rotActionToDoForTurn.toString());


        return new HowTurn(rotActionToDoForTurn, differenceOfAngle);
    }




    /**
     * This method move the sailor at a certain position, this method also verify if we can move the sailor at this
     * position. Template of the JSON output:
     * {
     *         "sailorId": 1,
     *         "type": "MOVING",
     *         "xdistance": 0,
     *         "ydistance": 0
     * }
     * @param sailor
     * @param xDistance
     * @param yDistance
     * @return
     */
    public Optional<String> moveSailorSomewhere(Marin sailor, int xDistance, int yDistance) {

        Deck boatDeck = deck;
        int deckY = boatDeck.getLength();   // Hauteur du bateau
        int deckX = boatDeck.getWidth();    // Largeur du bateau

        if (xDistance > deckX || yDistance > deckY) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        Moving moving = new Moving(sailor.getId() ,xDistance, yDistance);
        String stringMoving = null;

        try {
            stringMoving = objectMapper.writeValueAsString(moving);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (stringMoving != null) {
            return Optional.of(stringMoving);
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Boat)) return false;
        var castedObj = (Boat)obj;
        return castedObj.name == name;
    }











}
