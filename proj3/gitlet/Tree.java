package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;



/** Class for tree which point to file.
 * @author Huixuan Lin
 */
public class Tree implements Serializable {

    /** Constructor fo tree. */
    public Tree() {
        _staged = new HashMap<>();
        _stagedName = new TreeSet<>();
        _tracked = new HashMap<>();
        _removal = new TreeSet<>();
        _treeSha1 = "no tree";
    }

    /** Add BLOB to the tree, waiting to be committed. */
    public void add(Blob blob) throws IOException {
        blob.saveContent();
        _staged.put(blob.getName(), blob.getSha1());
        _stagedName.add(blob.getName());

    }

    /** Mark file with FILENAME to be removed. */
    public void toRemove(String fileName) {
        _removal.add(fileName);
        _tracked.remove(fileName);
    }

    /** Unstage the file with FILENAME if it is currently in the tree. */
    public void rm(String fileName) {
        _staged.remove(fileName);
        _stagedName.remove(fileName);

    }

    /** After committing, make all staged files unstaged. */
    public void commit() {
        _tracked.putAll(_staged);
        Object[] fileSha1 = _tracked.keySet().toArray();
        _treeSha1 = Utils.sha1(fileSha1);
        _staged.clear();
        _stagedName.clear();
        _removal.clear();
    }

    /** Return true if all tracked files are staged. */
    public boolean allStaged() {
        return _staged.isEmpty() && _removal.isEmpty();
    }

    /** Return the collection of staged files. */
    public HashMap<String, String> getStaged() {
        return _staged;
    }
    /** Return the ordered name of staged files. */
    public Set<String> getStagedName() {
        return _stagedName;
    }

    /** Return the collection of tracked files. */
    public HashMap<String, String> getTracked() {
        return _tracked;
    }

    /** Return the collection of all file names
     *  which are going to be removed. */
    public Set<String> getRemoval() {
        return _removal;
    }
    /** Return the ash1 code for tracked files. */
    public String getSha1() {
        return _treeSha1;
    }

    /** Set the tracked files of this tree into TRACKED. */
    public void setTracked(HashMap<String, String> tracked) {
        _tracked.putAll(tracked);
    }
    /** Save a tree to a file as future use. */
    public void saveTree() throws IOException {
        Utils.join(Main.OBJECT, getSha1()).createNewFile();
        Utils.writeObject(Utils.join(Main.OBJECT, getSha1()), this);
    }

    /** Reads in and deserializes a tree from a file with SHA1 code.
     * @param  sha1 of tree to load
     * @return Tree read from file */
    public static Tree fromFile(String sha1) {
        File treeFile = Utils.join(Main.OBJECT, sha1);
        if (!treeFile.exists()) {
            throw new IllegalArgumentException(
                    "No tree file with that name found.");
        }
        return Utils.readObject(treeFile, Tree.class);
    }


    /** Collection of tracked files. Key is the file name,
     *  value is the sha1 code for corresponding file. */
    private HashMap<String, String> _tracked;
    /** Collection of staged files. Key is the file name,
     * value is the sha1 code for corresponding file. */
    private HashMap<String, String> _staged;
    /** Sorted set of staged files' name. */
    private SortedSet<String> _stagedName;
    /** Collection of files going to be removed. */
    private SortedSet<String> _removal;
    /** SHA1 code for this tree. */
    private String _treeSha1;
}
