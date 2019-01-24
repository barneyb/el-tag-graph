package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.*;

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

		Graph graph = new SingleGraph("Sample");

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
			if ("".equals(parts[1])) { // node
				Node n = graph.addNode(parts[0]);
				n.addAttribute("ui.label", parts[0]);
				n.addAttribute("n", Integer.parseInt(parts[2]));
			} else { // edge
				Edge e = graph.addEdge(parts[0] + "-" + parts[1], parts[0], parts[1]);
				e.addAttribute("ui.label", parts[2]);
				e.addAttribute("weight", Integer.parseInt(parts[2]));
			}
		}

		graph.display();
	}

}
