package common.parameters;

/**
 * Parameters of simulation for $$l_1$$ (epidemic) layer
 * <p>
 * p_beta: probability that agent becomes infected (S -> I)
 * <p>
 * p_gamma: probability that agent goes in a quarantine (I -> Q)
 * <p>
 * p_mu: probability that agent recovers from an illness (I -> R, Q -> R)
 * <p>
 * p_kappa: probability that agent dies (I -> D, Q -> D)
 * <p>
 * max_infected_time: number of timestamp spends in `I` state (depends on opinion)
 */
public final class EpidemicLayerParameters {
    private final double beta;
    private final double gamma;
    private final double mu;
    private final double kappa;
    private final double maxInfectedTime;

    public EpidemicLayerParameters() {
        this.beta = 0.5;
        this.gamma = 0.5;
        this.mu = 0.9;
        this.kappa = 0.1;
        this.maxInfectedTime = 10;
    }

    public EpidemicLayerParameters(double beta,
                                   double gamma,
                                   double mu,
                                   double kappa,
                                   double maxInfectedTime) {
        this.beta = beta;
        this.gamma = gamma;
        this.mu = mu;
        this.kappa = kappa;
        this.maxInfectedTime = maxInfectedTime;
    }

    public double getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }

    public double getMu() {
        return mu;
    }

    public double getKappa() {
        return kappa;
    }

    public double getMaxInfectedTime() {
        return maxInfectedTime;
    }

    @Override
    public String toString() {
        return "beta=" + beta + "_gamma=" + gamma + "_mu=" + mu + "_kappa=" + kappa + "_maxInfectedTime=" + maxInfectedTime;
    }
}