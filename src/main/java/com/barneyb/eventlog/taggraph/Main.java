package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader("sample-tags.csv"));
		r.readLine(); // headers

		Graph graph = new SingleGraph("Sample");

		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split(",");
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
