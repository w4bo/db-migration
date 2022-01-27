package test

import io.github.cdimascio.dotenv.Dotenv
import it.w4bo.database.connect
import it.w4bo.database.getConnString
import it.w4bo.database.migrate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * Test Migration.
 */
class TestMigration {

    companion object {
        val dotenv = Dotenv.load()
        val WAIT = 1000 * 60 * 5

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            try {
                connect(dotenv.get("MYSQL_A_DBMS"), dotenv.get("MYSQL_A_IP"), dotenv.get("MYSQL_A_PORT").toInt(), dotenv.get("MYSQL_A_DB"), dotenv.get("MYSQL_A_USER"), dotenv.get("MYSQL_A_PWD"), WAIT)
                connect(dotenv.get("MYSQL_B_DBMS"), dotenv.get("MYSQL_B_IP"), dotenv.get("MYSQL_B_PORT").toInt(), dotenv.get("MYSQL_B_DB"), dotenv.get("MYSQL_B_USER"), dotenv.get("MYSQL_B_PWD"), WAIT)
                connect(dotenv.get("ORACLE_A_DBMS"), dotenv.get("ORACLE_A_IP"), dotenv.get("ORACLE_A_PORT").toInt(), dotenv.get("ORACLE_A_DB"), dotenv.get("ORACLE_A_USER"), dotenv.get("ORACLE_A_PWD"), WAIT)
            } catch (e: Exception) {
                fail { e.message!! }
            }
        }
    }

    @Test
    fun testMysql() {
        val iurl = getConnString(dotenv.get("MYSQL_A_DBMS"), dotenv.get("MYSQL_A_IP"), dotenv.get("MYSQL_A_PORT").toInt(), dotenv.get("MYSQL_A_DB"))
        val iconn = DriverManager.getConnection(iurl, dotenv.get("MYSQL_A_USER"), dotenv.get("MYSQL_A_PWD"))
        val select = "SELECT * FROM CUSTOMER"
        Assertions.assertTrue(rs2list(iconn.createStatement().executeQuery(select)).size > 0)
    }

    @Test
    fun testOracle() {
        val iurl = getConnString(dotenv.get("ORACLE_A_DBMS"), dotenv.get("ORACLE_A_IP"), dotenv.get("ORACLE_A_PORT").toInt(), dotenv.get("ORACLE_A_DB"))
        val iconn = DriverManager.getConnection(iurl, "foodmart", dotenv.get("ORACLE_A_PWD"))
        val select = "SELECT * FROM CUSTOMER"
        Assertions.assertTrue(rs2list(iconn.createStatement().executeQuery(select)).size > 0)
    }

    /**
     * Test migration from MYSQL to MYSQL
     */
    @Test
    fun mysql2mysql() {
        migrate(
            dotenv.get("MYSQL_A_DBMS"),
            dotenv.get("MYSQL_A_IP"),
            dotenv.get("MYSQL_A_PORT").toInt(),
            dotenv.get("MYSQL_A_DB"),
            dotenv.get("MYSQL_B_DBMS"),
            dotenv.get("MYSQL_B_IP"),
            dotenv.get("MYSQL_B_PORT").toInt(),
            dotenv.get("MYSQL_B_DB"),
            dotenv.get("MYSQL_A_USER"),
            dotenv.get("MYSQL_A_PWD"),
            dotenv.get("MYSQL_B_USER"),
            dotenv.get("MYSQL_B_PWD")
        )

        val iurl = getConnString(
            dotenv.get("MYSQL_A_DBMS"),
            dotenv.get("MYSQL_A_IP"),
            dotenv.get("MYSQL_A_PORT").toInt(),
            dotenv.get("MYSQL_A_DB")
        )
        val ourl = getConnString(
            dotenv.get("MYSQL_B_DBMS"),
            dotenv.get("MYSQL_B_IP"),
            dotenv.get("MYSQL_B_PORT").toInt(),
            dotenv.get("MYSQL_B_DB")
        )
        val iconn = DriverManager.getConnection(iurl, dotenv.get("MYSQL_A_USER"), dotenv.get("MYSQL_A_PWD"))
        val oconn = DriverManager.getConnection(ourl, dotenv.get("MYSQL_B_USER"), dotenv.get("MYSQL_B_PWD"))
        val select = "SELECT * FROM CUSTOMER"
        val l1 = rs2list(iconn.createStatement().executeQuery(select))
        val l2 = rs2list(oconn.createStatement().executeQuery(select))
        Assertions.assertEquals(l1.size, l2.size)
    }

    fun rs2list(rs: ResultSet): MutableList<Any> {
        val l: MutableList<Any> = mutableListOf()
        while (rs.next()) {
            l += rs.getObject(1)
        }
        return l
    }
}