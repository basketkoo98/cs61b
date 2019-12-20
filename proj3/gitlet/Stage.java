package gitlet;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/** The staging area for gitlet.
 * @author Huixuan Lin
 */
public class Stage implements Serializable {
    /** Constructor of stage under BRANCH. */
    public Stage(String branch) {
        _tracked = new HashMap<>();
        _tree = new LinkedList<>();
        _branch = branch.replace("/", "-");
    }

    /** Copy information except branch name from STAGE to this. */
    public void copy(Stage stage) {
        _tree = stage.getTreeLink();
        _tracked = stage.getTracked();
    }

    /** Add BLOB to the staging area, waiting to be committed. */
    public void add(Blob blob) throws IOException {
        if (!hasTree()) {
            Tree currentTree = new Tree();
            currentTree.setTracked(_tracked);
            _tree.add(currentTree);
        }
        _tree.get(0).add(blob);
        updateTracked();
    }

    /** Mark file with FILENAME to be removed. */
    public void toRemove(String fileName) {
        if (!hasTree()) {
            Tree currentTree = new Tree();
            currentTree.setTracked(_tracked);
            _tree.add(currentTree);
        }
        _tree.get(0).toRemove(fileName);
        updateTracked();
    }

    /** Unstage the file with FILENAME if it is currently staged. */
    public void rm(String fileName) {
        if (hasTree()) {
            _tree.get(0).rm(fileName);
            updateTracked();
        }
    }

    /** After committing, make all staged files unstaged. */
    public void commit() {
        if (hasTree()) {
            _tree.get(0).commit();
            updateTracked();
        }
    }

    /** Return true if all tracked files are staged. */
    public boolean allStaged() {
        if (!hasTree()) {
            return true;
        } else {
            return _tree.get(0).allStaged();
        }
    }

    /** Return true if there is a tree in staging area. */
    public boolean hasTree() {
        return _tree != null && !_tree.isEmpty();
    }
    /** Clear the current tree. */
    public void clear() {
        _tree.clear();
    }

    /** Return the collection of staged files. */
    public HashMap<String, String> getStaged() {
        return _tree.get(0).getStaged();
    }

    /** Update the tracked file of the staging area. */
    public void updateTracked() {
        _tracked.clear();
        _tracked.putAll(_tree.get(0).getTracked());
    }

    /** Return the ordered name of staged files. */
    public Set<String> getStagedName() {
        return _tree.get(0).getStagedName();
    }

    /** Return the collection of tracked files. */
    public HashMap<String, String> getTracked() {
        return _tracked;
    }

    /** Return the collection of all file names
     * which are going to be removed. */
    public Set<String> getRemoval() {
        return _tree.get(0).getRemoval();
    }
    /** Return the ash1 code for tracked files. */
    public String getSha1() {
        return _tree.get(0).getSha1();
    }

    /** Return the current tree to be committed. */
    public Tree getTree() {
        return hasTree() ? _tree.get(0) : null;
    }

    /** Return the tree link. */
    public LinkedList<Tree> getTreeLink() {
        return _tree;
    }
    /** Save a stage as current branch to a file as future use. */
    public void saveStage() throws IOException {
        saveStage(_branch);
    }

    /** Save a stage as BRANCH to a file as future use. */
    public void saveStage(String branch) throws IOException {
        Utils.join(Main.STAGE, branch).createNewFile();
        _branch = branch;
        Utils.writeObject(Utils.join(Main.STAGE, branch), this);
    }

    /** Reads in and deserializes a stage of BRANCH.
     * @return Stage read from file */
    public static Stage fromFile(String branch) {
        if (!Utils.join(Main.STAGE, branch.replace("/", "-")).exists()) {
            throw new IllegalArgumentException(
                    "No stage of branch with this name found.");
        }
        return Utils.readObject(Utils.join(Main.STAGE,
                branch.replace("/", "-")), Stage.class);
    }

    /** The tree in the staging area. */
    private LinkedList<Tree> _tree;
    /** Collection of tracked files. Key is the file name,
     * value is the sha1 code for corresponding file. */
    private HashMap<String, String> _tracked;
    /** Branch name of this staging area. */
    private String _branch;
}
