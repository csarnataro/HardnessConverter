package com.graniteng.hardnessconverter.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.conversions.Values.hardnesses
import com.graniteng.hardnessconverter.conversions.Values.scales
import com.graniteng.hardnessconverter.conversions.Values.ranges

import com.graniteng.hardnessconverter.utils.SingleLiveEvent

class MainViewModel : ViewModel() {

    private var _convertedValue = MutableLiveData<String>()
    val convertedValue: LiveData<String>
        get() = _convertedValue

    private val _showAlert = SingleLiveEvent<Pair<Int, List<String>>>()
    val showAlert: LiveData<Pair<Int, List<String>>>
        get() = _showAlert


    @SuppressLint("DefaultLocale")
    fun calculate(selectedSourceScale: Int, selectedTargetScale: Int, valueText: String?) {
        val valueToConvert: Number? = checkInput(
            selectedSourceScale = selectedSourceScale,
            selectedTargetScale = selectedTargetScale,
            valueAsString = valueText
        )
        if (valueToConvert != null) {

            // if input is ok, the selected scales are saved as default scales (available next time)
//            val settings: SharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0)
//            val editor = settings.edit()
//            editor.putInt("selectedSourceScale", selectedSourceScale)
//            editor.putInt("selectedTargetScale", selectedTargetScale)
//
//            // Commit the edits!
//            editor.commit()

            val durx = valueToConvert.toFloat()
            val dury: Float
            var i = 1
            val ind_dur_x: Int = selectedSourceScale
            val ind_dur_y: Int = selectedTargetScale
            var a: String = hardnesses[i][ind_dur_x]
            var aNum = if (a == "-") {
                durx + 1
            } else {
                a.toFloat()
            }
            while (aNum > durx) {
                i += 1
                a = hardnesses[i][ind_dur_x]
                aNum = if (a == "-") {
                    durx + 1
                } else if (a == "+") {
                    durx - 1
                } else {
                    a.toFloat()
                }
            }
            val dur1x: String = hardnesses[i - 1][ind_dur_x]
            val dur2x: String = hardnesses[i][ind_dur_x]
            val dur1y: String = hardnesses[i - 1][ind_dur_y]
            val dur2y: String = hardnesses[i][ind_dur_y]
            if (dur1x == "-" || dur1x == "+") {
                _convertedValue.value = "N/A"
                return
            } else if (dur2x == "-" || dur2x == "+") {
                _showAlert.value = Pair(
                    R.string.error_target_out_of_range,
                    listOf(
                        scales[selectedSourceScale],
                        ranges[selectedSourceScale][0],
                        ranges[selectedSourceScale][1]
                    )
                )
                _convertedValue.value = "N/A"
                return
            } else if (dur1y == "-" || dur2y == "-" || dur1y == "+" || dur2y == "+") {
                _showAlert.value = Pair(
                    R.string.error_no_matching,
                    listOf(
                        scales[selectedTargetScale],
                        scales[selectedSourceScale]
                    )
                )
                _convertedValue.value = "N/A"
                return
            } else {
                val dur1xInt = dur1x.toFloat()
                val dur1yInt = dur1y.toFloat()
                val dur2xInt = dur2x.toFloat()
                val dur2yInt = dur2y.toFloat()
                dury = dur1yInt + (dur2yInt - dur1yInt) / (dur2xInt - dur1xInt) * (durx - dur1xInt)
                _convertedValue.value = String.format("%.2f", dury)
                return
            }
        }
    }

    private fun checkInput(
        selectedSourceScale: Int,
        selectedTargetScale: Int,
        valueAsString: String?
    ): Number? {

        // check scales
        if (selectedSourceScale < 0 || selectedTargetScale < 0) {
            _showAlert.value = Pair(R.string.error_choose_scale, emptyList())
            return null
        }

        // check existence of input
        if (valueAsString == null) {
            _showAlert.value = Pair(R.string.value_not_numeric, emptyList())
            return null
        }

        var originalValueAsString = valueAsString

        var value: Float?
        try {
            value = originalValueAsString.toFloat()
        } catch (nfe: NumberFormatException) {
            if (originalValueAsString.contains(".")) {
                originalValueAsString = originalValueAsString.replace("\\.".toRegex(), ",")
            } else if (originalValueAsString.contains(",")) {
                originalValueAsString = originalValueAsString.replace(",".toRegex(), "\\.")
            }
            value = try {
                originalValueAsString.toFloat()
            } catch (nfe2: NumberFormatException) {
                null
            }
        }
        return if (value != null) {
            value
        } else {
            _showAlert.value = Pair(R.string.value_not_numeric, emptyList())
            null
        }
    }

}