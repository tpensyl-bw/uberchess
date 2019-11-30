//Copyright 2005, Tommy Pensyl, All Rights Reserved.
//
//*** Version .30 *** November 18, 2005 ***
//
//-you can now play against the computer
//-computer looks only at immediate consequences
//-does not look at its opponents counter moves (only 1 ply)
//
//*** Things to add: ***
//
//-AI (monkey is still pretty stupid)
//-rest of GUI (new game, resign, offer draw(?))
//-pawn promotion
//-en passent
//-check testing
//-win detection
//-new game

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.util.Vector;

public class Chess extends Applet implements Runnable {
	final static int BLACK = -1;
	final static int WHITE = 1;

	public static int computer;
	public static int human;

	final static int EMPTY = 0;
	final static int PAWN = 1;
	final static int KNIGHT = 2;
	final static int BISHOP = 3;
	final static int ROOK = 4;
	final static int QUEEN = 5;
	final static int KING = 6;

	final static Color COLOR_LIGHT = Color.LIGHT_GRAY;
	final static Color COLOR_DARK = Color.DARK_GRAY;
	final static Color COLOR_SELECTED = Color.GREEN;
	final static Color COLOR_DESTINATIONS = Color.ORANGE;
	final static Color COLOR_BACKGROUND = Color.BLACK;

	public static int boardSize; 
	public static int tileSize;
	int boardX, boardY;

	public static String testString;

	Chessboard board;
	Thread mainLoop;

	BufferedImage appletImage;
	Graphics2D appletG;
	BufferedImage boardImage;
	Graphics2D boardG;

	MouseClicker clickMe;

	ChessAI monkey;

	public void init() {

int[] testArray1 = new int[3];
testArray1[0] = 0;
testArray1[1] = 1;
testArray1[2] = 2;

int[] testArray2 = testArray1.clone();
testArray2[0] = 10;
testArray2[1] = 11;

testString = Integer.toString(testArray1[0]) + Integer.toString(testArray1[1]) + Integer.toString(testArray2[0]);

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
		if(mainLoop == null) {	
			mainLoop = new Thread(this);
			mainLoop.start();
		}
	}
	public void run() {
		Thread thisThread = Thread.currentThread();

		board.newGame();
		repaint();

		while (mainLoop == thisThread) {
			if(Array.getLength(board.getAllMoves()) == 0) {
				Chess.testString = "stalemate!";
				repaint();
			}
			if(board.currentPlayer == Chess.computer) {
				Chess.testString = "Computer is deciding on a move...";
				board.makeMove(monkey.getMove());
				repaint();
				board.currentPlayer *= - 1;
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
		int tx = 10; int ty = 20;
		g.drawString(Chess.testString, tx, ty);
		g.drawString(Chess.testString, tx+2, ty);
		g.drawString(Chess.testString, tx, ty+2);
		g.drawString(Chess.testString, tx+2, ty+2);
		g.setColor(Color.WHITE);
		g.drawString(Chess.testString, 11, 21);
	}
	class MouseClicker extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
			e.translatePoint(-boardX, -boardY);
			board.click(e);
			repaint();
		}
	}
}
class ChessAI{
	double[] pieceValue;
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
		pieceValue[Chess.KING] = 200;
	}
	public Move getMove() {
		Move[] legalMoves = board.getAllMoves();
		Node child;
		Move bestMove = legalMoves[0];
		double bestScore = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < Array.getLength(legalMoves); i++) {
			child = board.branch(legalMoves[i]);
			double score = -getScore(child);
			if(score > bestScore) {
				bestScore = score;
				bestMove = legalMoves[i];
			}
		}

		//Overrides previous with random move *teehee* ^_^
		//int choiceMove = new Double(Math.floor(Math.random() * Array.getLength(legalMoves))).intValue();

