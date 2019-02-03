package com.barneyb.eventlog.taggraph.suggest;

import com.barneyb.eventlog.taggraph.TagDistillery;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Thing implements Suggester {

    private final Graph etGraph;
    private final Graph tagGraph;

    public Thing(Graph etGraph) {
        this.etGraph = etGraph;
        this.tagGraph = new TagDistillery(etGraph).distilled();
    }

    @Override
    public List<WeightedTag> suggestions(Set<String> tags, int count) {
        if (tags.isEmpty()) {

        } else {

        }
        return new ArrayList<>();
    }

}
