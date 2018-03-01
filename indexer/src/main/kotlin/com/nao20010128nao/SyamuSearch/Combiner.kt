package com.nao20010128nao.SyamuSearch

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
    println("Finding available DBs...")
    val (dest) = args
    val dbs = args.drop(1).map { File(it) }.filter { it.exists() && checkDb(it) }
    println("DBs files: ${dbs.size}")
    val loaded = dbs.map { it.readDB() }.flatMap { it }
    loaded.writeDB(File(dest))
}

fun checkDb(file: File): Boolean = try {
    DataInputStream(FileInputStream(file)).use { dis ->
        dis.readInt()
        dis.readUTF()
        val count = dis.readInt()
        (0 until count).forEach { dis.readLong() }
        true
    }
} catch (e: Throwable) {
    false
}
