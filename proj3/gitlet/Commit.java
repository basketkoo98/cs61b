package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;

/** The class of commit, point to a tree.
 * @author Huixuan Lin
 */
public class Commit implements Serializable {

    /** Constructor of commit with tree TRACKED and MESSAGE.*/
    public Commit(Tree tracked, String message) {
        if (tracked == null) {
            _tree = new Tree();
            _treeSha1 = _tree.getSha1();
            _files = new HashMap<>();
        } else {
            _tree = tracked;
            _treeSha1 = tracked.getSha1();
            _files = tracked.getTracked();
        }
        _ancestors = new HashSet<>();
        _message = message;
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("EEE LLL d HH:mm:ss y Z");
        _timeStamps = dateTime.format(formatter);
        _commitSha1 = Utils.sha1(_treeSha1, message);
    }


    /** Return true if this commit has track any files. */
    public boolean hasFile() {
        return _files != null && !_files.isEmpty();
    }
    /** Return all committed file. Key is the
     *  file name, value is the sha1 code for corresponding file. */
    public HashMap<String, String> getFiles() {
        return _files;
    }

    /** Return the tree to which this commit point to. */
    public Tree getTree() {
        return _tree;
    }

    /** Return parent commit of this commit. */
    public Commit getParent() {
        return _parent;
    }

    /** Return second parent commit of this commit. */
    public Commit getSecondParent() {
        return _secondParent;
    }

    /** Set parent commit of this commit into PARENT. */
    public void setParent(Commit parent) {
        _parent = parent;
    }

    /** Set second parent commit of this commit into SECONDPARENT. */
    public void setSecondParent(Commit secondParent) {
        _secondParent = secondParent;
    }

    /** Add ANCESTORS to this commit. */
    public void addAncestors(Collection<String> ancestors) {
        _ancestors.addAll(ancestors);
    }

    /** Add ANCESTOR to this commit. */
    public void addAncestor(String ancestor) {
        _ancestors.add(ancestor);
    }

    /** Return the ancestors of this commit. */
    public HashSet<String> getAncestors() {
        return _ancestors;
    }
    /** Return true if COMMIT is a ancestor of this. */
    public boolean isAncestor(String commit) {
        return _ancestors.contains(commit);
    }
    /** Get commit message of this commit.
     * @return the commit message. */
    public String getMessage() {
        return _message;
    }

    /** Get time stamps of this commit.
     * @return The time stanps. */
    public String getTimeStamps() {
        return _timeStamps;
    }

    /** Return the ash1 code for current commit. */
    public String getCommitSha1() {
        return _commitSha1;
    }

    public String getFileSha1() {
        return _treeSha1;
    }

    /** Return the first seven digits of commit ids. */
    public String getId() {
        return getCommitSha1().substring(0, 7);
    }

    /** Set _merged into true. */
    public void merge() {
        _merged = true;
    }
    /** Return true if this is merge commit. */
    public boolean isMerged() {
        return _merged;
    }

    /** Save a commit to a file as future use. */
    public void saveCommit() throws IOException {
        Utils.join(Main.OBJECT, getCommitSha1()).createNewFile();
        Utils.writeObject(Utils.join(Main.OBJECT, getCommitSha1()), this);
    }

    /** Reads in and deserializes a commit from a file with SHA1 code.
     * @param  sha1 of commit to load
     * @return Commit read from file */
    public static Commit fromFile(String sha1) {
        File commitFile = Utils.join(Main.OBJECT, sha1);
        if (!commitFile.exists()) {
            throw new IllegalArgumentException(
                    "No tree file with that name found.");
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    /** Collection of blobs. Key is the file name,
     * value is the sha1 code for corresponding file. */
    private HashMap<String, String> _files;
    /** The first parent commit of this. */
    private Commit _parent;
    /** The tree to which this commit point to. */
    private Tree _tree;
    /** The second parent commit of this. */
    private Commit _secondParent;
    /** Commit message of this. */
    private String _message;
    /** The time when committing. */
    private String _timeStamps;
    /** SHA1 code of tree to which this commit point to. */
    private String _treeSha1;
    /** SHA1 code of this commit. */
    private String _commitSha1;
    /** The state of merge. */
    private boolean _merged;
    /** The sha1 code of ancestors of this commits. */
    private HashSet<String> _ancestors;

}
