package Question2;

import java.util.Arrays;
import java.util.Scanner;

public class MinimumRewards {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        System.out.print("Enter number of employees: ");
        int n = scanner.nextInt();
        int[] ratings = new int[n];

        System.out.println("Enter the performance ratings: ");
        for ( int i = 0; i < n; i++){
            ratings[i]= scanner.nextInt();
        }

        System.out.println("Minimum number of rewards needed: " + minRewards(ratings));

        scanner.close();

    }

    public static int minRewards(int[] ratings){
        int n = ratings.length;
        int[] rewards = new int[n];
        Arrays.fill(rewards,1); // Assign 1 reward to each

        for (int i =1; i<n; i++){
            if (ratings[i]>ratings[i - 1]){
                rewards[i] = rewards [i - 1] + 1;
            }
        }
        for (int i = n -2 ; i >=0;i --){
            if (ratings[i] > ratings[ i + 1]) {
                rewards [i]= Math.max(rewards[i], rewards[i + 1] + 1);
                
            }
        }

        int totalRewards = 0;
        for ( int reward : rewards){
            totalRewards += reward;

        }
        return totalRewards;
    }
}
