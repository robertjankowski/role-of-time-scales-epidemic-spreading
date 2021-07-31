import simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/results_remove_links_parameters.yaml");
        simulation.runAll("on", true);
    }
}