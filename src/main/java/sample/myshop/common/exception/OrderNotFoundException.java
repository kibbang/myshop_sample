package sample.myshop.common.exception;


public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }

    public OrderNotFoundException() {
    }

    public OrderNotFoundException(String message) {
    }
}
