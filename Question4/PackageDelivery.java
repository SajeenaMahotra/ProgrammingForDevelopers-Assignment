package Question4;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PackageDelivery extends JFrame {
    private JTextField packageField;
    private JTextField roadsField;
    private JButton calculateButton;
    private JLabel resultLabel;
    private GraphPanel graphPanel;
    private int[] packages;
    private int[][] roads;

    public PackageDelivery() {
        setTitle("Package Delivery Problem");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel packageLabel = new JLabel("Packages (comma separated):");
        packageField = new JTextField();
        JLabel roadsLabel = new JLabel("Roads (comma separated pairs):");
        roadsField = new JTextField();
        calculateButton = new JButton("Calculate");
        resultLabel = new JLabel("Result will be shown here");


        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateMinimumRoads();
            }
        });

        inputPanel.add(packageLabel);
        inputPanel.add(packageField);
        inputPanel.add(roadsLabel);
        inputPanel.add(roadsField);
        inputPanel.add(new JLabel()); 
        inputPanel.add(calculateButton);
        inputPanel.add(resultLabel);

        graphPanel = new GraphPanel();

       
        add(inputPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private void calculateMinimumRoads() {
        try {
          
            String[] packageValues = packageField.getText().split(",");
            packages = new int[packageValues.length];
            for (int i = 0; i < packageValues.length; i++) {
                packages[i] = Integer.parseInt(packageValues[i].trim());
            }

            String[] roadPairs = roadsField.getText().split(",");
            roads = new int[roadPairs.length / 2][2];
            for (int i = 0; i < roadPairs.length; i += 2) {
                roads[i / 2][0] = Integer.parseInt(roadPairs[i].trim());
                roads[i / 2][1] = Integer.parseInt(roadPairs[i + 1].trim());
            }

            int result = minRoads(packages, roads);
            resultLabel.setText("Minimum roads to traverse: " + result);

            graphPanel.setPackages(packages);
            graphPanel.setRoads(roads);
            graphPanel.repaint();
        } catch (Exception ex) {
            resultLabel.setText("Invalid input. Please check your input format.");
        }
    }

    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length;

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            adj.get(road[0]).add(road[1]);
            adj.get(road[1]).add(road[0]);
        }

        Set<Integer> packageLocations = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                packageLocations.add(i);
            }
        }

        if (packageLocations.isEmpty()) {
            return 0;
        }

        int minRoads = Integer.MAX_VALUE;
        for (int start = 0; start < n; start++) {
            Set<Integer> collected = new HashSet<>();
            int roadsTraversed = 0;

       
            collectPackages(start, 0, 2, adj, packageLocations, collected);


            if (collected.size() == packageLocations.size()) {
                minRoads = Math.min(minRoads, roadsTraversed);
                continue;
            }


            for (int neighbor : adj.get(start)) {
                Set<Integer> tempCollected = new HashSet<>(collected);
                int tempRoads = roadsTraversed + 1; 

        
                collectPackages(neighbor, 0, 2, adj, packageLocations, tempCollected);

        
                if (tempCollected.size() == packageLocations.size()) {
                    minRoads = Math.min(minRoads, tempRoads + 1); 
                }
            }
        }

        return minRoads;
    }

    private static void collectPackages(int current, int distance, int maxDistance, List<List<Integer>> adj, Set<Integer> packageLocations, Set<Integer> collected) {
        if (distance > maxDistance) {
            return;
        }


        if (packageLocations.contains(current)) {
            collected.add(current);
        }

        for (int neighbor : adj.get(current)) {
            collectPackages(neighbor, distance + 1, maxDistance, adj, packageLocations, collected);
        }
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PackageDelivery().setVisible(true);
            }
        });
    }


    class GraphPanel extends JPanel {
        private int[] packages;
        private int[][] roads;

        public void setPackages(int[] packages) {
            this.packages = packages;
        }

        public void setRoads(int[][] roads) {
            this.roads = roads;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (packages == null || roads == null) {
                return;
            }

            int radius = 20; 
            int padding = 50; 
            int width = getWidth();
            int height = getHeight();


            Map<Integer, Point> nodePositions = new HashMap<>();
            int numNodes = packages.length;
            double angleIncrement = 2 * Math.PI / numNodes;
            for (int i = 0; i < numNodes; i++) {
                int x = (int) (width / 2 + (width / 2 - padding) * Math.cos(i * angleIncrement));
                int y = (int) (height / 2 + (height / 2 - padding) * Math.sin(i * angleIncrement));
                nodePositions.put(i, new Point(x, y));
            }

            g.setColor(Color.BLACK);
            for (int[] road : roads) {
                Point p1 = nodePositions.get(road[0]);
                Point p2 = nodePositions.get(road[1]);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                int node = entry.getKey();
                Point p = entry.getValue();
                if (packages[node] == 1) {
                    g.setColor(Color.RED); 
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
                g.setColor(Color.WHITE);
                g.drawString(Integer.toString(node), p.x - 5, p.y + 5);
            }
        }
    }
}