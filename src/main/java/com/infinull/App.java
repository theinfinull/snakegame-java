package com.infinull;

import java.io.File;
import java.util.Scanner;

import javax.swing.*;

public class App {
    static JFrame frame = new JFrame(Config.frameName);

    static int highscore;

    public static void main(String[] args) throws Exception {
        // frame.setUndecorated(true); - to remove the default frame given in windows
        frame.setVisible(true);
        frame.setSize(Config.boardWidth, Config.boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // get reader
        try {
            Scanner input = new Scanner(new File("src/main/resources/highscore/highscore.txt"));
            highscore = input.nextInt();
            input.close();
        } catch (Exception e) {
            System.out.println("highscore.txt not found !");
            highscore = 0;
        }

        SnakeMenu menu = new SnakeMenu(Config.boardWidth, Config.boardHeight);

        // go into menu
        frame.add(menu);
        frame.pack();
        menu.requestFocus();
    }
}