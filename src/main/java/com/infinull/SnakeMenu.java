package com.infinull;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SnakeMenu extends JPanel implements ActionListener, KeyListener {
    // board
    int boardWidth;
    int boardHeight;
    final int blockSize = 25; // pixelsize of each square

    // images
    Image backgroundImg;
    Image menuImage;
    Image instructionsImage;
    Image highScoreImg;

    // game logic
    Timer menuLoop;

    boolean start;
    boolean instruction = false;
    boolean exit = false;

    SnakeMenu(int boardWidth, int boardHeight) {
        start = false;
        instruction = false;
        exit = false;

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        // set the images
        backgroundImg = Toolkit.getDefaultToolkit().getImage(Config.samppleBackgroundImg);
        menuImage = Toolkit.getDefaultToolkit().getImage(Config.menuImage);
        instructionsImage = Toolkit.getDefaultToolkit().getImage(Config.instructionImg);
        highScoreImg = Toolkit.getDefaultToolkit().getImage(Config.highScoreImg);

        // highscore

        // menu timer
        menuLoop = new Timer(50, this); // how long it takes to start timer, milliseconds gone between frames
        menuLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, this);
        draw(g);
    }

    public void draw(Graphics g) {
        // Menu box
        if (instruction) {
            g.drawImage(instructionsImage, 100, 117, 413, 379, this);
        } else {
            g.drawImage(menuImage, 8 * blockSize, 9 * blockSize, 200, 150, this);
            g.drawImage(highScoreImg, 9 * blockSize, 16 * blockSize + 13,
                    highScoreImg.getWidth(getFocusCycleRootAncestor()),
                    highScoreImg.getHeight(getFocusCycleRootAncestor()), this);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.DARK_GRAY);
            g.drawString("High Score: " + App.highscore, 10 * blockSize, 17 * blockSize + 5);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        if (start) {
            // start game
            menuLoop.stop();
            App.frame.remove(this);
            SnakeGame game = new SnakeGame(boardWidth, boardHeight);
            App.frame.add(game);
            App.frame.pack();
            game.requestFocus();
        } else if (exit) {
            System.exit(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // start game
            if (!instruction) {
                start = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // show instructions
            if (!instruction) {
                instruction = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // exit
            if (instruction) {
                instruction = false;
            } else {
                exit = true;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
