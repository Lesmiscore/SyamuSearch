package com.nao20010128nao.SyamuSearch

import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import java.io.File


fun main(args: Array<String>) {
    val (imageFile) = args
    val dbs: List<DB> = File("./dbs/").listFiles().map { it.readDB() }.flatMap { it }
    println("DB読み込み完了: ${dbs.size}")
    val imageId = processImage(File(imageFile))
    println("画像読み込み完了: ID: $imageId")
    var count = 1
    dbs.forEach {
        it.frames.forEachIndexed { number, frame ->
            val distance = java.lang.Long.bitCount(imageId xor frame)
            if (distance < 4) {
                println("類似地点: No. ${count++}")
                println("ハミング距離: $distance")
                println("動画名: ${it.name}")
                println("内部フレーム: $number")
                val period = Period.millis(number * 250)

                val formatter = PeriodFormatterBuilder()
                        .appendYears().appendSuffix("年", "年")
                        .appendMonths().appendSuffix("月", "月")
                        .appendWeeks().appendSuffix("週", "週")
                        .appendDays().appendSuffix("日", "日")
                        .appendHours().appendSuffix("時間", "時間")
                        .appendMinutes().appendSuffix("分", "分")
                        .appendSeconds().appendSuffix("秒", "秒")
                        .appendMillis3Digit().printZeroNever()
                        .toFormatter()

                val elapsed = formatter.print(period)
                println("動画内時間: $elapsed")
            }
        }
    }
}
