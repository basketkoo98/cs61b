package enigma;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Arrays;

import static enigma.EnigmaException.*;


/** Enigma simulator.
 *  @author Huixuan Lin
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }
        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        readConfig();
        boolean set = false;
        while (_input.hasNextLine()) {
            String information = _input.nextLine();
            Scanner scanInformation = new Scanner(information);
            if (information.startsWith("*")) {
                set = true;
                scanInformation.next();
                String[] rotors = new String[_machine.numRotors()];
                for (int i = 0; i < _machine.numRotors(); i++) {
                    rotors[i] = scanInformation.next();
                }
                HashSet<String> checkDuplicated
                        = new HashSet<>(Arrays.asList(rotors));
                if (rotors.length != checkDuplicated.size()) {
                    throw error("A rotor might "
                            + "be repeated in the setting line");
                }
                _machine.insertRotors(rotors);
                String setting = scanInformation.next();
                if (setting.length() != _machine.numRotors() - 1) {
                    throw error("The setting "
                            + "information is not enough", setting);
                }
                setUp(_machine, setting);
                String cycle = "";
                boolean ring = false;
                while (scanInformation.hasNext()) {
                    String next = scanInformation.next();
                    if (next.contains("(")) {
                        cycle += next;
                        cycle += " ";
                    } else {
                        if (!ring) {
                            _machine.setAlphabetRing(next);
                            setUp(_machine, setting);
                            ring = true;
                        } else {
                            throw error("Wrong format of plugboard");
                        }
                    }
                }
                _machine.setPlugboard(new Permutation(cycle, _alphabet));
            } else if (information.trim().isEmpty()) {
                _output.append("\n");
            } else {
                if (set) {
                    String output = _machine.convert(information);
                    printMessageLine(output);
                } else {
                    throw error("The input might not start with a setting.");
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private void readConfig() {
        try {
            _alphabet = new Alphabet(_config.next().trim());
            if (!InputPattern.checkMatch(InputPattern.ALPHABET,
                    _alphabet.getAlphabet())) {
                throw error("Wrong format of alphabet", _alphabet);
            }
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            if (numRotors <= pawls) {
                throw error("The number of pawls must be less"
                        + " or equal to the number of rotors", pawls);
            }
            int rotor = 0;
            HashSet<String> rotorName = new HashSet<>();
            String name = _config.next();
            while (_config.hasNext()) {
                if (!InputPattern.checkMatch(InputPattern.ROTORNAME, name)) {
                    throw error("Rotor might be misnamed", name);
                }
                String information = _config.next();
                String type;
                String notches = "";
                if (!InputPattern.checkMatch(InputPattern
                        .ROTORTYPE, information)) {
                    throw error("Wrong format of rotor's type and notch");
                }
                type = checkType(information);
                notches = information.substring(1);
                for (int index = 0; index < notches.length(); index++) {
                    if (!_alphabet.contains(notches.charAt(index))) {
                        throw error("The charactor of notch must in "
                                + "rotor's ring", notches);
                    }
                }
                String permutation = "";
                String next = _config.next();
                while (next.contains("(")) {
                    permutation =  permutation + next + " ";
                    if (_config.hasNext()) {
                        next = _config.next();
                    } else {
                        break;
                    }
                }
                addRotor(type, name, permutation, notches);
                rotorName.add(name);
                rotor += 1;
                name = next;
            }
            if (rotor != rotorName.size()) {
                throw error("A rotor might be"
                        + " repeated in the configuration.");
            }
            _machine = new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Add rotor to allRotor according to the TYPE of rotor, rotor's NAME,
     *  PERMUTATION of rotor and NOTCHES of rotor. */
    private void addRotor(String type, String name,
                          String permutation, String notches) {
        if (type.equals("reflector")) {
            allRotors.add(new Reflector(name,
                    new Permutation(permutation,
                            new Alphabet(_alphabet.getAlphabet()))));
        } else if (type.equals("fixedRotor")) {
            allRotors.add(new FixedRotor(name,
                    new Permutation(permutation,
                            new Alphabet(_alphabet.getAlphabet()))));
        } else {
            allRotors.add(new MovingRotor(name,
                    new Permutation(permutation,
                            new Alphabet(_alphabet.getAlphabet())), notches));
        }
    }

    /** Return TYPE of rotor according to INFORMATION. */
    private String checkType(String information) {
        String type;
        if (information.startsWith("M")) {
            type = "movingRotor";
        } else if (information.equals("R")) {
            type = "reflector";
        } else if (information.equals("N")) {
            type = "fixedRotor";
        } else {
            throw error("Rotor's type must be R, N, or M");
        }
        return type;
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            return allRotors.iterator().next();
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        while (msg.length() != 0) {
            if (msg.length() > 5) {
                _output.append(msg.substring(0, 5) + " ");
                msg = msg.substring(5);
            } else {
                _output.append(msg + "\n");
                break;
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Current machine. */
    private Machine _machine;

    /** All possible rotors. */
    private HashSet<Rotor> allRotors = new HashSet<Rotor>();
}
