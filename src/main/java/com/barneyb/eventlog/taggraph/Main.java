package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static com.barneyb.eventlog.taggraph.Constants.STDIO;

public class Main {

    public static void main(String[] args) throws IOException {
        Graph graph = new SingleGraph("Sample");
        FileSourceDGS src = new FileSourceDGS();
        src.addSink(graph);
        src.readAll(args.length == 0 || STDIO.equals(args[0])
                ? System.in
                : new FileInputStream(args[0]));

        graph.addAttribute("ui.stylesheet", "node.event { size: 10px; fill-color: #c66; text-mode: hidden; }\n" +
                "node.tag {  size: 15px; fill-color: #00c; text-background-mode: plain; text-alignment: at-right; text-size: 30; }\n");
        graph.display();

        Graph tree = new SingleGraph("tree");
        Node start = tree.addNode("start");
        start.addAttribute("ui.label", "Start");

        doLevel(tree, new Thing(graph), start, new TreeSet<>());

        tree.addAttribute("ui.stylesheet", "node { text-size: 30; }\n" +
                "edge { text-size: 24; }");
        tree.display();
    }

    private static void doLevel(Graph tree, Thing thing, Node curr, Set<String> selected) {
        for (String t : thing.suggestions(selected)) {
            Node n = tree.addNode(curr.getId() + "/" + t);
            n.addAttribute("ui.label", t);
            n.addAttribute("ui.class", "level-" + selected.size());
            Edge e = tree.addEdge(curr.getId() + ":" + n.getId(), curr, n, true);
            e.addAttribute("ui.class", "level-" + selected.size());
            if (selected.size() < 2) {
                Set<String> nsel = new TreeSet<>(selected);
                nsel.add(t);
                doLevel(tree, thing, n, nsel);
            }
        }
    }

}
