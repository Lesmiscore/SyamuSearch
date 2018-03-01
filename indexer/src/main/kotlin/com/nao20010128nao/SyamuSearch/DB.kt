package com.nao20010128nao.SyamuSearch

import java.io.*
import java.util.*

data class DB(val name: String, val frames: LongArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DB

        if (name != other.name) return false
        if (!Arrays.equals(frames, other.frames)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + Arrays.hashCode(frames)
        return result
    }

    fun serialize(): ByteArray = ByteArrayOutputStream().also {
        DataOutputStream(it).use { dos ->
            dos.writeUTF(name)
            dos.writeInt(frames.size)
            frames.forEach { dos.writeLong(it) }
        }
    }.toByteArray()
}

fun File.readDB(): List<DB> = inputStream().readDB()

fun InputStream.readDB(): List<DB> {
    return DataInputStream(this).use { dis ->
        val number = dis.readInt()
        (0 until number).map {
            val name = dis.readUTF()
            val frames = LongArray(dis.readInt())
            (0 until frames.size).forEach {
                frames[it] = dis.readLong()
            }
            DB(name, frames)
        }
    }
}

fun List<DB>.writeDB(file: File) = writeDB(file.outputStream())

fun List<DB>.writeDB(os:OutputStream) {
    DataOutputStream(os).use { dos ->
        dos.writeInt(size)
        forEach {
            dos.write(it.serialize())
        }
    }
}
