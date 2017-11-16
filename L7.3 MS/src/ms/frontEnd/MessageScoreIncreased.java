package ms.frontEnd;

import ms.gameMechanics.UserScore;
import ms.messageSystem.Address;

/**
 * @author e.shubin
 */
public final class MessageScoreIncreased extends MessageToFrontEnd {
    private UserScore score;

    public MessageScoreIncreased(Address from, Address to, UserScore score) {
        super(from, to);
        this.score = score;
    }

    @Override
    protected void exec(FrontEnd frontEnd) {
        frontEnd.setScore(score);
    }
}
