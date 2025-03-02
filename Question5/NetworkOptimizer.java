package Question5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class NetworkOptimizer extends JFrame {
    private JPanel graphPanel;
    private JTextField nodeField, costField, bandwidthField;
    private JButton addNodeButton, addEdgeButton, optimizeButton, shortestPathButton, reloadButton;
    private JLabel resultLabel;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private Node selectedNode1, selectedNode2;

    public NetworkOptimizer() {
        setTitle("Network Optimization Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setBackground(Color.WHITE);
        graphPanel.addMouseListener(new NodeSelectionListener());

        nodeField = new JTextField(10);
        costField = new JTextField(10);
        bandwidthField = new JTextField(10);
        addNodeButton = new JButton("Add Node");
        addEdgeButton = new JButton("Add Edge");
        optimizeButton = new JButton("Optimize Network");
        shortestPathButton = new JButton("Find Shortest Path");
        reloadButton = new JButton("Reload");
        resultLabel = new JLabel("Results will be shown here.");

        // Add components to the frame
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Node ID:"));
        controlPanel.add(nodeField);
        controlPanel.add(addNodeButton);
        controlPanel.add(new JLabel("Cost:"));
        controlPanel.add(costField);
        controlPanel.add(new JLabel("Bandwidth:"));
        controlPanel.add(bandwidthField);
        controlPanel.add(addEdgeButton);
        controlPanel.add(optimizeButton);
        controlPanel.add(shortestPathButton);
        controlPanel.add(reloadButton);
        add(controlPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        // Add event listeners
        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        optimizeButton.addActionListener(e -> optimizeNetwork());
        shortestPathButton.addActionListener(e -> findShortestPath());
        reloadButton.addActionListener(e -> reload());

        setVisible(true);
    }

    private void addNode() {
        String nodeId = nodeField.getText();
        if (!nodeId.isEmpty()) {
            nodes.add(new Node(nodeId, new Point((int) (Math.random() * 700), (int) (Math.random() * 500))));
            graphPanel.repaint();
            nodeField.setText("");
        }
    }

    private void addEdge() {
        if (selectedNode1 != null && selectedNode2 != null) {
            int cost = Integer.parseInt(costField.getText());
            int bandwidth = Integer.parseInt(bandwidthField.getText());
            edges.add(new Edge(selectedNode1, selectedNode2, cost, bandwidth));
            graphPanel.repaint();
            costField.setText("");
            bandwidthField.setText("");
        }
    }

    private void optimizeNetwork() {
        ArrayList<Edge> mst = kruskalMST();
        edges.clear();
        edges.addAll(mst);
        graphPanel.repaint();
        resultLabel.setText("Optimized Network (MST) created.");
    }

    private void findShortestPath() {
        if (selectedNode1 != null && selectedNode2 != null) {
            Map<Node, Integer> distances = dijkstra(selectedNode1);
            int distance = distances.get(selectedNode2);
            resultLabel.setText("Shortest path from " + selectedNode1.id + " to " + selectedNode2.id + ": " + distance);
        }
    }

    private void reload() {
        nodes.clear();
        edges.clear();
        selectedNode1 = null;
        selectedNode2 = null;
        graphPanel.repaint();
        resultLabel.setText("Network has been reset.");
    }

    private ArrayList<Edge> kruskalMST() {
        ArrayList<Edge> mst = new ArrayList<>();
        Collections.sort(edges, Comparator.comparingInt(e -> e.cost));
        DisjointSet<Node> ds = new DisjointSet<>();
        for (Node node : nodes) {
            ds.makeSet(node);
        }
        for (Edge edge : edges) {
            if (ds.findSet(edge.node1) != ds.findSet(edge.node2)) {
                mst.add(edge);
                ds.union(edge.node1, edge.node2);
            }
        }
        return mst;
    }

    private Map<Node, Integer> dijkstra(Node start) {
        Map<Node, Integer> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            for (Edge edge : edges) {
                if (edge.node1 == current || edge.node2 == current) {
                    Node neighbor = (edge.node1 == current) ? edge.node2 : edge.node1;
                    int newDist = distances.get(current) + edge.bandwidth;
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        pq.add(neighbor);
                    }
                }
            }
        }
        return distances;
    }

    private void drawGraph(Graphics g) {
        for (Edge edge : edges) {
            g.drawLine(edge.node1.position.x, edge.node1.position.y, edge.node2.position.x, edge.node2.position.y);
            g.drawString(edge.cost + "/" + edge.bandwidth, (edge.node1.position.x + edge.node2.position.x) / 2, (edge.node1.position.y + edge.node2.position.y) / 2);
        }
        for (Node node : nodes) {
            g.setColor(node == selectedNode1 || node == selectedNode2 ? Color.RED : Color.BLUE);
            g.fillOval(node.position.x - 10, node.position.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(node.id, node.position.x - 10, node.position.y - 15);
        }
    }

    private class NodeSelectionListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            for (Node node : nodes) {
                if (Math.abs(e.getX() - node.position.x) < 10 && Math.abs(e.getY() - node.position.y) < 10) {
                    if (selectedNode1 == null) {
                        selectedNode1 = node;
                    } else if (selectedNode2 == null) {
                        selectedNode2 = node;
                    } else {
                        selectedNode1 = node;
                        selectedNode2 = null;
                    }
                    graphPanel.repaint();
                    break;
                }
            }
        }
    }

    private static class Node {
        String id;
        Point position;

        Node(String id, Point position) {
            this.id = id;
            this.position = position;
        }
    }

    private static class Edge {
        Node node1, node2;
        int cost, bandwidth;

        Edge(Node node1, Node node2, int cost, int bandwidth) {
            this.node1 = node1;
            this.node2 = node2;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    private static class DisjointSet<T> {
        private Map<T, T> parent = new HashMap<>();

        void makeSet(T x) {
            parent.put(x, x);
        }

        T findSet(T x) {
            if (parent.get(x) == x) {
                return x;
            }
            parent.put(x, findSet(parent.get(x)));
            return parent.get(x);
        }

        void union(T x, T y) {
            T rootX = findSet(x);
            T rootY = findSet(y);
            if (rootX != rootY) {
                parent.put(rootX, rootY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkOptimizer::new);
    }
}