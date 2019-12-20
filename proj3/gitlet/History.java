package gitlet;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/** Class of history which store all commit history.
 * @author Huixuan Lin
 */
public class History implements Serializable {
    /** Constructor of history. */
    public History() {
        _commits = new HashMap<>();
    }

    /** Add COMMIT into the commit history. */
    public void addCommit(Commit commit) throws IOException {
        commit.getTree().saveTree();
        commit.saveCommit();
        _commits.put(commit.getCommitSha1(), commit);
    }

    /** Return true if there is no commit history. */
    public boolean isEmpty() {
        return _commits.isEmpty();
    }

    /** Return all commit history. */
    public HashMap<String, Commit> getCommits() {
        return _commits;
    }

    /** Return the commit with commit id COMMITID. */
    public Commit getCommit(String commitId) {
        return _commits.get(commitId);
    }

    /** Return true if there is a commit with id COMMITID. */
    public boolean hasCommit(String commitId) {
        return _commits.containsKey(commitId);
    }

    /** Save the history to a file as future use. */
    public void saveHistory() throws IOException {
        Utils.join(Main.HISTORY, "current").createNewFile();
        Utils.writeObject(Utils.join(Main.HISTORY, "current"), this);
    }

    /** Save the history of REMOTE to a file as future use. */
    public void saveHistory(String remote) throws IOException {
        Utils.join(Main.HISTORY, remote).createNewFile();
        Utils.writeObject(Utils.join(Main.HISTORY, remote), this);
    }

    /** Reads in and deserializes a history.
     * @return History read from file */
    public static History fromFile(String branch) {
        if (!branch.contains("-")) {
            if (!Utils.join(Main.HISTORY, "current").exists()) {
                throw new IllegalArgumentException(
                        "No history file found.");
            }
            return Utils.readObject(Utils.join(Main.HISTORY, "current"), History.class);
        } else {
            return remoteFromFile(branch.substring(0, branch.indexOf("-")));
        }

    }

    /** Reads in and deserializes a history of REMOTE.
     * @return History read from file */
    public static History remoteFromFile(String remote) {
        if (!Main.HISTORY.exists()) {
            throw new IllegalArgumentException(
                    "No history file found.");
        }
        return Utils.readObject(Utils.join(Main.HISTORY, remote), History.class);
    }
    /** History of commit. */
    private HashMap<String, Commit> _commits;

}
