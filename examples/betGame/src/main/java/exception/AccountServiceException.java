package exception;

//TODO: enrich exception
public class AccountServiceException extends Throwable {
    public AccountServiceException(final String error) {
        super(error);
    }
}
