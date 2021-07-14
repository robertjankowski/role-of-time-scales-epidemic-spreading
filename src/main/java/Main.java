import simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/example_config.yaml");
        simulation.runAll("", true);
    }
}