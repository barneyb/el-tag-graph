package com.barneyb.eventlog.taggraph.suggest;

import java.util.List;
import java.util.Set;

public interface Suggester {

    class WeightedTag {
        public final String tag;
        public final double weight;

        WeightedTag(String tag, double weight) {
            this.tag = tag;
            this.weight = weight;
        }
    }

    List<WeightedTag> suggestions(Set<String> tags, int count);
}
