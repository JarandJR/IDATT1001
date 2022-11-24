package Øving9;

import java.io.*;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Oving9AlgDat {
    public static void main(String[] args) {
        System.out.println("starting filereading");
        Graph g = null;
        try {
            BufferedReader brNodes = new BufferedReader(new FileReader("src/Øving9/noder.txt"));
            BufferedReader brEdges = new BufferedReader(new FileReader("src/Øving9/kanter.txt"));
            BufferedReader brPoints = new BufferedReader(new FileReader("src/Øving9/interessepkt.txt"));
            String preProcessedFilename = "src/Øving9/Norden-preprocessed.txt";
            int[] landmarks = {3007953};

            g = new Graph(preProcessedFilename/*, landmarks*/, brNodes, brEdges, brPoints);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished reading\n\nstarting dijkstra");

        int startNode = 232073;
        int endNode = 2518780;
        g.dijkstra(g.getNodes()[startNode]);
        System.out.println("Finished Dijkstra");

        int dijkstraDist = ((Prev)g.getNodes()[endNode].getData()).getDist();
        int nodesVisitedD = countNodes(g);

        g.altAlgorithm(g.getNodes()[startNode], g.getNodes()[endNode]);
        int nodesVisitedALT = countNodes(g);

        System.out.println("Alt-route: \n");
        Node n =g.getNodes()[endNode];
        Prev p;
        while(n!=null){
            System.out.println(n);
            p=(Prev)n.getData();
            n=p.getPrev();
        }
        int altDist = ((Prev)g.getNodes()[endNode].getData()).getDist();

        System.out.println();
        System.out.println("dijkstra / ALT\n" + nodesVisitedD + " / " + nodesVisitedALT);
        System.out.println("Time dijkstra: "+formatSeconds(dijkstraDist/100));
        System.out.println("Time ALT: "+formatSeconds(altDist/100));
        System.out.println("finished djikstra\n");
    }

    private static int countNodes(Graph g) {
        int counter = 0;
        for (int i = 0; i < g.getN(); i++)
            if (g.getVisited()[i]) counter++;

        return counter;
    }

    private static String formatSeconds(int timeInSeconds) {
        int secondsLeft = timeInSeconds % 3600 % 60;
        int minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        int hours = (int) Math.floor(timeInSeconds / 3600);

        String HH = ((hours       < 10) ? "0" : "") + hours;
        String MM = ((minutes     < 10) ? "0" : "") + minutes;
        String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;

        return HH + ":" + MM + ":" + SS;
    }
}

class Graph {
    private int N, K, I;
    private Node[] nodes;
    private boolean[] found;
    private boolean[] visited;
    private int[] landmarks;
    private int[][] toLandmark;
    private int[][] fromLandmark;
    private PriorityQueue<Node> pq;

    public Graph(String preProcessedFileName, int[] landmarks, BufferedReader brNodes, BufferedReader brEdges, BufferedReader brPointsOfInterests) throws IOException {
        makeNewGraph(brNodes, brEdges, brPointsOfInterests);
        this.landmarks = landmarks;
        if (!new File(preProcessedFileName).exists())
            preProcessData(preProcessedFileName);
        else
            loadPreProcessedData(preProcessedFileName);
    }

    public Graph(String preProcessedFileName, BufferedReader brNodes, BufferedReader brEdges, BufferedReader brPointsOfInterests) throws IOException {
        makeNewGraph(brNodes, brEdges, brPointsOfInterests);
        if (!new File(preProcessedFileName).exists())
            preProcessData(preProcessedFileName);
        else
            loadPreProcessedData(preProcessedFileName);
    }

    public Graph(int n, int k, int i) {
        this.N = n;
        this.K = k;
        this.I = i;
    }

    private void preProcessData(String fileName) throws IOException {
        fromLandmark = makePreprocessedArray(this);
        Graph transposedGraph = getTransposedGraph();
        transposedGraph.setLandmarks(this.landmarks);
        toLandmark = makePreprocessedArrayTransposed(transposedGraph, pq);
        writePreprocessedDataToFile(fileName);
    }

    public int[][] makePreprocessedArray(Graph g) {
        int[][] data = new int[g.getLandmarks().length][g.getNodes().length];

        for (int i = 0; i < g.getLandmarks().length; i++) {
            g.dijkstra(g.getNodes()[g.getLandmarks()[i]]);
            for (int j = 0; j < g.getNodes().length; j++)
                data[i][j] = ((Prev)g.getNodes()[j].getData()).getDistance();
        }
        return data;
    }

    public int[][] makePreprocessedArrayTransposed(Graph g, PriorityQueue<Node> pq) {
        int[][] data = new int[g.getLandmarks().length][g.getNodes().length];

        for (int i = 0; i < g.getLandmarks().length; i++) {
            g.dijkstraTransposed(g.getNodes()[g.getLandmarks()[i]], pq);
            for (int j = 0; j < g.getNodes().length; j++)
                data[i][j] = ((Prev)g.getNodes()[j].getData()).getDistance();
        }
        return data;
    }

    private void writePreprocessedDataToFile(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            for (int landmark : landmarks) fw.write(landmark + "   ");
            fw.write("\n");

            for (int j = 0; j < N; j++) {
                for (int i = 0; i < landmarks.length; i++)
                    fw.write(fromLandmark[i][j] + "    ");
                fw.write("\n");
            }
            for (int j = 0; j < N; j++) {
                for (int i = 0; i < landmarks.length; i++)
                    fw.write(toLandmark[i][j] + "    ");
                fw.write("\n");
            }
        } catch (IOException e) {
            throw new IOException("Something went wrong when writing preprocessed data to file");
        }
    }

    private Graph getTransposedGraph() {
        Graph transposed = new Graph(this.N, this.K, this.I);
        Node[] nodes = new Node[transposed.getN()];
        for (int i = 0; i < transposed.getN(); i++)
            nodes[i] = new Node(i, this.nodes[i].getLatitude(), this.nodes[i].getLongitude());
        transposed.setNodes(nodes);

        for (int i = 0; i < N; i++) {
            for (WEdge w = (WEdge) this.nodes[i].getFirstEdge(); w != null; w = (WEdge) w.getNext()){
                int to = this.nodes[i].getIndex();
                int from = w.getTo().getIndex();
                int weight = w.getWeight();
                transposed.getNodes()[from].setFirstEdge(new WEdge(transposed.getNodes()[to], (WEdge) transposed.getNodes()[from].getFirstEdge(), weight));
            }
        }
        return transposed;
    }

    private void loadPreProcessedData(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int numberOfLandmarks = st.countTokens();
        landmarks = new int[numberOfLandmarks];

        for (int i = 0; i < numberOfLandmarks; i++) {
            int landmark = Integer.parseInt(st.nextToken());
            landmarks[i] = landmark;
        }

        fromLandmark = readData(br, numberOfLandmarks);
        toLandmark = readData(br, numberOfLandmarks);
    }

    private int[][] readData(BufferedReader br, int numberOfLandmarks) throws IOException {
        int[][] data = new int[numberOfLandmarks][N];
        for (int j = 0; j < N; j++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int i = 0; i < numberOfLandmarks; i++)
                data[i][j] = Integer.parseInt(st.nextToken());
        }
        return data;
    }

    private void makeNewGraph(BufferedReader brNodes, BufferedReader brEdges, BufferedReader brPointsOfInterests)throws IOException {
        readNodes(brNodes);
        readEdges(brEdges);
        readPointsOfInterests(brPointsOfInterests);
    }

    private void readNodes(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new Node[N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            int value = Integer.parseInt(st.nextToken());
            double latitude = Double.parseDouble(st.nextToken());
            double longitude = Double.parseDouble(st.nextToken());
            nodes[i] = new Node(value,latitude,longitude);
        }
    }

    private void readEdges(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            int weight = Integer.parseInt(st.nextToken());
            WEdge w = new WEdge(nodes[to], (WEdge) nodes[from].getFirstEdge(), weight);
            nodes[from].setFirstEdge(w);
        }
    }

    private void readPointsOfInterests(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        I = Integer.parseInt(st.nextToken());
        for (int i = 0; i < I; i++) {
            st = new StringTokenizer(br.readLine());
            int nodeIndex = Integer.parseInt(st.nextToken());
            int category = Integer.parseInt(st.nextToken());
            String name = st.nextToken();
            nodes[nodeIndex].setBitCategory(category);
            nodes[nodeIndex].setName(name);
        }
    }

    public void dijkstra(Node s) {
        visited = new boolean[N];
        found = new boolean[N];

        initPrev(s);
        pq = makePrioQue(s);
        found[s.getIndex()] = true;
        while(!pq.isEmpty()){
            Node n = pq.poll();
            visited[n.getIndex()] = true;
            for(WEdge w = (WEdge)n.getFirstEdge(); w!= null; w=(WEdge) w.getNext())
                shorten(n , w, pq);
        }
    }

    public void dijkstraTransposed(Node s, PriorityQueue<Node> pq) {
        visited = new boolean[N];
        found = new boolean[N];

        initPrev(s);
        pq.clear();
        pq.add(s);
        found[s.getIndex()] = true;
        while(!pq.isEmpty()){
            Node n = pq.poll();
            visited[n.getIndex()] = true;
            for(WEdge w = (WEdge)n.getFirstEdge(); w!= null; w=(WEdge) w.getNext())
                shorten(n , w, pq);
        }
    }

    private void shorten(Node n, WEdge w, PriorityQueue<Node> pq){
        Prev nd = (Prev)n.getData();
        Prev md = (Prev)w.getTo().getData();
        if(!found[w.getTo().getIndex()]){
            pq.add(w.getTo());
            found[w.getTo().getIndex()] = true;
        }
        if(md.getDistance() > nd.getDistance() + w.getWeight()){
            md.setDist(nd.getDistance() + w.getWeight());
            md.setPrev(n);
            pq.remove(w.getTo());
            pq.add(w.getTo());
        }
    }

    private PriorityQueue<Node> makePrioQue(Node s){
        pq = new PriorityQueue<>(N, (a, b) -> ((Prev) a.getData()).getDistance() - ((Prev) b.getData()).getDistance());
        pq.add(s);
        return pq;
    }

    private void initPrev(Node s) {
        for (int i = N; i-- > 0;)
            nodes[i].setData(new Prev());
        ((Prev)s.getData()).setDist(0);
    }

    public void altAlgorithm(Node s, Node endNode) {
        visited = new boolean[N];
        found = new boolean[N];

        initPrev(s);
        pq = makePrioQue(s);
        found[s.getIndex()] = true;
        while(!visited[endNode.getIndex()] && !pq.isEmpty()){
            Node n = pq.poll();
            visited[n.getIndex()] = true;
            for(WEdge w = (WEdge)n.getFirstEdge(); w!= null; w=(WEdge) w.getNext()) {
                shorten(n, w, endNode);
            }
        }
    }

    private void shorten(Node n, WEdge w, Node endNode){
        Prev nd = (Prev)n.getData();
        Prev md = (Prev)w.getTo().getData();
        if(!found[w.getTo().getIndex()]){
            calculateEstimate(w.getTo(), endNode);
            pq.add(w.getTo());
            found[w.getTo().getIndex()] = true;
        }
        if(md.getDistance() > nd.getDistance() + w.getWeight()){
            md.setDist(nd.getDist() + w.getWeight());
            md.setPrev(n);
            pq.remove(w.getTo());
            pq.add(w.getTo());
        }
    }


    private void calculateEstimate(Node n, Node endNode) {
        int largestEstimate = 0;
        int previous = -1;
        for (int i = 0; i < landmarks.length; i++) {
            int estimateFromLandmark = fromLandmark[i][endNode.getIndex()] - fromLandmark[i][n.getIndex()];
            int estimateToLandmark = toLandmark[i][n.getIndex()] - toLandmark[i][endNode.getIndex()];
            largestEstimate = Math.max(estimateToLandmark, estimateFromLandmark);
            if (previous > largestEstimate) largestEstimate = previous;
            previous = largestEstimate;
        }
        if (largestEstimate > 0) ((Prev)n.getData()).setEstimate(largestEstimate);
    }

    public Node[] getNodes() {
        return nodes;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int getI() {
        return I;
    }

    public void setI(int i) {
        I = i;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public int[] getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(int[] landmarks) {
        this.landmarks = landmarks;
    }

    public boolean[] getVisited() {
        return visited;
    }
}

class Prev {
    private int dist;
    private Node prev;
    private int estimate;
    static int inf = 1_000_000_000;

    public Prev() {
        dist = inf;
        estimate = 0;
    }

    public int getDistance() {
        return dist + estimate;
    }

    public int getDist() {
        return dist;
    }

    public Node getPrev() {
        return prev;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }
}

class WEdge extends Edge{
    private int weight;

    public WEdge(Node to, WEdge next, int weight) {
        super(next, to);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}

class Edge {
    Edge next;
    Node to;

    public Edge(Edge next, Node to) {
        this.next = next;
        this.to = to;
    }

    public Edge getNext() {
        return next;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}

class Node {
    private int index;
    private Edge edge1;
    private Object data;
    private String name;
    private int bitCategory;
    private double latitude;
    private double longitude;

    public Node(int i, double latitude, double longitude) {
        this.name = "";
        this.index = i;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Edge getFirstEdge() {
        return edge1;
    }

    public void setFirstEdge(Edge edge1) {
        this.edge1 = edge1;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getBitCategory() {
        return bitCategory;
    }

    public void setBitCategory(int bitCategory) {
        this.bitCategory = bitCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
