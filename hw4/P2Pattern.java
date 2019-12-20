/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static final String P1 = "(0?[1-9]|1[012])/(0?[1-9]|[1-2]\\d|3[01])/([1][9]\\d{2}|[2-9]\\d{3})"; //FIXME: Add your regex here

    /** Pattern to match 61b notation for literal IntLists. */
    public static final String P2 = "[(](([0]|[1-9][0-9]*), *)*([0]|[1-9][0-9]*)[)]"; //FIXME: Add your regex here

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static final String P3 = "(([a-zA-Z]|[0-9])|([a-zA-Z]|[0-9])([a-zA-Z]|[0-9]|[-\\.])*([a-zA-Z]|[0-9]))(\\.(([a-zA-Z]|[0-9])|([a-zA-Z]|[0-9])([a-zA-Z]|[0-9]|[-\\.])*([a-zA-Z]|[0-9])))*\\.(([a-zA-Z]|[0-9])|([a-zA-Z]|[0-9])([a-zA-Z]|[0-9]|[-\\.]){0,4}([a-zA-Z]|[0-9]))"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static final String P4 = "[a-zA-Z_$][0-9a-zA-Z_$]*"; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
//    public static final String P5 = "(\\d{1,2}|[01]\\d{2}|[2]([0-4]\\d|[5][0-5]))(\\.(\\d{1,2}|[01]\\d{2}|[2]([0-4]\\d|[5][0-5]))){3}"; //FIXME: Add your regex here
    public static final String P5 = "[^()\\s]+";
}
