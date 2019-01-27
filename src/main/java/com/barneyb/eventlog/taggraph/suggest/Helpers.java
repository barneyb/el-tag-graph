package com.barneyb.eventlog.taggraph.suggest;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashSet;
import java.util.Set;

public final class Helpers {

    private Helpers() {}

    static Iterable<Node> getTaggedEvents(Graph etGraph, Set<String> tags) {
        // find the events we care about
        Set<Node> events = null;
        for (String t : tags) {
            Node tagNode = etGraph.getNode(t);
            Set<Node> tagEvents = new HashSet<>();
            for (Edge e : tagNode.getEachEdge()) {
                tagEvents.add(e.getOpposite(tagNode));
            }
            if (events == null) {
                events = tagEvents;
            } else {
                events.retainAll(tagEvents);
            }
        }
        return events;
    }
}
