package common.network;

import common.agent.Agent;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import utils.Utils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Network {

    public static Layer barabasiAlbertModel(int n, int m) {
        var gen = new BarabasiAlbertGraphGenerator<Integer, DefaultEdge>(3, m, n);
        Supplier<DefaultEdge> sEdge = DefaultEdge::new;
        Layer g = new Layer(SupplierUtil.createIntegerSupplier(), sEdge, false);
        gen.generateGraph(g);
        return g;
    }

    public static Layer barabasiAlbertModel(int n) {
        return barabasiAlbertModel(n, 3);
    }

    public static List<Agent> getNeighbors(Graph<Agent, DefaultEdge> g, int idAgent) {
        // TODO: should I get neighbours by agentId or in different way ?
        //   Graphs.neighborListOf(g, ...)
        return null;
    }

    public static void addRandomlyEdges(Layer g, int nEdges) {
        var counter = 0;
        while (counter < nEdges) {
            var node = Utils.getRandomSetElement(g.vertexSet());
            var connected = g.edgesOf(node)
                    .stream()
                    .map(g::getEdgeTarget)
                    .collect(Collectors.toSet());
            var unconnected = Utils.setDifference(g.vertexSet(), connected);
            if (unconnected.isEmpty()) {
                continue;
            }
            var newNode = Utils.getRandomSetElement(unconnected);
            if (g.containsEdge(node, newNode) || newNode.equals(node)) {
                continue;
            }
            g.addEdge(node, newNode);
            counter++;
        }

    }

    /**
     * Create bilayer network with additional `additional_virtual_links` in virtual layer.
     *
     * @param size                   Network size
     * @param additionalVirtualLinks Number of additional edges in virtual layer
     * @param m                      related to BA model
     * @param p                      TODO: implement Holme et. al model
     * @return two layers, epidemic and virtual one
     */
    public static Pair<Layer, Layer> createBilayerNetwork(int size, int additionalVirtualLinks, int m, double p) {
        var l1 = barabasiAlbertModel(size, m);
        var l2 = (Layer) l1.clone();
        addRandomlyEdges(l2, additionalVirtualLinks);
        return new Pair<>(l1, l2);
    }
}
