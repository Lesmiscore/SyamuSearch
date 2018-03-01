package com.nao20010128nao.SyamuSearch

import java.io.File

fun main(args: Array<String>) {
    args.map { File(it) }.forEach {
        println("On file: ${it.absolutePath}")
        try {
            val dbs = it.readDB()
            dbs.forEachIndexed { index, db ->
                println(" No. ${index + 1}")
                println("  Name: ${db.name}")
                println("  Frames: ${db.frames.size}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}