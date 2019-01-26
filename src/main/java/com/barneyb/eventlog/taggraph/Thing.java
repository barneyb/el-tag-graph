package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

import static com.barneyb.eventlog.taggraph.Constants.*;

public class Thing {

    public static final int DEFAULT_MAX_SUGGESTIONS = 5;

    private final Graph etGraph;
    private final int maxSuggestions;

    public Thing(Graph etGraph) {
        this.etGraph = etGraph;
        maxSuggestions = DEFAULT_MAX_SUGGESTIONS;
    }

    private static class WeightedTag {
        final String tag;
        final double weight;

        private WeightedTag(String tag, double weight) {
            this.tag = tag;
            this.weight = weight;
        }
    }

    List<String> suggestions(Set<String> curr) {
        SortedSet<WeightedTag> byWeight = new TreeSet<>(Comparator.comparingDouble((WeightedTag t) -> t.weight).reversed());
        if (curr.isEmpty()) {
            // grab them all!
            for (Node n : etGraph) {
                if (TYPE_TAG.equals(n.getAttribute(ATTR_TYPE))) {
                    byWeight.add(new WeightedTag(n.getId(), n.getAttribute(ATTR_WEIGHT)));
                }
            }
        } else {
            // find the events we care about
            Set<Node> events = null;
            for (String t : curr) {
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
            // now build the weighted tags those events carry
            Map<String, Double> tagMap = new HashMap<>();
            for (Node eventNode : events) {
                for (Edge e : eventNode.getEachEdge()) {
                    String tag = e.getOpposite(eventNode).getId();
                    if (curr.contains(tag)) continue;
                    tagMap.merge(tag, eventNode.getAttribute(ATTR_WEIGHT), Double::sum);
                }
            }
            for (String t : tagMap.keySet()) {
                byWeight.add(new WeightedTag(t, tagMap.get(t)));
            }
        }
        List<String> suggs = new ArrayList<>();
        for (WeightedTag wt : byWeight) {
            if (suggs.size() >= maxSuggestions) break;
            suggs.add(wt.tag);
        }
        return suggs;
    }

}
