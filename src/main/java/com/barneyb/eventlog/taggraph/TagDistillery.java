package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import static com.barneyb.eventlog.taggraph.Constants.*;

public class TagDistillery {

    private final Graph etGraph;
    private final Graph graph;

    public TagDistillery(Graph etGraph) {
        this.etGraph = etGraph;
        this.graph = new SingleGraph(etGraph.getId() + " Distilled");
        distill();
    }

    private void distill() {
        for (Node tag : etGraph) {
            if (!TYPE_TAG.equals(tag.getAttribute(ATTR_TYPE))) continue;
            for (Edge ee : tag.getEachLeavingEdge()) {
                Node event = ee.getOpposite(tag);
                int tagCount = event.getDegree();
                for (Edge te : event.getEachLeavingEdge()) {
                    Node otherTag = te.getOpposite(event);
                    if (tag == otherTag) continue;
                    if (tag.getId().compareTo(otherTag.getId()) > 0) continue;
                    // get the edge on the distilled graph
                    Edge e = getEdge(copy(tag), copy(otherTag));
                    add(e, ATTR_WEIGHT, event.getNumber(ATTR_WEIGHT));
                    add(e, ATTR_COUNT, 1);
                }
            }
        }
        for (Node n : graph.getEachNode()) {
            n.setAttribute("ui.label", n.getId());
        }
        for (Edge e : graph.getEachEdge()) {
            double w = e.getAttribute(ATTR_WEIGHT);
            e.setAttribute("ui.label", String.format("%.3f", w));
        }
    }

    private Node copy(Node t) {
        Node n = getNode(t.getId());
        n.setAttribute(ATTR_WEIGHT, (Double) t.getAttribute(ATTR_WEIGHT));
        n.setAttribute(ATTR_COUNT, (Integer) t.getAttribute(ATTR_COUNT));
        return n;
    }

    private Node getNode(String id) {
        Node n = graph.getNode(id);
        if (n == null) {
            n = graph.addNode(id);
        }
        return n;
    }

    private Edge getEdge(Node a, Node b) {
        String id = a.getId() + ":" + b.getId();
        Edge e = graph.getEdge(id);
        if (e == null) {
            e = graph.addEdge(id, a, b);
        }
        return e;
    }

    private void add(Element e, String attr, Number delta) {
        e.setAttribute(attr, (
                e.hasAttribute(attr)
                        ? e.getNumber(attr)
                        : 0) + delta.doubleValue());
    }

    public Graph distilled() {
        return graph;
    }

}
