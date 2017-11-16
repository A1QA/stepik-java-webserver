package ms.gameMechanics;

import ms.messageSystem.Abonent;
import ms.messageSystem.Address;
import ms.messageSystem.Message;

/**
 * @author e.shubin
 */
public abstract class MessageToGameMechanics extends Message {
    public MessageToGameMechanics(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if (abonent instanceof GameMechanics) {
            exec((GameMechanics) abonent);
        }
    }

    protected abstract void exec(GameMechanics gameMechanics);
}
