import simulation.Simulation;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation("config/test_epidemic_layer.yaml");
        simulation.runAll("test_epidemic");
    }
}