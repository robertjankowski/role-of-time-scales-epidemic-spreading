package pl.wf.common.agent;

import java.util.List;

public class AgentMetrics {
    private final int simulationStep;
    private double meanOpinion = 0;
    private double susceptibleRate = 0;
    private double infectedRate = 0;
    private double quarantinedRate = 0;
    private double recoveredRate = 0;
    private double deadRate = 0;

    public AgentMetrics(int simulationStep, List<Agent> agents) {
        this.simulationStep = simulationStep;
        for (var agent : agents) {
            switch (agent.getState()) {
                case SUSCEPTIBLE -> susceptibleRate += 1;
                case INFECTED -> infectedRate += 1;
                case QUARANTINED -> quarantinedRate += 1;
                case DEAD -> deadRate += 1;
                case RECOVERED -> recoveredRate += 1;
            }
            meanOpinion += agent.getOpinion();
        }
        int size = agents.size();
        meanOpinion /= size;
        susceptibleRate /= size;
        infectedRate /= size;
        quarantinedRate /= size;
        deadRate /= size;
        recoveredRate /= size;
    }

    @Override
    public String toString() {
        return simulationStep + "\t" +
                meanOpinion + "\t" +
                susceptibleRate + "\t" +
                infectedRate + "\t" +
                quarantinedRate + "\t" +
                recoveredRate + "\t" +
                deadRate;
    }
}
