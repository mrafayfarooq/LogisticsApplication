/**
 * Created by Muhammad Rafay on 4/9/17.
 *
 * Null Exception class for handling invalid or empty input.
 *
 * It takes a string as input and append "not found"
 */
class NullException extends Exception {
    private final String empty;
    public NullException(String e) {
        this.empty = e;
    }
    public void printException() {
        System.out.println(this.empty + " not found");
    }
}
