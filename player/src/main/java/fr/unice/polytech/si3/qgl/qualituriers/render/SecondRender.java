package fr.unice.polytech.si3.qgl.qualituriers.render;

import fr.unice.polytech.si3.qgl.qualituriers.game.GameInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.RoundInfo;
import fr.unice.polytech.si3.qgl.qualituriers.game.goal.RegattaGoal;
import fr.unice.polytech.si3.qgl.qualituriers.utils.CheckPoint;

import java.util.Arrays;

/**
 * @author Alexandre Arcil, CLODONG Yann
 * • Un seul point de passage
 * • Le point de passage ne sera pas en face de vous : il va falloir apprendre à tourner
 * • Votre bateau sera plus grand: 4 cases de long pour 2 cases de large
 * • Votre bateau contiendra 6 rames
 * • Vos 4 marins ne seront pas forcement placés sur des rames au début de la partie : déplacez les avec l'action MOVING.
 */
public class SecondRender extends Render {

    private final CheckPoint[] checkpoints;
    private int nextCheckpoint = 0;

    public SecondRender(GameInfo gameInfo) {
        super(gameInfo);

        var goal = (RegattaGoal)gameInfo.getGoal();
        checkpoints = goal.getCheckPoints();
    }

    /**
     * @return the angle to reach the next checkpoint
     */
    public double getAngleToRotate() {
        var checkpoint = checkpoints[nextCheckpoint];
        return gameInfo.getShip().getTransform().getAngleToSee(checkpoint.getPosition());
    }


    @Override
    public String nextRound(RoundInfo round) {
        // Don't forget to change nextCheckpoint when it's reached
        return null;
    }

}
