package ms.accountService;

import ms.frontEnd.MessageRegistered;
import ms.messageSystem.Address;
import ms.messageSystem.Message;

/**
 * @author e.shubin
 */
public final class MessageRegister extends MessageToAccountService {
    private String name;
    private String password;

    public MessageRegister(Address from, Address to, String name, String password) {
        super(from, to);
        this.name = name;
        this.password = password;
    }

    @Override
    protected void exec(AccountService service) {
        boolean result = service.getAccountDAO().register(name, password);
        final Message back = new MessageRegistered(getTo(), getFrom(), name, result);
        service.getMessageSystem().sendMessage(back);
    }
}
