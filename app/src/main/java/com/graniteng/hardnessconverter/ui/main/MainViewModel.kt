package com.graniteng.hardnessconverter.ui.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.conversions.Values.hardnesses
import com.graniteng.hardnessconverter.conversions.Values.ranges
import com.graniteng.hardnessconverter.conversions.Values.scales
import com.graniteng.hardnessconverter.utils.AlertInfo
import com.graniteng.hardnessconverter.utils.SingleLiveEvent

class MainViewModel: AndroidViewModel {

    companion object {
        const val PREF_FROM_SCALE = "selectedSourceScale"
        const val PREF_TO_SCALE = "selectedTargetScale"
    }

    constructor(application: Application) : super(application) {
        restorePreferences()
    }

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(getApplication())


    private var _fromScaleSelectedIndex = MutableLiveData<Int>()
    val fromScaleSelectedIndex: LiveData<Int>
        get() = _fromScaleSelectedIndex

    private var _toScaleSelectedIndex = MutableLiveData<Int>()
    val toScaleSelectedIndex: LiveData<Int>
        get() = _toScaleSelectedIndex

    private var _convertedValue = MutableLiveData<String>()
    val convertedValue: LiveData<String>
        get() = _convertedValue

    private val _showAlert = SingleLiveEvent<AlertInfo>()
    val showAlert: LiveData<AlertInfo>
        get() = _showAlert


    fun calculate(selectedSourceScale: Int, selectedTargetScale: Int, valueText: String?) {
        val valueToConvert: Number? = checkInput(
            selectedSourceScale = selectedSourceScale,
            selectedTargetScale = selectedTargetScale,
            valueAsString = valueText
        )
        if (valueToConvert != null) {
            storePreferences(
                selectedSourceScale = selectedSourceScale,
                selectedTargetScale = selectedTargetScale
            )
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
                aNum = when (a) {
                    "-" -> durx + 1
                    "+" -> durx - 1
                    else -> a.toFloat()
                }
            }
            val dur1x: String = hardnesses[i - 1][ind_dur_x]
            val dur2x: String = hardnesses[i][ind_dur_x]
            val dur1y: String = hardnesses[i - 1][ind_dur_y]
            val dur2y: String = hardnesses[i][ind_dur_y]
            return when {
                (dur1x == "-" || dur1x == "+") -> {
                    _showAlert.value = AlertInfo(
                        R.string.error_source_out_of_range,
                        listOf(
                            scales[selectedSourceScale],
                            ranges[selectedSourceScale][0],
                            ranges[selectedSourceScale][1]
                        ),
                        true
                    )
                    _convertedValue.value = "N/A"
                }
                (dur2x == "-" || dur2x == "+") -> {
                    _showAlert.value = AlertInfo(
                        R.string.error_target_out_of_range,
                        listOf(
                            scales[selectedSourceScale],
                            ranges[selectedSourceScale][0],
                            ranges[selectedSourceScale][1]
                        ),
                        true
                    )
                    _convertedValue.value = "N/A"
                }
                (dur1y == "-" || dur2y == "-" || dur1y == "+" || dur2y == "+") -> {
                    _showAlert.value = AlertInfo(
                        R.string.error_no_matching,
                        listOf(
                            scales[selectedTargetScale],
                            scales[selectedSourceScale]
                        ),
                        true
                    )
                    _convertedValue.value = "N/A"
                }
                else -> {
                    val dur1xInt = dur1x.toFloat()
                    val dur1yInt = dur1y.toFloat()
                    val dur2xInt = dur2x.toFloat()
                    val dur2yInt = dur2y.toFloat()
                    dury =
                        dur1yInt + (dur2yInt - dur1yInt) / (dur2xInt - dur1xInt) * (durx - dur1xInt)
                    _convertedValue.value = String.format("%.2f", dury)

                }
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
            _showAlert.value = AlertInfo(R.string.error_choose_scale)
            return null
        }

        // check existence of input
        if (valueAsString == null) {
            _showAlert.value = AlertInfo(R.string.value_not_numeric)
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
            _showAlert.value = AlertInfo(R.string.value_not_numeric)
            null
        }
    }

    private fun storePreferences(selectedSourceScale: Int, selectedTargetScale: Int) {
        preferences.edit().apply {
            putInt(PREF_FROM_SCALE, selectedSourceScale)
            putInt(PREF_TO_SCALE, selectedTargetScale)
            apply()
        }
    }


    private fun restorePreferences(){
        // Restore preferences
        _fromScaleSelectedIndex.value = preferences.getInt("selectedSourceScale", -1)
        _toScaleSelectedIndex.value = preferences.getInt("selectedTargetScale", -1)

    }



}