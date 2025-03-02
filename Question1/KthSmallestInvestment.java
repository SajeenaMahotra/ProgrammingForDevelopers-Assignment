package Question1;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class KthSmallestInvestment {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the size of returns1: ");
        int n =scanner.nextInt();
        int[] returns1 = new int[n];
        System.out.println("Enter elements of returns1 (sorted order): ");
        for (int i = 0; i<n; i++){
            returns1[i]=scanner.nextInt();
        }

        System.out.print("Enter the size of returns2: ");
        int m =scanner.nextInt();
        int[] returns2 = new int[m];
        System.out.println("Enter elements of returns1 (sorted order): ");
        for (int i = 0; i<m; i++){
            returns2[i]=scanner.nextInt();
        }

        System.out.print("Enter k (target index of lowest combined return): ");
        int k= scanner.nextInt();

        int result = KthSmallestProduct(returns1, returns2, k);
        System.out.println("The " + k + "-th smallest product is: " + result);

        scanner.close();
    }

    public static int KthSmallestProduct(int[] returns1, int[] returns2, int k){

        PriorityQueue<int[]>minHeap = new PriorityQueue<>((a,b)-> Integer.compare(a[0], b[0]));
        HashSet<String> visited = new HashSet<>();

        minHeap.offer(new int[]{returns1[0]* returns2[0],0,0});
        visited.add("0,0");

        int[] directions ={1,0,0,1};

        while (k--> 1){
            int[] curr = minHeap.poll();
            int i = curr[1],j=curr[2];


            for (int d = 0; d < 2;d++){
                int ni = i + directions[d*2];
                int nj = j + directions[d*2 +1 ];

                if (ni < returns1.length && nj < returns2.length){
                        String key = ni + "," + nj;
                        if (!visited.contains(key)){
                            minHeap.offer(new int[]{returns1[ni]* returns2[nj], ni, nj});
                            visited.add(key);
                        }
                }
            }
        }
        return minHeap.poll()[0];
    }
}
