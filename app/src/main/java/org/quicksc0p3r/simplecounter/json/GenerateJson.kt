package org.quicksc0p3r.simplecounter.json

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.Label

@OptIn(ExperimentalStdlibApi::class)
fun GenerateJson(counters: List<Counter>, labels: List<Label>): ByteArray {
    val builder = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter = builder.adapter<CountersAndLabels>()
    val countersAndLabels = CountersAndLabels(counters, labels)
    return adapter.toJson(countersAndLabels).toByteArray()
}