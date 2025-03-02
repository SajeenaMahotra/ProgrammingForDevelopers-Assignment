package Question6;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class WebCrawler extends JFrame {
    private JTextField urlInput;
    private JTextArea contentArea;
    private JButton crawlButton;
    private JButton reloadButton;
    private ExecutorService executorService;
    private BlockingQueue<String> urlQueue;

    public WebCrawler() {
        
        setTitle("Multithreaded Web Crawler");
        setSize(800, 600);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

      
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        urlInput = new JTextField(50);
        crawlButton = new JButton("Crawl");
        reloadButton = new JButton("Reload");
        inputPanel.add(new JLabel("Enter URLs (comma separated):"));
        inputPanel.add(urlInput);
        inputPanel.add(crawlButton);
        inputPanel.add(reloadButton);

     
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(contentArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    
        executorService = Executors.newFixedThreadPool(10);
        urlQueue = new LinkedBlockingQueue<>();

       
        crawlButton.addActionListener(e -> {
            String urls = urlInput.getText().trim();
            if (!urls.isEmpty()) {
                contentArea.setText(""); 
                for (String url : urls.split(",")) {
                    urlQueue.add(url.trim());
                }
                startCrawling();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter valid URLs.");
            }
        });


        reloadButton.addActionListener(e -> {
            contentArea.setText(""); 
            startCrawling();
        });
    }

    private void startCrawling() {
        while (!urlQueue.isEmpty()) {
            String url = urlQueue.poll();
            executorService.submit(() -> {
                try {
                    String content = fetchWebPage(url);
                    SwingUtilities.invokeLater(() -> contentArea.append("Content from " + url + ":\n" + content + "\n\n"));
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> contentArea.append("Error fetching URL: " + url + "\n"));
                }
            });
        }
    }

    private String fetchWebPage(String url) throws IOException {
        StringBuilder content = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return content.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebCrawler crawler = new WebCrawler();
            crawler.setVisible(true);
        });
    }
}