package org.quicksc0p3r.simplecounter.json

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@OptIn(ExperimentalStdlibApi::class)
fun ParseJson(json: ByteArray): CountersAndLabels? {
    val builder = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter = builder.adapter<CountersAndLabels>()
    val jsonString = String(json, Charsets.UTF_8)
    return adapter.fromJson(jsonString)
}