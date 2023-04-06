package by.guavapay.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Unable to find user by id " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}