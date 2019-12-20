package tablut;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Formatter;

import static tablut.Utils.*;
import static tablut.Move.ROOK_MOVES;
import static tablut.Piece.*;
import static tablut.Square.*;


/** The state of a Tablut Game.
 *  @author Huixuan Lin
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;


    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial position of king. */
    static final Square[] INITIAL_KING = {
        THRONE
    };

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _moveCount = model.moveCount();
        _turn = model.turn();
        _locations.put(BLACK, model.pieceLocations(BLACK));
        _locations.put(WHITE, model.pieceLocations(WHITE));
        _locations.put(KING, model.pieceLocations(KING));

    }

    /** Copies LOCATION into me. */
    void copy(HashMap<Piece, HashSet<Square>> location) {
        _locations.put(BLACK, new HashSet<>(location.get(BLACK)));
        _locations.put(WHITE, new HashSet<>(location.get(WHITE)));
        _locations.put(KING, new HashSet<>(location.get(KING)));
    }

    /** Copies LOCATION into a new hash map and return. */
    HashMap<Piece, HashSet<Square>> copyMap(HashMap<Piece,
            HashSet<Square>> location) {
        HashMap<Piece, HashSet<Square>> copied = new HashMap<>();
        copied.put(BLACK, new HashSet<>(location.get(BLACK)));
        copied.put(WHITE, new HashSet<>(location.get(WHITE)));
        copied.put(KING, new HashSet<>(location.get(KING)));
        return copied;
    }

    /** Clears the board to the initial position. */
    void init() {
        _moveCount = 0;
        _turn = BLACK;
        _winner = null;
        _repeated = false;
        _limit = 0;
        _locations.put(BLACK, new HashSet<>());
        _locations.put(WHITE, new HashSet<>());
        _locations.put(KING, new HashSet<>());
        _previous.put(WHITE, new ArrayList<>());
        _previous.put(BLACK, new ArrayList<>());
        for (Square position : INITIAL_ATTACKERS) {
            _locations.get(BLACK).add(position);
        }
        for (Square position : INITIAL_DEFENDERS) {
            _locations.get(WHITE).add(position);
        }
        for (Square position : INITIAL_KING) {
            _locations.get(KING).add(position);
        }
        clearUndo();
        checkRepeated();
    }

    /** Set the move limit to N.  It is an error
     * if 2*LIM <= moveCount(). */
    void setMoveLimit(int n) {
        if (2 * n <= _moveCount) {
            throw error("either player has already made at least %d moves", n);
        } else {
            _limit = n;
        }
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (_previous.get(_turn).contains(encodedBoard())) {
            _winner = _turn;
            _repeated = true;
        }
        _previous.get(_turn).add(encodedBoard());
        _modules.add(copyMap(_locations));
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        if (_locations.get(KING).isEmpty()) {
            return null;
        } else {
            return _locations.get(KING).iterator().next();
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        if (_locations.get(BLACK).contains(sq(col, row))) {
            return BLACK;
        }
        if (_locations.get(WHITE).contains(sq(col, row))) {
            return WHITE;
        }
        if (_locations.get(KING).contains(sq(col, row))) {
            return KING;
        }
        return EMPTY;
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(row - '1', col - 'a');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _locations.get(BLACK).remove(s);
        _locations.get(WHITE).remove(s);
        _locations.get(KING).remove(s);
        if (p != EMPTY) {
            _locations.get(p).add(s);
        }
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
        _moveCount++;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return a SqList which contains all Squares pass when
     * moving FROM to TO. Assuming this is a rook move.
     */
    SqList pass(Square from, Square to) {
        SqList passSquare = new SqList();
        int direction = from.direction(to);
        for (int step = 1; true; step++) {
            Square possible = from.rookMove(direction, step);
            if (possible != to) {
                passSquare.add(possible);
                continue;
            }
            break;
        }
        return passSquare;
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        return from.isRookMove(to)
                && pass(from, to).isEmpty();
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from) == BLACK ? _turn
                == BLACK : _turn == WHITE;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (get(from) == EMPTY) {
            return false;
        }
        if (!from.isRookMove(to)) {
            return false;
        }
        for (Square pass : pass(from, to)) {
            if (get(pass) != EMPTY) {
                return false;
            }
        }
        if (get(to) != EMPTY) {
            return false;
        }
        if (get(from) == WHITE && to == THRONE) {
            return false;
        }
        return isLegal(from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        _moveCount++;
        Piece piece = get(from);
        put(EMPTY, from);
        put(piece, to);
        Piece nowPlay = _turn;
        for (Square possible : vOpposite(to)) {
            if (canBeCaptured(possible)) {
                capture(possible);
            }
        }
        for (Square possible : hOpposite(to)) {
            if (canBeCaptured(possible)) {
                capture(possible);
            }
        }

        _turn = _turn == BLACK ? WHITE : BLACK;
        checkRepeated();
        if (_locations.get(KING).isEmpty()) {
            _winner = BLACK;
        } else if (kingPosition().isEdge()) {
            _winner = WHITE;
        } else if (!hasMove(_turn)) {
            _winner = nowPlay;
        } else if (_limit != 0) {
            if (_moveCount >= 2 * _limit) {
                _winner = nowPlay;
            }
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** return true if POSSIBLE is a hostile square for SQ. */
    private boolean isHostile(Square possible, Square sq) {
        if (possible == THRONE && get(possible) == EMPTY) {
            return true;
        }
        if (get(sq) == BLACK) {
            if (get(possible).opponent() == BLACK) {
                return true;
            }
        } else {
            return get(possible) == BLACK;

        }
        return false;
    }

    /** return two vertical opposite squares which enclose SQUARE. */
    private HashSet<Square> vOpposite(Square square) {
        HashSet<Square> result = new HashSet<>();
        if (square.row() == 0) {
            result.add(sq(square.col(), 1));
        } else if (square.row() == BOARD_SIZE - 1) {
            result.add(sq(square.col(), 7));
        } else {
            result.add(sq(square.col(), square.row() + 1));
            result.add(sq(square.col(), square.row() - 1));
        }
        return result;
    }

    /** return two horizontal opposite squares which enclose SQUARE. */
    private HashSet<Square> hOpposite(Square square) {
        HashSet<Square> result = new HashSet<>();
        if (square.col() == 0) {
            result.add(sq(1, square.row()));
        } else if (square.col() == BOARD_SIZE - 1) {
            result.add(sq(7, square.row()));
        } else {
            result.add(sq(square.col() + 1, square.row()));
            result.add(sq(square.col() - 1, square.row()));
        }
        return result;
    }

    /** return four surrounded squares of SQUARE. */
    HashSet<Square> surround(Square square) {
        HashSet<Square> result = new HashSet<>();
        result.addAll(hOpposite(square));
        result.addAll(vOpposite(square));
        return result;
    }

    /** return ture if the piece at SQ satisfies the condition
     * to be captured. */
    private boolean canBeCaptured(Square sq) {
        if (get(sq) == KING
                && (sq == THRONE
                || sq == NTHRONE
                || sq == STHRONE
                || sq == ETHRONE
                || sq == WTHRONE)) {
            for (Square adjacent : surround(sq)) {
                if (!isHostile(adjacent, sq)) {
                    return false;
                }
            }
            return true;
        }
        if (vOpposite(sq).size() == 2) {
            boolean capture = true;
            for (Square adjacent : vOpposite(sq)) {
                if (!isHostile(adjacent, sq)) {
                    capture = false;
                }
            }
            if (capture) {
                return capture;
            }
        }
        if (hOpposite(sq).size() == 2) {
            boolean capture = true;
            for (Square adjacent : hOpposite(sq)) {
                if (!isHostile(adjacent, sq)) {
                    capture = false;
                }
            }
            if (capture) {
                return capture;
            }
        }
        return false;
    }

    /** Capture the piece at SQ. */
    private void capture(Square sq) {
        put(EMPTY, sq);
    }
    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        put(EMPTY, sq0.between(sq2));
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _moveCount--;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    void undoPosition() {
        if (!_repeated && _moveCount != 0) {
            _previous.get(_turn).remove(_previous.get(_turn).size() - 1);
            _modules.remove(_modules.size() - 1);
        }
        _turn = _turn == BLACK ? WHITE : BLACK;
        copy(_modules.get(_modules.size() - 1));
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _previous.get(WHITE).clear();
        _previous.get(BLACK).clear();
        _modules.clear();
        _moveCount = 0;
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> result = new ArrayList<>();
        for (Move.MoveList[] list : ROOK_MOVES) {
            for (int d = 0; d < 4; d++) {
                for (Move possible : list[d]) {
                    if (isLegal(possible)
                            && get(possible.from()) == side) {
                        result.add(possible);
                    }
                }
            }

        }
        return result;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        if (side == BLACK) {
            return !legalMoves(BLACK).isEmpty();
        } else {
            return !legalMoves(WHITE).isEmpty()
                    || !legalMoves(KING).isEmpty();
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        return new HashSet<>(_locations.get(side));
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** The current players' locations. */
    private HashMap<Piece, HashSet<Square>> _locations = new HashMap<>();
    /** The move limit of this board. */
    private int _limit;
    /** The previous position of board. */
    private HashMap<Piece, ArrayList<String>> _previous = new HashMap<>();
    /** The previous locations of board. */
    private ArrayList<HashMap<Piece,
            HashSet<Square>>> _modules = new ArrayList<>();


}
