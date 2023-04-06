package by.guavapay.exception;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException(Long id) {
        super("Unable to find parcel by id " + id);
    }

    public ParcelNotFoundException(Long id, String created) {
        super("Unable to find parcel with id %s for user %s."
                .formatted(id, created));
    }
}