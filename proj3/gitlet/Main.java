package gitlet;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny stupid version-control system.
 * import java.util.Set;
 * @author Huixuan Lin
 */
public class Main {

    /** Current Working Directory. */
    static final File CWD = new File(".");
    /** GitLet repo directory. */
    static final File REPO = Utils.join(CWD, ".gitlet");
    /** GitLet object directory. */
    static final File OBJECT = Utils.join(REPO, "object");
    /** GitLet branch directory. */
    static final File BRANCH = Utils.join(REPO, "branch");
    /** GitLet current branch file. */
    static final File CURRENT_BRANCH = Utils.join(REPO, "current-branch");
    /** GitLet staging area directory. */
    static final File STAGE = Utils.join(REPO, "stage");
    /** GitLet commit history file. */
    static final File HISTORY = Utils.join(REPO, "history");
    /** GitLet remote directory. */
    static final File REMOTE = Utils.join(REPO, "remote");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        validateArgsFormat(args[0], args);
        if (args[0].equals("init")) {
            init(args);
        } else {
            if (!REPO.exists()) {
                exitWithError("Not in an initialized Gitlet directory.");
            }
            _branch = Branch.fromFile();
            _stagingArea = Stage.fromFile(_branch.getName());
            _history = History.fromFile(_branch.getName());
            switchMethod(args);
        }
        _stagingArea.saveStage();
        _history.saveHistory();
        saveCurrentBranch();
    }

    /** Help method for Main, switch to do the corresponding method
     * accoding to the ARGS. */
    public static void switchMethod(String... args) throws IOException {
        switch (args[0]) {
        case "add":
            add(args);
            break;
        case "commit":
            commit(args);
            break;
        case "rm":
            rm(args);
            break;
        case "log":
            log(args);
            break;
        case "global-log":
            globalLog(args);
            break;
        case "find":
            find(args);
            break;
        case "status":
            status(args);
            break;
        case "checkout":
            checkout(args);
            break;
        case "branch":
            branch(args);
            break;
        case "rm-branch":
            rmBranch(args);
            break;
        case "reset":
            reset(args);
            break;
            case "merge":
            merge(args);
            break;
        case "add-remote":
            addRemote(args);
            break;
        case "rm-remote":
            rmRemote(args);
            break;
        case "push":
            push(args);
            break;
        case "fetch":
            fetch(args);
            break;
        case "pull":
            pull(args);
            break;
        default:
            exitWithError("No command with that name exists.");
        }
    }

    /**
     * Prints out MESSAGE and exits with error code -1.
     * Note:
     *     The functionality for erroring/exit codes is different within Gitlet
     *     so DO NOT use this as a reference.
     *     Refer to the spec for more information.
     * @param message message to print
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * @param args Array in format: {'init'}
     */
    public static void init(String[] args) throws IOException {
        if (REPO.exists()) {
            exitWithError("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
        REPO.mkdir();
        OBJECT.mkdir();
        BRANCH.mkdir();
        STAGE.mkdir();
        REMOTE.mkdir();
        HISTORY.mkdir();
        _stagingArea = new Stage("master");
        new Branch("master").saveBranch();
        _branch = Branch.fromFile("master");
        Commit tracked = new Commit(null, "initial commit");
        _history.addCommit(tracked);
        _branch.addCommit(tracked.getCommitSha1());

    }


    /**
     * Adds a copy of the files as it currently exists to the staging area.
     * @param args Array in format: {'add', fileName}
     */
    public static void add(String[] args) throws IOException {
        if (!Utils.join(CWD, args[1]).exists()) {
            exitWithError("File does not exist.");
        }
        boolean hasMarkRemove = false;
        if (_stagingArea.hasTree()) {
            if (_stagingArea.getRemoval().contains(args[1])) {
                _stagingArea.getRemoval().remove(args[1]);
                hasMarkRemove = true;
            }
        }
        if (!hasMarkRemove) {
            Blob newTracked = new Blob(args[1]);
            if (!_history.isEmpty() && _history.
                    getCommit(_branch.getHead()).getFiles() != null
                    && _history.getCommit(_branch.getHead()).
                    getFiles().containsKey(args[1])
                    && _history.getCommit(_branch.getHead()).
                    getFiles().get(args[1]).equals(newTracked.getSha1())) {
                _stagingArea.rm(args[1]);
            } else {
                _stagingArea.add(newTracked);
            }
        }
    }

    /**
     * Saves a snapshot of certain files in the current commit and staging
     * area so they can be restored at a later time, creating a new commit.
     * @param args Array in format: {'commit', message}
     */
    public static void commit(String[] args) throws IOException {
        if (_stagingArea.allStaged()) {
            exitWithError("No changes added to the commit.");
        }
        if (args.length == 1 || (args.length == 2
                    && args[1].trim().isEmpty())) {
            exitWithError("Please enter a commit message.");
        }
        _stagingArea.commit();
        Commit tracked = new Commit(_stagingArea.getTree(), args[1]);
        _stagingArea.clear();
        tracked.addAncestors(_branch.getCommits());
        if (!_history.isEmpty()) {
            tracked.setParent(_history.getCommit(_branch.getHead()));
        }
        _history.addCommit(tracked);
        _branch.addCommit(tracked.getCommitSha1());
    }

    /**
     * Unstage the file if it is currently staged. If the file is tracked
     * in the current commit, mark it to indicate that it is not to be
     * included in the next commit, and remove the file from the working
     * directory if the user has not already done so.
     * @param args Array in format: {'rm', fileName}
     */
    public static void rm(String[] args) {
        boolean removed = false;
        if (_stagingArea.hasTree()) {
            if (_stagingArea.getTree().getStaged().containsKey(args[1])) {
                removed = true;
                _stagingArea.rm(args[1]);
            }
        }
        if (Commit.fromFile(_branch.getHead()).getFiles().
                containsKey(args[1])) {
            removed = true;
            _stagingArea.toRemove(args[1]);
            Utils.restrictedDelete(Utils.join(CWD, args[1]));
        }
        if (!removed) {
            exitWithError("No reason to remove the file.");
        }

    }


    /**
     *  Starting at the current head commit, display information about
     *  each commit backwards along the commit tree until the initial
     *  commit, following the first parent commit links, ignoring any
     *  second parents found in merge commits.
     * @param args Array in format: {'log'}
     */
    public static void log(String[] args) {
        Commit commit = _history.getCommit(_branch.getHead());
        while (commit != null) {
            System.out.println("===\ncommit " + commit.getCommitSha1());
            if (commit.isMerged()) {
                System.out.println("Merge: " + commit.getParent().getId()
                        + " " + commit.getSecondParent().getId());
            }
            System.out.println("Date: "
                    + commit.getTimeStamps()
                    + "\n" + commit.getMessage());
            System.out.println();
            commit = commit.getParent();
        }
    }


    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     * @param args Array in format: {'global-log'}
     */
    public static void globalLog(String[] args) {
        for (Commit commit : _history.getCommits().values()) {
            System.out.println("===\ncommit " + commit.getCommitSha1());
            if (commit.isMerged()) {
                System.out.println("Merge: " + commit.getParent().getId()
                        + " " + commit.getSecondParent().getId());
            }
            System.out.println("Date: "
                    + commit.getTimeStamps()
                    + "\n" + commit.getMessage());
            System.out.println();
        }
    }


    /**
     * Prints out the ids of all commits that have the
     * given commit message, one per line.
     * @param args Array in format: {'find', commitMessage}
     */
    public static void find(String[] args) {
        boolean find = false;
        for (Commit commit : _history.getCommits().values()) {
            if (commit.getMessage().contains(args[1])) {
                System.out.println(commit.getCommitSha1());
                find = true;
            }
        }
        if (!find) {
            exitWithError("Found no commit with that message.");
        }
    }

    /**
     * Displays what branches currently exist,
     * and marks the current branch with a *.
     * Also displays what files have been staged
     * or marked for untracking.
     * @param args Array in format: {'status'}
     */
    public static void status(String[] args) {
        System.out.println("=== Branches ===");
        Stack<String> otherBranch = new Stack<>();
        for (String branch : BRANCH.list()) {
            if (branch.equals(_branch.getName())) {
                System.out.println("*" + branch);
            } else {
                otherBranch.push(branch);
            }
        }
        while (!otherBranch.isEmpty()) {
            System.out.println(otherBranch.pop());
        }

        System.out.println("\n=== Staged Files ===");
        if (!_stagingArea.allStaged()) {
            for (String name : _stagingArea.getStagedName()) {
                System.out.println(name);
            }
        }
        System.out.println("\n=== Removed Files ===");
        if (_stagingArea.hasTree() && _stagingArea.getRemoval()
                != null && !_stagingArea.getRemoval().isEmpty()) {
            for (String name : _stagingArea.getRemoval()) {
                System.out.println(name);
            }
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        HashSet<String> fileName = new HashSet<>();
        if (CWD.list() != null) {
            fileName.addAll(Arrays.asList(CWD.list()));
        }
        fileName.addAll(_stagingArea.getTracked().keySet());
        if (_stagingArea.hasTree()) {
            fileName.addAll(_stagingArea.getStagedName());
        }
        for (String file : fileName) {
            if (!Utils.join(CWD, file).exists()
                    || (Utils.join(CWD, file).exists()
                    && Utils.join(CWD, file).isFile())
                    && !Utils.join(CWD, file).getName().startsWith(".")
                    && !Utils.join(CWD, file).getName().equals("Makefile")
                    && !Utils.join(CWD, file).getName().endsWith(".iml")) {
                if (modifyNotStaged(Utils.join(CWD, file)) != null) {
                    if (modifyNotStaged(Utils.
                            join(CWD, file)).equals("deleted")) {
                        System.out.println(file + " (deleted)");
                    } else if (modifyNotStaged((Utils.
                            join(CWD, file))).equals("modified")) {
                        System.out.println(file + " (modified)");
                    }
                }
            }
        }
        System.out.println("\n=== Untracked Files ===");
        if (CWD.list() != null) {
            for (String name : CWD.list()) {
                if (isUntracked(name)) {
                    System.out.println(name);
                }
            }
        }
    }
    /** Help method for status. Return tree if file with
     * NAME is the untracked file.
     */
    public static boolean isUntracked(String name) {
        return !_history.getCommit(_branch.getHead()).
                getFiles().containsKey(name)
                && (_stagingArea.allStaged()
                || (!_stagingArea.allStaged()
                && !_stagingArea.getStagedName().contains(name)))
                && !name.startsWith(".") && !name.endsWith(".iml")
                && !name.equals("Makefile")
                && !Utils.join(CWD, name).isDirectory();
    }

    /** Helper method for status. Return the situation
     * of FILE in which it is modified but not staged. */
    public static String modifyNotStaged(File file) {

        if (!file.exists() && !_stagingArea.allStaged()
                &&_stagingArea.getStaged().
                containsKey(file.getName())) {
            return "deleted";
        }
        if (_history.getCommit(_branch.getHead()).
                getFiles().containsKey(file.getName())
                && !file.exists() && (!_stagingArea.hasTree()
                || !_stagingArea.getRemoval().contains(file.getName()))) {
            return "deleted";
        }
        if (file.exists()) {
            Blob possible = new Blob(file.getName());
            if ((_stagingArea.allStaged()
                    || (!_stagingArea.allStaged()
                    && (!_stagingArea.getStagedName().contains(file.getName())
                    && !_stagingArea.getRemoval().contains(file.getName()))))
                    && _history.getCommit(_branch.getHead()).
                    getFiles().containsKey(file.getName())
                    && !_history.getCommit(_branch.getHead()).
                    getFiles().get(file.getName()).equals(possible.getSha1())) {
                return "modified";
            }
            if (!_stagingArea.allStaged() && _stagingArea.
                    getStaged().containsKey(file.getName())
                    && !_stagingArea.getStaged().
                    get(file.getName()).equals(possible.getSha1())) {
                return "modified";
            }
        }

        return null;
    }

    /**
     * Checkout is a kind of general command that
     * can do a few different things depending
     * on what its arguments are. There are 3 possible use cases.
     * @param args Array in one of following formats:
     *             {'checkout', '--', fileName}
     *             {'checkout', commitId, '--', fileName}
     *             {'checkout', branchName}
     */
    public static void checkout(String[] args) throws IOException {
        if (args.length == 2) {
            checkoutArg2(args);
        } else if (args.length == 3) {
            checkoutArg3(args);
        } else {
            checkoutArg4(args);
        }
    }

    /** Help method for checkou situationwith 4 args.
     * @param args Array in formats:
     *              {'checkout', commitId, '--', fileName}
     */
    public static void checkoutArg4(String[] args) throws IOException {
        boolean find = false;
        Commit commit = null;
        for (String id : OBJECT.list()) {
            if (id.startsWith(args[1])) {
                find = true;
                commit = Commit.fromFile(id);
                break;
            }
        }
        if (!find) {
            exitWithError("No commit with that id exists.");
        }
        if (!commit.getFiles().containsKey(args[3])) {
            exitWithError("File does not exist in that commit.");
        }
        Utils.join(CWD, args[3]).createNewFile();
        Utils.writeContents(Utils.join(CWD, args[3]),
                Blob.fromFile(commit.
                        getFiles().get(args[3])));
    }

    /** Help method for checkout situation with 3 args.
     * @param args Array in formats:
     *             {'checkout', '--', fileName}
     */
    public static void checkoutArg3(String[] args) throws IOException {
        if (!_history.getCommit(_branch.getHead()).
                getFiles().containsKey(args[2])) {
            exitWithError("File does not exist in that commit.");
        }
        Utils.join(CWD, args[2]).createNewFile();
        Utils.writeContents(Utils.join(CWD, args[2]),
                Blob.fromFile(_history.getCommit(_branch.getHead()).
                        getFiles().get(args[2])));
    }

    /** Help method for checkout situation with 2 args.
     * @param args Array in formats: {'checkout', branchName}
     */
    public static void checkoutArg2(String[] args) throws IOException {
        String branchName = args[1].replace("/", "-");
        if (!Arrays.asList(BRANCH.list()).contains(branchName)) {
            exitWithError("No such branch exists.");
        } else if (_branch.getName().equals(branchName)) {
            exitWithError("No need to checkout the current branch.");
        } else {
            if (CWD.list() != null) {
                for (String file : CWD.list()) {
                    if (!file.startsWith(".") && !_stagingArea.getTracked().
                            containsKey(file) && Commit.fromFile(Branch.
                            fromFile(branchName).getHead()).hasFile()
                            && Commit.fromFile(Branch.fromFile(branchName).
                            getHead()).getFiles().containsKey(file)
                            && !Blob.fromFile(Commit.fromFile(Branch.
                            fromFile(branchName).getHead()).getFiles().get(file)).
                            equals(Utils.readContentsAsString(Utils.
                                    join(CWD, file)))) {
                        exitWithError("There is an untracked file "
                                + "in the way; delete it or add it first.");
                    }
                }
            }
            HashMap<String, String> fileMap =
                    Commit.fromFile(Branch.
                            fromFile(branchName).getHead()).getFiles();
            if (fileMap != null) {
                for (String name : fileMap.keySet()) {
                    Utils.join(CWD, name).createNewFile();
                    Utils.writeContents(Utils.join(CWD,
                            name), Blob.fromFile(fileMap.get(name)));
                }
            }
            for (File file : CWD.listFiles()) {
                if (!file.isHidden() && !file.isDirectory()
                        && !file.getName().equals("Makefile")
                        && !file.getName().endsWith(".iml")
                        && !fileMap.containsKey(file.getName())
                        && _stagingArea.getTracked() != null
                        && _stagingArea.getTracked().
                        containsKey(file.getName())) {
                    file.delete();
                }
            }
            _branch = Branch.fromFile(branchName);
        }
        _stagingArea.clear();
    }

    /**
     * Creates a new branch with the given name, and
     * points it at the current head node.
     * A branch is nothing more than a name for a
     * reference (a SHA-1 identifier) to a commit node.
     * @param args Array in format: {'branch', branchName}
     */
    public static void branch(String[] args) throws IOException {
        if (Arrays.asList(BRANCH.list()).contains(args[1])) {
            exitWithError("A branch with that name already exists.");
        } else {
            new Branch(args[1], _branch.getHead()).saveBranch();
            Branch.fromFile(args[1]).addCommits(_branch.getCommits());
            _stagingArea.saveStage(args[1]);
        }
    }

    /** Write current branch name into file. */
    public static void saveCurrentBranch() throws IOException {
        CURRENT_BRANCH.createNewFile();
        Utils.writeContents(CURRENT_BRANCH, _branch.getName());
        _branch.saveBranch();
    }

    /**
     * Deletes the branch with the given name. This
     * only means to delete the pointer associated
     * with the branch; it does not mean to delete
     * all commits that were created under the
     * branch, or anything like that.
     * @param args Array in format: {'rm-branch', branchName}
     */
    public static void rmBranch(String[] args) {
        if (_branch.getName().equals(args[1])) {
            exitWithError("Cannot remove the current branch.");
        } else if (!Arrays.asList(BRANCH.list()).contains(args[1])) {
            exitWithError("A branch with that name does not exist.");
        } else {
            Utils.join(BRANCH, args[1]).delete();
            Utils.join(STAGE, args[1]).delete();
        }
    }

    /**
     * Checks out all the files tracked by the
     * given commit. Removes tracked files that
     * are not present in that commit. Also moves
     * the current branch's head to that commit node.
     * @param args Array in format: {'reset', commitId}
     */
    public static void reset(String[] args) throws IOException {
        boolean find = false;
        Commit commit = null;
        for (String id : OBJECT.list()) {
            if (id.startsWith(args[1])) {
                find = true;
                commit = Commit.fromFile(id);
                break;
            }
        }
        if (!find) {
            exitWithError("No commit with that id exists.");
        } else {
            if (CWD.list() != null) {
                for (String file : CWD.list()) {
                    if (!file.startsWith(".") && !_stagingArea.
                            getTracked().
                            containsKey(file) && commit.hasFile()
                            && commit.
                            getFiles().containsKey(file)
                            && !Blob.fromFile(commit.getFiles().
                            get(file)).equals(Utils.readContentsAsString(Utils.
                            join(CWD, file)))) {
                        exitWithError("There is an untracked file "
                                + "in the way; delete it or add it first.");
                    }
                }
                for (File file : CWD.listFiles()) {
                    if (!file.isHidden()
                            && !file.getName().equals("Makefile")
                            && !file.getName().endsWith(".iml")) {
                        file.delete();
                    }
                }
            }
            HashMap<String, String> fileMap = commit.getFiles();
            for (String name : fileMap.keySet()) {
                Utils.writeContents(Utils.join(CWD, name),
                        Blob.fromFile(fileMap.get(name)));
            }
        }
        _stagingArea.clear();
        _branch.addCommit(commit.getCommitSha1());
        _history.addCommit(commit);

    }

    /**
     * Merges files from the given branch into the current branch.
     * @param args Array in format: {'merge', branchName}
     */
    public static void merge(String[] args) throws IOException {
        String branchName = args[1].replace("/", "-");
        if (_stagingArea.hasTree()) {
            exitWithError("You have uncommitted changes.");
        } else if (!Arrays.asList(BRANCH.list()).contains(branchName)) {
            exitWithError("A branch with that name does not exist.");
        } else if (_branch.getName().equals(branchName)) {
            exitWithError("Cannot merge a branch with itself");
        } else {
            Branch mergeBranch = Branch.fromFile(branchName);
            if (Commit.fromFile(mergeBranch.getHead()).hasFile()) {
                for (String file : Commit.fromFile(mergeBranch.
                        getHead()).getFiles().keySet()) {
                    if (!file.startsWith(".") && !_stagingArea.
                            getTracked().containsKey(file)
                            && Utils.join(CWD, file).exists()
                            && !Blob.fromFile(Commit.fromFile(
                                    mergeBranch.getHead()).getFiles().
                            get(file)).equals(Utils.
                            readContentsAsString(Utils.join(CWD, file)))) {
                        exitWithError("There is an untracked "
                                + "file in the way; delete "
                                + "it or add it first.");
                    }
                }
            }
            String splitPoint = findSplitPoint(mergeBranch);
            if (_branch.getHead().equals(splitPoint)) {
                _branch.copy(mergeBranch);
                saveCurrentBranch();
                _stagingArea.copy(Stage.fromFile(mergeBranch.getName()));
                _stagingArea.saveStage();
                update(Commit.fromFile(mergeBranch.getHead()));
                exitWithError("Current branch fast-forwarded.");
            } else if (mergeBranch.getHead().equals(splitPoint)) {
                exitWithError("Given branch is an "
                        + "ancestor of the current branch.");
            } else if (doMerge(branchName, splitPoint)) {
                System.out.println("Encountered a merge conflict.");
            }
        }
    }

    /** Help method for merge. Find the split point with
     * this branch and MERGEBRANCH.
     * @return The sha1 code of split point commit.
     */
    public static String findSplitPoint(Branch mergeBranch) {
        String splitPoint = _branch.getCommits().getFirst();
        int minDistance = Integer.MAX_VALUE;
        int index = 1;
        while (index < Math.min(_branch.size(), mergeBranch.size())) {
            if (mergeBranch.getCommit(index).
                    equals(_branch.getCommit(index))) {
                splitPoint =  _branch.getCommit(index);
            } else {
                int currentCommitDistance, mergeCommitDistance;
                if (Commit.fromFile(mergeBranch.getHead()).isAncestor(
                        _branch.getCommit(index))) {
                    currentCommitDistance = distance(_branch.
                            getCommit(index), _branch.getHead(),mergeBranch);
                    if (currentCommitDistance < minDistance) {
                        splitPoint = _branch.getCommit(index);
                        minDistance = currentCommitDistance;
                    }
                }
                if (Commit.fromFile(_branch.getHead()).isAncestor(
                        mergeBranch.getCommit(index))) {
                    mergeCommitDistance = distance(mergeBranch.
                            getCommit(index), _branch.getHead(),mergeBranch);
                    if (mergeCommitDistance < minDistance) {
                        splitPoint = mergeBranch
                                .getCommit(index);
                        minDistance = mergeCommitDistance;
                    }
                }
            }
            index++;
        }
        return splitPoint;
    }
    /** Help method for merge. Update files in the current working
     * directory according to the COMMIT.
     */
    public static void update(Commit commit) throws IOException {
        for (File file : CWD.listFiles()) {
            if (!file.isHidden()
                    && !file.getName().equals("Makefile")
                    && !file.getName().endsWith(".iml")) {
                file.delete();
            }
        }
        for (String fileName : commit.getFiles().keySet()) {
            Utils.join(CWD, fileName).createNewFile();
            Utils.writeContents(Utils.join(CWD, fileName),
                    Blob.fromFile(commit.getFiles().get(fileName)));
        }
    }


    /** Help method for merge. Return the distance
     * from commit FROM to TO, with MERGEBRANCH */
    public static int distance(String from, String to, Branch mergeBranch) {
        if (_branch.contains(to)) {
            if (_branch.contains(from)) {
                if (_branch.getCommits().indexOf(to)
                        < _branch.getCommits().indexOf(from)) {
                    return _branch.size() + mergeBranch.size();
                } else {
                    return _branch.getCommits().indexOf(to)
                            - _branch.getCommits().indexOf(from);
                }
            } else  {
                if (Commit.fromFile(to).getSecondParent()!= null) {
                    return 1 + distance(from,
                            Commit.fromFile(to).getSecondParent().
                                    getCommitSha1(), mergeBranch);
                } else {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParent().
                                    getCommitSha1(), mergeBranch);
                }
            }
        } else  {
            if (mergeBranch.contains(from)) {
                if (mergeBranch.getCommits().indexOf(to)
                        < mergeBranch.getCommits().indexOf(from)) {
                    return _branch.size() + mergeBranch.size();
                } else {
                    return mergeBranch.getCommits().indexOf(to)
                            - mergeBranch.getCommits().indexOf(from);
                }
            } else  {
                if (Commit.fromFile(to).getSecondParent()!= null) {
                    return 1 + distance(from,
                            Commit.fromFile(to).getSecondParent().
                                    getCommitSha1(), mergeBranch);
                } else {
                    return 1 + distance(from,
                            Commit.fromFile(to).getParent().
                                    getCommitSha1(), mergeBranch);
                }
            }
        }
    }


    /** Help method for merge, distinguish merge
     * situation from GIVENBRANCH and SPLITPOINT.
     * @return Conflict situation.*/
    public static boolean doMerge(String givenBranch,
                               String splitPoint) throws IOException {
        Branch mergeBranch = Branch.fromFile(givenBranch);
        HashMap<String, String> mergeFileMap
                = Commit.fromFile(mergeBranch.getHead()).getFiles();
        HashMap<String, String> splitPointFileMap
                = Commit.fromFile(splitPoint).getFiles();
        HashMap<String, String> currentFileMap
                = Commit.fromFile(_branch.getHead()).getFiles();
        HashSet<String> allFileName = new HashSet<>(mergeFileMap.keySet());
        allFileName.addAll(splitPointFileMap.keySet());
        allFileName.addAll(currentFileMap.keySet());
        boolean conflict = false;
        for (String fileName : allFileName) {
            if (!splitPointFileMap.containsKey(fileName)) {
                if (!currentFileMap.containsKey(fileName)
                        && mergeFileMap.containsKey(fileName)) {
                    checkout(new String[]{"checkout",
                            mergeBranch.getHead(), "--", fileName});
                    _stagingArea.add(new Blob(fileName));
                }
                if (currentFileMap.containsKey(fileName)
                        && mergeFileMap.containsKey(fileName)
                        && !currentFileMap.get(fileName).
                        equals(mergeFileMap.get(fileName))) {
                    String content = "<<<<<<< HEAD" + System.lineSeparator()
                            + Blob.fromFile(currentFileMap.get(fileName))
                            + "=======" + System.lineSeparator()
                            + Blob.fromFile(mergeFileMap.
                            get(fileName)) + ">>>>>>>";
                    Utils.writeContents(Utils.join(CWD, fileName), content);
                    conflict = true;
                }
            } else {
                conflict = mergeSplitExist(mergeFileMap,
                        splitPointFileMap, currentFileMap,
                        fileName, conflict);
            }
        }
        _stagingArea.commit();
        Commit tracked = new Commit(_stagingArea.
                getTree(), "Merged " + givenBranch.replace("-", "/")
                + " into " + _branch.getName().replace("-", "/") + ".");
        tracked.merge();
        tracked.setParent(Commit.fromFile(_branch.getHead()));
        tracked.setSecondParent(Commit.fromFile(mergeBranch.getHead()));
        tracked.addAncestors(_branch.getCommits());
        tracked.addAncestor(Commit.fromFile(mergeBranch.getHead()).getCommitSha1());
        tracked.addAncestors(Commit.fromFile(mergeBranch.getHead()).getAncestors());
        _history.addCommit(tracked);
        _branch.addCommit(tracked.getCommitSha1());
        _stagingArea.clear();
        for (File file : CWD.listFiles()) {
            if (tracked.getFiles() != null && !tracked.
                    getFiles().containsKey(file.getName())
                    && !file.isHidden() && !file.isDirectory()
                    && !file.getName().equals("Makefile")
                    && !file.getName().endsWith(".iml")) {
                file.delete();
            }
        }
        return conflict;
    }

    /** Help method for merge. This part is the situation when
     * split point map contains the FILENAME. Get data from
     * MERGEFILEMAP, SPLITPOINTFILEMAP, CURRENTFILEMAP, former conflic
     * situation CONFLICT
     * @return Conflict situation. True when file merging face conflict.
     */
    public static boolean mergeSplitExist(
            HashMap<String, String> mergeFileMap,
            HashMap<String, String> splitPointFileMap,
            HashMap<String, String> currentFileMap,
            String fileName, boolean conflict
    ) throws IOException {
        if (mergeFileMap.containsKey(fileName)
                && currentFileMap.containsKey(fileName)
                && !splitPointFileMap.get(fileName).
                equals(mergeFileMap.get(fileName))
                && splitPointFileMap.get(fileName).
                equals(currentFileMap.get(fileName))) {
            Utils.writeContents(Utils.join(CWD,
                    fileName), Blob.fromFile(
                    mergeFileMap.get(fileName)));
            _stagingArea.add(new Blob(fileName));
        } else if (!mergeFileMap.containsKey(fileName)
                && currentFileMap.containsKey(fileName)
                && splitPointFileMap.get(fileName).
                equals(currentFileMap.get(fileName))) {
            rm(new String[]{"rm", fileName});
        } else if ((currentFileMap.containsKey(fileName)
                && mergeFileMap.containsKey(fileName)
                && !currentFileMap.get(fileName).
                equals(mergeFileMap.get(fileName))
                && !currentFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName))
                && !mergeFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))
                || (!currentFileMap.containsKey(fileName)
                && mergeFileMap.containsKey(fileName)
                && !mergeFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))
                || (currentFileMap.containsKey(fileName)
                && !mergeFileMap.containsKey(fileName)
                && !currentFileMap.get(fileName).
                equals(splitPointFileMap.get(fileName)))) {
            String current = currentFileMap.containsKey(fileName)
                    ? System.lineSeparator()
                    + Blob.fromFile(currentFileMap.get(fileName))
                    : System.lineSeparator();
            String merge = mergeFileMap.containsKey(fileName)
                    ? System.lineSeparator()
                    + Blob.fromFile(mergeFileMap.get(fileName))
                    : System.lineSeparator();
            String content = "<<<<<<< HEAD"
                    + current + "======="
                    + merge + ">>>>>>>";
            Utils.join(CWD, fileName).createNewFile();
            Utils.writeContents(Utils.join(CWD, fileName), content);
            _stagingArea.add(new Blob(fileName));
            return true;
        }
        return conflict;
    }

    /** Help method for merge. Save all tracked files
     * in COMMIT into current working direcotry. */
    public static void saveFiles(Commit commit) throws IOException {
        for (String fileName : commit.getFiles().keySet()) {
            Blob.saveContent(fileName, commit.getFiles().get(fileName));
        }
    }

    /**
     * Saves the given login information under the
     * given remote name. Attempts to push or
     * pull from the given remote name will then
     * attempt to use this .gitlet directory.
     * @param args Array in format: {'add-remote',
     *             remoteName, remoteDirectoryName/.gitlet}
     */
    public static void addRemote(String[] args) throws IOException {
        if (REMOTE.list() != null && Arrays.asList(REMOTE.list()).contains(args[1])) {
            exitWithError("A remote with that name already exists.");
        } else {
            new Remote(args[1], args[2]);
        }
    }

    /**
     * Remove information associated with the given
     * remote name. The idea here is that if you ever
     * wanted to change a remote that you added, you
     * would have to first remove it and then re-add it.
     * @param args Array in format: {'rm-remote', remoteName}
     */
    public static void rmRemote(String[] args) {
        if (!Arrays.asList(REMOTE.list()).contains(args[1])) {
            exitWithError("A remote with that name does not exist.");
        } else {
            Utils.join(REMOTE, args[1]).delete();
            Utils.join(HISTORY, args[1]).delete();
            for (File file : STAGE.listFiles()) {
                if (file.getName().contains(args[1] + "-")) {
                    file.delete();
                }
            }
        }
    }

    /**
     * Attempts to append the current branch's commits
     * to the end of the given branch at the given remote.
     * @param args Array in format: {'push', remoteName, remoteBranchName}
     */
    public static void push(String[] args) throws IOException {
        Remote  remote = Remote.fromFile(args[1]);
        if (!remote.getRepo().exists()) {
            exitWithError("Remote directory not found.");
        } else {
            HashMap<String, String> fileMap = new HashMap<>();
            Commit pushCommit = Commit.fromFile(_branch.getHead());
            History history = remote.historyFromFile();
            Branch branch;
            for (String name : pushCommit.getFiles().keySet()) {
                fileMap.put(name, Blob.fromFile(pushCommit.getFiles().get(name)));
            }
            if (!Utils.join(remote.getBranch(), args[2]).exists()) {
                branch = new Branch(args[2], _branch.getHead());
                Utils.join(remote.getBranch(), args[2]).createNewFile();
            } else {
                branch = remote.branchFromFile(args[2]);
                if (_history.getCommit(branch.getHead()) == null) {
                    exitWithError("Please pull "
                            + "down remote changes before pushing.");
                }
            }
            branch.addCommit(pushCommit.getCommitSha1());
            Utils.join(remote.getObject(), pushCommit.getCommitSha1()).createNewFile();
            Utils.writeObject(Utils.join(remote.getObject(), pushCommit.getCommitSha1()), pushCommit);
            remote.reset(fileMap);
            history.addCommit(pushCommit);
            Utils.writeObject(Utils.join(remote.getBranch(), args[2]), branch);
            Utils.writeObject(Utils.join(remote.getHistory(), "current"), history);
        }
    }


    /** Help method for fetch. Save COMMIT in cwd. */
    public static void saveCommit(Commit commit, Remote remote) throws IOException {
        Utils.writeObject(Utils.join(OBJECT, commit.getCommitSha1()), commit);
        Utils.writeObject(Utils.join(OBJECT,
                commit.getTree().getSha1()), commit.getTree());
        for (String fileName : commit.getTree().getTracked().keySet()) {
            String sha1 = commit.getTree().getTracked().get(fileName);
            String content = remote.blobFromFile(sha1);
            Utils.join(OBJECT, sha1).createNewFile();
            Utils.writeContents(Utils.join(OBJECT, sha1), content);
        }
    }

    /**
     * Brings down commits from the remote Gitlet
     * repository into the local Gitlet repository.
     * @param args Array in format: {'fetch',
     *             remoteName, remoteBranchName}
     */
    public static void fetch(String[] args) throws IOException {
        Remote remote = Remote.fromFile(args[1]);
        if (!remote.getRepo().exists()) {
            exitWithError("Remote directory not found.");
        } else if (!Utils.join(remote.getBranch(), args[2]).exists()) {
            exitWithError("That remote does not have that branch.");
        } else {
            Branch branch = remote.branchFromFile(args[2]);
            branch.setName(args[1] + "-" + args[2]);
            branch.saveBranch();
            Commit commit = remote.commitFromFile(branch.getHead());
            saveCommit(commit, remote);
            Stage stage = remote.stageFromFile(args[2]);
            Utils.join(STAGE, args[1] + "-" + args[2]).createNewFile();
            Utils.writeObject(Utils.join(STAGE, args[1] + "-" + args[2]), stage);
            History history = remote.historyFromFile();
            history.saveHistory(args[1]);
        }
    }

    /**
     * Fetches branch remoteName/remoteBranchName
     * as for the fetch command, and then merges
     * that fetch into the current branch.
     * @param args Array in format: {'pull', remoteName, remoteBranchName}
     */
    public static void pull(String[] args) throws IOException {
        fetch(new String[]{"fetch", args[1], args[2]});
        merge(new String[]{"merge", args[1] + "/" + args[2]});
    }

    /**
     * Checks whether the arguments of corresponding
     * command have correct format,
     * throws a RuntimeException if they do not have correct format.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     */
    public static void validateArgsFormat(String cmd, String[] args) {
        boolean match;
        switch (cmd) {
        case "commit":
            match = args.length == 1 || args.length == 2;
            break;
        case "init":
        case "log":
        case "global-log":
        case "status":
            match = args.length == 1;
            break;
        case "checkout":
            match = (args.length == 2)
                    || (args.length == 3 && args[1].equals("--"))
                    || (args.length == 4 && args[2].equals("--"));
            break;
        case "add-remote":
            match = args.length == 3 && args[2].matches(".+/\\.gitlet");
            break;
        case "push":
        case "fetch":
        case "pull":
            match = args.length == 3;
            break;
        default:
            match = args.length == 2;
        }
        if (!match) {
            exitWithError("Incorrect operands.");
        }
    }

    /** The stage area of CWD. */
    private static Stage _stagingArea;
    /** The commit history of CWD. */
    private static History _history = new History();
    /** All branches. */
    private static HashMap<String, Branch> _branches = new HashMap<>();
    /** Current branche. */
    private static Branch _branch;
}
