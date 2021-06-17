import simulation.Simulation;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation("config/test_beta_on_virtual_remove_links.yaml");
        simulation.runAll("exp");
    }
}