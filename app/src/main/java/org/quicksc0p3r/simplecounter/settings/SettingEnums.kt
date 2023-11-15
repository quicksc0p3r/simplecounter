package org.quicksc0p3r.simplecounter.settings

enum class LightDarkSetting(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2)
}

enum class ColorSetting(val value: Int) {
    SYSTEM(0),
    RED(1),
    ORANGE(2),
    YELLOW(3),
    GREEN(4),
    BLUE(5),
    PURPLE(6)
}

enum class CounterCardStyleSetting(val value: Int) {
    NORMAL(0),
    COMPACT(1)
}