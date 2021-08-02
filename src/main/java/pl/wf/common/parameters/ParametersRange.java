package pl.wf.common.parameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParametersRange {
    private double start;
    private double end;
    private int count;
    private List<Double> vals;
    private ParameterType type;

    public ParametersRange() {
    }

    public ParametersRange(double start, double end, int count, List<Double> vals, ParameterType type) {
        this.start = start;
        this.end = end;
        this.count = count;
        this.vals = vals;
        this.type = type;
    }

    public List<Double> getParametersRange() {
        if (vals == null) {
            return IntStream.range(0, count)
                    .boxed()
                    .map(i -> start + i * (end - start) / (count - 1))
                    .collect(Collectors.toList());
        }
        return vals;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public List<Double> getVals() {
        return vals;
    }

    public void setVals(List<Double> vals) {
        this.vals = vals;
    }

    @Override
    public String toString() {
        return "ParametersRange{" +
                "start=" + start +
                ", end=" + end +
                ", count=" + count +
                ", vals=" + vals +
                ", type=" + type +
                '}';
    }
}
