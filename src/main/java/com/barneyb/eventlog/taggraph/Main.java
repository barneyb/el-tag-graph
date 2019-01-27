package com.barneyb.eventlog.taggraph;

import com.barneyb.eventlog.taggraph.suggest.SimpleWeight;
import com.barneyb.eventlog.taggraph.suggest.Suggester;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

import java.io.FileInputStream;
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


        doPathTree(new SimpleWeight(raw));
    }

    private static void doPathTree(Suggester suggester) {
        Graph paths = new SingleGraph("tree");
        Node start = paths.addNode("start");
        start.addAttribute("ui.label", "Start");
        start.addAttribute("ui.class", "start");
        doLevel(paths, suggester, start, new TreeSet<>());

//        if (args.length >= 2) {
//            FileSink sink = new FileSinkDGS();
//            paths.addSink(sink);
//            sink.writeAll(paths, new FileOutputStream(args[1]));
//        }

        paths.addAttribute("ui.stylesheet", "node { text-size: 30; shape: freeplane; fill-color: #fff8; stroke-mode: plain; size-mode: fit; }\n" +
                "node.start { fill-color: #0f0c; }\n" +
                "node.level0 { fill-color: #f99; }\n" +
                "node.level1 { fill-color: #d9b; }\n" +
                "node.level2 { fill-color: #b9d;}\n" +
                "node.level3 { fill-color: #99f;}\n" +
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

    private static void doLevel(Graph g, Suggester suggester, Node curr, Set<String> selected) {
        for (SimpleWeight.WeightedTag wt : suggester.suggestions(selected, 5)) {
            Node n = g.addNode(curr.getId() + "/" + wt.tag);
            n.addAttribute("ui.label", wt.tag);
            n.addAttribute("ui.class", "level" + selected.size());
            Edge e = g.addEdge(curr.getId() + ":" + n.getId(), curr, n);
            e.addAttribute("ui.label", Math.round(wt.weight * 100) / 100.0);
            if (selected.size() < 3) {
                Set<String> nsel = new TreeSet<>(selected);
                nsel.add(wt.tag);
                doLevel(g, suggester, n, nsel);
            }
        }
    }

}
