/**
 * Created by Muhammad Rafay on 4/9/17.
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
