package ms.frontEnd;

import ms.accountService.Account;
import ms.messageSystem.Address;

/**
 * @author e.shubin
 */
public final class MessageIsAuthenticated extends MessageToFrontEnd {
    private String sessionId;
    private Account account;

    public MessageIsAuthenticated(Address from, Address to, String sessionId, Account account) {
        super(from, to);
        this.sessionId = sessionId;
        this.account = account;
    }

    @Override
    protected void exec(FrontEnd frontEnd) {
        frontEnd.authenticated(sessionId, account);
    }
}
