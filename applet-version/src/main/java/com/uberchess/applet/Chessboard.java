package com.uberchess.applet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;

public class Chessboard extends Node {

    final Image WHITE_PAWN;
    final Image BLACK_PAWN;
    final Image WHITE_KNIGHT;
    final Image BLACK_KNIGHT;
    final Image WHITE_BISHOP;
    final Image BLACK_BISHOP;
    final Image WHITE_ROOK;
    final Image BLACK_ROOK;
    final Image WHITE_QUEEN;
    final Image BLACK_QUEEN;
    final Image WHITE_KING;
    final Image BLACK_KING;
    Chess parent;
    int selectedX, selectedY;
    int border;

    public Chessboard(Chess parent) {
        super();
        this.parent = parent;
        newGame();

        WHITE_PAWN = parent.getImage(parent.getCodeBase(), "images/white_pawn.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_PAWN = parent.getImage(parent.getCodeBase(), "images/black_pawn.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        WHITE_KNIGHT = parent.getImage(parent.getCodeBase(), "images/white_knight.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_KNIGHT = parent.getImage(parent.getCodeBase(), "images/black_knight.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        WHITE_BISHOP = parent.getImage(parent.getCodeBase(), "images/white_bishop.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_BISHOP = parent.getImage(parent.getCodeBase(), "images/black_bishop.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        WHITE_ROOK = parent.getImage(parent.getCodeBase(), "images/white_rook.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_ROOK = parent.getImage(parent.getCodeBase(), "images/black_rook.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        WHITE_QUEEN = parent.getImage(parent.getCodeBase(), "images/white_queen.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_QUEEN = parent.getImage(parent.getCodeBase(), "images/black_queen.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        WHITE_KING = parent.getImage(parent.getCodeBase(), "images/white_king.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
        BLACK_KING = parent.getImage(parent.getCodeBase(), "images/black_king.gif")
                .getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);

        border = 2;
    }

    public void newGame() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                piece[x][y] = 0;
            }
        }
        for (int x = 0; x < 8; x++) {
            piece[x][1] = Chess.PAWN * Chess.BLACK;
            piece[x][6] = Chess.PAWN * Chess.WHITE;
        }
        piece[0][0] = Chess.ROOK * Chess.BLACK;
        piece[1][0] = Chess.KNIGHT * Chess.BLACK;
        piece[2][0] = Chess.BISHOP * Chess.BLACK;
        piece[3][0] = Chess.QUEEN * Chess.BLACK;
        piece[4][0] = Chess.KING * Chess.BLACK;
        piece[5][0] = Chess.BISHOP * Chess.BLACK;
        piece[6][0] = Chess.KNIGHT * Chess.BLACK;
        piece[7][0] = Chess.ROOK * Chess.BLACK;
        piece[0][7] = Chess.ROOK * Chess.WHITE;
        piece[1][7] = Chess.KNIGHT * Chess.WHITE;
        piece[2][7] = Chess.BISHOP * Chess.WHITE;
        piece[3][7] = Chess.QUEEN * Chess.WHITE;
        piece[4][7] = Chess.KING * Chess.WHITE;
        piece[5][7] = Chess.BISHOP * Chess.WHITE;
        piece[6][7] = Chess.KNIGHT * Chess.WHITE;
        piece[7][7] = Chess.ROOK * Chess.WHITE;

        currentPlayer = Chess.WHITE;
        selectedX = 42;
        selectedY = 42;
    }

    public boolean isLegal(Move tryMove) {
        //checks to see if a move is legal by simply generating all moves first.
        //NOT to be used by computer cuz its very inefficient for that purpose.
        //(actually its inefficient even for this purpose, but insignificantly.
        //instead, used when a human player attempts to make a move.
//still need to test for check... bleh!
        Move[] pieceMoves = getPieceMoves(tryMove.fromX, tryMove.fromY);
        for (int i = 0; i < Array.getLength(pieceMoves); i++) {
            if (Move.areEqual(pieceMoves[i], tryMove)) {
                return true;
            }
        }
        return false;
    }

    public void paint(Graphics g) {
        //Draw each square and its piece if it has one.
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (Math.IEEEremainder(x + y, 2) == 0) {
                    g.setColor(Chess.COLOR_LIGHT);
                } else {
                    g.setColor(Chess.COLOR_DARK);
                }
                g.fillRect(x * Chess.tileSize, y * Chess.tileSize, Chess.tileSize, Chess.tileSize);
                drawPiece(g, x, y);
            }
        }
        //Highlight selected piece and possible moves
        if (selectedX != 42 && selectedY != 42) {
            Move[] destinations = getPieceMoves(selectedX, selectedY);
            for (int i = 0; i < Array.getLength(destinations); i++) {
                highlightSquare(g, destinations[i].toX, destinations[i].toY, Chess.COLOR_DESTINATIONS);
            }
            highlightSquare(g, selectedX, selectedY, Chess.COLOR_SELECTED);
        }
    }

