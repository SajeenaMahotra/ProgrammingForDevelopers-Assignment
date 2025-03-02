package Question1;
import java.util.Scanner;

public class CriticalTemperature {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter minimum number of indentical samples (k): ");
        int k = scanner.nextInt();

        System.out.print("Enter the number of temperature levels (n): ");
        int n = scanner.nextInt();
        
        int result=minMeasurements(k, n);
        System.out.println("Minimum measuremnets required to  find the critical temperature: " + result);

        scanner.close();
    }

    public static int minMeasurements(int k,int n){
        int[][] dp= new int[k+1][n+1];

        for (int i = 1; i<= k; i++){
            for(int j=1; j<=n; j++){
                dp[i][j]=j; //Worst case: Linear Search
            }
        }

        for (int  i=2 ; i <=k; i++){
            for (int  j=1; j<=n; j++){
                int low = 1, high = j, result = j;
                while (low <= high){
                    int mid =(low + high)/2;
                    int breakCase = dp[i-1][mid-1]; //if it breaks
                    int nonBreakcase = dp[i][j-mid];// if it doesnot break
                    int worstcase = 1 + Math.max(breakCase,nonBreakcase);

                    result =Math.min(result,worstcase);

                    if(breakCase > nonBreakcase){
                        high= mid -1;
                    }else{
                        low = mid +1;
                    }
                }
                dp[i][j]=result;

            }
        }
        return dp[k][n];
    }
}




