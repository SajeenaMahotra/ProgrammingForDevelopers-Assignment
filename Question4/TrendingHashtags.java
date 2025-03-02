package Question4;

import java.util.*;
import java.util.regex.*;

public class TrendingHashtags {
    public static void main(String[] args) {
        List<String> tweets = Arrays.asList(
            "Enjoying a great start to the day. #HappyDay #MorningVibes",
            "Another #HappyDay with good vibes! #FeelGood",
            "Productivity peaks! #WorkLife #ProductiveDay",
            "Exploring new tech frontiers. #TechLife #Innovation",
            "Gratitude for today's moments. #HappyDay #Thankful",
            "Innovation drives us. #TechLife #FutureTech",
            "Connecting with nature's serenity. #Nature #Peaceful"
        );

        List<String> dates = Arrays.asList(
            "2024-02-01", "2024-02-03", "2024-02-04", 
            "2024-02-04", "2024-02-05", "2024-02-07", "2024-02-09"
        );

        List<Map.Entry<String, Integer>> result = findTrendingHashtags(tweets, dates);

        System.out.println("+-----------+-------+");
        System.out.println("| hashtag   | count |");
        System.out.println("+-----------+-------+");
        for (Map.Entry<String, Integer> entry : result) {
            System.out.printf("| %-9s | %-5d |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+-----------+-------+");
    }

    public static List<Map.Entry<String, Integer>> findTrendingHashtags(List<String> tweets, List<String> dates) {
        Map<String, Integer> hashtagCount = new HashMap<>();
        Pattern pattern = Pattern.compile("#\\w+");  

        for (int i = 0; i < tweets.size(); i++) {
            if (!dates.get(i).startsWith("2024-02")) continue; 

            Matcher matcher = pattern.matcher(tweets.get(i));
            while (matcher.find()) {
                String hashtag = matcher.group();
                hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
        sortedHashtags.sort((a, b) -> (b.getValue().equals(a.getValue())) 
            ? a.getKey().compareTo(b.getKey()) 
            : b.getValue() - a.getValue());

        return sortedHashtags.subList(0, Math.min(3, sortedHashtags.size())); 
    }
}
