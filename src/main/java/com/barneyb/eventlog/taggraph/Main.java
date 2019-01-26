package com.barneyb.eventlog.taggraph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

import static com.barneyb.eventlog.taggraph.Constants.STDIO;

public class Main {

    public static void main(String[] args) throws IOException {
        Graph graph = new MultiGraph("Sample");
        FileSourceDGS src = new FileSourceDGS();
        src.addSink(graph);
        src.readAll(args.length == 0 || STDIO.equals(args[0])
                ? System.in
                : new FileInputStream(args[0]));

        graph.addAttribute("ui.stylesheet", "node.event { fill-color: red; text-mode: hidden; }\n" +
                "node.tag { fill-color: blue; text-background-mode: plain; text-alignment: at-right; text-offset: 5px; text-size: 30; }\n");
        Viewer viewer = graph.display();
        View view = viewer.getDefaultView();

        // https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view
        ((Component) view).addMouseWheelListener(e -> {
            e.consume();
            int i = e.getWheelRotation();
            double factor = Math.pow(1.25, i);
            Camera cam = view.getCamera();
            double zoom = cam.getViewPercent() * factor;
            Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
            Point3 guClicked = cam.transformPxToGu(e.getX(), e.getY());
            double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
            double x = guClicked.x + (pxCenter.x - e.getX())/newRatioPx2Gu;
            double y = guClicked.y - (pxCenter.y - e.getY())/newRatioPx2Gu;
            cam.setViewCenter(x, y, 0);
            cam.setViewPercent(zoom);
        });
    }

}
