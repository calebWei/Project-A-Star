package org.softeng306.visualization;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The `GraphUpdate` class is used for updating and managing a GraphStream graph visualization.
 * It provides methods to modify the appearance of nodes, edges, and sprites in the graph.
 */
public class GraphUpdate extends Viewer {

    private static final String DEFAULT_NODE_STYLE = """
        text-alignment: center;
        stroke-mode: plain;
        stroke-color:white;
        fill-mode: plain;
        fill-color: black;
        text-style: bold;
        size: 40px, 40px;
        text-size: 20px;
        text-color: white;
        """;

    private static final String UI_STYLE = "ui.style";
    private static final String UI_LABEL = "ui.label";
    private static final String UI_HIDE = "ui.hide";

    private final Graph currentGraph;
    private final SpriteManager spriteManager;
    private boolean isShowInfo = false;
    private boolean isMove = false;

    /**
     * Constructs a `GraphUpdate` instance for managing a GraphStream graph visualization.
     *
     * @param graph          The GraphStream graph to be managed.
     * @param threadingModel The threading model for rendering (e.g., SWING or EDT).
     */
    public GraphUpdate(Graph graph, ThreadingModel threadingModel) {
        super(graph, threadingModel);
        this.currentGraph = graph;
        this.spriteManager= new SpriteManager(this.currentGraph);
        initializeGraphProperties();
    }

    private void initializeGraphProperties() {
        currentGraph.addAttribute("ui.antialias");
        currentGraph.addAttribute("ui.quality");
        currentGraph.addAttribute("ui.stylesheet", """
            graph {
            fill-mode: plain;
            fill-color: #414141;
            padding: 40px;
            }
            """);


        // Style list of nodes
        for (Node node : currentGraph) {
            node.setAttribute(UI_LABEL, node.getId());
            node.addAttribute(UI_STYLE, DEFAULT_NODE_STYLE);
             spriteManager.addSprite(node.getId());
        }

        // Style list of edges
        int edgeCount = currentGraph.getEdgeCount();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = currentGraph.getEdge(i);
            edge.addAttribute(UI_LABEL,edge.getAttribute("weight") + "");
            edge.addAttribute(UI_STYLE, """
                fill-mode: plain; fill-color: rgba(255,255,255,100);
                text-size: 17px; text-color: rgba(255,255,255,255);
                text-alignment: along;
                """);
        }
    }

    /**
     * Updates the appearance of nodes and their information in the graph.
     *
     * @param graph The updated GraphStream graph.
     */
    public void updateGraph(Graph graph) {
        List<Node> nodesList = new ArrayList<>(graph.getNodeSet());
        for (Node node : nodesList) {
            if ((int) node.getAttribute("processor") != -1) {

                //reset style
                node.removeAttribute("ui.style");
                node.addAttribute("ui.style",
                        "text-alignment: center;\n"
                                + "\tstroke-mode: plain;\n"
                                + "\tstroke-color:white;\n"
                                + "\tfill-mode: plain;\n"
                                + "\tfill-color:" +"#d66060" + ";\n"
                                + "\tsize: 40px, 40px;\n"
                                + "\ttext-style: bold;\n"
                                + "\ttext-size: 20px;\n"
                                + "\ttext-color: white;\n");

                //Update nodes colours (for processor allocation)
                node.removeAttribute(UI_STYLE); //reset style
                node.addAttribute(UI_STYLE, """
                    text-alignment: center;
                    stroke-mode: plain;
                    stroke-color:white;
                    fill-mode: plain;
                    fill-color: #d66060;
                    size: 40px, 40px;
                    text-style: bold;
                    text-size: 20px;
                    text-color: white;
                    """);


                //Update nodes information using Sprites
                Sprite sprite = spriteManager.getSprite(node.getId());
                sprite.addAttribute(UI_LABEL,
                        "P: " + (Integer.parseInt(node.getAttribute("processor").toString())+1) + " " + "T: " + node.getAttribute("startTime"));
                sprite.addAttribute(UI_STYLE, """
                    text-alignment: under;
                    fill-mode: plain; fill-color: rgba(0,0,0,0);
                    text-background-color: rgba(255,255,255,180);
                    text-background-mode: rounded-box;
                    padding: 3px;
                    text-size: 16px;
                    """);
                sprite.attachToNode(node.getId());

                //Handle sprite display
                if (!isShowInfo) {
                    spriteManager.getSprite(node.getId()).addAttribute(UI_HIDE);
                } else {
                    spriteManager.getSprite(node.getId()).removeAttribute(UI_HIDE);
                }

            } else {
                //Reset style -> no processor assigned
                node.removeAttribute(UI_STYLE);
                spriteManager.getSprite(node.getId()).addAttribute(UI_HIDE);
                node.addAttribute(UI_STYLE, DEFAULT_NODE_STYLE);
            }
        }

    }

    public void clickIsMove(ViewPanel viewPanel){
        isMove = !isMove;
        if (isMove){
            viewPanel.setMouseManager(null);
        }else {
            setMouseManager(viewPanel);
        }

    }
    public void setMouseManager(ViewPanel viewPanel) {
        if (!isMove) {
            MouseManager manager = new DefaultMouseManager() {

                @Override
                public void mouseDragged(MouseEvent event) {
                    // For when the graph is not movable
                }

                @Override
                protected void mouseButtonPress(MouseEvent event) {
                    super.mouseButtonPress(event);
                    curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                }

                @Override
                public void mousePressed(MouseEvent event) {
                    super.mousePressed(event);
                    curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                }
            };

            viewPanel.setMouseManager(manager);
        } else {
            //set view panel to default
            viewPanel.setMouseManager(null);
        }
    }

    public void clickShowInfo(Graph graphInput){
        isShowInfo = !isShowInfo;
        List<Node> nodes = new ArrayList<>(graphInput.getNodeSet());
        if (!isShowInfo) {
            for (Node node : nodes) {
                spriteManager.getSprite(node.getId()).addAttribute(UI_HIDE);
            }
        } else {
            for (Node node : nodes) {
                spriteManager.getSprite(node.getId()).removeAttribute(UI_HIDE);
            }
        }
    }

}
