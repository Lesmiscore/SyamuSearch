package com.nao20010128nao.SyamuSearch

import java.io.File

fun main(args:Array<String>){
    val (imageFile)=args
    val dbs:List<DB> = File("dbs").listFiles().map { it.readDB() }.flatMap { it }
    println("DB読み込み完了: ${dbs.size}")
    val imageId= processImage(File(imageFile))
    println("画像読み込み完了: ID: $imageId")
}
