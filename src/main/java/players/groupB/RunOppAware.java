package players.groupB;

import core.AbstractPlayer;
import evaluation.RunArg;
import evaluation.tournaments.RoundRobinTournament;
import games.GameType;
import players.basicMCTS.BasicMCTSPlayer;
import players.simple.RandomPlayer;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Standalone runner for testing OppAwareMCTS without modifying RunGames.
 */
public class RunOppAware {

    public static void main(String[] args) {
        try {
            LinkedList<AbstractPlayer> agents = new LinkedList<>();
            agents.add(new OppAwareMCTSPlayer());
            agents.add(new BasicMCTSPlayer());
            agents.add(new RandomPlayer());

            GameType game = GameType.valueOf("SushiGo");
            int nPlayers = 3;

            Map<RunArg, Object> dummyConfig = new EnumMap<>(RunArg.class);
            dummyConfig.put(RunArg.verbose, true);
            dummyConfig.put(RunArg.matchups, 20);
            dummyConfig.put(RunArg.mode, "exhaustive");
            dummyConfig.put(RunArg.addTimeStamp, false);
            dummyConfig.put(RunArg.destDir, "output_oppaware");
            dummyConfig.put(RunArg.seed, 42L);
            dummyConfig.put(RunArg.budget, 0);
            dummyConfig.put(RunArg.byTeam, false);
            dummyConfig.put(RunArg.evalMethod, "Win");

            System.out.println("Running OppAwareMCTS tournament...");
            RoundRobinTournament tournament = new RoundRobinTournament(
                    agents, game, nPlayers, null, dummyConfig
            );
            tournament.run();
            System.out.println("✅ Tournament finished successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
