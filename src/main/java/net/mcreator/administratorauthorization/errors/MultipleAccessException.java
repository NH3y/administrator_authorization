package net.mcreator.administratorauthorization.errors;

public class MultipleAccessException extends RuntimeException {
    public MultipleAccessException(String message) {
        super(message);
    }
}
