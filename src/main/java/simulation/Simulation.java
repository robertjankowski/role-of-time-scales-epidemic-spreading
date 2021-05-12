package simulation;

import common.SimulationConfig;
import common.agent.Agent;
import common.agent.AgentMetrics;
import common.network.Initializer;
import common.network.Layer;
import common.network.Network;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    private SimulationConfig config;
    private List<AgentMetrics> metrics;
    private Random random;

    public Simulation(String configPath) {
        try {
            config = SimulationConfig.loadConfig(configPath);
            metrics = new LinkedList<>();
            random = new Random();
        } catch (IOException e) {
            System.err.println("Could not load simulation config. Exiting");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void run() {
        int m = 3;
        int additionalVirtualLinks = getnAdditionalLinks(config.getnAgents(), config.getAdditionalLinksFraction(), m);
        // TODO: for now I set to m=3, and p is not working (only BA model)
        var layers = Network.createBilayerNetwork(config.getnAgents(), additionalVirtualLinks, m, 0.0);
        var agents = Initializer.initializeAgents(config.getnAgents(),
                                                  config.getPositiveOpinionFraction(),
                                                  config.getInfectedFraction(),
                                                  config.getFractionIllnessA(),
                                                  config.getFractionIllnessB());
        System.out.println(config);
        for (int i = 0; i < config.getnSteps(); i++) {
            var node = random.nextInt(config.getnAgents());
            singleStep(node, layers, agents, config);
            if (i % config.getnSaveSteps() == 0)
                metrics.add(new AgentMetrics(i, agents));
        }
        System.out.println("Finished");
    }

    public void saveMetrics() {
        try {
            // TODO: save to correct file
            // prepare custom file name ?
            List<String> convertedMetrics = metrics.stream().map(AgentMetrics::toString).collect(Collectors.toList());
            convertedMetrics.add(0,
                                 "step\tmeanOpinion\tsusceptibleRate\tinfectedRate\tquarantinedRate\trecoveredRate\tdeadRate");
            FileUtils.writeLines(new File("test_metrics.txt"),
                                 convertedMetrics);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to write out names");
        }
    }

    private void singleStep(int node, Pair<Layer, Layer> layers, List<Agent> agents, SimulationConfig config) {
        if (config.isVirtualLayer()) {
            virtualLayerStep(node, layers, agents, config);
        }
        if (config.isEpidemicLayer()) {
            epidemicLayerStep(node, layers, agents, config);
        }
    }

    private void virtualLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents, SimulationConfig config) {
        if (random.nextDouble() < config.getqVoterParameters().getP()) {
            voterActNonConformity(node, agents);
        } else {
            voterActConformity(node, layers.getSecond(), agents, config);
        }
    }

    private void voterActConformity(int node, Layer virtualLayer, List<Agent> agents, SimulationConfig config) {
        int q = config.getqVoterParameters().getQ();
        var neighbours = Graphs.neighborListOf(virtualLayer, node).stream()
                .limit(q)
                .collect(Collectors.toList());
        if (neighbours.isEmpty()) return;

        // If not enough neighbours
        while (neighbours.size() < q) {
            var randomNeighbour = random.nextInt(neighbours.size());
            neighbours.add(neighbours.get(randomNeighbour));
        }
        int totalOpinion = 0;
        for (int neighbour : neighbours) {
            totalOpinion += agents.get(neighbour).getOpinion();
        }
        var agent = agents.get(node);
        if (totalOpinion == q) {
            agent.setOpinion(1);
            agents.set(node, agent);
        } else if (totalOpinion == -q) {
            agent.setOpinion(-1);
            agents.set(node, agent);
        }
    }

    private void voterActNonConformity(int node, List<Agent> agents) {
        if (random.nextDouble() < 0.5) {
            var agent = agents.get(node);
            agent.flipOpinion();
            agents.set(node, agent);
        }
    }

    private void epidemicLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents, SimulationConfig config) {
        // TODO
    }

    /**
     * @param size                    Size of BA network
     * @param fractionAdditionalLinks Fraction of new links
     * @param m                       parameter from BA
     * @return fraction of total edges in BA network
     */
    private int getnAdditionalLinks(int size, double fractionAdditionalLinks, int m) {
        return (int) (m * size * fractionAdditionalLinks);
    }
}
