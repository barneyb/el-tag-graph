package com.barneyb.eventlog.taggraph;

import java.io.*;

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
                out.printf("an %s type=event ui.class=event weight=%f%n", event, weight);
            } else if (event == null) { // a tag!
                out.printf("an %s type=tag ui.class=tag n=%d weight=%f%n", tag, n, weight);
            } else { // a use!
                out.printf("ae %s-%s %s %s type=use ui.class=use weight=%f%n", event, tag, event, tag, weight);
            }
        }
    }
}