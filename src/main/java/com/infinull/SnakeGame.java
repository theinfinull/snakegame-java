package com.infinull;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Block {
        int x; // x coordinate
        int y; // y coordinate
        Image img;

        Block(int x, int y, String location) {
            this.x = x;
            this.y = y;
            this.img = Toolkit.getDefaultToolkit().getImage(location);
        }
    }

    // board
    int boardWidth;
    int boardHeight;
    final int blockSize = Config.blockSize;

    // images
    Image backgroundImg;
    Image scoreImg;
    Image highscoreImg;
    Image gameOverImg;

    // highscore
    int highscore;

    // snake
    Block snakeHead;
    ArrayList<Block> snakeBody;

    // food
    Block food;
    Random random;

    // game logic
    int velocityX;
    int velocityY;
    Timer gameLoop;
    boolean gameOver;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        // highscore
        highscore = App.highscore;

        // set the images
        backgroundImg = Toolkit.getDefaultToolkit().getImage(Config.backgroundImg);
        scoreImg = Toolkit.getDefaultToolkit().getImage(Config.scoreImg);
        highscoreImg = Toolkit.getDefaultToolkit().getImage(Config.highScoreMiniImg);
        gameOverImg = Toolkit.getDefaultToolkit().getImage(Config.gameOverImg);

        // set the blocks
        snakeHead = new Block(5, 5, Config.snakeHeadImg); // default starting position
        snakeBody = new ArrayList<Block>();
        food = new Block(10, 10, Config.foodImg); // default starting position
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        // game timer
        gameLoop = new Timer(100, this); // how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, this);
        draw(g);
    }

    public void draw(Graphics g) {
        // Food
        g.drawImage(food.img, food.x * blockSize, food.y * blockSize, blockSize, blockSize, this);

        // Snake Head
        g.drawImage(snakeHead.img, snakeHead.x * blockSize, snakeHead.y * blockSize, blockSize, blockSize, this);

        // Snake Body
        for (int i = 0; i < snakeBody.size(); i++) {
            Block snakePart = snakeBody.get(i);
            g.drawImage(snakePart.img, snakePart.x * blockSize, snakePart.y * blockSize, blockSize, blockSize, this);
        }

        // Score
        int len = (int) Math.log10(snakeBody.size());
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawImage(scoreImg, 13, 13, 75 + (8 * len - 1), 25, this);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Score: " + String.valueOf(snakeBody.size()), blockSize - 3, blockSize + 5);

        g.drawImage(highscoreImg, 18 * blockSize + 12, 13, 125, 25, this);
        g.drawString("High Score: " + highscore, 19 * blockSize - 3, blockSize + 5);

        if (gameOver) {
            g.drawImage(gameOverImg, 220, 208, 160, 197, this);
        }

    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / blockSize);
        food.y = random.nextInt(boardHeight / blockSize);
    }

    public void move() {
        // eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Block(food.x, food.y, Config.snakeBodyImg));
            placeFood();
        }

        // update highscore
        if (snakeBody.size() > highscore) {
            highscore = snakeBody.size();
        }

        // move snake body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Block snakePart = snakeBody.get(i);
            if (i == 0) { // right before the head
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Block prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        // move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // game over conditions
        for (int i = 0; i < snakeBody.size(); i++) {
            Block snakePart = snakeBody.get(i);

            // collide with snake head
            if (collision(snakeHead, snakePart)) {
                // make the head go back
                snakeBody.get(0).img = Toolkit.getDefaultToolkit().getImage(Config.snakeHeadDeadImg);
                // make the body red
                snakeBody.get(i).img = Toolkit.getDefaultToolkit().getImage(Config.snakeBodyRedImg);
                gameOver = true;
            }
        }

        // passed left border || right border || top border || bottom border
        if (snakeHead.x * blockSize < 0 || snakeHead.x * blockSize > boardWidth - blockSize ||
                snakeHead.y * blockSize < 0 || snakeHead.y * blockSize > boardHeight - blockSize) {
            // make the head go back
            if (snakeBody.size() > 0) {
                snakeBody.get(0).img = Toolkit.getDefaultToolkit().getImage(Config.snakeHeadDeadImg);
            }
            gameOver = true;
        }
    }

    public boolean collision(Block tile1, Block tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void gameOver() {
        // set highscore
        if (highscore > App.highscore) {
            try {
                App.highscore = highscore;
                PrintWriter output = new PrintWriter(new File("src/main/resources/highscore/highscore.txt"));
                output.write(String.valueOf(snakeBody.size()));
                output.close();
            } catch (FileNotFoundException exception) {
                System.err.println("highscore.txt not found !");
            }
        }
        gameLoop.stop();
        App.frame.remove(this);
        SnakeMenu menu = new SnakeMenu(boardWidth, boardHeight);
        App.frame.add(menu);
        App.frame.pack();
        menu.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) { // called every x milliseconds by gameLoop timer
        if (!gameOver) {
            move();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // ? logging
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (gameOver && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameOver();
        }
    }

    // not needed
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}