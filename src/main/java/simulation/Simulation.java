package simulation;

import common.SimulationConfig;
import common.agent.Agent;
import common.agent.AgentMetrics;
import common.agent.AgentState;
import common.network.Initializer;
import common.network.Layer;
import common.network.Network;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public void runAll(String fileMetricsPrefix) {
        if (config.getFirstParameterRange() == null) {
            run(fileMetricsPrefix);
        }
        var parametersRange = config.getFirstParameterRange().getParametersRange();
        var pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK)
                .setTaskName("Running experiment: " + config.getFirstParameterRange().getType());
        for (double val : ProgressBar.wrap(parametersRange, pbb)) {
            switch (config.getFirstParameterRange().getType()) {
                case q -> config.getqVoterParameters().setQ((int) val);
                case p -> config.getqVoterParameters().setP(val);
                case beta -> config.getEpidemicLayerParameters().setBeta(val);
                case gamma -> config.getEpidemicLayerParameters().setGamma(val);
                case mu -> config.getEpidemicLayerParameters().setMu(val);
                case kappa -> config.getEpidemicLayerParameters().setKappa(val);
                case maxInfectedTime -> config.getEpidemicLayerParameters().setMaxInfectedTime(val);
            }
            run(fileMetricsPrefix);
        }
    }

    public void run(String fileMetricsPrefix) {
        for (int i = 0; i < config.getnRuns(); i++) {
            int m = 3;
            int additionalVirtualLinks =
                    getnAdditionalLinks(config.getnAgents(), config.getAdditionalLinksFraction(), m);
            // TODO: for now I set to m=3, and p is not working (only BA model)
            var layers = Network.createBilayerNetwork(config.getnAgents(), additionalVirtualLinks, m, 0.0);
            var agents = Initializer.initializeAgents(config.getnAgents(),
                                                      config.getPositiveOpinionFraction(),
                                                      config.getInfectedFraction(),
                                                      config.getFractionIllnessA(),
                                                      config.getFractionIllnessB());
            for (int step = 0; step < config.getnSteps(); step++) {
                var node = random.nextInt(config.getnAgents());
                singleStep(node, layers, agents);
                if (step % config.getnSaveSteps() == 0)
                    metrics.add(new AgentMetrics(step, agents));
            }
            saveMetrics(fileMetricsPrefix, i);
            metrics.clear();
        }
    }

    private void saveMetrics(String prefix, int nRun) {
        try {
            var convertedMetrics = metrics.stream().map(AgentMetrics::toString).collect(Collectors.toList());
            convertedMetrics.add(0,
                                 "step\tmeanOpinion\tsusceptibleRate\tinfectedRate\tquarantinedRate\trecoveredRate\tdeadRate");
            var path = Paths.get(config.getOutputFolder());
            Files.createDirectories(path);
            FileUtils.writeLines(new File(path.toString(), prepareFilename(prefix, nRun)), convertedMetrics);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to write out names");
        }
    }

    private String prepareFilename(String prefix, int nRun) {
        return prefix + "_NAGENTS=" + config.getnAgents() +
                "_NSTEPS=" + config.getnSteps() +
                "_FRAC_LINKS=" + config.getAdditionalLinksFraction() +
                "_FRAC_POS_OPINION=" + config.getPositiveOpinionFraction() +
                "_FRAC_A=" + config.getFractionIllnessA() +
                "_FRAC_B=" + config.getFractionIllnessB() +
                "_FRAC_INFECTED=" + config.getInfectedFraction() +
                "_QVOTER=" + config.getqVoterParameters() +
                "_EPIDEMIC=" + config.getEpidemicLayerParameters() +
                "_NRUN=" + nRun + ".tsv";
    }

    private void singleStep(int node, Pair<Layer, Layer> layers, List<Agent> agents) {
        if (config.isVirtualLayer()) {
            virtualLayerStep(node, layers, agents);
        }
        if (config.isEpidemicLayer()) {
            epidemicLayerStep(node, layers, agents);
        }
    }

    private void virtualLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents) {
        if (random.nextDouble() < config.getqVoterParameters().getP()) {
            voterActNonConformity(node, agents);
        } else {
            voterActConformity(node, layers.getSecond(), agents);
        }
    }

    private void voterActConformity(int node, Layer virtualLayer, List<Agent> agents) {
        int q = config.getqVoterParameters().getQ();
        var neighbours = Graphs.neighborListOf(virtualLayer, node).stream()
                .limit(q)
                .collect(Collectors.toList());
        if (neighbours.isEmpty()) return;

        // If not enough neighbours
        if (neighbours.size() < q) {
            // For now if agent does not have enough neighbours it won't change his opinion.
            // while (...)
            // var randomNeighbour = random.nextInt(neighbours.size());
            // neighbours.add(neighbours.get(randomNeighbour));
            return;
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

    private void epidemicLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents) {
        var agent = agents.get(node);
        var epidemicLayer = layers.getFirst();
        switch (agent.getState()) {
            case SUSCEPTIBLE -> {
                for (int neighbor : Graphs.neighborListOf(epidemicLayer, node)) {
                    if (agents.get(neighbor).getState() == AgentState.INFECTED) {
                        // S --> I
                        if (random.nextDouble() < getCombinedBetaProbability(agent)) {
                            agent.setState(AgentState.INFECTED);
                            break;
                        }
                    }
                }
            }
            case INFECTED -> {
                agent.incrementInfectedTime(config);
                if (agent.getInfectedTime() >= config.getEpidemicLayerParameters().getMaxInfectedTime()) {

                    // TODO: implement transitions
                    // I --> Q

                    // I --> R

                    // I --> D
                }
            }
            case QUARANTINED -> {
                // Q --> R

                // Q --> D
            }
        }

    }

    private double getCombinedBetaProbability(Agent agent) {
        // S --> I
        double beta = config.getEpidemicLayerParameters().getBeta();
        if (config.isVirtualLayer()) {
            if (agent.getOpinion() == 1) {
                return beta / 2;
            } else {
                return beta;
            }
        } else {
            return beta;
        }
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
