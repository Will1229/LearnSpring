package exception;

//TODO: enrich exception
public class BetServiceException extends Throwable {
    public BetServiceException(final String error) {
        super(error);
    }
}
