package common;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.List;
import java.util.function.Supplier;

public class Network {

    public static Graph<Integer, DefaultEdge> barabasiAlbertModel(int n, int m) {
        var gen = new BarabasiAlbertGraphGenerator<Integer, DefaultEdge>(3, m, n);
        Supplier<DefaultEdge> sEdge = DefaultEdge::new;
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), sEdge, false);
        gen.generateGraph(g);
        return g;
    }

    public static Graph<Integer, DefaultEdge> barabasiAlbertModel(int n) {
        return barabasiAlbertModel(n, 3);
    }

    public static List<Agent> getNeighbors(Graph<Agent, DefaultEdge> g, int idAgent) {
        // TODO: should I get neighbours by agentId or in different way ?
        //   Graphs.neighborListOf(g, ...)
        return null;
    }
}
