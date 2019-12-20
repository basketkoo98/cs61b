package enigma;

import java.util.HashSet;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Huixuan Lin
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int starFind = 0;
        int endFind = 0;
        while (cycles.indexOf("(", starFind) != -1) {
            endFind = cycles.indexOf(")", starFind);
            String cycle = "";
            if (_cycles.size() == 0) {
                cycle = cycles.substring(starFind + 1, endFind);
            } else {
                cycle = cycles.substring(starFind + 2, endFind);
            }
            for (int index = 0; index < cycle.length(); index++) {
                if (!alphabet.contains(cycle.charAt(index))) {
                    error("The character in permutation"
                            + "must in the alphabet", cycles);
                }
            }
            addCycle(cycle);
            starFind = endFind + 1;
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return permute(c - size());
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycles != null) {
            for (String cycle : _cycles) {
                if (cycle.indexOf(p) > -1
                        && cycle.indexOf(p) < cycle.length() - 1) {
                    return cycle.charAt(cycle.indexOf(p) + 1);
                }
                if (cycle.indexOf(p) == cycle.length() - 1) {
                    return cycle.charAt(0);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (_cycles != null) {
            for (String cycle : _cycles) {
                if (cycle.indexOf(c) > 0) {
                    return cycle.charAt(cycle.indexOf(c) - 1);
                }
                if (cycle.indexOf(c) == 0) {
                    return cycle.charAt(cycle.length() - 1);
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_cycles != null) {
            String toCheck = "";
            for (String cycle : _cycles) {
                toCheck += cycle;
            }
            for (int index = 0; index < _alphabet.size(); index++) {
                if (toCheck.indexOf(_alphabet.toChar(index)) == -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private HashSet<String> _cycles = new HashSet<>();

}
