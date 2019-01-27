package com.barneyb.eventlog.taggraph.suggest;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

import static com.barneyb.eventlog.taggraph.Constants.*;
import static com.barneyb.eventlog.taggraph.suggest.Helpers.getTaggedEvents;

public class SimpleWeight implements Suggester {

    private final Graph etGraph;

    public SimpleWeight(Graph etGraph) {
        this.etGraph = etGraph;
    }

    public List<WeightedTag> suggestions(Set<String> tags, int count) {
        SortedSet<WeightedTag> byWeight = new TreeSet<>(Comparator.comparingDouble((WeightedTag t) -> t.weight).reversed());
        if (tags.isEmpty()) {
            // grab them all!
            for (Node n : etGraph) {
                if (TYPE_TAG.equals(n.getAttribute(ATTR_TYPE))) {
                    byWeight.add(new WeightedTag(n.getId(), n.getAttribute(ATTR_WEIGHT)));
                }
            }
        } else {
            // now build the weighted tags those events carry
            Map<String, Double> tagMap = new HashMap<>();
            for (Node eventNode : getTaggedEvents(etGraph, tags)) {
                for (Edge e : eventNode.getEachEdge()) {
                    String tag = e.getOpposite(eventNode).getId();
                    if (tags.contains(tag)) continue;
                    tagMap.merge(tag, eventNode.getAttribute(ATTR_WEIGHT), Double::sum);
                }
            }
            for (String t : tagMap.keySet()) {
                byWeight.add(new WeightedTag(t, tagMap.get(t)));
            }
        }
        List<WeightedTag> suggs = new ArrayList<>();
        for (WeightedTag wt : byWeight) {
            if (suggs.size() >= count) break;
            suggs.add(wt);
        }
        return suggs;
    }

}
