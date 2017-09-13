package dbService;

import dbService.dao.AccountDAO;
import dbService.dao.UsersDAO;
import dbService.dataSets.User;
import dbService.dataSets.UsersDataSet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author v.chibrikov
 *         <p>
 *         Пример кода для курса на https://stepic.org/
 *         <p>
 *         Описание курса и лицензия: https://github.com/vitaly-chibrikov/stepic_java_webserver
 */
public class DBService {
    private static final String hibernate_show_sql = "true";
    private static final String hibernate_hbm2ddl_auto = "update"; // create

    private final SessionFactory sessionFactory;

    public DBService() {
        //Configuration configuration = getH2Configuration();
        Configuration configuration = getMySqlConfiguration();

        sessionFactory = createSessionFactory(configuration);
    }

    @SuppressWarnings("UnusedDeclaration")
    private Configuration getMySqlConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UsersDataSet.class);
        configuration.addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/db_example");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "root");
        configuration.setProperty("hibernate.show_sql", hibernate_show_sql);
        configuration.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return configuration;
    }

    private Configuration getH2Configuration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UsersDataSet.class);
        configuration.addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./h2db");
        configuration.setProperty("hibernate.connection.username", "tully"); // tully
        configuration.setProperty("hibernate.connection.password", "tully"); // tully
        configuration.setProperty("hibernate.show_sql", hibernate_show_sql);
        configuration.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return configuration;
    }


    // метод для выполнения задания
    public long addUser(String login, String password) throws DBException {
        try {
            Session session = sessionFactory.openSession();

            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            long id = dao.addUser(login, password);

            transaction.commit();
            session.close();
            return id;

        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    // метод для выполнения задания
    public User getUserByLogin(String login) throws DBException {
        try {
            Session session = sessionFactory.openSession();

            AccountDAO dao = new AccountDAO(session);
            User user = dao.getUserByLogin(login);

            session.close();
            return user;

        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    // метод для выполнения задания
    public String getUsersPassword(String login) throws DBException {

        User user = getUserByLogin(login);
        return user == null ? null : user.getPassword();


        // вызывается после getUserByLogin(), ибо юзера может не быть и getPassword() -> NPE вызовет
        // можно, конечно, сделать проверку на null и кидать чекед эксепшен
//        try {
//            Session session = sessionFactory.openSession();
//
//            AccountDAO dao = new AccountDAO(session);
//            String usersPass = dao.getUserByLogin(login).getPassword();
//
//            session.close();
//            return usersPass;
//
//        } catch (HibernateException e) {
//            throw new DBException(e);
//        }
    }



    public UsersDataSet getUser(long id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            UsersDataSet dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String name) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            long id = dao.insertUser(name);
            transaction.commit();
            session.close();
            return id;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {

        // for new hibernate 5.2.10.Final
//        try (Session session = sessionFactory.openSession()) {
//            session.doWork(connection -> {
//                System.out.println("DB name: "    + connection.getMetaData().getDatabaseProductName());
//                System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
//                System.out.println("Driver: "     + connection.getMetaData().getDriverName());
//                System.out.println("Autocommit: " + connection.getAutoCommit());
//            });
//        }

        // old
        try {
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            Connection connection = sessionFactoryImpl.getConnectionProvider().getConnection();
            System.out.println("DB name: "    + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: "     + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    protected void finalize() throws Throwable {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        super.finalize();
    }

    public void close() {
        sessionFactory.close();
    }


}
