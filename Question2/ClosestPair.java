package Question2;

import java.util.Scanner;

public class ClosestPair {
    public static void main(String[] args) {
        Scanner scanner = new  Scanner(System.in);

        System.out.println("Enter the number of points: ");
        int n = scanner.nextInt();
        int [] x_coords = new int[n];
        int[] y_coords = new  int [n];

        System.out.println("Enter the x-coordinates: ");
        for (int i =0; i< n; i++){
            x_coords[i] = scanner .nextInt();

        }
        System.out.println("Enter the y -coordinates");
        for (int i= 0; i< n; i++){
            y_coords[i]= scanner.nextInt();
        }
        int[] closestPair = findClosestPair(x_coords, y_coords);

        System.out.println("Closest pair of points: [" + closestPair[0] + " , "+closestPair[1] + "]" );
        scanner.close();
    }

    public static int[] findClosestPair(int[] x_coords, int[] y_coords){
        int n = x_coords.length;
        int[] closestPair = new int[2];
        int minDistance= Integer.MAX_VALUE;

        for  (int i = 0; i<n; i++){
            for (int j  = i +1 ; j <n; j++){

                int distance = Math.abs(x_coords[i]- x_coords[j])+Math.abs(y_coords[i]- y_coords[j]);

                if (distance < minDistance || (distance == minDistance && i < closestPair[0])||
                (distance == minDistance && i == closestPair[0] && j < closestPair[1])){
                    minDistance = distance;
                    closestPair[0] = i;
                    closestPair[1] = j ;
                }
            }
        }
        return closestPair;
    }

}


