package common.agent;

import java.util.List;

public class AgentMetrics {

    public static double meanOpinion(List<Agent> agents) {
        return agents.stream()
                .mapToDouble(Agent::getOpinion)
                .average()
                .orElse(0);
    }

    public static double susceptibleRate(List<Agent> agents) {
        return epidemicMetric(agents, AgentState.SUSCEPTIBLE);
    }

    public static double infectedRate(List<Agent> agents) {
        return epidemicMetric(agents, AgentState.INFECTED);
    }

    public static double quarantinedRate(List<Agent> agents) {
        return epidemicMetric(agents, AgentState.QUARANTINED);
    }

    public static double recoveredRate(List<Agent> agents) {
        return epidemicMetric(agents, AgentState.RECOVERED);
    }

    public static double deadRate(List<Agent> agents) {
        return epidemicMetric(agents, AgentState.DEAD);
    }

    private static double epidemicMetric(List<Agent> agents, AgentState state) {
        return (double) (agents
                .stream()
                .filter(a -> a.getState() == state)
                .count()) / agents.size();
    }
}
