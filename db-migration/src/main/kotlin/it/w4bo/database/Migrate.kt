@file:JvmName("Migrate")

package it.w4bo.database

import io.github.cdimascio.dotenv.Dotenv
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSetMetaData

/**
 * Get the connection string
 * @param dbms DBMS type (e.g., oracle or mysql)
 * @param ip IP
 * @param port PORT
 * @param db Database name
 */
fun getConnString(dbms: String, ip: String, port: Int, db: String?): String {
    return when (dbms) {
        "mysql" -> "jdbc:mysql://$ip:$port${if (db == null) { "" } else { "/$db" }}?serverTimezone=UTC&autoReconnect=true"
        "oracle" -> "jdbc:oracle:thin:@$ip:$port/$db"
        else -> ""
    }
}

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()
    val parser = ArgParser("Data migration")
    val iip by parser.option(ArgType.String, shortName = "iip").default(dotenv.get("iip"))
    val oip by parser.option(ArgType.String, shortName = "oip").default(dotenv.get("oip"))
    val iport by parser.option(ArgType.Int, shortName = "iport").default(dotenv.get("iport").toInt())
    val oport by parser.option(ArgType.Int, shortName = "oport").default(dotenv.get("oport").toInt())
    val idbms by parser.option(ArgType.String, shortName = "idbms").default(dotenv.get("idbms"))
    val odbms by parser.option(ArgType.String, shortName = "odbms").default(dotenv.get("odbms"))
    val idb by parser.option(ArgType.String, shortName = "idb").default(dotenv.get("idb"))
    val odb by parser.option(ArgType.String, shortName = "odb").default(dotenv.get("odb"))
    val iuser by parser.option(ArgType.String, shortName = "iuser").default(dotenv.get("iuser"))
    val ouser by parser.option(ArgType.String, shortName = "ouser").default(dotenv.get("ouser"))
    val ipwd by parser.option(ArgType.String, shortName = "ipwd").default(dotenv.get("ipwd"))
    val opwd by parser.option(ArgType.String, shortName = "opwd").default(dotenv.get("opwd"))
    val table by parser.option(ArgType.String, description = "tables").default(dotenv.get("tables"))
    parser.parse(args)

    migrate(idbms, iip, iport, idb, odbms, oip, oport, odb, iuser, ipwd, ouser, opwd, table)
}

fun migrate(
    idbms: String,
    iip: String,
    iport: Int,
    idb: String,
    odbms: String,
    oip: String,
    oport: Int,
    odb: String,
    iuser: String,
    ipwd: String,
    ouser: String,
    opwd: String,
    table: String = ""
) {
    // Check that all the drivers exist
    Class.forName("com.mysql.cj.jdbc.Driver")
    Class.forName("oracle.jdbc.driver.OracleDriver")

    // Set incoming/outgoing connections
    val iurl = getConnString(idbms, iip, iport, idb)
    val ourl = getConnString(odbms, oip, oport, odb)
    val iconn = DriverManager.getConnection(iurl, iuser, ipwd)
    val oconn = DriverManager.getConnection(ourl, ouser, opwd)
    oconn.autoCommit = false
    val tables: MutableList<String> = table.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

    if (tables.isEmpty()) {
        val select =
            when (idbms) {
                "oracle" -> "select table_name from all_tables where lower(owner) = lower('$iuser')"
                "mysql" -> "select table_name from information_schema.tables where lower(table_schema) = lower('$idb')"
                else -> TODO("Not implemented")
            }
        val ist = iconn.createStatement()
        val rs = ist.executeQuery(select)
        while (rs.next()) {
            tables += rs.getObject(1).toString()
        }
    }

    tables.forEach { it ->
        print("$it... ")
        val itable = tableName(idbms, it)
        val otable = tableName(odbms, it)

        val startTime = System.currentTimeMillis()
        val select = "SELECT * FROM $itable"
        val ist = iconn.createStatement()
        val rs = ist.executeQuery(select)

        val rsmd: ResultSetMetaData = rs.getMetaData()
        disableIndexes(odbms, oconn)
        val cols = rsmd.columnCount
        val outquery = "INSERT INTO $otable VALUES (${(1..cols).map { "?" }.reduce { a, b -> "$a,$b" }})"
        val ost = oconn.prepareStatement(outquery)
        var c = 0L

        try {
            while (rs.next()) {
                (1..cols).forEach { ost.setObject(it, rs.getObject(it)) }
                ost.addBatch()
                c += 1
                if (c % 100000 == 0L) {
                    ost.executeBatch()
                }
            }
            ost.executeBatch()
            oconn.commit()
        } catch (e: Exception) {
            println(e.message)
        }
        ost.close()
        ist.close()
        println("Done $c tuples in ${System.currentTimeMillis() - startTime} ms.")
    }
}

/**
 * Quote table name depending on the DBMS
 * @param dbms DBMS tpye
 * @param table table name
 */
fun tableName(dbms: String, table: String): String {
    return when (dbms) {
        "oracle" -> "\"$table\""
        "mysql" -> "`$table`"
        else -> TODO("Not implemented")
    }
}

fun disableIndexes(dbms: String, conn: Connection) {
    when (dbms) {
        "mysql" -> {
            conn.createStatement().executeQuery("SET autocommit=0")
            conn.createStatement().executeQuery("SET unique_checks=0")
            conn.createStatement().executeQuery("SET foreign_key_checks=0")
        }
        "oracle" -> {
            // do nothing
        }
        else -> TODO("Not implemented")
    }
}