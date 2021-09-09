package pl.wf.common.network;

import pl.wf.common.AgeStatistics;
import pl.wf.common.agent.Agent;
import pl.wf.common.agent.AgentState;
import pl.wf.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Initializer {

    /**
     * @param size                    Number of agents
     * @param positiveOpinionFraction Fraction of agents with positive opinions
     * @param proPiSFraction          Fraction of agents voting for PiS
     * @param infectedFraction        Fraction of infected agents
     * @param fractionIllnessA        Fraction of agents with illness A
     * @param fractionIllnessB        Fraction of agents with illness B
     * @return List with the initialized agents
     */
    public static List<Agent> initializeAgents(int size,
                                               double positiveOpinionFraction,
                                               double proPiSFraction,
                                               double infectedFraction,
                                               double fractionIllnessA,
                                               double fractionIllnessB,
                                               double maxInfectedTimeMean,
                                               double maxInfectedTimeStd) {
        List<Agent> agents = new ArrayList<>();
        var r = new Random();
        var femalesAges = AgeStatistics.generateAge("", size, "F");
        var malesAges = AgeStatistics.generateAge("", size, "M");
        for (int i = 0; i < size; i++) {
            int opinion = 1;
            if (r.nextDouble() > positiveOpinionFraction)
                opinion = -1;

            int politicalSupport = 1;
            if (r.nextDouble() > proPiSFraction)
                politicalSupport = -1;

            var agent = new Agent(opinion, politicalSupport, Utils.nextGaussian(maxInfectedTimeMean, maxInfectedTimeStd));

            // Age based on gender (50/50)
            if (r.nextBoolean()) {
                assert femalesAges != null;
                agent.setAge(femalesAges.get(i));
            } else {
                assert malesAges != null;
                agent.setAge(malesAges.get(i));
            }

            if (r.nextDouble() < fractionIllnessA)
                agent.setHasIllnessA(true);
            if (r.nextDouble() < fractionIllnessB)
                agent.setHasIllnessB(true);

            if (r.nextDouble() < infectedFraction)
                agent.setState(AgentState.INFECTED);
            agents.add(agent);
        }
        return agents;
    }
}
