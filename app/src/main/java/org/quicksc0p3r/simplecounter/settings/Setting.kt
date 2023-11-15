package org.quicksc0p3r.simplecounter.settings

data class Setting(
    val name: String,
    val value: Int,
    val minSDK: Int? = null,
    val minVer: String? = null
)
