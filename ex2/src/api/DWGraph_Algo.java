package api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph g;

    /*
     *Constructor method which initializes as new WGraph_DS object the directed_weighted_graph g parameter .
     */
    public DWGraph_Algo() {
        g = new DWGraph_DS();
    }

    /*
     *Method which make the parameter this.g to point on the inputted graph g .
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.g = g;
    }

    /*
     *Method which returns a pointer to the this.g graph this class is working on .
     */
    @Override
    public directed_weighted_graph getGraph() {
        if (this.g == null) {
            return null;
        }
        return this.g;
    }

    /*
     *Compute a deep copy of this weighted graph.
     */
    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph g1 = new DWGraph_DS();
        copyNodes(g1);
        copyEdges(g1);
        return g1;

    }

    /*
     *Method hard copy the nodes from the graph contaied in this class to the given target graph.
     */
    private void copyNodes(directed_weighted_graph target) {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            node_data fromNode = itr.next();
            node_data copiedNode = copyNode(fromNode);
            target.addNode(copiedNode);

        }
    }

    /*
     *Method hard copy the given node and returns it.
     */
    private node_data copyNode(node_data fromNode) {
        node_data copiedNode = new Node(fromNode.getKey());
        copiedNode.setInfo(fromNode.getInfo() + "");
        if (fromNode.getLocation() != null) {
            copiedNode.setLocation(new Location(fromNode.getLocation().x(), fromNode.getLocation().y(), fromNode.getLocation().z()));
        }
        copiedNode.setTag(fromNode.getTag());
        copiedNode.setWeight(fromNode.getWeight());
        return copiedNode;

    }

    /*
     *Method performs a copy of the edges the contained in this class graph to the given graph.
     */
    private void copyEdges(directed_weighted_graph target) {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            Iterator<edge_data> itr1 = this.g.getE(itr.next().getKey()).iterator();
            while (itr1.hasNext()) {
                edge_data fromEdge = itr1.next();
                target.connect(fromEdge.getSrc(), fromEdge.getDest(), fromEdge.getWeight());
                target.getEdge(fromEdge.getSrc(), fromEdge.getDest()).setInfo(fromEdge.getInfo());
                target.getEdge(fromEdge.getSrc(), fromEdge.getDest()).setTag(fromEdge.getTag());

            }
        }
    }

    /*
     *Returns true if and only if (iff) there is a valid path from each node to each other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     */
    @Override
    public boolean isConnected() {
        if (this.g.nodeSize() == 0 || this.g.nodeSize() == 1 || this.g.edgeSize() == this.g.nodeSize() * (this.g.nodeSize() - 1)) {
            return true;
        }
        if (this.g.edgeSize() < this.g.nodeSize()) {
            return false;
        }
        defaultValuesForEachNode();
        node_data sourceLocation = this.g.getV().iterator().next();
        DFSFromNode(sourceLocation);
        if (someNodeWasNotSeenByTheDFSAlgorithm()) {

            defaultValuesForEachNode();
            return false;
        }
        ((DWGraph_DS) (this.g)).Transpose();
        defaultValuesForEachNode();
        DFSFromNode(sourceLocation);
        if (someNodeWasNotSeenByTheDFSAlgorithm()) {
            ((DWGraph_DS) (this.g)).Transpose();
            defaultValuesForEachNode();

            return false;
        }
        ((DWGraph_DS) (this.g)).Transpose();
        defaultValuesForEachNode();
        return true;
    }

    /*
     *Method which implements the DFS algorithm and returns the time on which the algorithm finished his job.
     */
    private Integer DFSFromNode(node_data src) {
        defaultValuesForEachNode();
        Integer currentTime = new Integer(0);
        Stack<node_data> stk = new Stack<node_data>();

        helpDFS(src, currentTime, stk);
        return currentTime;
    }

    /*
     *Returns the last node the DFS algorithm worked on, returns null in case there is no such thing .
     */
    private node_data getTheNodeWithHighestEndTime(Integer highestTime) {

        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            node_data node = itr.next();
            if (node.getTag() == highestTime.intValue()) {
                return node;
            }
        }
        return null;
    }

    /*
     *Sets all the Tag's and Info's parameters of each node to it's default value .
     */
    private void defaultValuesForEachNode() {
        defaultTagForEachNode();
        defaultInfoForEachNode();
    }

    /*
     *Sets all the Tag parameters of each node to their default value .
     */
    private void defaultTagForEachNode() {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            node_data defaultedNode = itr.next();
            setTagToDefault(defaultedNode);
        }
    }

    /*
     *Sets all the Info parameters of each node to their default value .
     */
    private void defaultInfoForEachNode() {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            node_data defaultedNode = itr.next();
            setInfoToDefault(defaultedNode);
        }
    }

    /*
     *Sets the Tag parameter of the inputted node to default value.
     */
    private void setTagToDefault(node_data defaultedNode) {
        defaultedNode.setTag(-1);
    }

    /*
     *Sets the Info parameter of the inputted node to default value.
     */
    private void setInfoToDefault(node_data defaultedNode) {
        defaultedNode.setInfo(new String(""));
    }

    /*
     *Straightly implements the DFS algorithm .
     */
    private void helpDFS(node_data src, Integer currentTime, Stack<node_data> stk) {

        if (src.getInfo().equals("P")) {
            return;
        }
        currentTime++;
        src.setTag(currentTime);
        src.setInfo("P");
        stk.push(src);
        Iterator<edge_data> itr = this.g.getE(src.getKey()).iterator();
        while (itr.hasNext()) {

            edge_data edge = itr.next();
            if (edge == null) {

                continue;
            }
            if (((DWGraph_DS) (this.g)).getTranspose()) {
                helpDFS(this.g.getNode(edge.getSrc()), currentTime, stk);
            } else {
                helpDFS(this.g.getNode(edge.getDest()), currentTime, stk);
            }
        }

        currentTime++;
        src.setInfo("P");
        src.setTag(currentTime);
        stk.pop();
    }

    /*
     *Boolean method which checks if there are still some nodes left untouched after running the DFS algorithm the determine the connectivity of the graph .
     */
    private boolean someNodeWasNotSeenByTheDFSAlgorithm() {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            if (itr.next().getInfo().equals("")) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (this.g.getNode(src) == null || this.g.getNode(dest) == null) {
            return -1;
        }
        if (this.g.getEdge(src, dest) == null) {
            return -1;
        }
        if (src == dest) {
            return 0;
        }
        defaultValuesForEachNode();
        DFSFromNode(this.g.getNode(src));
        if (this.g.getNode(dest).getInfo().equals("")) {
            defaultValuesForEachNode();
            return -1;
        }
        defaultValuesForEachNode();
        Dijkstra(src);
        double answer = this.g.getNode(dest).getWeight();
        defaultValuesForEachNode();
        setParentOfEveryNodeInGraphToDefault();
        return answer;
    }

    private void Dijkstra(int src) {
        setTheDistancesToInf();
        this.g.getNode(src).setTag(0);
        LinkedList<node_data> queue = new LinkedList<node_data>();
        ((Node) (this.g.getNode(src))).setParent(null);
        queue.addLast(this.g.getNode(src));
        while (!queue.isEmpty()) {
            node_data parent = queue.pop();
            addToQueueEachAdjacentNodeWhichIsConnectedSomehowToTheDest(queue, parent);
        }
    }

    /*
     *  Helping method to the Dijkstra(int src) method , which is marking all the tags of all nodes in the graph to infinity .
     */
    private void setTheDistancesToInf() {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            itr.next().setWeight(Double.POSITIVE_INFINITY);
        }
    }
    /*
     *Helping method for Dijkstra(int src) method which adds new nodes to the algorithm queue dependently if its tag parameter is lower than the tag of parent summarized with the weight of the node between them and if the node is determined as valid by the previously produced BFS algorithm. Mainly exist to make Dijkstra(int src) method more readable .
     */

    private void addToQueueEachAdjacentNodeWhichIsConnectedSomehowToTheDest(LinkedList<node_data> queue, node_data parent) {
        Iterator<edge_data> itr = this.g.getE(parent.getKey()).iterator();
        while (itr.hasNext()) {
            node_data node = this.g.getNode(itr.next().getDest());
            if (queue.contains(node)) {
                node = queue.get(queue.indexOf(node));
            }
            double distance = parent.getWeight() + this.g.getEdge(parent.getKey(), node.getKey()).getWeight();
            if (distance < node.getWeight()) {
                node.setWeight(distance);
                ((Node) (node)).setParent(parent);
                queue.addLast(node);
            }
        }

    }

    /*
     *  Helper method that it used after the Dijkstra algorithm to set all the parent parameters to default value null .
     */
    private void setParentOfEveryNodeInGraphToDefault() {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            ((Node) (itr.next())).setParent(null);
        }
    }


    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        return null;

    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     *
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        return false;

    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     *
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        return false;
    }
}