Chess.testString = "Board Score = " + Double.toString(bestScore);
		return bestMove;
	}
	public double getScore(Node evaluee) {
		double score = 0;	
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				int piece = evaluee.piece[i][j];
				if(piece > 0)
					score += pieceValue[piece];
				else
					score -= pieceValue[Math.abs(piece)];
			}
		}
		return score;
	}
}
class Node{
//still need to implement:
	Move lastMove;
	int whiteKingCastle, whiteQueenCastle, blackKingCastle, blackQueenCastle;
//
	int currentPlayer;
	int[][] piece;
	public Node() {
		piece = new int[8][8];
	}
	public Node branch(Move m) {
		Node newNode = new Node();
//does it need to be negative??? be sure to check later
		newNode.currentPlayer = -currentPlayer;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				newNode.piece[i][j] = piece[i][j];
			}
		}
		newNode.makeMove(m);
		return newNode;
	}
	public void makeMove(Move m) {
		int movee = piece[m.fromX][m.fromY];
		if(Math.abs(movee) == Chess.PAWN && m.toY == ((1 - movee) * 3.5))
			piece[m.toX][m.toY] = Chess.QUEEN * movee;
		else
			piece[m.toX][m.toY] = piece[m.fromX][m.fromY];
		piece[m.fromX][m.fromY] = 0;
	}
	public Move[] getAllMoves() {
		//returns a list of all legal moves for current player. (does not test for check.)
		MoveList allMoves = new MoveList(1,1);
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				allMoves.add(getPieceMoves(x, y));
			}
		}	
		return allMoves.toArray();
	}
	public Move[] getPieceMoves(int x, int y) {
		//returns a list of all legal moves for piece at coords (x, y)
		int movee = piece[x][y];
		MoveList movesTemp = new MoveList(10, 10);
		if(movee == Chess.PAWN * currentPlayer) {
			if(piece[x][y-movee] == 0) {
				movesTemp.add(new Move(x, y, x, y - movee, movee));
				if(y == (2.5 * movee + 3.5) && piece[x][y-2*movee] == 0)
					movesTemp.add(new Move(x, y, x, y-2*movee, movee));
			}
			if(x < 7 && piece[x+1][y-movee] != 0 && movee != (piece[x+1][y-movee]/Math.abs(piece[x+1][y-movee])))
				movesTemp.add(new Move(x, y, x+1, y-movee, movee));
			if(x > 0 && piece[x-1][y-movee] != 0 && movee != (piece[x-1][y-movee]/Math.abs(piece[x-1][y-movee])))
				movesTemp.add(new Move(x, y, x-1, y-movee, movee));
		} else if(movee == Chess.KNIGHT * currentPlayer) {
			tryAdd(new Move(x, y, x + 1, y + 2, movee), movesTemp);
			tryAdd(new Move(x, y, x - 1, y + 2, movee), movesTemp);
			tryAdd(new Move(x, y, x + 1, y - 2, movee), movesTemp);
			tryAdd(new Move(x, y, x - 1, y - 2, movee), movesTemp);
			tryAdd(new Move(x, y, x + 2, y + 1, movee), movesTemp);
			tryAdd(new Move(x, y, x - 2, y + 1, movee), movesTemp);
			tryAdd(new Move(x, y, x + 2, y - 1, movee), movesTemp);
			tryAdd(new Move(x, y, x - 2, y - 1, movee), movesTemp);
		} else if(movee == Chess.BISHOP * currentPlayer) {
			movesTemp.add(get1DMoves(x, y, 1, 1));
			movesTemp.add(get1DMoves(x, y, 1, -1));
			movesTemp.add(get1DMoves(x, y, -1, 1));
			movesTemp.add(get1DMoves(x, y, -1, -1));
		} else if(movee == Chess.ROOK * currentPlayer) {
			movesTemp.add(get1DMoves(x, y, 1, 0));
			movesTemp.add(get1DMoves(x, y, -1, 0));
			movesTemp.add(get1DMoves(x, y, 0, 1));
			movesTemp.add(get1DMoves(x, y, 0, -1));
		} else if(movee == Chess.QUEEN * currentPlayer) {
			movesTemp.add(get1DMoves(x, y, 1, 0));
			movesTemp.add(get1DMoves(x, y, -1, 0));
			movesTemp.add(get1DMoves(x, y, 0, 1));
			movesTemp.add(get1DMoves(x, y, 0, -1));
			movesTemp.add(get1DMoves(x, y, 1, 1));
			movesTemp.add(get1DMoves(x, y, 1, -1));
			movesTemp.add(get1DMoves(x, y, -1, 1));
			movesTemp.add(get1DMoves(x, y, -1, -1));
		} else if(movee == Chess.KING * currentPlayer) {
			for(int xd = -1; xd <= 1; xd++) {
				for(int yd = -1; yd <= 1; yd++) {
					tryAdd(new Move(x, y, x+xd, y+yd, movee), movesTemp);
				}
			}
		}
		return movesTemp.toArray();
	}
	public Move[] get1DMoves(int x, int y, int mx, int my) {
		MoveList temp1D = new MoveList(8, 8);
		int brk = 0;
		int movee = piece[x][y];
		for(int i = 1; x+mx*i >= 0 && y+my*i >= 0 && x+mx*i <= 7 && y+my*i <= 7 && brk == 0; i++) {
			int to = piece[x+mx*i][y+my*i];
			if(to == 0) {
				temp1D.add(new Move(x, y, x+mx*i, y+my*i, movee));
			} else {
				if(Math.abs(to)/to != currentPlayer)
					temp1D.add(new Move(x, y, x+mx*i, y+my*i, movee));
				brk = 1;
			}
		}
		return temp1D.toArray();
	}
	public void tryAdd(Move movee, MoveList listee) {
		//checks that destination is on board and does not contain a friendly piece.
		if((movee.toX <= 7 && movee.toX >= 0 && movee.toY <= 7 & movee.toY >= 0) && (piece[movee.toX][movee.toY] * currentPlayer <= 0))
			listee.add(movee);
	}
}
class Chessboard extends Node{
	Chess parent;
	int selectedX, selectedY;
	int border;

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
	public Chessboard(Chess parent) {
		super();
		this.parent = parent;
		newGame();

		WHITE_PAWN = parent.getImage(parent.getCodeBase(), "images/white_pawn.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_PAWN = parent.getImage(parent.getCodeBase(), "images/black_pawn.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		WHITE_KNIGHT = parent.getImage(parent.getCodeBase(), "images/white_knight.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_KNIGHT = parent.getImage(parent.getCodeBase(), "images/black_knight.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		WHITE_BISHOP = parent.getImage(parent.getCodeBase(), "images/white_bishop.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_BISHOP = parent.getImage(parent.getCodeBase(), "images/black_bishop.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		WHITE_ROOK = parent.getImage(parent.getCodeBase(), "images/white_rook.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_ROOK = parent.getImage(parent.getCodeBase(), "images/black_rook.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		WHITE_QUEEN = parent.getImage(parent.getCodeBase(), "images/white_queen.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_QUEEN = parent.getImage(parent.getCodeBase(), "images/black_queen.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		WHITE_KING = parent.getImage(parent.getCodeBase(), "images/white_king.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);
		BLACK_KING = parent.getImage(parent.getCodeBase(), "images/black_king.gif").getScaledInstance(Chess.tileSize, Chess.tileSize, Image.SCALE_SMOOTH);

		border = 2;
	}
	public void newGame() {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				piece[x][y] = 0;
			}
		}
		for(int x = 0; x < 8; x++) {
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
		for(int i = 0; i < Array.getLength(pieceMoves); i++) {
			if(Move.areEqual(pieceMoves[i], tryMove))
				return true;
		}
		return false;
	}
	public void paint(Graphics g) {		
		//Draw each square and its piece if it has one.
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(Math.IEEEremainder(x+y, 2) == 0)
					g.setColor(Chess.COLOR_LIGHT);
				else
					g.setColor(Chess.COLOR_DARK);
				g.fillRect(x * Chess.tileSize, y * Chess.tileSize, Chess.tileSize, Chess.tileSize);
				drawPiece(g, x, y);
			}
		}
		//Highlight selected piece and possible moves	
		if(selectedX != 42 && selectedY != 42) {
			Move[] destinations = getPieceMoves(selectedX, selectedY);
			for(int i = 0; i < Array.getLength(destinations); i++) {
				highlightSquare(g, destinations[i].toX, destinations[i].toY, Chess.COLOR_DESTINATIONS);
			}
			highlightSquare(g, selectedX, selectedY, Chess.COLOR_SELECTED);
		}
	}
	public void drawPiece(Graphics g, int x, int y) {
		switch(piece[x][y]) {
			case (Chess.PAWN * Chess.WHITE): g.drawImage(WHITE_PAWN, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.PAWN * Chess.BLACK): g.drawImage(BLACK_PAWN, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.KNIGHT * Chess.WHITE): g.drawImage(WHITE_KNIGHT, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.KNIGHT * Chess.BLACK): g.drawImage(BLACK_KNIGHT, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.BISHOP * Chess.WHITE): g.drawImage(WHITE_BISHOP, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.BISHOP * Chess.BLACK): g.drawImage(BLACK_BISHOP, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.ROOK * Chess.WHITE): g.drawImage(WHITE_ROOK, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.ROOK * Chess.BLACK): g.drawImage(BLACK_ROOK, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.QUEEN * Chess.WHITE): g.drawImage(WHITE_QUEEN, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.QUEEN * Chess.BLACK): g.drawImage(BLACK_QUEEN, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.KING * Chess.WHITE): g.drawImage(WHITE_KING, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			case (Chess.KING * Chess.BLACK): g.drawImage(BLACK_KING, Chess.tileSize * x, Chess.tileSize * y, parent); break;
			default: break;
		}
	}
	public void highlightSquare(Graphics g, int x, int y, Color color) {
		g.setColor(color);
		g.drawRect(x * Chess.tileSize, y * Chess.tileSize, Chess.tileSize, Chess.tileSize);
		g.drawRect(x * Chess.tileSize - 1, y * Chess.tileSize - 1, Chess.tileSize + 2, Chess.tileSize + 2);

	}
	public void click(MouseEvent e) {
		if(currentPlayer != Chess.human) {
			return;
		}
		int mx = e.getX();
		int my = e.getY();
		int x = 42;
		int y = 42;
		for(int i = 0; i < 8; i++) {
			if(mx > i * Chess.tileSize && mx < (i + 1) * Chess.tileSize)
				x = i;
			if(my > i * Chess.tileSize && my < (i + 1) * Chess.tileSize)
				y = i;
		}
		if(x != 42 && y != 42) {
			if(piece[x][y] != 0 && Math.abs(piece[x][y])/piece[x][y] == currentPlayer) {
				if(x == selectedX && y == selectedY) {
					selectedX = 42;
					selectedY = 42;
				} else {
					selectedX = x;
					selectedY = y;
				}
			} else if (selectedX != 42 && selectedY != 42) {
				Move letsMove = new Move(selectedX, selectedY, x, y, piece[selectedX][selectedY]);
				if(isLegal(letsMove)) {
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

class Move {
	//This represents a move, storing the from/to coords and the piece moving.
	public int fromX, fromY, toX, toY;
	public int piece;
	final static String PIECE_CHARS = " pnbrqk";
	final static String PIECE_FILES = "ABCDEFGH";
	final static String PIECE_RANKS = "87654321";
	public Move(int fromX, int fromY, int toX, int toY, int piece) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.piece = piece;
	}
	public String toString() {
		String returnee = "move is ";
		returnee += Character.toString(PIECE_CHARS.charAt(Math.abs(piece)));
		returnee += Character.toString(PIECE_FILES.charAt(fromX));
		returnee += Integer.toString(fromY);
		returnee += Character.toString(PIECE_FILES.charAt(toX));
		returnee += Integer.toString(toY + 1);
		return returnee;
	}
	public static boolean areEqual(Move m1, Move m2) {
		if(m1.fromX == m2.fromX && m1.fromY == m2.fromY && m1.toX == m2.toX && m1.toY == m2.toY && m1.piece == m2.piece)
			return true;
		else
			return false;
	}
}
class MoveList {
	//This class is a list of Move objects.
	Move[] data;
	int max, increment, size;
	public MoveList(int max, int increment) {
		data = new Move[max];
		this.max = max;
		this.increment = increment;
		size = 0;
	}
	public void add(Move addee) {
		if(size >= max)
			increaseSize(increment);
		data[size] = addee;
		size++;
	}
	public void add(Move[] addee) {
		int addedSize = Array.getLength(addee);
		if((size + addedSize) >= max)
			increaseSize(size + addedSize - max + 1);
		for(int i = 0; i < addedSize; i++) {
			add(addee[i]);
		}
	}
	public void increaseSize(int increase) {
		max = max + increase;
		Move[] tempData = data;
		data = new Move[max];
		for(int i = 0; i < size; i++) {
			data[i] = tempData[i];
		}
	}
	public Move[] toArray() {
		Move[] returnee = new Move[size];
		for(int i = 0; i < size; i++) {
			returnee[i] = data[i];
		}
		return returnee;
	}
}
