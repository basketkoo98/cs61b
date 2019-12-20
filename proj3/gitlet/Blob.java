package gitlet;
import java.io.File;
import java.io.IOException;

/** Blob class which point to a file.
 * @author Huixuan Lin
 */
public class Blob {
    /** Constructor of Blob which point to file with NAME. */
    public Blob(String name) {
        _name = name;
        _content = Utils.readContents(Utils.join(Main.CWD, name));
        _fileSha1 = Utils.sha1(_content);

    }

    /**
     * Reads in and deserializes a blob from a file with name NAME in OBJECT.
     * @param  sha1 of blob to load
     * @return content of the file to which this blob point
     */
    public static String fromFile(String sha1) {
        File blobFile = Utils.join(Main.OBJECT, sha1);
        if (!blobFile.exists()) {
            throw new IllegalArgumentException(
                    "No blob file with that name found.");
        }
        return Utils.readContentsAsString(blobFile);
    }


    /** Get content of the file this blob point to.
     * @return Content of the file
     * */
    public static byte[] getContent() {
        return _content;
    }

    /** Get SHA1 code of the file this blob point to.
     * @return sha1 code of the file
     * */
    public static String getSha1() {
        return _fileSha1;
    }

    /** Get name of the the file this blob point to.
     * @return name of this file
     * */
    public static String getName() {
        return _name;
    }

    /** Save file content as a file which name is sha1 code. */
    public static void saveContent() throws IOException {
        Utils.join(Main.OBJECT, _fileSha1).createNewFile();
        Utils.writeContents(Utils.join(Main.OBJECT, _fileSha1), _content);
    }

    /** Save file with FILENAME into file with SHA1 code
     * into current working directory. */
    public static void saveContent(String fileName,
                                   String sha1) throws IOException {
        Utils.join(Main.OBJECT, sha1).createNewFile();
        Utils.writeContents(Utils.join(Main.CWD, fileName),
                Utils.readContentsAsString(Utils.join(Main.OBJECT, sha1)));
    }

    /** Content of the file this blob point to. */
    private static byte[] _content;
    /** SHA1 code fo the content of the file this blob point to. */
    private static String _fileSha1;
    /** Name of the file this blob point to. */
    private static String _name;

}
