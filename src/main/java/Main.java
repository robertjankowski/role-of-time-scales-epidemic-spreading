import simulation.Simulation;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation("config/test_beta_on_virtual_more_qvoter.yaml");
        simulation.runAll("exp");
    }
}