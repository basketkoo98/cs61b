package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

/** Class of branch which user can checkout.
 * @author Huixuan Lin
 */
public class Branch implements Serializable {
    /** Constructor of branch with NAME. */
    Branch(String name) {
        _name = name;
    }

    /** Constructor of branch with NAME and HEAD commit. */
    Branch(String name, String head) {
        _name = name;
        _head = head;
    }

    /** Change name of this branch. */
    public void setName(String name) {
        _name = name;
    }
    /** Constructor of REMOTE's branch with NAME and HEAD commit. */
    Branch(String remote, String name, String head) {
        _name = remote + "-" + name;
        _head = head;
    }

    /** Copy head information from BRANCH to this. */
    public void copy(Branch branch) {
        _head = branch.getHead();
        _commits = branch.getCommits();
    }

    /** Save a branch to a file as future use. */
    public void saveBranch() throws IOException {
        Utils.join(Main.BRANCH, _name).createNewFile();
        Utils.writeObject(Utils.join(Main.BRANCH, _name), this);
    }

    /** Reads in and deserializes current branch.
     * @return The current branch. */
    public static Branch fromFile() {
        if (!Main.CURRENT_BRANCH.exists()) {
            throw new IllegalArgumentException(
                    "No current branch file found.");
        }
        String currentBranch = Utils.readContentsAsString(Main.CURRENT_BRANCH);
        return fromFile(currentBranch);
    }

    /** Reads in and deserializes the branch with BRANCHNAME.
     * @return The branch with corresponding name. */
    public static Branch fromFile(String branchName) {
        if (!Utils.join(Main.BRANCH, branchName).exists()) {
            throw new IllegalArgumentException(
                    "No branch file with that name found.");
        }
        return Utils.readObject(Utils.join(Main.
                BRANCH, branchName), Branch.class);
    }

    /** Get the name of this branch.
     * @return the name of branch. */
    public String getName() {
        return _name;
    }

    /** Get the head of this branch.
     * @return the sha1 code of head commit. */
    public String getHead() {
        return _head;
    }

    /** Change the head of this branch with HEAD. */
    public void changeHead(String head) {
        _head = head;
        Utils.writeContents(Utils.join(Main.BRANCH, _name), head);
    }

    /** Add commit with COMMITID under this branch. */
    public void addCommit(String commitId) {
        _commits.add(commitId);
        changeHead(commitId);
    }

    /** Add commit with COMMITID under BRANCH. */
    public void addCommit(String commitId, Branch branch) throws IOException {
        branch.addCommit(commitId);
        changeHead(commitId);
        branch.saveBranch();
    }

    /** Add COMMITS under this branch. */
    public void addCommits(LinkedList<String> commits) throws IOException {
        _commits.addAll(commits);
        saveBranch();
    }

    /** Get the SHA1 code of commit at INDEX.
     * @return the sha1 code fo commit. */
    public String getCommit(int index) {
        return _commits.get(index);
    }
    /** Get the collection of SHA1 code of commits under this branch.
     * @return collection of commits this branch has. */
    public LinkedList<String> getCommits() {
        return _commits;
    }

    /** Return true if this branch contain commit with COMMITID. */
    public boolean contains(String commitId) {
        return _commits.contains(commitId);
    }

    /** Get the length of this branch.
     * @return the number of commit this branch has. */
    public int size() {
        return _commits.size();
    }

    /** The name of this branch. */
    private String _name;
    /** The SHA1 code of the commit at the front of the this branch. */
    private String _head = "";
    /** Collection of the SHA1 code of commits under this branch. */
    private LinkedList<String> _commits = new LinkedList<>();
}
