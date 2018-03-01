package com.nao20010128nao.SyamuSearch

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    val (filename, dest) = args
    val name = File(filename).name
    val tmp = File(rand() + ".tmp")
    println("Processing the video...")
    proc("mkdir", "-p", tmp.absolutePath).waitFor()
    Runtime.getRuntime().addShutdownHook(Thread {
        proc("rm", "-rf", tmp.absolutePath).waitFor()
    })
    proc("ffmpeg", "-i", filename, "-r", "4", "${tmp.absolutePath}/image_%05d.png").waitFor()
    println("Collecting images...")
    val files = tmp.listFiles().also { it.sortBy { it.absolutePath } }
    println("Found: ${files.size}")
    // we don't use DB class to reduce memory
    DataOutputStream(BufferedOutputStream(FileOutputStream(dest))).use { dos ->
        dos.writeInt(1) // db length
        dos.writeUTF(name)
        dos.writeInt(files.size)
        files.forEach {
            dos.writeLong(processImage(it))
        }
    }
}

fun proc(vararg command: String): Process = ProcessBuilder().command(*command).inheritIO().start()

fun rand(size: Int = 16): String = ByteArray(size).run {
    SecureRandom().nextBytes(this)
    joinToString("") { "%02x".format(it) }
}

fun processImage(file: File, width: Int = 9, height: Int = 8): Long {
    val bmp = ImageIO.read(file)
    val argb = run {
        val resized = BufferedImage(bmp.width, bmp.height, BufferedImage.TYPE_4BYTE_ABGR)
        resized.createGraphics().also {
            it.drawImage(bmp, 0, 0, bmp.width, bmp.height, null)
            it.dispose()
        }
        resized
    }
    val bmp32Data = run {
        val binary = (argb.raster.dataBuffer as DataBufferByte).data
        val result = ByteArray(binary.size)
        (0 until (width * height)).forEach {
            val a = binary[it * 4 + 0]
            val b = binary[it * 4 + 1]
            val g = binary[it * 4 + 2]
            val r = binary[it * 4 + 3]
            result[it * 4 + 0] = b
            result[it * 4 + 1] = g
            result[it * 4 + 2] = r
            result[it * 4 + 3] = a
        }
        result
    }
    val data = run {
        val result = ByteArray(width * height * 4)
        val s = 12
        var pos = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val srcX0 = x * bmp.width / width
                val srcY0 = y * bmp.height / height

                var r = 0
                var g = 0
                var b = 0
                var a = 0

                for (yy in 0 until s) {
                    for (xx in 0 until s) {
                        val dx = xx * bmp.width / width / s
                        val dy = yy * bmp.height / height / s
                        val p = (srcX0 + dx + (srcY0 + dy) * bmp.width) * 4
                        b += bmp32Data[p]
                        g += bmp32Data[p + 1]
                        r += bmp32Data[p + 2]
                        a += bmp32Data[p + 3]
                    }
                }

                result[pos++] = (b / s / s).toByte()
                result[pos++] = (g / s / s).toByte()
                result[pos++] = (r / s / s).toByte()
                result[pos++] = (a / s / s).toByte()
            }
        }
        result
    }
    return run {
        val mono = IntArray(data.size / 4)
        for (i in 0 until mono.size) {
            mono[i] = 29 * data[i * 4] + 150 * data[i * 4 + 1] + 77 * data[i * 4 + 2]
        }

        var result = 0L
        var p = 0
        for (y in 0..7) {
            for (x in 0..7) {
                result = result shl 1 or if (mono[p] > mono[p + 1]) 1 else 0
                p++
            }
            p++
        }
        result
    }
}
