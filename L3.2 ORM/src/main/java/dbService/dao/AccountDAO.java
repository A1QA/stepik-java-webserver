package dbService.dao;

import dbService.dataSets.User;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class AccountDAO {

    private Session session;

    public AccountDAO(Session session) {
        this.session = session;
    }

    public Long addUser(String login, String password) {
        return (Long) session.save(new User(login, password));
    }

    public User getUserByLogin(String login) {

        Query query = session.createQuery("from User where login = :paramLogin"); // тут якобы Error'ы,
                                                                        // т.к. нет файла конфигурации гипернейта
        query.setParameter("paramLogin", login);
        List list = query.list();
        if (!list.isEmpty()) {
            return (User) list.get(0);
        } else {
            return null;
        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public User getUserById(String id) {
        return (User)session.get(User.class, id);
    }

}
