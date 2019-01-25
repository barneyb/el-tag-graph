package com.barneyb.eventlog.taggraph;

import java.util.*;
import java.util.function.Function;

/**
 * I start with an extractor and maybe a collection of things. I accept new
 * things and/or decay applied to those things. It's illegal to decay a thing I
 * don't know about (yet or anymore). The next thing is one with the highest
 * extracted value.
 *
 * <p>I <em>think</em> I'm a priority queue? Ish?
 *
 * @author bboisvert
 */
public class DecayingThings<E> {

    private class Item implements Comparable<Item> {
        final E value;
        double weight;

        private Item(E value) {
            this.value = value;
            this.weight = extractor.apply(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (! (obj instanceof DecayingThings.Item)) return false;
            //noinspection unchecked
            Item it = (Item) obj;
            return value.equals(it.value);
        }

        @Override
        public int compareTo(Item o) {
            if (value.equals(o.value)) return 0;
            // reversed so we're descending
            return Double.compare(o.weight, weight);
        }

        public void decay(double d) {
            weight -= d;
        }

    }

    private final Function<E, Double> extractor;
    private final SortedSet<Item> items = new TreeSet<>();
    private final Map<E, Double> decay = new HashMap<>();

    public DecayingThings(Function<E, Double> extractor) {
        this.extractor = extractor;
    }

    public DecayingThings(Function<E, Double> extractor, Collection<E> things) {
        this(extractor);
        for (E e : things) {
            add(e);
        }
    }

    public void add(E thing) {
        items.add(new Item(thing));
    }

    public void decay(E thing, double d) {
        if (decay.containsKey(thing)) {
            d += decay.get(thing);
        }
        decay.put(thing, d);
    }

    public E first() {
        while (! isEmpty()) {
            Item it = items.first();
            items.remove(it);
            if (!decay.containsKey(it.value)) return it.value;
            it.decay(decay.remove(it.value));
            items.add(it);
        }
        throw new IllegalStateException("What?");
    }

    public boolean contains(E thing) {
        return items.contains(new Item(thing));
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

}
