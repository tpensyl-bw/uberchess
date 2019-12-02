package com.uberchess.applet;

import java.lang.reflect.Array;

public class ChessAI {

    final static int DEPTH = 2;
    final static int MAX_EXTRA = 0;
    double[] pieceValue;
    double winValue;
    Chessboard board;

    ChessAI(Chessboard board) {
        this.board = board;
        pieceValue = new double[7];
        pieceValue[Chess.EMPTY] = 0;
        pieceValue[Chess.PAWN] = 1;
        pieceValue[Chess.KNIGHT] = 3;
        pieceValue[Chess.BISHOP] = 3.5;
        pieceValue[Chess.ROOK] = 5;
        pieceValue[Chess.QUEEN] = 9;
        pieceValue[Chess.KING] = 255;
///need to implement:
        winValue = pieceValue[Chess.KING] - (2 * pieceValue[Chess.KNIGHT] + 2 * pieceValue[Chess.BISHOP]
                + 2 * pieceValue[Chess.ROOK] + 9 * pieceValue[Chess.QUEEN]);
    }

    public Move getNextMove() {
        //pretty much assumes computer is black

        Move[] legalMoves = board.getAllMoves();
        String debug = "";
        int randomSeed = new Double(Math.floor(Math.random() * Array.getLength(legalMoves))).intValue();
        Move bestMove = legalMoves[randomSeed];
        Node child = board.branch(bestMove);
        double bestScore = -getScore(child, ChessAI.DEPTH);

        for (int i = 0; i < Array.getLength(legalMoves); i++) {
            child = board.branch(legalMoves[i]);
            double score = -getScore(child, ChessAI.DEPTH);
            if (score > bestScore) {
                bestScore = score;
                bestMove = legalMoves[i];
            }
            if (score == bestScore) {
                debug = legalMoves[i].toString() + ":" + Double.toString(score) + " " + debug;
            }
        }

        Chess.testString = debug + Double.toString(bestScore);
        return bestMove;
    }

    double getScore(Node board, int d) {
        Move[] legalMoves = board.getAllMoves();
// || Array.getLength(legalMoves) == 0
        if (d <= -ChessAI.MAX_EXTRA) {
            return evaluate(board);
        }
        //here's some quiescenceness pwnage
        if (d <= 0 && !board.lastMove.isCapture) {
            return evaluate(board);
        }
        Node child;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < Array.getLength(legalMoves); i++) {
            child = board.branch(legalMoves[i]);
            double score = -getScore(child, d - 1);
            if (score > bestScore) {
                bestScore = score;
            }
        }
        return bestScore;
    }

    public double evaluate(Node evaluee) {
        //evaluation function is very simple right now
        //it just uses material value
        //takes into account current player for sake of minimax

        double score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = evaluee.piece[i][j];
                if (piece > 0) {
                    score += pieceValue[piece];
                } else {
                    score -= pieceValue[Math.abs(piece)];
                }
            }
        }
        score *= evaluee.currentPlayer;
        return score;
    }

}
