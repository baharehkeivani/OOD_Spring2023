package graph;

import org.gephi.graph.api.*;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.*;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PNGExporter;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GraphVisualizer {

    private String projectName = "";

    public GraphVisualizer(String projectName) {
        this.projectName = projectName;
    }

    public void visualize() {
        // Get a project controller
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();

        // Get the graph model
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();

        // Create a new Gephi graph
        org.gephi.graph.api.Graph gephiGraph = graphModel.getDirectedGraph();

        // Add nodes to the Gephi graph
        for (CustomNode node : Graph.getNodes()) {
            String nodeId = node.getLabel();
            // Check if the node with the given ID already exists in the graph
            Node existingNode = graphModel.getGraph().getNode(nodeId);
            // If the node does not exist, create and add it to the graph - we also check in CustomNode while creating the Node
            if (existingNode == null) {
                Node gephiNode = graphModel.factory().newNode(nodeId);
                gephiNode.setLabel(node.getLabel());
                gephiNode.setSize(5);
                gephiGraph.addNode(gephiNode);
            } else {
                System.err.println("Error: Node with ID " + nodeId + " already exists in the graph.");
            }
        }

        // Add edges to the Gephi graph
        for (CustomNode node : Graph.getNodes()) {
            for (CustomEdge edge : node.getEdges()) {
                Node fromNode = graphModel.getGraph().getNode(edge.getFrom().getLabel());
                Node toNode = graphModel.getGraph().getNode(edge.getTo().getLabel());
                // null checks for fromNode and toNode
                if (fromNode != null && toNode != null) {
                    Edge gephiEdge = graphModel.factory().newEdge(fromNode, toNode, edge.getWeight(), true);
                    gephiGraph.addEdge(gephiEdge);
                }
//                else {
//                    System.out.println("Note: Null node(s) encountered when creating edge from " + edge.getFrom().getLabel() + " to " + edge.getTo().getLabel());
//                }
            }
        }


        // Open the graph in the Gephi workspace
        pc.openWorkspace(pc.getCurrentWorkspace());


        // Preview the graph
        System.err.println("\n ++++++++ \n Graph Visualisation Process Started\n ++++++++ \n");
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 2);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, new Font("Arial", Font.PLAIN, 12));
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);

        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);
        ForceAtlas2 forceAtlas2 = new ForceAtlas2Builder().buildLayout();
        autoLayout.addLayout(forceAtlas2, 1.0f);
        autoLayout.execute();


        // Export the graph as a PNG image
        ExportController exportController = Lookup.getDefault().lookup(ExportController.class);
        PNGExporter pngExporter = (PNGExporter) exportController.getExporter("png");
        pngExporter.setWorkspace(pc.getCurrentWorkspace());
        pngExporter.setWidth(8000); // Increase the width of the output image
        pngExporter.setHeight(4000); // Increase the height of the output image
        pngExporter.setMargin(0); // Increase the antialiasing for better quality

        try {
            System.err.println("\n ******** \nExtraction Process Started\n ******** \n");
            exportController.exportFile(new File("graph_photos/" +projectName + ".png"), pngExporter);
            System.err.println("\n -------- \nExtraction Process ended\n -------- \n");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to export the graph as a PNG image.");
        }
        // Close the Gephi project
        pc.closeCurrentWorkspace();
    }
}