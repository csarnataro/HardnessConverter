package com.graniteng.hardnessconverter

fun MainActivity.setTabletMode() {
    val isTabletConf: String = getString(R.string.isTablet)
    MainActivity.isTablet = if (isTabletConf == "true") {
        1
    } else {
        0
    }
}

fun isTablet(): Boolean {
    return MainActivity.isTablet == 1
}
