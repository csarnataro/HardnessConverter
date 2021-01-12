package com.graniteng.hardnessconverter.utils

import android.widget.TextView
import androidx.core.text.HtmlCompat
import java.util.*

fun setUpHTMLInView(view: TextView) {
    var copyright = view.text
    copyright = copyright.replace(
        "__YEAR__".toRegex(), Calendar.getInstance().get(Calendar.YEAR).toString()
    )

    view.text = HtmlCompat.fromHtml(copyright, HtmlCompat.FROM_HTML_MODE_LEGACY); // Html.fromHtml(copyright)
}
