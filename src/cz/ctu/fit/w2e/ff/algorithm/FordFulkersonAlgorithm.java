/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ctu.fit.w2e.ff.algorithm;

import java.util.LinkedList;
import javax.swing.JOptionPane;
import org.gephi.data.attributes.api.*;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Milan Dojčinovski - <dojcinovski.milan (at) gmail.com> - @m1ci 
 */
public class FordFulkersonAlgorithm {
    private boolean[] visited;   // visited[v] - true if s->v is in residual s->v path
    private Edge[] edgeTo;       // edgeTo[v] - last edge on shortest residual s->v path
    int max_value = 0;           // current max-flow value
    
    private LinkedList<Edge> flow_edges = new LinkedList<Edge>();
    
    public void run(int source, int target, Graph graph){
        
        System.out.println("------------------------");
        //set label of the edges (capacity)
        for (Edge e : graph.getEdges()) {
            e.getEdgeData().setLabel(e.getEdgeData().getAttributes().getValue("Capacity").toString());
        }          
        
        //loop until exists autmenting path
        while(existsAugmentingPath(graph, source, target)){            
            
            // amount by which the flow can be increased
            // set enaugh high value at the begining
            int flow_upper_bound = 10000;
            
            //compute the what is the maximum possible flow increase
            //crawl from the target to the source
            for (int v = target; v != source; v = getOpositeEdgeNode(edgeTo[v], v)) {
                flow_upper_bound = Math.min(flow_upper_bound, residualCapacity(edgeTo[v], v));
                //System.out.println(residualCapacity(edgeTo[v], v));
            }
            
            // augment the flow with the maximum possible flow
            // crawl from the target to the source and augment the edges flow
            for (int v = target; v != source; v = getOpositeEdgeNode(edgeTo[v], v)) {
                
                //increase the flow of the edge
                addResidualFlowTo(edgeTo[v], v, flow_upper_bound);
                
                // add the edge to edges with some flow
                flow_edges.add(edgeTo[v]);
            }
            
            max_value += flow_upper_bound;

            System.out.println("***** Max-flow = " + max_value + " *****");
            System.out.println("------------------------");
        }
        
        // 1. set edge labels (flow/capacity) after running FF algorithm
        // 2. color the edges with some amount of flow
        for (Edge e : graph.getEdges()) {
            e.getEdgeData().setLabel(e.getEdgeData().getAttributes().getValue("Flow").toString()+"/"+e.getEdgeData().getAttributes().getValue("Capacity").toString());
            if(!flow_edges.contains(e)){
                e.getEdgeData().setColor( (float)0.972549, (float)0.972549, (float)1);
            }else{
                e.getEdgeData().setColor(1, 0, 0);
            }
        }
        JOptionPane.showMessageDialog(null, "Final maximum flow is " + max_value);
        System.out.println("Final maximum flow is " + max_value);
    }
    
    //breath-first search if exists augmenting path
    public boolean existsAugmentingPath(Graph graph, int s, int t){
        
        visited = new boolean[graph.getNodeCount()+1];
        edgeTo = new Edge[graph.getNodeCount()+1];
        
        //initialize FIFO queue
        FIFO fifo = new FIFO();
        
        //add first node (source) node to the queue
        fifo.add(s);
        
        //mark the source node as visited
        visited[s] = true;
        
        while(!fifo.isEmpty()){
            
            //get node from the queue which wasn't visited
            int v = fifo.get();

            //crawl all edges that connects the current node
            for(Edge e : getNodeEdges(graph, v)) {
                
                //return the other side of the node v
                int w = getOpositeEdgeNode(e, v);
                
                //check if there is residual capactiy c(v, w) > 0
                //residual capacity is the remaining "not occupied" capacity
                if(residualCapacity(e, w) > 0){
                    
                    //check if w node wasn't already visited
                    if(!visited[w]){
                        
                        //mark the w node as visited
                        visited[w] = true;
                        
                        //add the edge as residual edge
                        edgeTo[w] = e;
                        
                        //add the w node to the queue
                        fifo.add(w);
                    }                    
                }
            }
        }
        //if true, it means we there is augmented path to the target node t
        return visited[t];
    }
    
    // return residual capacity for edge e and node w
    public int residualCapacity(Edge e, int w){
        
        if (w == e.getSource().getId()) {
            return Integer.parseInt(e.getEdgeData().getAttributes().getValue("Flow").toString());
        } else if (w == e.getTarget().getId()) {
            return Integer.parseInt(e.getEdgeData().getAttributes().getValue("Capacity").toString()) - Integer.parseInt(e.getEdgeData().getAttributes().getValue("Flow").toString());
        } else{
            throw new RuntimeException("Illegal endpoint for method residualCapacity");
        }
    }
    
    // return list of edges for a node
    public EdgeIterable getNodeEdges(Graph graph, int node_id){
        EdgeIterable edges = graph.getEdges(graph.getNode(node_id));
        return edges;
    }
    
    //return oposite node of an edge node
    public int getOpositeEdgeNode(Edge e, int v){
        int source = e.getSource().getId();
        int target = e.getTarget().getId();
        
        if      (v == source) return target;
        else if (v == target) return source;
        else throw new RuntimeException("Illegal endpoint for method Get local target");
    }
    
    // augment flow of an edge
    public void addResidualFlowTo(Edge e, int v, int bottle) {
        int source = e.getSource().getId();
        int target = e.getTarget().getId();
        int flow = Integer.parseInt(e.getEdgeData().getAttributes().getValue("Flow").toString());
        
        if (v == source){
            e.getEdgeData().getAttributes().setValue("Flow", flow-bottle);            
        } else if (v == target) {
            e.getEdgeData().getAttributes().setValue("Flow", flow+bottle);            
        } else throw new RuntimeException("Illegal endpoint");
    }
}