package com.graniteng.hardnessconverter.utils

data class AlertInfo(
    val messageId: Int,
    val arguments: List<Any> = emptyList(),
    val showInfo: Boolean = false
)