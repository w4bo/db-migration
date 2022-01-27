package it.w4bo.database

import io.github.cdimascio.dotenv.Dotenv
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.sql.DriverManager

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()
    val parser = ArgParser("Migrating data")
    val iip by parser.option(ArgType.String, shortName = "iip").default(dotenv.get("iip"))
    val iport by parser.option(ArgType.Int, shortName = "iport").default(dotenv.get("iport").toInt())
    val idbms by parser.option(ArgType.String, shortName = "idbms").default(dotenv.get("idbms"))
    val idb by parser.option(ArgType.String, shortName = "idb").default(dotenv.get("idb"))
    val iuser by parser.option(ArgType.String, shortName = "iuser").default(dotenv.get("iuser"))
    val ipwd by parser.option(ArgType.String, shortName = "ipwd").default(dotenv.get("ipwd"))
    val waitTime by parser.option(ArgType.Int, shortName = "wait").default(dotenv.get("WAIT_WAIT_TIME").toInt())
    parser.parse(args)
    connect(idbms, iip, iport, idb, iuser, ipwd, waitTime)
}

fun connect(
    idbms: String,
    iip: String,
    iport: Int,
    idb: String,
    iuser: String,
    ipwd: String,
    waitTime: Int
) {
    val iurl = getConnString(idbms, iip, iport, idb)
    val select =
        when (idbms) {
            "oracle" -> "select * from dual"
            "mysql" -> "select 1"
            else -> TODO("Not implemented")
        }
    var serviceDown = true
    val start = System.currentTimeMillis()
    while (serviceDown && (waitTime == 0 || System.currentTimeMillis() - start < waitTime)) {
        try {
            val iconn = DriverManager.getConnection(iurl, iuser, ipwd)
            val ist = iconn.createStatement()
            ist.executeQuery(select).close()
            ist.close()
            iconn.close()
            serviceDown = false
        } catch (e: Exception) {
            println("Error: ${e.message}")
            Thread.sleep(1000)
        }
    }
    if (serviceDown) {
        throw IllegalArgumentException("Unreachable database")
    }
}