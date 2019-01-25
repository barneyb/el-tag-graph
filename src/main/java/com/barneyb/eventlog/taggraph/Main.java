package com.barneyb.eventlog.taggraph;

import org.graphstream.algorithm.PageRank;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.*;
import java.util.Comparator;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) throws IOException {
		BufferedReader r;
		if (args.length == 0) {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("sample-tags.csv");
			assert in != null;
			r = new BufferedReader(new InputStreamReader(in));
		} else {
			r = new BufferedReader(new FileReader(args[0]));
		}

		Graph graph = new MultiGraph("Sample");

		String line;
		boolean started = false;
		while ((line = r.readLine()) != null) {
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
				for (int i = (int) (100 * Double.parseDouble(parts[3])); i > 0; i--) {
					e = graph.addEdge(parts[0] + "-" + parts[1] + ":" + i, parts[0], parts[1]);
				}
			}
		}
		PageRank pageRank = new PageRank();
		pageRank.setVerbose(true);
		pageRank.init(graph);

		TreeSet<Node> ranked = new TreeSet<>(
				Comparator.comparingDouble(pageRank::getRank).reversed());

		for (Node node : graph) {
			ranked.add(node);
		}

		int i;

		System.out.println("Top-Ranked Nodes:");
		i = 0;
		for (Node n : ranked) {
			System.out.printf("%s (%.2f%%)%n", n.getId(), pageRank.getRank(n) * 100);
			if (++i == 10) break;
		}

	}

}
