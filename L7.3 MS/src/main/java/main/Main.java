package main;

import accountService.AccountService;
import frontEnd.FrontEnd;
import gameMechanics.GameMechanics;
import main.clients.Client;
import messageSystem.MessageSystem;

/**
 * @author e.shubin
 * Original:        https://github.com/esin88/MessageSystem
 *
 * Addition lesson: https://youtu.be/sv3nwJHKGS0
 *                  https://park.mail.ru/materials/video/887/
 */
public final class Main {
    public static void main(String[] args) {

        final MessageSystem messageSystem = new MessageSystem();

        final Thread accountServiceThread = new Thread(new AccountService(messageSystem));
        accountServiceThread.setDaemon(true);
        accountServiceThread.setName("Account Service");

        final Thread gameMechanicsThread = new Thread(new GameMechanics(messageSystem));
        gameMechanicsThread.setDaemon(true);
        gameMechanicsThread.setName("Game Mechanics");

        final FrontEnd frontEnd = new FrontEnd(messageSystem);
        final Thread frontEndThread = new Thread(frontEnd);
        frontEndThread.setDaemon(true);
        frontEndThread.setName("FrontEnd");

        accountServiceThread.start();
        gameMechanicsThread.start();
        frontEndThread.start();

        final Client[] clients = {
                new Client(frontEnd, "bob"),
                new Client(frontEnd, "duke"),
                new Client(frontEnd, "alice"),
                new Client(frontEnd, "kate"),
                new Client(frontEnd, "john"),
                new Client(frontEnd, "dave"),
                new Client(frontEnd, "luke"),
                new Client(frontEnd, "chewie"),
                new Client(frontEnd, "anna"),
                new Client(frontEnd, "sasha"),
        };

        for (Client client : clients) {
            new Thread(client).start();
        }
    }
}
