package com.barneyb.eventlog.taggraph;

import org.graphstream.stream.file.FileSinkDGS;

import java.io.*;

/**
 * I convert an eventlog-extract CSV file to equivalent DGS file for loading. I
 * could have been a FileSource, but wanted to keep the translation separate
 * from "load for work". I also could have used FileSinkDGS, but wanted more
 * parity between the CSV and DGS file (not line-per-attribute).
 */
public class ExtractToDGS {

    private static final String STDIO = "-";

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
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) continue;
            String[] parts = line.split(",");
            String event = "".equals(parts[0]) ? null : parts[0];
            String tag = "".equals(parts[1]) ? null : parts[1];
            int n = Integer.parseInt(parts[2]);
            double weight = Double.parseDouble(parts[3]);
            if (tag == null) { // an event!
                assert event != null;
                out.printf("an \"%s\" type=event ui.class=event ui.label=\"%s\" weight=%f%n", event, event, weight);
            } else if (event == null) { // a tag!
                out.printf("an \"%s\" type=tag ui.class=tag ui.label=\"%s\" n=%d weight=%f%n", tag, tag, n, weight);
            } else { // a use!
                out.printf("ae \"%s-%s\" \"%s\" \"%s\" type=use ui.class=use weight=%f%n", event, tag, event, tag, weight);
            }
        }
    }
}
