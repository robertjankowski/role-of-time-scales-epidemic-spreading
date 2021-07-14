package common.parameters;

import java.text.DecimalFormat;

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
    private final static DecimalFormat df = new DecimalFormat("0.00");

    private double beta;
    private double gamma;
    private double mu;
    private double kappa;

    public EpidemicLayerParameters() {
        this.beta = 0.5;
        this.gamma = 0.5;
        this.mu = 0.9;
        this.kappa = 0.1;
    }

    public EpidemicLayerParameters(double beta,
                                   double gamma,
                                   double mu,
                                   double kappa) {
        this.beta = beta;
        this.gamma = gamma;
        this.mu = mu;
        this.kappa = kappa;
    }

    public EpidemicLayerParameters(EpidemicLayerParameters other) {
        this.beta = other.beta;
        this.gamma = other.gamma;
        this.mu = other.mu;
        this.kappa = other.kappa;
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

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setMu(double mu) {
        this.mu = mu;
    }

    public void setKappa(double kappa) {
        this.kappa = kappa;
    }

    @Override
    public String toString() {
        return "beta=" + df.format(beta) +
                "_gamma=" + df.format(gamma) +
                "_mu=" + df.format(mu) +
                "_kappa=" + df.format(kappa);
    }
}