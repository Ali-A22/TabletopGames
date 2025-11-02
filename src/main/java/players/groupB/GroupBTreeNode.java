package players.groupB;

import core.AbstractGameState;
import core.actions.AbstractAction;
import players.basicMCTS.BasicMCTSParams;
import players.simple.RandomPlayer;
import utilities.Utils;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * GroupBTreeNode
 * --------------
 * Simplified copy of BasicTreeNode for Group B’s MCTS experiments.
 */
public class GroupBTreeNode {

    protected final GroupBMCTSPlayer player;
    protected final GroupBTreeNode parent;
    protected final AbstractGameState state;
    protected final Random rnd;
    protected final int depth;

    protected Map<AbstractAction, GroupBTreeNode> children = new HashMap<>();
    protected double totValue;
    protected int nVisits;

    private final RandomPlayer randomPlayer = new RandomPlayer();

    public GroupBTreeNode(GroupBMCTSPlayer player, GroupBTreeNode parent, AbstractGameState state, Random rnd) {
        this.player = player;
        this.parent = parent;
        this.state = state;
        this.rnd = rnd;
        this.depth = (parent == null) ? 0 : parent.depth + 1;

        if (state.isNotTerminal()) {
            for (AbstractAction action :
                    player.getForwardModel().computeAvailableActions(state, player.getParameters().actionSpace)) {
                children.put(action, null);
            }
        }
        randomPlayer.setForwardModel(player.getForwardModel());
    }

    // Core search functions
    public GroupBTreeNode treePolicy() {
        GroupBTreeNode cur = this;
        BasicMCTSParams p = (BasicMCTSParams) player.getParameters();

        while (cur.state.isNotTerminal() && cur.depth < p.maxTreeDepth) {
            List<AbstractAction> unexp = cur.unexpandedActions();
            if (!unexp.isEmpty()) {
                return cur.expand();
            } else {
                AbstractAction action = cur.ucb();
                cur = cur.children.get(action);
            }
        }
        return cur;
    }

    protected List<AbstractAction> unexpandedActions() {
        return children.keySet().stream().filter(a -> children.get(a) == null).collect(toList());
    }

    protected GroupBTreeNode expand() {
        List<AbstractAction> notChosen = unexpandedActions();
        AbstractAction chosen = notChosen.get(rnd.nextInt(notChosen.size()));
        AbstractGameState nextState = state.copy();
        player.getForwardModel().next(nextState, chosen.copy());
        GroupBTreeNode child = new GroupBTreeNode(player, this, nextState, rnd);
        children.put(chosen, child);
        return child;
    }

    private AbstractAction ucb() {
        AbstractAction bestAction = null;
        double bestValue = -Double.MAX_VALUE;
        BasicMCTSParams p = (BasicMCTSParams) player.getParameters();

        for (AbstractAction a : children.keySet()) {
            GroupBTreeNode c = children.get(a);
            if (c == null) continue;

            double value = c.totValue / (c.nVisits + p.epsilon);
            double exploration = p.K * Math.sqrt(Math.log(this.nVisits + 1) / (c.nVisits + p.epsilon));
            double uct = Utils.noise(value + exploration, p.epsilon, rnd.nextDouble());

            if (uct > bestValue) {
                bestValue = uct;
                bestAction = a;
            }
        }
        return bestAction;
    }

    public double rollOut() {
        AbstractGameState rolloutState = state.copy();
        int rolloutDepth = 0;

        while (!finishRollout(rolloutState, rolloutDepth)) {
            List<AbstractAction> acts =
                    player.getForwardModel().computeAvailableActions(rolloutState, player.getParameters().actionSpace);
            if (acts.isEmpty()) break;
            AbstractAction next = acts.get(rnd.nextInt(acts.size()));
            player.getForwardModel().next(rolloutState, next);
            rolloutDepth++;
        }

        return player.getParameters().getStateHeuristic().evaluateState(rolloutState, rolloutState.getCurrentPlayer());
    }

    private boolean finishRollout(AbstractGameState st, int depth) {
        BasicMCTSParams p = (BasicMCTSParams) player.getParameters();
        return depth >= p.rolloutLength || !st.isNotTerminal();
    }

    public void backUp(double result) {
        GroupBTreeNode n = this;
        while (n != null) {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }

    public AbstractAction bestAction() {
        double bestValue = -Double.MAX_VALUE;
        AbstractAction bestAction = null;
        BasicMCTSParams p = (BasicMCTSParams) player.getParameters();

        for (AbstractAction a : children.keySet()) {
            GroupBTreeNode c = children.get(a);
            if (c == null) continue;
            double val = Utils.noise(c.nVisits, p.epsilon, rnd.nextDouble());
            if (val > bestValue) {
                bestValue = val;
                bestAction = a;
            }
        }
        return bestAction;
    }
}
