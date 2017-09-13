package dbService.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author v.chibrikov
 *         <p>
 *         Пример кода для курса на https://stepic.org/
 *         <p>
 *         Описание курса и лицензия: https://github.com/vitaly-chibrikov/stepic_java_webserver
 */
public class Executor {
    private final Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public int execUpdate(String update) throws SQLException {
        Statement stmt = connection.createStatement();

//      stmt.execute(update);
        int result = stmt.executeUpdate(update);
        stmt.close();
        return result;
    }

    public <T> T execQuery(String query,
                           ResultSetHandler<T> handler)
            throws SQLException {
        Statement stmt = connection.createStatement();

        //stmt.execute(query);
        //ResultSet result = stmt.getResultSet();
        ResultSet result = stmt.executeQuery(query);

        T value = handler.handle(result);
        result.close();
        stmt.close();

        return value;
    }

}
