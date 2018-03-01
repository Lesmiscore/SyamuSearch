package com.nao20010128nao.SyamuSearch

import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream


fun main(args: Array<String>) {
    val (dest) = args
    println("Processing the videos...")
    val dbFile = DataOutputStream(BufferedOutputStream(FileOutputStream(dest)))
    Runtime.getRuntime().addShutdownHook(Thread {
        dbFile.close()
    })
    dbFile.writeInt(args.size - 1) // db length
    args.drop(1).forEach { filename ->
        println("Processing: $filename")
        val tmp = File(rand() + ".tmp")
        val name = File(filename).name
        proc("mkdir", "-p", tmp.absolutePath).waitFor()
        Runtime.getRuntime().addShutdownHook(Thread {
            proc("rm", "-rf", tmp.absolutePath).waitFor()
        })
        proc("ffmpeg", "-i", filename, "-r", "4", "${tmp.absolutePath}/image_%05d.png").waitFor()
        println("Collecting images...")
        val files = tmp.listFiles().also { it.sortBy { it.absolutePath } }
        println("Found: ${files.size}")

        dbFile.also { dos ->
            dos.writeUTF(name)
            dos.writeInt(files.size)
            files.forEach {
                dos.writeLong(processImage(it))
            }
        }
    }
}