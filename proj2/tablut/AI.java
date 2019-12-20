package tablut;
import java.util.ArrayList;
import static java.lang.Math.*;
import static tablut.Board.*;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Huixuan Lin
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (myPiece() == BLACK) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (sense == 1) {
            return findBlack(board, depth, saveMove, alpha, beta);

        } else {
            return findWhite(board, depth, saveMove, alpha, beta);
        }
    }



    /** Return a best move for black piece in BOARD,
     * searching to depth DEPTH. Any move with value >= BETA
     * is also "good enough". Store the best move into _lastFoundMove
     * if SAVEMOVE. Black player won't process a move after which the
     * board's score is less than what the white player knows
     * he can get (ALPHA). */
    private int findBlack(Board board,
                          int depth, boolean saveMove,
                          int alpha, int beta) {
        if (staticScore(board) == -INFTY || depth == 0) {
            return staticScore(board);
        }
        int currentValue = staticScore(board);
        for (Move possible : board.legalMoves(BLACK)) {
            if (_lastFoundMove == null) {
                _lastFoundMove = possible;
            }
            Board copy = new Board(board);
            copy.makeMove(possible);
            int value = findWhite(copy, depth - 1, false, alpha, beta);
            if (value > currentValue && saveMove) {
                _lastFoundMove = possible;
            }
            currentValue = max(value, currentValue);
            alpha = Math.max(currentValue, alpha);
            if (alpha >= beta) {
                break;
            }

        }
        return currentValue;
    }


    /** Return a best move for white piece in BOARD,
     * searching to depth DEPTH. Any move with value <= ALPHA
     * is also "good enough".Store the best move into _lastFoundMove
     * if SAVEMOVE. Black player won't process a move after which the
     * board's score is less than what the white player knows
     * he can get (BETA). */
    private int findWhite(Board board,
                          int depth, boolean saveMove,
                          int alpha, int beta) {
        if (staticScore(board) == INFTY || depth == 0) {
            return staticScore(board);
        }
        int currentValue = staticScore(board);
        ArrayList<Move> legal = new ArrayList<>();
        legal.addAll(board.legalMoves(KING));
        legal.addAll(board.legalMoves(WHITE));
        for (Move possible : legal) {
            if (_lastFoundMove == null) {
                _lastFoundMove = possible;
            }
            Board copy = new Board(board);
            copy.makeMove(possible);
            int value = findBlack(copy, depth - 1, false, alpha, beta);
            if (value < currentValue && saveMove) {
                _lastFoundMove = possible;
            }
            currentValue = min(value, currentValue);
            beta = Math.min(currentValue, beta);
            if (alpha >= beta) {
                break;
            }

        }
        return currentValue;
    }


    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        final int move = 16;
        if (board.moveCount() < move) {
            return 4;
        } else {
            return 3;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        final int whiteWeight = 5;
        final int blackWeight = 3;
        Square king = board.kingPosition();
        if (board.winner() == BLACK) {
            return INFTY;
        }
        if (board.winner() == WHITE) {
            return -INFTY;
        }
        int white = board.pieceLocations(WHITE).size();
        int black = board.pieceLocations(BLACK).size();
        int value = black * blackWeight - white * whiteWeight;
        if (king == THRONE) {
            value += 0;
        } else if (king == NTHRONE
                || king == STHRONE || king == ETHRONE
                || king == WTHRONE) {
            value += 1;
        } else {
            value += 2;
        }
        for (Square sq : board.surround(king)) {
            if (board.get(sq) == BLACK) {
                value += 4;
            } else {
                value -= 2;
            }
        }
        for (Square sq : Square.diagonal(king)) {
            if (board.get(sq) == BLACK) {
                value += 3;
            } else {
                value -= 1;
            }
        }
        value += 10 * king.distanceToEdge();
        return value;
    }

}
