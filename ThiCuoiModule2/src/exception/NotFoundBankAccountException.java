package exception;

public class NotFoundBankAccountException extends Exception {
    public NotFoundBankAccountException() {
        super("Tai khoan khong ton tai.");
    }
}
