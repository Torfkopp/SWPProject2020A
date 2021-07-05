package de.uol.swp.server.usermanagement.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Helper Class to open and close SQL database connections
 *
 * @author Marvin Drees
 * @since 2021-07-04
 */
public class SqlConnector {

    private static final Logger LOG = LogManager.getLogger(SqlConnector.class);

    /**
     * Helper method to close a provided connection
     * and SQL statement.
     *
     * @param conn  The connection to be closed
     * @param pstmt The statement to be closed
     *
     * @author Marvin Drees
     * @since 2021-07-02
     */
    protected void closeConnection(Connection conn, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage());
        }
    }

    /**
     * Helper method to open a connection to the SQL database.
     *
     * @return The opened connection
     *
     * @throws SQLException Exception when something goes wrong opening the connection
     * @author Marvin Drees
     * @since 2021-07-02
     */
    protected Connection openConnection(String dbUrl, String user, String pass) throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl, user, pass);
        conn.setAutoCommit(true);
        return conn;
    }
}
