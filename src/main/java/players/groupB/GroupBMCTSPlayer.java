package players.groupB;

import core.AbstractGameState;
import core.actions.AbstractAction;
import players.basicMCTS.BasicMCTSParams;
import players.basicMCTS.BasicMCTSPlayer;
import utilities.ElapsedCpuTimer;

import java.util.List;

/**
 * GroupBMCTSPlayer
 * ----------------
 * Wrapper for BasicMCTSPlayer that allows custom tree node
 * and heuristic extensions for Group B experiments.
 */
public class GroupBMCTSPlayer extends BasicMCTSPlayer {

    public GroupBMCTSPlayer() {
        super();
    }

    public GroupBMCTSPlayer(BasicMCTSParams params) {
        super(params);
    }

    @Override
    public AbstractAction _getAction(AbstractGameState gameState, List<AbstractAction> actions) {
        // Standard MCTS selection process
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        return super._getAction(gameState, actions);
    }

    @Override
    public GroupBMCTSPlayer copy() {
        BasicMCTSParams copyParams = (BasicMCTSParams) this.getParameters().copy();
        return new GroupBMCTSPlayer(copyParams);
    }

    @Override
    public String toString() {
        return "GroupBMCTS";
    }
}
