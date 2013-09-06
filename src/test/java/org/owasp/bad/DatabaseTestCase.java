package org.owasp.bad;

import org.apache.commons.dbutils.DbUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.AbstractDatabaseSetup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.greaterThan;

/**
 * User: massoo
 */

// TODO: feed the test case with good and bad input
public class DatabaseTestCase extends AbstractDatabaseSetup {

    @Test
    public void testDatabaseInteraction() {

        String userEmail = "bademail@owasp.org' or 1=1--";

        Connection connection = null;

        try {
            connection = getDataSource().getConnection();

            // BAD: there is no validation step as in the good example

            ResultSet resultSet = getLoginByEmail(connection, userEmail);
            Assert.assertThat(resultSet, CoreMatchers.notNullValue());

            int rowcount = 0;
            while (resultSet.next()) {
                LOG.info(resultSet.getString("email"));
                rowcount++;
            }

            // NOTICE: if more logins are returned than a successful SQL injection attack has taken place
            Assert.assertThat("SQL injection: more rows selected than intended", rowcount, greaterThan(1));

            // TODO: do something with the resultSet and verify with assertThat
        } catch (SQLException e) {
            LOG.error("Could not execute query: {}", e.getMessage());
            Assert.fail();
        } catch (Exception e) {
            LOG.error("Error {}", e.getMessage());
            Assert.fail();
        } finally {
            // NOTICE: using DBUtils we are sure that the connection won't remain open
            DbUtils.closeQuietly(connection);
        }

    }

    public ResultSet getLoginByEmail(Connection connection, String email) throws SQLException {
        Statement statement = connection.createStatement();

        // BAD: Dynamic SQL queries build with string concatenation which can be exploited by an attacker
        return statement.executeQuery("select * from login where email = '" + email + "'");
    }

}
