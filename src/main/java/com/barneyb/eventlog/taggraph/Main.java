package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.*;

public class Main {

	public static void main(String[] args) throws IOException {
		Reader r;
		if (args.length == 0) {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("sample-tags.csv");
			assert in != null;
			r = new InputStreamReader(in);
		} else {
			r = new FileReader(args[0]);
		}

		Graph graph = new MultiGraph("Sample");
		loadEL(r, graph);

		DecayingThings<Node> ranked = new DecayingThings<>(
				(Node a) -> a.getAttribute("weight")
		);
		for (Node node : graph) {
			ranked.add(node);
		}

		System.out.println("Top-Ranked Nodes:");
		for (int i = 0; i < 10 && ! ranked.isEmpty(); i++) {
			Node n = ranked.first();
			for (Edge e : n.getEachLeavingEdge()) {
				Node o = e.getOpposite(n);
				if (ranked.contains(o)) {
					ranked.decay(o, e.getAttribute("weight"));
				}
			}
			System.out.println(n);
		}
	}

	private static void loadEL(Reader r, Graph graph) throws IOException {
		BufferedReader br = new BufferedReader(r);
		String line;
		boolean started = false;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			if (! started) {
				started = true;
				if (! Character.isDigit(parts[2].charAt(0))) {
					continue;
				}
			}
			Element e;
			if ("".equals(parts[1])) { // node
				e = graph.addNode(parts[0]);
				e.addAttribute("ui.label", parts[0]);
			} else { // edge
				e = graph.addEdge(parts[0] + "-" + parts[1], parts[0], parts[1]);
			}
			e.addAttribute("n", Integer.parseInt(parts[2]));
			e.addAttribute("weight", Double.parseDouble(parts[3]));
		}
	}

}
