package Øving9;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Oving9AlgDat {
    public static void main(String[] args) {

    }
}

class Graph {
    private int N, K;
    Node[] nodes;

    public Graph(BufferedReader br) {
        try {
            makeNewGraph(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeNewGraph(BufferedReader br)throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N=Integer.parseInt(st.nextToken());
        nodes=new Node[N];
        for(int i = 0; i<N; i++) nodes[i] = new Node();
        K=Integer.parseInt(st.nextToken());
        for(int i = 0; i<K; i++){
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            int weight = Integer.parseInt(st.nextToken());
            WEdge w = new WEdge(nodes[from],(WEdge) nodes[to].getFirstEdge(),weight);
            nodes[from].setFirstEdge(w);
        }
    }

    public void djikstra(Node start) {
        initPrev(start);
        PriorityQueue<Node> pq = makePrioQue();

        for (int i = N; i > 1; --i) {
            Node n = pq.poll();//getMin(i, prio);
            for (WEdge e = (WEdge)n.getFirstEdge(); e != null; e = e.getNext())
                shorten(n, e);
        }
    }

    private void shorten(Node n, WEdge e){
        Prev nd = (Prev)n.getData(), md=(Prev)e.getTo().getData();
        if(md.getDistance() > nd.getDistance() + e.getWeight()){
            md.setDist(nd.getDistance() + e.getWeight());
            md.setPrev(n);
        }
    }

    private PriorityQueue<Node> makePrioQue(){
        PriorityQueue<Node> pq = new PriorityQueue<>(this.N,(a,b)->((Prev)a.getData()).getDistance() - ((Prev)b.getData()).getDistance());
        for(int i = 0; i<N; i++){
            pq.add(nodes[i]);
        }
        return pq;
    }

    private void initPrev(Node start) {
        for (int i = N; i-- > 0;)
            nodes[i].setData(new Prev());
        ((Prev)start.getData()).setDist(0);
    }
}

class Prev {
    private int dist;
    private Node prev;
    static int inf = 1_000_000_000;

    public Prev() {
        dist = inf;
    }

    public int getDistance() {
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
}

class WEdge extends Edge{
    private WEdge next;
    private int weight;

    public WEdge(Node to, WEdge next, int weight) {
        super(next, to);
        this.weight = weight;
    }

    public WEdge getNext() {
        return next;
    }

    public void setNext(WEdge next) {
        this.next = next;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

class Edge {
    private Edge next;
    private Node to;

    public Edge(Edge next, Node to) {
        this.next = next;
        this.to = to;
    }

    public Edge getNext() {
        return next;
    }

    public void setNext(Edge next) {
        this.next = next;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}

class Node {
    private Edge edge1;
    private Object data;

    public Node() {
    }

    public Node(Edge edge1, Object data) {
        this.edge1 = edge1;
        this.data = data;
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
}