    public void drawPiece(Graphics g, int x, int y) {
        switch (piece[x][y]) {
            case (Chess.PAWN * Chess.WHITE):
                g.drawImage(WHITE_PAWN, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.PAWN * Chess.BLACK):
                g.drawImage(BLACK_PAWN, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.KNIGHT * Chess.WHITE):
                g.drawImage(WHITE_KNIGHT, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.KNIGHT * Chess.BLACK):
                g.drawImage(BLACK_KNIGHT, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.BISHOP * Chess.WHITE):
                g.drawImage(WHITE_BISHOP, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.BISHOP * Chess.BLACK):
                g.drawImage(BLACK_BISHOP, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.ROOK * Chess.WHITE):
                g.drawImage(WHITE_ROOK, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.ROOK * Chess.BLACK):
                g.drawImage(BLACK_ROOK, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.QUEEN * Chess.WHITE):
                g.drawImage(WHITE_QUEEN, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.QUEEN * Chess.BLACK):
                g.drawImage(BLACK_QUEEN, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.KING * Chess.WHITE):
                g.drawImage(WHITE_KING, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            case (Chess.KING * Chess.BLACK):
                g.drawImage(BLACK_KING, Chess.tileSize * x, Chess.tileSize * y, parent);
                break;
            default:
                break;
        }
    }

    public void highlightSquare(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.drawRect(x * Chess.tileSize, y * Chess.tileSize, Chess.tileSize, Chess.tileSize);
        g.drawRect(x * Chess.tileSize - 1, y * Chess.tileSize - 1, Chess.tileSize + 2, Chess.tileSize + 2);

    }

    public void click(MouseEvent e) {
        if (currentPlayer != Chess.human) {
            return;
        }
        int mx = e.getX();
        int my = e.getY();
        int x = 42;
        int y = 42;
        for (int i = 0; i < 8; i++) {
            if (mx > i * Chess.tileSize && mx < (i + 1) * Chess.tileSize) {
                x = i;
            }
            if (my > i * Chess.tileSize && my < (i + 1) * Chess.tileSize) {
                y = i;
            }
        }
        if (x != 42 && y != 42) {
            if (piece[x][y] != 0 && Math.abs(piece[x][y]) / piece[x][y] == currentPlayer) {
                if (x == selectedX && y == selectedY) {
                    selectedX = 42;
                    selectedY = 42;
                } else {
                    selectedX = x;
                    selectedY = y;
                }
            } else if (selectedX != 42 && selectedY != 42) {
                Move letsMove = new Move(selectedX, selectedY, x, y, piece[selectedX][selectedY]);
                if (isLegal(letsMove)) {
                    makeMove(letsMove);
                    currentPlayer *= -1;
//debug
                    Chess.testString = "move is legal";
//
                    selectedX = 42;
                    selectedY = 42;
                } else {
                    Chess.testString = "move is not legal, gah!";
//___________make an "errreree" sound like wrongitudinal
                }
            }
        }
    }


}
