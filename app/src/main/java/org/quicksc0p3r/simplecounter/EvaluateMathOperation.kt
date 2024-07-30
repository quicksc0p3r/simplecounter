package org.quicksc0p3r.simplecounter

import kotlin.math.pow
import kotlin.math.roundToInt

fun evaluateMathOperation(operand: Int, operationString: String, minValue: Int, maxValue: Int): Int? {
    val operator = if (operationString.isNotEmpty()) operationString[0] else null
    if (operator == null || operator !in "+-*/^") return null

    val operand2 = operationString.substring(1).filterNot { it.isWhitespace() }.toDoubleOrNull()
    if (operator == '/' && operand2 == 0.0) return null
    var result: Double? = null

    if (operand2 != null)
        when (operator) {
            '+' -> result = operand + operand2
            '-' -> result = operand - operand2
            '*' -> result = operand * operand2
            '/' -> result = operand / operand2
            '^' -> result = operand.toDouble().pow(operand2)
        }

    if (result != null && !result.isNaN()) {
        val intResult = result.roundToInt()
        return if (intResult in minValue..maxValue)
            intResult
        else
            null
    }
    else
        return null
}