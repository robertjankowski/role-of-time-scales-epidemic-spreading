package common.network;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.function.Supplier;

/**
 * Wrapper (alias) for the SimpleGraph class
 */
public class Layer extends SimpleGraph<Integer, DefaultEdge> {
    public Layer(Class<? extends DefaultEdge> edgeClass) {
        super(edgeClass);
    }

    public Layer(Supplier<Integer> vertexSupplier, Supplier<DefaultEdge> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }
}
