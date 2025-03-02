package Question3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class NetworkConnection {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter numberof devices: ");
        int n =sc.nextInt();
        
        int[] modules = new int[n];
        System.out.println("Enter module installation costs: ");
        for(int i= 0; i < n; i++){
            modules [i] = sc.nextInt();
        }

        System.out.print("Enter number of connections: ");
        int m =sc.nextInt();
        List<int[]> connections = new ArrayList<>();

        System.out.println("Enter connections (device1  device2 cost): ");
        for (int i = 0; i < m; i++){
            int d1= sc.nextInt();
            int d2 = sc.nextInt();
            int cost = sc.nextInt();
            connections.add(new int[]{d1,d2,cost});
        }
        sc.close();

        System.out.println("Minimum cost to connect all devices: "+ minCostToConnect(n,modules,connections));
    }

    static int minCostToConnect(int n , int[] modules, List<int []> connections){
        List<int[]> edges = new ArrayList<>(connections);

        for (int i = 0; i< n; i++){
            edges.add(new int[]{0 , i+1, modules[i]});
        }

        edges.sort(Comparator.comparingInt(a -> a[2]));


        UnionFind uf = new UnionFind (n+1);
        int totalCost = 0, count = 0;


        for (int[] edge : edges){
            if(uf.union(edge[0],edge[1])){
                totalCost +=    edge[2];
                count++;
                if(count == n) break;
            }
        }
        return totalCost;

    }
}

class UnionFind {
    int[] parent,rank  ;
    UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);  // Path compression
        return parent[x];
    }

    boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false;
        if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
        else if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
        else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}

