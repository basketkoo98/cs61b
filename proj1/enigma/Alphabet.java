package enigma;

import static enigma.EnigmaException.*;
/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Huixuan Lin
 */
class Alphabet {
    /** The alphabet that THIS uses. */
    private String alphabet;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        alphabet = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Set alphabet's ring that WORD becomes the first order. */
    void setRing(char word) {
        int index = alphabet.indexOf(word);
        if (index != 0) {
            alphabet = alphabet.substring(index) + alphabet.substring(0, index);
        }
    }

    /** Returns the alphabet. */
    String getAlphabet() {
        return alphabet;
    }
    /** Returns the size of the alphabet. */
    int size() {
        return alphabet.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return alphabet.indexOf(ch) != -1 ? true : false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return alphabet.charAt(index);
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int index = alphabet.indexOf(ch);
        if (index == -1) {
            throw error("The input must be in the alphabet");
        }
        return index;
    }
}
