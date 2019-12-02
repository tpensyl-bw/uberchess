package com.uberchess.applet;

public class Node {


    //still need to implement:
    boolean whiteKingCastle, whiteQueenCastle, blackKingCastle, blackQueenCastle;
    //
    Move lastMove;
    int currentPlayer;
    int[][] piece;

    public Node() {
        piece = new int[8][8];
        lastMove = null;
    }

    public Node branch(Move m) {
        Node newNode = new Node();
        newNode.currentPlayer = -currentPlayer;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newNode.piece[i][j] = piece[i][j];
            }
        }
        newNode.makeMove(m);
        return newNode;
    }

    public void makeMove(Move m) {
        int movee = piece[m.fromX][m.fromY];
        if (piece[m.toX][m.toY] != 0) {
            m.isCapture = true;
        } else {
            m.isCapture = false;
        }
        if (Math.abs(movee) == Chess.PAWN && m.toY == ((1 - movee) * 3.5)) {
            piece[m.toX][m.toY] = Chess.QUEEN * movee;
        } else {
            piece[m.toX][m.toY] = piece[m.fromX][m.fromY];
        }
        piece[m.fromX][m.fromY] = 0;
        lastMove = m;
    }

    public Move[] getAllMoves() {
        //returns a list of all legal moves for current player. (does not test for check.)
        MoveList allMoves = new MoveList(1, 1);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                allMoves.add(getPieceMoves(x, y));
            }
        }
        return allMoves.toArray();
    }

    public Move[] getPieceMoves(int x, int y) {
        //returns a list of all legal moves for piece at coords (x, y)
        int movee = piece[x][y];
        MoveList movesTemp = new MoveList(10, 10);
        if (movee == Chess.PAWN * currentPlayer) {
            if (piece[x][y - movee] == 0) {
                movesTemp.add(new Move(x, y, x, y - movee, movee));
                if (y == (2.5 * movee + 3.5) && piece[x][y - 2 * movee] == 0) {
                    movesTemp.add(new Move(x, y, x, y - 2 * movee, movee));
                }
            }
            if (x < 7 && piece[x + 1][y - movee] != 0 && movee != (piece[x + 1][y - movee] / Math
                    .abs(piece[x + 1][y - movee]))) {
                movesTemp.add(new Move(x, y, x + 1, y - movee, movee));
            }
            if (x > 0 && piece[x - 1][y - movee] != 0 && movee != (piece[x - 1][y - movee] / Math
                    .abs(piece[x - 1][y - movee]))) {
                movesTemp.add(new Move(x, y, x - 1, y - movee, movee));
            }
        } else if (movee == Chess.KNIGHT * currentPlayer) {
            tryAdd(new Move(x, y, x + 1, y + 2, movee), movesTemp);
            tryAdd(new Move(x, y, x - 1, y + 2, movee), movesTemp);
            tryAdd(new Move(x, y, x + 1, y - 2, movee), movesTemp);
            tryAdd(new Move(x, y, x - 1, y - 2, movee), movesTemp);
            tryAdd(new Move(x, y, x + 2, y + 1, movee), movesTemp);
            tryAdd(new Move(x, y, x - 2, y + 1, movee), movesTemp);
            tryAdd(new Move(x, y, x + 2, y - 1, movee), movesTemp);
            tryAdd(new Move(x, y, x - 2, y - 1, movee), movesTemp);
        } else if (movee == Chess.BISHOP * currentPlayer) {
            movesTemp.add(get1DMoves(x, y, 1, 1));
            movesTemp.add(get1DMoves(x, y, 1, -1));
            movesTemp.add(get1DMoves(x, y, -1, 1));
            movesTemp.add(get1DMoves(x, y, -1, -1));
        } else if (movee == Chess.ROOK * currentPlayer) {
            movesTemp.add(get1DMoves(x, y, 1, 0));
            movesTemp.add(get1DMoves(x, y, -1, 0));
            movesTemp.add(get1DMoves(x, y, 0, 1));
            movesTemp.add(get1DMoves(x, y, 0, -1));
        } else if (movee == Chess.QUEEN * currentPlayer) {
            movesTemp.add(get1DMoves(x, y, 1, 0));
            movesTemp.add(get1DMoves(x, y, -1, 0));
            movesTemp.add(get1DMoves(x, y, 0, 1));
            movesTemp.add(get1DMoves(x, y, 0, -1));
            movesTemp.add(get1DMoves(x, y, 1, 1));
            movesTemp.add(get1DMoves(x, y, 1, -1));
            movesTemp.add(get1DMoves(x, y, -1, 1));
            movesTemp.add(get1DMoves(x, y, -1, -1));
        } else if (movee == Chess.KING * currentPlayer) {
            for (int xd = -1; xd <= 1; xd++) {
                for (int yd = -1; yd <= 1; yd++) {
                    tryAdd(new Move(x, y, x + xd, y + yd, movee), movesTemp);
                }
            }
        }
        return movesTemp.toArray();
    }

    public Move[] get1DMoves(int x, int y, int mx, int my) {
        MoveList temp1D = new MoveList(8, 8);
        int brk = 0;
        int movee = piece[x][y];
        for (int i = 1; x + mx * i >= 0 && y + my * i >= 0 && x + mx * i <= 7 && y + my * i <= 7 && brk == 0; i++) {
            int to = piece[x + mx * i][y + my * i];
            if (to == 0) {
                temp1D.add(new Move(x, y, x + mx * i, y + my * i, movee));
            } else {
                if (Math.abs(to) / to != currentPlayer) {
                    temp1D.add(new Move(x, y, x + mx * i, y + my * i, movee));
                }
                brk = 1;
            }
        }
        return temp1D.toArray();
    }

    public void tryAdd(Move movee, MoveList listee) {
        //checks that destination is on board and does not contain a friendly piece.
        if ((movee.toX <= 7 && movee.toX >= 0 && movee.toY <= 7 & movee.toY >= 0) && (
                piece[movee.toX][movee.toY] * currentPlayer <= 0)) {
            listee.add(movee);
        }
    }


}
