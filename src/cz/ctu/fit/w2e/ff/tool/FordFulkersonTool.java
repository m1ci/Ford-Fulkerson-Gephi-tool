/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ctu.fit.w2e.ff.tool;

import cz.ctu.fit.w2e.ff.algorithm.FordFulkersonAlgorithm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.tools.spi.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Milan Dojčinovski - <dojcinovski.milan (at) gmail.com> - @m1ci 
 */

@ServiceProvider(service = Tool.class)
public class FordFulkersonTool implements Tool {

    private boolean sourceNode = false;
    private boolean targetNode = false;
    private Node sNode = null;
    private Node tNode = null;
    private final FordFulkersonToolUI ui = new FordFulkersonToolUI();

    @Override
    public void select() {
    }

    @Override
    public void unselect() {
    }

    @Override
    public ToolEventListener[] getListeners() {
        return new ToolEventListener[]{
            new NodeClickEventListener() {
                @Override
                public void clickNodes(Node[] nodes) {
                    // select source or target node
                    if (sourceNode) {
                        // first selected node
                        sNode = nodes[0];
                        // node color
                        sNode.getNodeData().setColor(1, 0, 0);
                        // source node selected
                        sourceNode = false;
                        System.out.println("Source node with ID " + nodes[0] + " was selected.");
                    } else if (targetNode) {
                        // first selected node
                        tNode = nodes[0];
                        // node color
                        tNode.getNodeData().setColor(1, 0.6f, 0);
                        // target node selected
                        targetNode = false;
                        System.out.println("Target node with ID " + nodes[0] + " was selected.");
                    }
                }
            }
        };
    }

    @Override
    public ToolUI getUI() {
        return ui;
    }

    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION;
    }

    private class FordFulkersonToolUI implements ToolUI {

        @Override
        public JPanel getPropertiesBar(Tool tool) {
            JPanel panel = new JPanel();

            JButton source = new JButton("Source");
            source.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    // select source
                    sourceNode = true;
                }
            });

            JButton target = new JButton("Target");
            target.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    // select target
                    targetNode = true;
                }
            });

            JButton run = new JButton("Run");

            run.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {                    
                    //Get current graph
                    GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                    Graph graph = gc.getModel().getGraph();
                    
                    FordFulkersonAlgorithm ff = new FordFulkersonAlgorithm();
                    ff.run(sNode.getId(), tNode.getId(), graph);
                }
            });
            
            JButton init = new JButton("Init");

            init.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    System.out.println("Initialization...");
                    //Get current graph
                    GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                    Graph graph = gc.getModel().getGraph();
                    
                    //List columns
                    AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
                    AttributeModel model = ac.getModel();

                    AttributeColumn flow = model.getEdgeTable().getColumn("Flow");

                    //if not exists flow column, create it
                    if (flow == null) {
                        flow = model.getEdgeTable().addColumn("Flow", org.gephi.data.attributes.api.AttributeType.INT);
                    }
                    //set edge labels
                    for (Edge e : graph.getEdges()) {
                        e.getEdgeData().setLabel("0/"+e.getEdgeData().getAttributes().getValue("Capacity").toString());
                        e.getEdgeData().getAttributes().setValue("Flow", 0);
                    }
                    //set node labels
                    for (Node n : graph.getNodes()) {
                        n.getNodeData().setLabel(n.getNodeData().getId());                        
                        n.getNodeData().setColor(0, (float)0.74902, 1);
                        n.getNodeData().setSize(24);
                    }

                    //if not exists flow column, create it
                    if (flow == null) {
                        flow = model.getEdgeTable().addColumn("Flow", org.gephi.data.attributes.api.AttributeType.INT);
                    }                    
                }
            });            
            panel.add(source);
            panel.add(target);
            panel.add(run);
            panel.add(init);
            return panel;
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public String getName() {
            return "Ford-Fulkerson Tool";
        }

        @Override
        public String getDescription() {
            return "Ford-Fulkerson Tool";
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
}