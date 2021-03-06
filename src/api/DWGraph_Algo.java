package api;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph g;

    /*
     *Constructor method which initializes as new WGraph_DS object the directed_weighted_graph g parameter .
     */
    public DWGraph_Algo() {
        this.init(new DWGraph_DS());
    }

    /**
     *Method which make the parameter this.g to point on the inputted graph g .
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.g = g;
        Tarjan_Node.nodes = new HashMap<>();
    }

    /**
     *Method which returns a pointer to the this.g graph this class is working on .
     */
    @Override
    public directed_weighted_graph getGraph() {
        if (this.g == null) {
            return null;
        }
        return this.g;
    }

    /**
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
            geo_location location = new Location(fromNode.getLocation().x(), fromNode.getLocation().y(), fromNode.getLocation().z());
            copiedNode.setLocation(location);
        }
        copiedNode.setTag(fromNode.getTag());
        copiedNode.setWeight(fromNode.getWeight());
        return copiedNode;
    }

    /*
     *Method performs a copy of the edges the contained in this class graph to the given graph.
     */
    private void copyEdges(directed_weighted_graph target) {
        for (api.node_data node_data : this.g.getV()) {
            for (edge_data fromEdge : this.g.getE(node_data.getKey())) {
                target.connect(fromEdge.getSrc(), fromEdge.getDest(), fromEdge.getWeight());
                target.getEdge(fromEdge.getSrc(), fromEdge.getDest()).setInfo(fromEdge.getInfo());
                target.getEdge(fromEdge.getSrc(), fromEdge.getDest()).setTag(fromEdge.getTag());
            }
        }
    }

    /**
     *Returns true if and only if (iff) there is a valid path from each node to each other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     */
    @Override
    public boolean isConnected() {
        if (this.g.nodeSize() == 0 || this.g.nodeSize() == 1 || this.g.edgeSize() == this.g.nodeSize() * (this.g.nodeSize() - 1)) {
            return true;
        }
        if (this.g.edgeSize() < this.g.nodeSize() - 1) {
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
    private void DFSFromNode(node_data src) {
        int counter = 0;
        Stack<node_data> stk = new Stack<node_data>();
        stk.push(src);
        boolean transpose = ((DWGraph_DS) (this.g)).getTranspose();
        if (!transpose) {
            while (!stk.isEmpty()) {
                node_data peekedFromStack = stk.peek();
                if (peekedFromStack.getInfo().equals("")) {
                    peekedFromStack.setTag(counter);
                    peekedFromStack.setInfo("P");
                    counter++;
                    for (edge_data edge : this.g.getE(peekedFromStack.getKey())) {
                        if (edge == null) {
                            continue;
                        }
                        if (this.g.getNode(edge.getDest()).getTag() > -1) {
                            continue;
                        }
                        stk.push(this.g.getNode(edge.getDest()));
                    }
                } else {
                    stk.pop().setTag(counter);
                    counter++;
                }
            }
        } else {
            while (!stk.isEmpty()) {
                node_data peekedFromStack = stk.peek();
                if (peekedFromStack.getInfo().equals("")) {
                    peekedFromStack.setTag(counter);
                    peekedFromStack.setInfo("P");
                    counter++;
                    for (edge_data edge : this.g.getE(peekedFromStack.getKey())) {
                        if (edge == null) {
                            continue;
                        }
                        if (this.g.getNode(edge.getSrc()).getTag() > -1) {
                            continue;
                        }
                        stk.push(this.g.getNode(edge.getSrc()));
                    }
                } else {
                    stk.pop().setTag(counter);
                    counter++;
                }
            }
        }
    }
    private void defaultValuesForEachNode() {
        defaultTagForEachNode();
        defaultInfoForEachNode();
        defaultWeightForEachNode();
    }

    /*
     *Sets all the Tag's and Info's parameters of each node to it's default value .
     */
    private void defaultTagAndInfoForEachNode() {
        defaultTagForEachNode();
        defaultInfoForEachNode();
    }

    /*
     *Sets all the Tag parameters of each node to their default value .
     */
    private void defaultTagForEachNode() {
        for (node_data defaultedNode : this.g.getV()) {
            setTagToDefault(defaultedNode);
        }
    }

    /*
     *Sets all the Info parameters of each node to their default value .
     */
    private void defaultInfoForEachNode() {
        for (node_data defaultedNode : this.g.getV()) {
            setInfoToDefault(defaultedNode);
        }
    }

    /*
     *Sets all node's weight parameter to default value.
     */
    private void defaultWeightForEachNode() {
        for (node_data defaultedNode : this.g.getV()) {
            setWeightToDefault(defaultedNode);
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
     *Sets specific node weight value to default.
     */
    private void setWeightToDefault(node_data defaultedNode) {
        defaultedNode.setWeight(0.0d);
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
        for (edge_data edge : this.g.getE(src.getKey())) {
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
        for (api.node_data node_data : this.g.getV()) {
            if (node_data.getInfo().equals("")) {
                return true;
            }
        }
        return false;
    }

    /**
     * find all strongly connected components in the graph
     * @return
     */
    public HashMap<Integer, ArrayList<node_data>> findSCC() {
        HashMap<Integer,ArrayList<node_data>> all_scc = new HashMap<>();
        ArrayList<node_data> n_list;
        if (this.g.nodeSize() == 0 || this.g.nodeSize() == 1 || this.g.edgeSize() == this.g.nodeSize() * (this.g.nodeSize() - 1)) {
            n_list = new ArrayList<>(g.getV());
            all_scc.put(0,n_list);
        }
        for (node_data node:g.getV()) {
            new Tarjan_Node(node);
        }
        Tarjan_Node.ids = 0;
        Tarjan_Node.stack = new Stack<>();
        for (Tarjan_Node n:Tarjan_Node.nodes.values()) {
            if (!n.visited) {
                SCC_DFS(n);
            }
        }

        for (Tarjan_Node n:Tarjan_Node.nodes.values()) {
            if (all_scc.containsKey(n.getLow_key())) {
                n_list = all_scc.get(n.getLow_key());
                n_list.add(n.getNode());
            }
            else{
                n_list = new ArrayList<>();
                n_list.add(n.getNode());
                all_scc.put(n.getLow_key(),n_list);
            }
        }
        return all_scc;
    }
    /*
        dfs for finding scc
     */
    private void SCC_DFS(Tarjan_Node at){
        Stack<Tarjan_Node> stack = Tarjan_Node.stack;
        HashMap<Integer,Tarjan_Node> nodes = Tarjan_Node.nodes;
        boolean b = true;
        stack.push(at);
        at.setInStack(true);
        at.id = Tarjan_Node.ids;
        at.low_key = at.id;
        at.setVisited(true);
        Tarjan_Node.ids++;
        Tarjan_Node to;
        for (edge_data edge : g.getE(at.node.getKey())) {
            to = nodes.get(edge.getDest());
            if (!to.visited)
                    SCC_DFS(to);
            if(to.inStack)
                at.low_key = Math.min(at.low_key,to.low_key);
        }
        if (at.low_key == at.id)
            for (to = stack.pop();;to = stack.pop()) {
                to.inStack = false;
                to.low_key = at.id;
                if(to == at)
                    break;
            }
    }
    /**
     *Performs dijkstra algorithm from a specific node for in-game purposes
     * @param _src
     * @return
     */
    public directed_weighted_graph dijkstraForAll(int _src) {
        node_data src = g.getNode(_src);
        if (this.g.getNode(src.getKey()) != null) {
            if (this.g.getNode(src.getKey()).equals(src)) {
                defaultValuesForEachNode();
                Dijkstra(src.getKey());
                defaultTagAndInfoForEachNode();
                dw_graph_algorithms dwgal = new DWGraph_Algo();
                dwgal.init(this.g);
                directed_weighted_graph dwg = dwgal.copy();
                defaultValuesForEachNode();
                setParentOfEveryNodeInGraphToDefault();
                return dwg;
            }
            return null;
        }
        return null;
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
        double answer = shortestPathDistNoParentDefaultingPart(src, dest);
        setParentOfEveryNodeInGraphToDefault();
        return answer;
    }

    private double shortestPathDistNoParentDefaultingPart(int src, int dest) {
        if (this.g.getNode(src) == null || this.g.getNode(dest) == null) {
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
        return answer;
    }

    private void Dijkstra(int src) {
        setTheDistancesToInf();
        this.g.getNode(src).setWeight(0);
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
        for (api.node_data node_data : this.g.getV()) {
            node_data.setWeight(Double.POSITIVE_INFINITY);
        }
    }

    /*
     *Helping method for Dijkstra(int src) method which adds new nodes to the algorithm queue dependently if its tag parameter is lower than the tag of parent summarized with the weight of the node between them and if the node is determined as valid by the previously produced BFS algorithm. Mainly exist to make Dijkstra(int src) method more readable .
     */
    private void addToQueueEachAdjacentNodeWhichIsConnectedSomehowToTheDest(LinkedList<node_data> queue, node_data parent) {
        for (api.edge_data edge_data : this.g.getE(parent.getKey())) {
            node_data node = this.g.getNode(edge_data.getDest());
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
        for (api.node_data node_data : this.g.getV()) {
            ((Node) node_data).setParent(null);
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
        double distance = shortestPathDist(src, dest);
        if (distance == -1) {
            return null;
        }
        List<node_data> list = new LinkedList<node_data>();
        if (src == dest) {
            list.add(this.g.getNode(src));
            return list;
        }
        shortestPathDistNoParentDefaultingPart(src, dest);
        pathBuilder(list, this.g.getNode(dest));
        setParentOfEveryNodeInGraphToDefault();
        return list;
    }

    private void pathBuilder(List<node_data> list, node_data dest) {
        while (((Node) (dest)).getParent() != null) {
            ((LinkedList<node_data>) (list)).addFirst(dest);
            dest = ((Node) (dest)).getParent();
        }
        ((LinkedList<node_data>) (list)).addFirst(dest);
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(this.g, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Reader reader = Files.newBufferedReader(Paths.get(file));
            Node_dataDeserializer node_dataDeserializer = new Node_dataDeserializer();
            gsonBuilder.registerTypeAdapter(node_data.class, node_dataDeserializer);
            Gson gson = gsonBuilder.create();
            this.g = gson.fromJson(reader, DWGraph_DS.class);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // for reading the json input and building a node
    private class Node_dataDeserializer implements JsonDeserializer<node_data> {
        @Override
        public node_data deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Node node = jsonDeserializationContext.deserialize(jsonObject, Node.class);
            return node;
        }
    }

    /**
     * This method load a graph to this graph algorithm,
     * given a String in json format from the game server
     * add create a graph from it.
     *
     * @param g
     */
    public void load_graph(String g) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        json_des data = gson.fromJson(g, json_des.class);
        graph_creator(data);
    }

    // the graph creator given json_des
    private void graph_creator(json_des data) {
        g = new DWGraph_DS();
        Node node;
        for (node_reader n : data.Nodes) {
            node = new Node(n.id);
            node.setLocation(new Location(n.pos));
            g.addNode(node);
        }
        for (edge_reader e : data.Edges) {
            g.connect(e.src, e.dest, e.w);
        }
    }

    // a class that build to be like the graph json from the game server
    private static class json_des {
        private ArrayList<edge_reader> Edges;
        private ArrayList<node_reader> Nodes;


    }

    // subclass for json_des represent an edge
    private class edge_reader {
        private int src;
        private int dest;
        private double w;
    }

    // subclass for json_des represent a node
    private class node_reader {
        private String pos;
        private int id;
    }

    private static class Tarjan_Node{
        static Stack<Tarjan_Node> stack;
        static HashMap<Integer,Tarjan_Node> nodes = new HashMap<>();
        static int ids;
        Node node;
        int id;
        int low_key;
        boolean inStack;
        boolean visited;

        public Tarjan_Node(node_data node) {
            this.node = ((Node)(node));
            this.id = 0;
            this.visited = false;
            this.inStack = false;
            this.low_key = Integer.MAX_VALUE;
            nodes.put(node.getKey(),this);
        }
        public node_data getNode() {
            return node;
        }

        public void setNode(node_data node) {
            this.node = ((Node)(node));
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLow_key() {
            return low_key;
        }

        public void setLow_key(int low_key) {
            this.low_key = low_key;
        }

        public boolean isInStack() {
            return inStack;
        }

        public void setInStack(boolean inStack) {
            this.inStack = inStack;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }
    }
}
