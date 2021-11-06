package pl.wf.simulation;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import pl.wf.common.SimulationConfig;
import pl.wf.common.agent.Agent;
import pl.wf.common.agent.AgentMetrics;
import pl.wf.common.agent.AgentState;
import pl.wf.common.network.Initializer;
import pl.wf.common.network.Layer;
import pl.wf.common.network.Network;
import pl.wf.common.parameters.ParameterType;
import pl.wf.utils.RandomCollectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    private SimulationConfig config;
    private Random random;

    public Simulation(String configPath) {
        try {
            config = SimulationConfig.loadConfig(configPath);
            random = new Random();
        } catch (IOException e) {
            System.err.println("Could not load pl.wf.simulation config. Exiting");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void runAll(String fileMetricsPrefix) throws InterruptedException {
        if (config.getFirstParameterRange() == null && config.getSecondParameterRange() == null) {
            runModel(fileMetricsPrefix, config);
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

            if (config.getThirdParameterRange() == null) {
                var pbb = new ProgressBarBuilder()
                        .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK)
                        .setTaskName("Running experiment: " + config.getFirstParameterRange().getType() +
                                " and " + config.getSecondParameterRange().getType());
                for (double x : ProgressBar.wrap(firstParameterRange, pbb)) {
                    for (double y : secondParameterRange) {
                        updateParameter(x, config.getFirstParameterRange().getType(), config);
                        updateParameter(y, config.getSecondParameterRange().getType(), config);
                        runModel(fileMetricsPrefix, config);
                    }
                }
            } else {
                var thirdParameterRange = config.getThirdParameterRange().getParametersRange();
                for (double x : firstParameterRange) {
                    for (double y : secondParameterRange) {
                        for (double z : thirdParameterRange) {
                            updateParameter(x, config.getFirstParameterRange().getType(), config);
                            updateParameter(y, config.getSecondParameterRange().getType(), config);
                            updateParameter(z, config.getThirdParameterRange().getType(), config);
                            runModel(fileMetricsPrefix, config);
                        }
                    }
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
            runModel(fileMetricsPrefix, config);
        }
    }

    public void runModel(String fileMetricsPrefix, SimulationConfig config) {
        List<AgentMetrics> metrics = new LinkedList<>();
        for (int i = 0; i < config.getnRuns(); i++) {
            int additionalVirtualLinks =
                    getnAdditionalLinks(config.getnAgents(), config.getAdditionalLinksFraction(), config.getNetworkM());
            var layers = Network.createBilayerNetwork(
                    config.getnAgents(), additionalVirtualLinks, config.getNetworkM(), config.getNetworkP());
            var nodeMapping = Network.createNodeMapping(config.getnAgents(), config.isDegreeCorrelated());
            var agents = Initializer.initializeAgents(
                    config.getnAgents(),
                    config.getPositiveOpinionFraction(),
                    config.getProPisFraction(),
                    config.getInfectedFraction(),
                    config.getVaccinationFraction(),
                    config.getFractionIllnessA(),
                    config.getFractionIllnessB(),
                    config.getMaxInfectedTimeMean(),
                    config.getMaxInfectedTimeStd());
            for (int j = 0; j < config.getnSteps(); j++) {
                // Update all nodes on the epidemic layer
                if (config.isEpidemicLayer()) {
                    for (int a = 0; a < config.getnAgents(); a++) {
                        var node = random.nextInt(config.getnAgents());
                        var mappedNode = nodeMapping.get(node);
                        epidemicLayerStep(mappedNode, layers, agents, config);
                    }
                }
                // Update all nodes on the opinion layer
                if (config.isVirtualLayer()) {
                    for (int n = 0; n < config.getnQVoterPerStep(); n++) {
                        for (int a = 0; a < config.getnAgents(); a++) {
                            var node = random.nextInt(config.getnAgents());
                            virtualLayerStep(node, layers, agents, config);
                        }
                    }
                }
                if (j % config.getnSaveSteps() == 0)
                    metrics.add(new AgentMetrics(j, agents));
            }
            saveMetrics(fileMetricsPrefix, i, config, metrics);
            metrics.clear();
        }
    }

    private void updateParameter(double x, ParameterType type, SimulationConfig config) {
        switch (type) {
            case q -> config.getqVoterParameters().setQ((int) x);
            case p -> config.getqVoterParameters().setP(x);
            case beta -> config.getEpidemicLayerParameters().setBeta(x);
            case zeta -> config.getEpidemicLayerParameters().setZeta(x);
            case alpha -> config.getEpidemicLayerParameters().setAlpha(x);
            case gamma -> config.getEpidemicLayerParameters().setGamma(x);
            case mu -> config.getEpidemicLayerParameters().setMu(x);
            case kappa -> config.getEpidemicLayerParameters().setKappa(x);
            case maxInfectedTimeMean -> config.setMaxInfectedTimeMean(x);
            case maxInfectedTimeStd -> config.setMaxInfectedTimeStd(x);
            case positiveOpinionFraction -> config.setPositiveOpinionFraction(x);
            case networkP -> config.setNetworkP(x);
            case pisProFraction -> config.setProPisFraction(x);
            case nQVoterPerStep -> config.setnQVoterPerStep((int) x);
            case N -> config.setnAgents((int) x);
        }
    }

    private void saveMetrics(String prefix, int nRun, SimulationConfig config, List<AgentMetrics> metrics) {
        try {
            var convertedMetrics = metrics.stream().map(AgentMetrics::toString).collect(Collectors.toList());
            convertedMetrics.add(0,
                    "step\tmeanOpinion\tsusceptibleRate\tvaccinationRate\tinfectedRate\tquarantinedRate\trecoveredRate\tdeadRate");
            var path = Paths.get(config.getOutputFolder());
            Files.createDirectories(path);
            FileUtils.writeLines(new File(path.toString(), prepareFilename(prefix, nRun, config)), convertedMetrics);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to write out names");
        }
    }

    private String prepareFilename(String prefix, int nRun, SimulationConfig config) {
        return prefix + "_NAGENTS=" + config.getnAgents() +
                "_NSTEPS=" + config.getnSteps() +
                "_NETWORKP=" + config.getNetworkP() +
                "_FRAC_LINKS=" + config.getAdditionalLinksFraction() +
                "_FRAC_POS_OPINION=" + config.getPositiveOpinionFraction() +
                "_FRAC_INFECTED=" + config.getInfectedFraction() +
                "_QVOTER=" + config.getqVoterParameters() +
                "_PIS=" + config.getProPisFraction() +
                "_EPIDEMIC=" + config.getEpidemicLayerParameters() +
                "_QVOTERSTEPS=" + config.getnQVoterPerStep() +
                //"_I_TIME_MEAN=" + config.getMaxInfectedTimeMean() +
                //"_I_TIME_STD=" + config.getMaxInfectedTimeStd() +
                "_NRUN=" + nRun + ".tsv";
    }

    private void virtualLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents, SimulationConfig config) {
        if (random.nextDouble() < config.getqVoterParameters().getP()) {
            voterActNonConformity(node, agents, config);
        } else {
            voterActConformity(node, layers.getSecond(), agents, config);
        }
    }

    private void voterActConformity(int node, Layer virtualLayer, List<Agent> agents, SimulationConfig config) {
        var agent = agents.get(node);
        int q = config.getqVoterParameters().getQ();
        if (config.isNeglectNeighboursPiS()) {
            // If someone is a PiS voter, she/he does not consider the group influence
            if (agent.getPoliticalSupport() == 1) {
                return;
            }
        }

        var neighbours = Graphs.neighborListOf(virtualLayer, node).stream()
                .collect(RandomCollectors.toImprovedLazyShuffledStream())
                .limit(q)
                .collect(Collectors.toCollection(LinkedList::new));
        if (neighbours.isEmpty() || neighbours.size() < q) return;

        int totalOpinion = 0;
        for (int neighbour : neighbours) {
            totalOpinion += agents.get(neighbour).getOpinion();
        }
        if (totalOpinion == q) {
            agent.setOpinion(1);
            agents.set(node, agent);
        } else if (totalOpinion == -q) {
            agent.setOpinion(-1);
            agents.set(node, agent);
        }
    }

    private void voterActNonConformity(int node, List<Agent> agents, SimulationConfig config) {
        var agent = agents.get(node);
        var x = random.nextDouble();
        if (agent.getPoliticalSupport() == 1) { // pro PiS (political party)
            if (x > config.getPisVaccinationCorrelation()) { // TODO: check it during simulations !
                agent.setOpinion(1);
            } else {
                agent.setOpinion(-1);
            }
        } else { // against PiS (political party) -> normal q-voter
            if (x < 0.5) {
                agent.flipOpinion();
            }
        }
        agents.set(node, agent);
    }

    private void epidemicLayerStep(int node, Pair<Layer, Layer> layers, List<Agent> agents, SimulationConfig config) {
        var agent = agents.get(node);
        var epidemicLayer = layers.getFirst();
        switch (agent.getState()) {
            case SUSCEPTIBLE -> {
                // S -> V
                if (random.nextDouble() < getCombinedZetaProbability(agent)) {
                    agent.setState(AgentState.VACCINATED);
                    break;
                }
                // S -> I
                var neighbours = Graphs.neighborListOf(epidemicLayer, node);
                Collections.shuffle(neighbours);
                if (config.isLinksRemoval()) {
                    if (agent.getOpinion() == 1 && neighbours.size() > 0) {
                        var toRemove = random.nextInt(neighbours.size());
                        neighbours = neighbours.stream().skip(toRemove).collect(Collectors.toList());
                    }
                }
                for (int neighbor : neighbours) {
                    if (agents.get(neighbor).getState() == AgentState.INFECTED) {
                        if (random.nextDouble() < getCombinedBetaProbability(agent)) {     // S --> I
                            agent.setState(AgentState.INFECTED);
                            break;
                        }
                    }
                }
            }
            case VACCINATED -> {
                if (random.nextDouble() < getCombinedAlphaProbability(agent)) { // V -> I
                    agent.setState(AgentState.INFECTED);
                }
            }
            case INFECTED -> {
                agent.incrementInfectedTime(config);
                if (agent.getInfectedTime() >= agent.getMaxInfectedTime()) {
                    if (random.nextDouble() < getCombinedGammaProbability(agent)) {        // I --> Q
                        agent.setState(AgentState.QUARANTINED);
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

    private double getCombinedZetaProbability(Agent agent) {
        // S -> V
        double zeta = config.getEpidemicLayerParameters().getZeta();
        return zeta;
    }

    private double getCombinedAlphaProbability(Agent agent) {
        // V -> I
        double alpha = config.getEpidemicLayerParameters().getAlpha();
        return alpha;
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
        return (int) (m * fractionAdditionalLinks * size);
    }
}
