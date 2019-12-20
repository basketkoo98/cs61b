package enigma;

import java.util.HashMap;
import java.util.Collection;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Huixuan Lin
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int number = 1;
        int fixed = 0;
        boolean hasInsertMoving = false;
        for (String rotor : rotors) {
            boolean hasFound = false;
            for (Rotor possible : _allRotors) {
                if (possible.name().equals(rotor)) {
                    if (number == 1) {
                        if (!possible.reflecting()) {
                            throw error("The first rotor must be a reflector.");
                        }
                    }
                    if (possible.rotates()) {
                        hasInsertMoving = true;
                    } else {
                        fixed += 1;
                    }
                    if (hasInsertMoving & !possible.rotates()) {
                        throw error("Non-moving rotor should"
                                + " be at the left of moving rotor");
                    }
                    _myRotors.put(number, possible);
                    hasFound = true;
                }
            }
            if (!hasFound) {
                throw error("Can not find such rotor");
            }
            number += 1;
        }
        if (fixed != numRotors() - numPawls()) {
            throw error("The number of moving rotors has "
                    + "conflict with the given number of pawls");
        }
    }

    /** Set my rotors' alphabets' ring according to RINGSETTING, which
     *  must be a string of numRotors()-1 characters in my alphabet. The
     *  first letter refers to the leftmost rotor setting (not counting
     *  the reflector). */
    void setAlphabetRing(String ringSetting) {
        for (int index = ringSetting.length() - 1; index >= 0; index--) {
            try {
                _myRotors.get(_myRotors.size() - ringSetting.length()
                        + index + 1).permutation()
                        .alphabet().setRing(ringSetting.charAt(index));
            } catch (StringIndexOutOfBoundsException exce) {
                throw error("The character of ring "
                        + "setting must be in the alphabet", ringSetting);
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int index = setting.length() - 1; index >= 0; index--) {
            try {
                _myRotors.get(_myRotors.size() - setting.length()
                        + index + 1).set(setting.charAt(index));
            } catch (EnigmaException excp) {
                throw error("The character "
                        + "of setting must be in the alphabet", setting);
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int input = _alphabet.toInt(_plugboard.permute(_alphabet.toChar(c)));
        for (int index = _numRotors; index > 0; index--) {
            input = _myRotors.get(index).permutation().wrap(input
                    + _myRotors.get(index).setting());
            int output = _alphabet.toInt(_myRotors.get(index)
                    .permutation().permute(_alphabet.toChar(input)));
            output = _myRotors.get(index).permutation()
                    .wrap(output - _myRotors.get(index).setting());
            input = output;
        }
        for (int index = 2; index <= _numRotors; index++) {
            input = _myRotors.get(index).permutation()
                    .wrap(input + _myRotors.get(index).setting());
            int output = _alphabet.toInt(_myRotors.get(index)
                    .permutation().invert(_alphabet.toChar(input)));
            output = _myRotors.get(index).permutation()
                    .wrap(output - _myRotors.get(index).setting());
            input = output;
        }
        int output = _alphabet.toInt(_plugboard
                .invert(_alphabet.toChar(input)));
        return output;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        msg = msg.trim();
        while (msg.length() != 0) {
            char toInput = msg.charAt(0);
            rotate();
            char output = _alphabet.toChar(convert(_alphabet.toInt(toInput)));
            result += output;
            if (msg.length() == 1) {
                break;
            } else {
                msg = msg.substring(1);
                msg = msg.trim();
            }
        }
        return result;
    }

    /** Rotate my rotors according to the rules. */
    void rotate() {
        boolean[] notch = new boolean[_numRotors + 1];
        notch[_numRotors] = true;
        for (int index = _numRotors; index > _numRotors - _pawls; index--) {
            notch[index - 1] = _myRotors.get(index).atNotch();
        }
        for (int index = _numRotors; index > _numRotors - _pawls; index--) {
            if ((notch[index] & _myRotors.get(index).rotates())
                    | (notch[index - 1] & _myRotors.get(index - 1).rotates())) {
                _myRotors.get(index).advance();
            }

        }
    }
    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of my rotors. */
    private int _numRotors;

    /** Number of my pawls. */
    private int _pawls;

    /** Pumutation of my plugboard. */
    private Permutation _plugboard;

    /** Collection of allRotors. */
    private Collection<Rotor> _allRotors;

    /** My rotor slots. */
    private HashMap<Integer, Rotor> _myRotors = new HashMap<Integer, Rotor>();


}
