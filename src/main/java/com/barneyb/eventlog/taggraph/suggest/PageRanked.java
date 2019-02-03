package com.barneyb.eventlog.taggraph.suggest;

import org.graphstream.algorithm.PageRank;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import static com.barneyb.eventlog.taggraph.Constants.ATTR_TYPE;
import static com.barneyb.eventlog.taggraph.Constants.TYPE_EVENT;

public class PageRanked implements Suggester {

    public Graph ranked;

    public PageRanked(Graph raw) {
        int counter = 0;
        ranked = new MultiGraph(raw.getId() + "-ranked");
        Set<Node> visited = new HashSet<>();
        for (Node tn : raw.getEachNode()) {
            if (TYPE_EVENT.equals(tn.getAttribute(ATTR_TYPE))) continue;
            if (!visited.add(tn)) continue;
            Node rtn = ranked.addNode(tn.getId());
            rtn.addAttribute("ui.label", tn.getId());
            for (Edge e1 : tn.getEachEdge()) {
                Node en = e1.getOpposite(tn);
                for (Edge e2 : en.getEachEdge()) {
                    Node otn = e2.getOpposite(en);
                    if (tn == otn) continue;
                    Node rotn;
                    if (visited.add(otn)) {
                        rotn = ranked.addNode(otn.getId());
                        rotn.addAttribute("ui.label", otn.getId());
                    } else {
                        rotn = ranked.getNode(otn.getId());
                    }
                    ranked.addEdge("" + counter++, rtn, rotn);
                }
            }
        }
        PageRank pageRank = new PageRank();
        pageRank.init(ranked);
        pageRank.compute();
        for (Node n : ranked) {
            n.setAttribute("ui.label", String.format("%s (%.3f)", n.getId(), n.getAttribute(PageRank.DEFAULT_RANK_ATTRIBUTE)));
        }
    }

    @Override
    public List<WeightedTag> suggestions(Set<String> tags, int count) {
        SortedSet<WeightedTag> byWeight = new TreeSet<>(Comparator.comparingDouble((WeightedTag t) -> t.weight).reversed());
        for (Node tn : ranked.getEachNode()) {
            if (TYPE_EVENT.equals(tn.getAttribute(ATTR_TYPE))) continue;
            if (tags.contains(tn.getId())) continue;
            if (! tags.isEmpty()) {
                boolean found = false;
                for (Edge e : tn.getEachEdge()) {
                    if (tags.contains(e.getOpposite(tn).getId())) {
                        found = true;
                        break;
                    }
                }
                if (! found) continue;
            }
            byWeight.add(new WeightedTag(tn.getId(), tn.getAttribute(PageRank.DEFAULT_RANK_ATTRIBUTE)));
        }
        List<WeightedTag> suggs = new ArrayList<>();
        for (WeightedTag wt : byWeight) {
            if (suggs.size() >= count) break;
            suggs.add(wt);
        }
        return suggs;
    }

}
