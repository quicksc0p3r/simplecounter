package org.quicksc0p3r.simplecounter.json

import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.Label

data class CountersAndLabels(
    val counters: List<Counter>,
    val labels: List<Label>
)