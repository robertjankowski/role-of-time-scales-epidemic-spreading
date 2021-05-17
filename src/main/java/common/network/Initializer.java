package common.network;

import common.AgeStatistics;
import common.agent.Agent;
import common.agent.AgentState;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Initializer {

    /**
     * @param size                    Number of agents
     * @param positiveOpinionFraction Fraction of agents with positive opinions
     * @param infectedFraction        Fraction of infected agents
     * @param fractionIllnessA        Fraction of agents with illness A
     * @param fractionIllnessB        Fraction of agents with illness B
     * @return Initialize agents
     */
    public static List<Agent> initializeAgents(int size,
                                               double positiveOpinionFraction,
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

            var agent = new Agent(opinion, Utils.nextGaussian(maxInfectedTimeMean, maxInfectedTimeStd));

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
