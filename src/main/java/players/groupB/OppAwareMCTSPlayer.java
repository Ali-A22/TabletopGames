package players.groupB;

import core.AbstractGameState;
import core.actions.AbstractAction;
import players.basicMCTS.BasicMCTSParams;
import players.basicMCTS.BasicMCTSPlayer;

/**
 * OppAwareMCTSPlayer
 * ------------------
 * Custom agent extending BasicMCTSPlayer with an opponent-aware heuristic.
 * Uses FrequencyOpponentModel + OpponentAwareHeuristic for adaptive play.
 */
public class OppAwareMCTSPlayer extends BasicMCTSPlayer {

    private final FrequencyOpponentModel opponentModel;
    private final OpponentAwareHeuristic heuristic;

    /** Default constructor for JSON and framework loading */
    public OppAwareMCTSPlayer() {
        super();
        this.opponentModel = new FrequencyOpponentModel();
        this.heuristic = new OpponentAwareHeuristic(opponentModel);

        // Tune parameters for deeper, smarter lookahead
        BasicMCTSParams params = this.getParameters();
        params.K = Math.sqrt(2);
        params.rolloutLength = 20;   // longer rollouts for multi-round decisions
        params.maxTreeDepth = 50;    // allows deeper planning horizon
        params.epsilon = 1e-6;

        // Inject improved heuristic
        params.heuristic = (gameState, playerId) ->
                heuristic.evaluate(gameState, playerId);
    }

    /** Parameterised constructor (used internally by copy()) */
    public OppAwareMCTSPlayer(BasicMCTSParams params) {
        super(params);
        this.opponentModel = new FrequencyOpponentModel();
        this.heuristic = new OpponentAwareHeuristic(opponentModel);

        params.K = Math.sqrt(2);
        params.rolloutLength = 20;
        params.maxTreeDepth = 50;
        params.epsilon = 1e-6;

        params.heuristic = (gameState, playerId) ->
                heuristic.evaluate(gameState, playerId);
    }

    @Override
    public AbstractAction _getAction(AbstractGameState gameState, java.util.List<AbstractAction> actions) {
        opponentModel.updateFromGameState(gameState);
        return super._getAction(gameState, actions);
    }

    @Override
    public OppAwareMCTSPlayer copy() {
        return new OppAwareMCTSPlayer((BasicMCTSParams) parameters.copy());
    }

    @Override
    public String toString() {
        return "OppAwareMCTS";
    }
}
