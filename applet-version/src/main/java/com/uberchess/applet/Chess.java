package com.uberchess.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

public class Chess extends Applet implements Runnable {

    final static int BLACK = -1;
    final static int WHITE = 1;
    final static int EMPTY = 0;
    final static int PAWN = 1;
    final static int KNIGHT = 2;
    final static int BISHOP = 3;
    final static int ROOK = 4;
    final static int QUEEN = 5;
    final static int KING = 6;
    final static Color COLOR_LIGHT = Color.LIGHT_GRAY;
    final static Color COLOR_DARK = Color.GRAY;
    final static Color COLOR_SELECTED = Color.GREEN;
    final static Color COLOR_DESTINATIONS = Color.ORANGE;
    final static Color COLOR_BACKGROUND = Color.BLACK;
    public static int computer;
    public static int human;
    public static int boardSize;
    public static int tileSize;
    public static String testString;
    int boardX, boardY;
    Chessboard board;
    Thread mainLoop;

    BufferedImage appletImage;
    Graphics2D appletG;
    BufferedImage boardImage;
    Graphics2D boardG;

    MouseClicker clickMe;

    ChessAI monkey;

    public void init() {
        testString = "v.5";

        boardSize = getWidth();
        tileSize = Math.round(boardSize / 8);

        board = new Chessboard(this);
        boardX = Math.round((getWidth() - boardSize) / 2);
        boardY = Math.round((getHeight() - boardSize) / 2);

        monkey = new ChessAI(board);
        Chess.computer = Chess.BLACK;
        Chess.human = Chess.WHITE;

        clickMe = new MouseClicker();
        addMouseListener(clickMe);

        appletImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        appletG = appletImage.createGraphics();
        boardImage = new BufferedImage(Chess.boardSize, Chess.boardSize, BufferedImage.TYPE_3BYTE_BGR);
        boardG = boardImage.createGraphics();
    }

    public void start() {
        if (mainLoop == null) {
            mainLoop = new Thread(this);
            mainLoop.start();
        }
    }

    public void run() {
        Thread thisThread = Thread.currentThread();

        board.newGame();
        repaint();

        while (mainLoop == thisThread) {
            if (Array.getLength(board.getAllMoves()) == 0) {
                Chess.testString = "stalemate!";
                repaint();
            }
            if (board.currentPlayer == Chess.computer) {
                Chess.testString = "Computer is deciding on a move...";
                repaint();
                board.makeMove(monkey.getNextMove());
                Chess.testString = "";
                repaint();
                board.currentPlayer *= -1;
            }
            try {
                thisThread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public void stop() {
        mainLoop = null;
    }

    public void update(Graphics g) {
        paint(appletG);
        g.drawImage(appletImage, 0, 0, this);
    }

    public void paint(Graphics g) {
        g.setColor(Chess.COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        board.paint(boardG);
        g.drawImage(boardImage, boardX, boardY, this);

//Chess.drawString used as output string, and is displayed in topleft.
        g.setColor(Color.RED);
        int tx = 10;
        int ty = 20;
        g.drawString(Chess.testString, tx, ty);
        g.drawString(Chess.testString, tx + 2, ty);
        g.drawString(Chess.testString, tx, ty + 2);
        g.drawString(Chess.testString, tx + 2, ty + 2);
        g.setColor(Color.WHITE);
        g.drawString(Chess.testString, 11, 21);
    }

    class MouseClicker extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            e.translatePoint(-boardX, -boardY);
            board.click(e);
            repaint();
        }
    }
}