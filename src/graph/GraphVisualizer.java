package graph;

import org.gephi.graph.api.*;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GraphVisualizer {

    public void visualize() {
        // Get a project controller
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();

        // Get the graph model
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();

        // Create a new Gephi graph
        org.gephi.graph.api.Graph gephiGraph = graphModel.getGraph();

        // Add nodes to the Gephi graph
        for (CustomNode node : Graph.getNodes()) {
            Node gephiNode = graphModel.factory().newNode(node.getLabel());
            gephiGraph.addNode(gephiNode);
        }

        // Add edges to the Gephi graph
        for (CustomNode node : Graph.getNodes()) {
            for (CustomEdge edge : node.getEdges()) {
                Node fromNode = graphModel.getGraph().getNode(edge.getFrom().getLabel());
                Node toNode = graphModel.getGraph().getNode(edge.getTo().getLabel());
                Edge gephiEdge = graphModel.factory().newEdge(fromNode, toNode, edge.getWeight(), true);
                gephiGraph.addEdge(gephiEdge);
            }
        }

        // Open the graph in the Gephi workspace
        pc.openWorkspace(pc.getCurrentWorkspace());

        // Refresh the graph layout
        AutoLayout autoLayout = new AutoLayout(20, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);
        ForceAtlas2 forceAtlas2Layout = new ForceAtlas2(new ForceAtlas2Builder());
        autoLayout.addLayout(forceAtlas2Layout, 1f);
        autoLayout.execute();

        // Preview the graph
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
//        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.5f));
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, new Font("Arial", Font.PLAIN, 12));
//        previewModel.getProperties().putValue(PreviewProperty.NODE_SIZE, 10f);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        previewController.refreshPreview();
//        previewController.renderPreview();
    }
}