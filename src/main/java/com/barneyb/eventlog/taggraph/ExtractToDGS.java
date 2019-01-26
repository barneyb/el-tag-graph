package com.barneyb.eventlog.taggraph;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.barneyb.eventlog.taggraph.Constants.*;

/**
 * I convert an eventlog-extract CSV file to equivalent DGS file for loading. I
 * could have been a FileSource, but wanted to keep the translation separate
 * from "load for work". I also could have used FileSinkDGS, but wanted more
 * parity between the CSV and DGS file (not line-per-attribute).
 */
public class ExtractToDGS {

    public static void main(String[] args) throws IOException {
        String source = args.length > 0 ? args[0] : STDIO;
        String sink = args.length > 1 ? args[1] : STDIO;
        BufferedReader r = new BufferedReader(new InputStreamReader(
                STDIO.equals(source)
                        ? System.in
                        : new FileInputStream(source)));
        PrintStream out = STDIO.equals(sink)
                ? System.out
                : new PrintStream(new FileOutputStream(sink));
        out.println("DGS004");
        out.println("null 0 0");
        String line;
        String thisEvent = null;
        Map<String, Samples> tagSamples = new HashMap<>();
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) continue;
            String[] parts = line.split(",");
            String event = parts[0];
            // timestamp
            double weight = Double.parseDouble(parts[2]);
            String tag = parts[3];
            if (! event.equals(thisEvent)) {
                thisEvent = event;
                out.printf("an \"%s\" %s=%s ui.class=event ui.label=\"%s\" %s=%f%n", event, ATTR_TYPE, TYPE_EVENT, event, ATTR_WEIGHT, weight);
            }
            if (tagSamples.containsKey(tag)) {
                tagSamples.get(tag).add(weight);
            } else {
                tagSamples.put(tag, new Samples(weight));
                out.printf("an \"%s\" %s=%s ui.class=tag ui.label=\"%s\"%n", tag, ATTR_TYPE, TYPE_TAG, tag);
            }
            out.printf("ae \"%s-%s\" \"%s\" \"%s\" %s=%s ui.class=use %s=%f%n", event, tag, event, tag, ATTR_TYPE, TYPE_USE, ATTR_WEIGHT, weight);
        }
        ArrayList<String> tagList = new ArrayList<>(tagSamples.keySet());
        Collections.sort(tagList);
        for (String t : tagList) {
            Samples ss = tagSamples.get(t);
            out.printf("cn \"%s\" %s=%d %s=%f%n", t, ATTR_COUNT, ss.size(), ATTR_WEIGHT, ss.sum());
        }
    }

    private static class Samples {
        private int n;
        private double sum;

        Samples() {}

        Samples(double firstSample) {
            add(firstSample);
        }

        void add(double s) {
            n += 1;
            sum += s;
        }

        int size() {
            return n;
        }

        double sum() {
            return sum;
        }

    }

}
