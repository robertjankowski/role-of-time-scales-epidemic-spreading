import simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/results_remove_links_parameters_beta_range.yaml");
        simulation.runAll("off", true);
    }
}