package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourceDGS;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Graph graph = new MultiGraph("Sample");
        FileSourceDGS src = new FileSourceDGS();
        src.addSink(graph);
        src.readAll(args.length == 0 || "-".equals(args[0])
                ? System.in
                : new FileInputStream(args[0]));

        graph.display();
    }

}
