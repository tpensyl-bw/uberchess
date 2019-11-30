//Copyright 2005, Tommy Pensyl, All Rights Reserved.
//Version .01
//Pretty much still writing EVERYTHING...
//

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.reflect.*;

public class Chess extends Applet implements Runnable {
	final static int BLACK = -1;
	final static int WHITE = 1;

	final static int PAWN = 1;
	final static int KNIGHT = 2;
	final static int BISHOP = 3;
	final static int ROOK = 4;
	final static int QUEEN = 5;
	final static int KING = 6;

	static int boardSize; 
	static int tileSize;

	String testString;

	Chessboard board;
	Thread drawFrame;
	BufferedImage bufferImage;
	Graphics2D buffer;

//	MouseGetter mouseInput;


	public void init() {

	testString = "default testString";

//SAMPLE CODE TESTING AREA
Move testMove = new Move(0, 0, 2, 2, BISHOP);
testString = testMove.toString();
//

//		mouseInput = new MouseGetter(this); 
//		addMouseListener(mouseInput);
//		addMouseMotionListener(mouseInput);

		boardSize = this.getWidth();
		tileSize = Math.round(boardSize / 8);

		board = new Chessboard();
		addMouseListener(board);
		bufferImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		buffer = bufferImage.createGraphics();
	}
	public void start() {
		if(drawFrame == null) {	
			drawFrame = new Thread(this);
			drawFrame.start();
		}
	}
	public void run() {
		Thread thisThread = Thread.currentThread();

		while (drawFrame == thisThread) {
/*			do stuffles
///AI!!!!		-if turn=computer: ???
			-if turn=player		
*/
			repaint();
			
			try {
				thisThread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}
	public void stop() {
		drawFrame = null;
	}
	public void update(Graphics g) {
		paint(buffer);
		g.drawImage(bufferImage, 0, 0, this);
	}
	public void paint(Graphics g) {
		board.paint(g);

		g.setColor(Color.BLUE);
		g.drawString(testString, 10, 20);
	}

//Using board directly as mousegetter so this is unneeded:
/*	class MouseGetter extends MouseAdapter implements MouseMotionListener {
		Chess receiver;
		public MouseGetter(Chess receiver) {
			this.receiver = receiver;
		}
		public void mousePressed(MouseEvent e) {
			//board.
		}
		public void mouseDragged(MouseEvent e) {
		}
		public void mouseMoved(MouseEvent e) {
		}
	}*/
}

class Chessboard extends MouseAdapter implements MouseMotionListener{
	final static Color COLOR_LIGHT = Color.LIGHT_GRAY;
	final static Color COLOR_DARK = Color.DARK_GRAY; 

	int[][] piece;
	public Chessboard() {
		piece = new int[8][8];
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
//add rest of pieces...
	}
	public Move[] getAllMoves() {
		int totalMoves = 0;
		Move[] allMoves = new Move[2];
		for(int x = 0; x < 8; x++) {
			for(int y = 2; y < 6; y++) {
				Move[] pieceMoves = getPieceMoves(piece[x][y]);
				int someMoves = Array.getLength(pieceMoves);
				for(int i = 0; i < someMoves; i++) {
					allMoves[totalMoves+someMoves] = pieceMoves[someMoves];
				}
				totalMoves =+ someMoves;
			}
		}	
		return allMoves;
	}
	public Move[] getPieceMoves(int movee) {
		Move[] moves = new Move[2];

//generate individual piece's moves
		return moves;
	}
	public void isLegal(Move move) {
		Move[] allMoves = getAllMoves();

		int movee = piece[move.fromX][move.fromY];
	}
	public void paint(Graphics g) {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
//which square/color in corner??
				if(Math.IEEEremainder(x+y, 2) == 0)
					g.setColor(COLOR_LIGHT);
				else
					g.setColor(COLOR_DARK);
				g.fillRect(x * Chess.tileSize, y * Chess.tileSize, Chess.tileSize, Chess.tileSize);
//draw pieces:
				
//TILE DEBUGGING HERE!!!
//				g.setColor(Color.BLUE);
//				g.drawString(Double.toString(x/2), x * Chess.tileSize, y * Chess.tileSize);
			}
		}
		//draw all tiles, draw pieces, draw selected square.
	}
	public void mousePressed(MouseEvent e) {
		//check which tile it clicked
	}
	public void mouseMoved(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
}

class Move {
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
}
class Coord {
	public double x, y;
	public Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}
}