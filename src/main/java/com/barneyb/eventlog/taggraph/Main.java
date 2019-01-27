package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSourceDGS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static com.barneyb.eventlog.taggraph.Constants.*;

public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Graph raw = new SingleGraph("Sample");
        FileSourceDGS src = new FileSourceDGS();
        src.addSink(raw);
        src.readAll(args.length < 1 || STDIO.equals(args[0])
                ? System.in
                : new FileInputStream(args[0]));

//        graph.addAttribute("ui.stylesheet", "node.event { size: 10px; fill-color: #c66; text-mode: hidden; }\n" +
//                "node.tag {  size: 15px; fill-color: #00c; text-alignment: at-right; text-size: 30; }\n");
//        graph.display();




        TagDistillery d = new TagDistillery(raw);
        Graph relations = d.distilled();

//        dump(raw      .getNode("masturbation"));
//        dump(relations.getNode("masturbation"));

//        relations.addAttribute("ui.stylesheet", "node { text-size: 30; shape: freeplane; fill-color: #fff8; stroke-mode: plain; size-mode: fit; }\n" +
//                "edge { text-size: 24; shape: freeplane; }");
//        relations.display();



        Graph paths = new SingleGraph("tree");
        Node start = paths.addNode("start");
        start.addAttribute("ui.label", "Start");
        doLevel(paths, new Thing(raw), start, new TreeSet<>());

        if (args.length >= 2) {
            FileSink sink = new FileSinkDGS();
            paths.addSink(sink);
            sink.writeAll(paths, new FileOutputStream(args[1]));
        }

        paths.addAttribute("ui.stylesheet", "node { text-size: 30; shape: freeplane; fill-color: #fff8; stroke-mode: plain; size-mode: fit; }\n" +
                "edge { text-size: 24; shape: freeplane; }");
        paths.display();
    }

    private static void dump(Node n) {
        System.out.println(n.getId() + " " + n.getAttribute(ATTR_COUNT) + " : " + n.getAttribute(ATTR_WEIGHT));
        double sum = 0;
        for (Edge e : n.getEachLeavingEdge()) {
            double w = e.getAttribute(ATTR_WEIGHT);
            sum += w;
            System.out.println("  " + e.getId() + " : " + w);
        }
        System.out.println("  " + sum);
    }

    private static void doLevel(Graph tree, Thing thing, Node curr, Set<String> selected) {
        for (Thing.WeightedTag wt : thing.suggestions(selected)) {
            Node n = tree.addNode(curr.getId() + "/" + wt.tag);
            n.addAttribute("ui.label", wt.tag);
            Edge e = tree.addEdge(curr.getId() + ":" + n.getId(), curr, n);
            e.addAttribute("ui.label", Math.round(wt.weight * 100) / 100.0);
            if (selected.size() < 3) {
                Set<String> nsel = new TreeSet<>(selected);
                nsel.add(wt.tag);
                doLevel(tree, thing, n, nsel);
            }
        }
    }

}
