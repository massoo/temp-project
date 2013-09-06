package org.owasp.good;

import org.apache.commons.dbutils.DbUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.AbstractDatabaseSetup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: massoo
 */

// TODO: feed the test case with good and bad input
public class DatabaseTestCase extends AbstractDatabaseSetup {

    // NOTICE: pattern used to white list trusted characters
    private static Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Test
    public void testDatabaseInteraction() {

        Connection connection = null;

        try {
            connection = getDataSource().getConnection();

            // NOTICE: We validate if the user input contains valid date to be passed to the SQL query
            validate(email);
            ResultSet resultSet = getLoginByEmail(connection, email);
            Assert.assertThat(resultSet, CoreMatchers.notNullValue());

            int rowcount = 0;
            while (resultSet.next()) {
                LOG.info(resultSet.getString("email"));
                rowcount++;
            }

            Assert.assertThat("SQL injection: more rows selected than intended", rowcount, CoreMatchers.is(1));
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
        PreparedStatement preparedStatement = connection.prepareStatement("select * from login where email = ?");
        preparedStatement.setString(1, email);

        return preparedStatement.executeQuery();
    }

    public void validate(String email) throws Exception {
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new Exception("Validation failed: Email doesn't match the pattern");
        }
    }

    /**
     * EXTRA: If you only expect x-amount records to be returned we advise to limit this through the SQL query (e.g.: select top 5, limit, ..)
     */

}
