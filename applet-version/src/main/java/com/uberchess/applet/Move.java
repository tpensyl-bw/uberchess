package com.uberchess.applet;

public class Move {


    final static String PIECE_CHARS = " PNBRQK";
    final static String PIECE_FILES = "ABCDEFGH";
    final static String PIECE_RANKS = "87654321";
    //This represents a move, storing the from/to coords and the piece moving.
    public int fromX, fromY, toX, toY;
    public int piece;
    public boolean isCapture;

    public Move(int fromX, int fromY, int toX, int toY, int piece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.piece = piece;
    }

    public static boolean areEqual(Move m1, Move m2) {
        if (m1.fromX == m2.fromX && m1.fromY == m2.fromY && m1.toX == m2.toX && m1.toY == m2.toY
                && m1.piece == m2.piece) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        String returnee = "";
        returnee += Character.toString(PIECE_CHARS.charAt(Math.abs(piece)));
        returnee += Character.toString(PIECE_FILES.charAt(fromX));
        returnee += Character.toString(PIECE_RANKS.charAt(fromY));
        returnee += Character.toString(PIECE_FILES.charAt(toX));
        returnee += Character.toString(PIECE_RANKS.charAt(toY));
        return returnee;
    }
}


