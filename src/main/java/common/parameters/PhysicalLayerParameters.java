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
public record PhysicalLayerParameters(double beta,
                                      double gamma,
                                      double mu,
                                      double kappa,
                                      double maxInfectedTime) {
}