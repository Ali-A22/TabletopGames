package players.groupB;

import core.AbstractGameState;

/**
 * OpponentAwareHeuristic (Enhanced)
 * ---------------------------------
 * Evaluates the quality of a game state for the active player.
 * Combines:
 *   (1) Self utility (normalised SushiGo! score)
 *   (2) A penalty for opponent aggressiveness (competition pressure)
 *   (3) A small bonus for unpredictability (to avoid being blocked)
 *
 * This version is smoother, normalised to [0,1], and tuned for stability.
 */
public class OpponentAwareHeuristic {

    private final FrequencyOpponentModel opponentModel;

    public OpponentAwareHeuristic(FrequencyOpponentModel model) {
        this.opponentModel = model;
    }

    /**
     * Evaluate a given AbstractGameState from the perspective of playerId.
     * @param gameState The current state of the game
     * @param playerId  The ID of the evaluating player
     * @return A numeric score in [0,1] — higher = better for playerId
     */
    public double evaluate(AbstractGameState gameState, int playerId) {
        // 1️⃣ Estimate player's self utility
        double selfUtility;
        try {
            selfUtility = gameState.getGameScore(playerId);
        } catch (Exception e) {
            selfUtility = 0.0; // fallback in case of early states
        }

        // SushiGo! total round scores typically peak around 80–100
        double maxExpectedScore = 80.0;
        double normalisedUtility = Math.max(0.0, Math.min(1.0, selfUtility / maxExpectedScore));

        // 2️⃣ Compute average opponent aggressiveness (how "competitive" they are)
        double totalAggressiveness = 0.0;
        int nPlayers = gameState.getNPlayers();
        int opponentCount = Math.max(1, nPlayers - 1);

        for (int opp = 0; opp < nPlayers; opp++) {
            if (opp == playerId) continue;
            totalAggressiveness += opponentModel.getAggressivenessScore(opp);
        }

        double avgAggressiveness = totalAggressiveness / opponentCount;

        // 3️⃣ Apply competition penalty (λ controls its influence)
        double lambda = 0.05;
        double pressurePenalty = lambda * avgAggressiveness;

        // 4️⃣ Add small reward for unpredictability (encourages risk-taking)
        double unpredictabilityBonus = 0.0;
        for (int opp = 0; opp < nPlayers; opp++) {
            if (opp == playerId) continue;
            double aggr = opponentModel.getAggressivenessScore(opp);
            // The less aggressive opponents are, the more freedom we have → small bonus
            unpredictabilityBonus += 0.02 * (1.0 / (1.0 + aggr));
        }

        // 5️⃣ Combine components
        double heuristicValue = normalisedUtility - pressurePenalty + unpredictabilityBonus;

        // Clamp to [0,1] for MCTS compatibility
        return Math.max(0.0, Math.min(1.0, heuristicValue));
    }
}
