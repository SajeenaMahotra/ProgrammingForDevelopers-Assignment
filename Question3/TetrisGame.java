package Question3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TetrisGame extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int TILE_SIZE = 30;
    private static final int DELAY = 500; 

    private Timer timer;
    private Color[][] board; 
    private Block currentBlock;
    private Block nextBlock;
    private Queue<Block> blockQueue;
    private Random random;
    private int score;

    public TetrisGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.WHITE);
        setFocusable(true);

      
        board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
        blockQueue = new Queue<>();
        random = new Random();
        score = 0;

        initializeGame();

        addKeyBindings();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initializeGame() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = null;
            }
        }

       
        blockQueue.clear();
        enqueueNewBlock();
        currentBlock = blockQueue.dequeue();
        enqueueNewBlock();
        nextBlock = blockQueue.dequeue();

    }

    private void enqueueNewBlock() {
        Block block = new Block(random.nextInt(7), new Point(BOARD_WIDTH / 2 - 1, 0));
        blockQueue.enqueue(block);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!moveBlockDown()) {
            placeBlock();
            checkForCompletedRows();
            if (isGameOver()) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                initializeGame();
                timer.start();
            } else {
                currentBlock = nextBlock; 
                enqueueNewBlock();
                nextBlock = blockQueue.dequeue();
            }
        }
        repaint();
    }

    private boolean moveBlockDown() {
        if (currentBlock.canMoveDown(board)) {
            currentBlock.moveDown();
            return true;
        }
        return false;
    }

    private void placeBlock() {
        for (Point p : currentBlock.getShape()) {
            int x = currentBlock.getPosition().x + p.x;
            int y = currentBlock.getPosition().y + p.y;
            board[y][x] = currentBlock.getColor(); 
        }
    }

    private void checkForCompletedRows() {
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean rowFilled = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == null) {
                    rowFilled = false;
                    break;
                }
            }
            if (rowFilled) {
                System.out.println("Row " + i + " is full. Removing...");
                removeRow(i);
                score += 100;  
                System.out.println("Score updated: " + score);
            }
        }
    }

    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = board[i - 1][j];
            }
        }
        for (int j = 0; j < BOARD_WIDTH; j++) {
            board[0][j] = null;
        }
    }

    private boolean isGameOver() {
        for (int j = 0; j < BOARD_WIDTH; j++) {
            if (board[0][j] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                g.drawRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != null) {
                    g.setColor(board[i][j]);
                    g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        if (currentBlock != null) {
            currentBlock.draw(g, TILE_SIZE);
        }

        if (nextBlock != null) {
            nextBlock.drawPreview(g, TILE_SIZE, BOARD_WIDTH * TILE_SIZE + 20, 20);
        }

        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, BOARD_WIDTH * TILE_SIZE + 20, 100);
    }

    private void addKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentBlock != null && currentBlock.canMoveLeft(board)) {
                    currentBlock.moveLeft();
                    repaint();
                }
            }
        });


        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentBlock != null && currentBlock.canMoveRight(board)) {
                    currentBlock.moveRight();
                    repaint();
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("UP"), "rotate");
        actionMap.put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentBlock != null && currentBlock.canRotate(board)) {
                    currentBlock.rotate();
                    repaint();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Block {
    private static final int[][][] SHAPES = {
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // Square
        {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, // Line
        {{0, 0}, {1, 0}, {2, 0}, {2, 1}}, // L-Shape
        {{0, 1}, {1, 1}, {2, 1}, {2, 0}}, // Reverse L-Shape
        {{0, 0}, {1, 0}, {1, 1}, {2, 1}}, // Z-Shape
        {{0, 1}, {1, 1}, {1, 0}, {2, 0}}, // Reverse Z-Shape
        {{0, 1}, {1, 0}, {1, 1}, {2, 1}}  // T-Shape
    };

    private int[][] shape;
    private Point position;
    private Color color;
    private Random random;

    public Block(int type, Point position) {
        this.shape = SHAPES[type];
        this.position = position;
        this.random = new Random();
        this.color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public Point[] getShape() {
        Point[] points = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            points[i] = new Point(shape[i][0], shape[i][1]);
        }
        return points;
    }

    public Point getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public boolean canMoveDown(Color[][] board) {
        for (Point p : getShape()) {
            int x = position.x + p.x;
            int y = position.y + p.y + 1;
            if (y >= board.length || board[y][x] != null) {
                return false;
            }
        }
        return true;
    }

    public void moveDown() {
        position.y++;
    }

    public boolean canMoveLeft(Color[][] board) {
        for (Point p : getShape()) {
            int x = position.x + p.x - 1;
            int y = position.y + p.y;
            if (x < 0 || board[y][x] != null) {
                return false;
            }
        }
        return true;
    }

    public void moveLeft() {
        position.x--;
    }

    public boolean canMoveRight(Color[][] board) {
        for (Point p : getShape()) {
            int x = position.x + p.x + 1;
            int y = position.y + p.y;
            if (x >= board[0].length || board[y][x] != null) {
                return false;
            }
        }
        return true;
    }

    public void moveRight() {
        position.x++;
    }

    public boolean canRotate(Color[][] board) {
        int[][] rotatedShape = rotateShape();
        for (int i = 0; i < rotatedShape.length; i++) {
            int x = position.x + rotatedShape[i][0];
            int y = position.y + rotatedShape[i][1];
            if (x < 0 || x >= board[0].length || y >= board.length || board[y][x] != null) {
                return false;
            }
        }
        return true;
    }

    public void rotate() {
        shape = rotateShape();
    }

    private int[][] rotateShape() {
        int[][] rotatedShape = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            rotatedShape[i][0] = -shape[i][1];
            rotatedShape[i][1] = shape[i][0];
        }
        return rotatedShape;
    }

    public void draw(Graphics g, int tileSize) {
        g.setColor(color);
        for (Point p : getShape()) {
            int x = (position.x + p.x) * tileSize;
            int y = (position.y + p.y) * tileSize;
            g.fillRect(x, y, tileSize, tileSize);
        }
    }

    public void drawPreview(Graphics g, int tileSize, int xOffset, int yOffset) {
        g.setColor(color);
        for (Point p : getShape()) {
            int x = xOffset + p.x * tileSize;
            int y = yOffset + p.y * tileSize;
            g.fillRect(x, y, tileSize, tileSize);
        }
    }
}

class Queue<T> {
    private Node<T> head;
    private Node<T> tail;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
        if (head == null) {
            head = tail;
        }
    }

    public T dequeue() {
        if (head == null) {
            throw new IllegalStateException("Queue is empty");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        return data;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void clear() {
        head = tail = null;
    }
}