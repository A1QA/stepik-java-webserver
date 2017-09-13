package dbService.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author v.chibrikov
 *         <p>
 *         Пример кода для курса на https://stepic.org/
 *         <p>
 *         Описание курса и лицензия: https://github.com/vitaly-chibrikov/stepic_java_webserver
 */
public interface ResultSetHandler<T> {
	T handle(ResultSet resultSet) throws SQLException;
}
