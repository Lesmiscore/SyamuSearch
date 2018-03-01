package com.nao20010128nao.SyamuSearch

import java.io.File

fun main(args: Array<String>) {
    args.map { File(it) }.forEach {
        println("On file: ${it.absolutePath}")
        try {
            val data = processImage(it)
            println(" Value in decimal: $data")
            println(" Binary: ${"%64s".format(java.lang.Long.toBinaryString(data)).replace(' ', '0')}")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}