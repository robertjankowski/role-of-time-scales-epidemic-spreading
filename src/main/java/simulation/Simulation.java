package simulation;

import common.SimulationConfig;
import common.agent.Agent;
import common.agent.AgentMetrics;
import common.agent.AgentState;
import common.network.Initializer;
import common.network.Layer;
import common.network.Network;
import common.parameters.ParameterType;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import utils.RandomCollectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Simulation {
    private SimulationConfig config;
    private Random random;
    private static final int N_THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
    private static final ReentrantLock lock = new ReentrantLock();

    public Simulation(String configPath) {
        try {
            config = SimulationConfig.loadConfig(configPath);
            random = new Random();
        } catch (IOException e) {
            System.err.println("Could not load simulation config. Exiting");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void runAll(String fileMetricsPrefix, boolean isMultithreaded) {
        if (config.getFirstParameterRange() == null && config.getSecondParameterRange() == null) {
            run(fileMetricsPrefix, config);
        } else if (config.getFirstParameterRange() != null && config.getSecondParameterRange() == null) {
            var parametersRange = config.getFirstParameterRange().getParametersRange();
            var type = config.getFirstParameterRange().getType();
            runAllByParameter(parametersRange, type, fileMetricsPrefix);
        } else if (config.getSecondParameterRange() != null && config.getFirstParameterRange() == null) {
            var parameterRange = config.getSecondParameterRange().getParametersRange();
            var type = config.getSecondParameterRange().getType();
            runAllByParameter(parameterRange, type, fileMetricsPrefix);
        } else {
            // run grid search
            var firstParameterRange = config.getFirstParameterRange().getParametersRange();
            var secondParameterRange = config.getSecondParameterRange().getParametersRange();
            var pbb = new ProgressBarBuilder()
                    .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK)
                    .setTaskName("Running experiment: " + config.getFirstParameterRange().getType() +
                            " and " + config.getSecondParameterRange().getType());
            for (double x : ProgressBar.wrap(firstParameterRange, pbb)) {
                for (double y : secondParameterRange) {
                    if (isMultithreaded) {
                        // TODO: Not working, config is not correctly updated ?
                        final SimulationConfig newConfig = new SimulationConfig(config);
                        updateParameter(x, newConfig.getFirstParameterRange().getType(), newConfig);
                        updateParameter(y, newConfig.getSecondParameterRange().getType(), newConfig);
                        executor.execute(() -> run(fileMetricsPrefix, newConfig));
                    } else {
                        updateParameter(x, config.getFirstParameterRange().getType(), config);
                        updateParameter(y, config.getSecondParameterRange().getType(), config);
                        run(fileMetricsPrefix, config);
                    }
                }
            }

            if (isMultithreaded) {
                executor.shutdown();
                try {
                    while (!executor.awaitTermination(24L, TimeUnit.HOURS)) {
                        System.out.println("Not yet. Still waiting for termination");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runAllByParameter(List<Double> parametersRange, ParameterType type, String fileMetricsPrefix) {
        var pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK)
                .setTaskName("Running experiment: " + config.getFirstParameterRange().getType());
        for (double val : ProgressBar.wrap(parametersRange, pbb)) {
            updateParameter(val, type, config);
            run(fileMetricsPrefix, config);
        }
    }

    public void run(String fileMetricsPrefix, SimulationConfig config) {
        System.out.println("config = " + config);
        List<AgentMetrics> metrics = new LinkedList<>();
        for (int i = 0; i < config.getnRuns(); i++) {
            int additionalVirtualLinks =
                    getnAdditionalLinks(config.getnAgents(), config.getAdditionalLinksFraction(), config.getNetworkM());
            var layers = Network.createBilayerNetwork(
                    config.getnAgents(), additionalVirtualLinks, config.getNetworkM(), config.getNetworkP());
            var agents = Initializer.initializeAgents(
                    config.getnAgents(),
                    config.getPositiveOpinionFraction(),
                    config.getInfectedFraction(),
                    config.getFractionIllnessA(),
                    config.getFractionIllnessB(),
                    config.getMaxInfectedTimeMean(),
                    config.getMaxInfectedTimeStd());
            for (int step = 0; step < config.getnSteps(); step++) {
                var node = random.nextInt(config.getnAgents());
                singleStep(node, layers, agents);
                if (step % config.getnSaveSteps() == 0)
                    metrics.add(new AgentMetrics(step, agents));
            }
            System.out.println("Thread.currentThread().getName() = sdsds = " + Thread.currentThread().getName());
            System.out.println("Saving");
            saveMetrics(fileMetricsPrefix, i, config, metrics);
            metrics.clear();
        }
    }

    private void updateParameter(double x, ParameterType type, SimulationConfig config) {
        switch (type) {
            case q -> config.getqVoterParameters().setQ((int) x);
            case p -> config.getqVoterParameters().setP(x);
            case beta -> config.getEpidemicLayerParameters().setBeta(x);
            case gamma -> config.getEpidemicLayerParameters().setGamma(x);
            case mu -> config.getEpidemicLayerParameters().setMu(x);
            case kappa -> config.getEpidemicLayerParameters().setKappa(x);
            case maxInfectedTimeMean -> config.setMaxInfectedTimeMean(x);
            case maxInfectedTimeStd -> config.setMaxInfectedTimeStd(x);
            case positiveOpinionFraction -> config.setPositiveOpinionFraction(x);
            case networkP -> config.setNetworkP(x);
        }
    }

    private void saveMetrics(String prefix, int nRun, SimulationConfig config, List<AgentMetrics> metrics) {
        try {
            lock.lock();
            System.out.println(config);
            System.out.println("metrics.get(0) = " + metrics.get(0));
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            var convertedMetrics = metrics.stream().map(AgentMetrics::toString).collect(Collectors.toList());
            convertedMetrics.add(0,
                    "step\tmeanOpinion\tsusceptibleRate\tinfectedRate\tquarantinedRate\trecoveredRate\tdeadRate");
            var path = Paths.get(config.getOutputFolder());
            Files.createDirectories(path);
            FileUtils.writeLines(new File(path.toString(), prepareFilename(prefix, nRun)), convertedMetrics);
            System.out.println("After saving");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to write out names");
        } finally {
            lock.unlock();
        }
    }

    private String prepareFilename(String prefix, int nRun) {
        return prefix + "_NAGENTS=" + config.getnAgents() +
                "_NSTEPS=" + config.getnSteps() +
                "_NETWORKP=" + config.getNetworkP() +
                "_FRAC_LINKS=" + config.getAdditionalLinksFraction() +
                "_FRAC_POS_OPINION=" + config.getPositiveOpinionFraction() +
                "_FRAC_A=" + config.getFractionIllnessA() +
                "_FRAC_B=" + config.getFractionIllnessB() +
                "_FRAC_INFECTED=" + config.getInfectedFraction() +
                "_QVOTER=" + config.getqVoterParameters() +
                "_EPIDEMIC=" + config.getEpidemicLayerParameters() +
                "_I_TIME_MEAN=" + config.getMaxInfectedTimeMean() +
                "_I_TIME_STD=" + config.getMaxInfectedTimeStd() +
                "_NRUN=" + nRun + ".tsv";
    }

    private void singleStep(int node, Pair<Layer, Layer> layers, List<Agent> agents) {
        if (config.isVirtualLayer()) {
            // Assumption: an agent could send more messages than meet people in one timestamp.
            for (int i = 0; i < config.getnQVoterPerStep(); i++) {
                virtualLayerStep(node, layers, agents);
            }
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
                .collect(RandomCollectors.toImprovedLazyShuffledStream())
                .limit(q)
                .collect(Collectors.toCollection(LinkedList::new));
        if (neighbours.isEmpty() || neighbours.size() < q) return;

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
                        if (random.nextDouble() < getCombinedBetaProbability(agent)) {     // S --> I
                            agent.setState(AgentState.INFECTED);
                            break;
                        }
                    }
                }
            }
            case INFECTED -> {
                agent.incrementInfectedTime(config);
                if (agent.getInfectedTime() >= agent.getMaxInfectedTime()) {
                    if (random.nextDouble() < getCombinedGammaProbability(agent)) {        // I --> Q
                        agent.setState(AgentState.QUARANTINED);
                        if (config.isLinksRemoval()) {
                            removeLinksBothLayers(node, layers);
                        }
                    } else if (random.nextDouble() < getCombinedMuProbability(agent)) {    // I --> R
                        agent.setState(AgentState.RECOVERED);
                    } else if (random.nextDouble() < getCombinedKappaProbability(agent)) { // I --> D
                        agent.setState(AgentState.DEAD);
                    }
                }
            }
            case QUARANTINED -> {
                // Q --> R
                if (random.nextDouble() < getCombinedMuProbability(agent)) {             // Q --> R
                    agent.setState(AgentState.RECOVERED);
                } else if (random.nextDouble() < getCombinedKappaProbability(agent)) {   // Q --> D
                    agent.setState(AgentState.DEAD);
                }
            }
        }
        agents.set(node, agent);
    }

    private void removeLinksBothLayers(int node, Pair<Layer, Layer> layers) {
        var epidemicLayer = layers.getFirst();
        var virtualLayer = layers.getSecond();
        removeEdges(node, epidemicLayer);
        removeEdges(node, virtualLayer);
    }

    private void removeEdges(int node, Layer layer) {
        var edgesToRemove = new ArrayList<>(layer.edgesOf(node));
        for (var edge : edgesToRemove) {
            layer.removeEdge(edge);
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

    private double getCombinedGammaProbability(Agent agent) {
        // I --> Q
        double gamma = config.getEpidemicLayerParameters().getGamma();
        return gamma;
    }

    private double getCombinedMuProbability(Agent agent) {
        // I --> R
        double mu = config.getEpidemicLayerParameters().getMu();
        return mu;
    }

    private double getCombinedKappaProbability(Agent agent) {
        double kappa = config.getEpidemicLayerParameters().getKappa();
        if (config.isComorbidities()) {
            if (agent.isHasIllnessA()) {
                kappa *= 2;
            } else if (agent.isHasIllnessB()) {
                kappa *= 3;
            } else if (agent.isHasIllnessA() & agent.isHasIllnessB()) {
                kappa *= 4;
            }
        }
        return kappa;
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
