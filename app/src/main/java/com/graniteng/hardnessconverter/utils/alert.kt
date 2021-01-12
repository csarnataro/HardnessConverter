package com.graniteng.hardnessconverter.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import com.graniteng.hardnessconverter.R


//fun Activity.alert(message: String?, showInfoButton: Boolean ) {
//    alert(message, showInfoButton, this)
//}

fun Fragment.alert(resourceId: Int) {
    alert(requireContext().getString(resourceId), false)
}

fun Fragment.alert(message: String?, showInfoButton: Boolean = false) {
    alert(message, showInfoButton, requireContext())
}


private fun Fragment.alert(message: String?, showInfoButton: Boolean, context: Context) {
    val inflater = LayoutInflater.from(context)
    val customTitle: View = inflater.inflate(R.layout.custom_alert_dialog, null).apply {
        (findViewById<TextView>(R.id.message)).text = message
        (findViewById<TextView>(R.id.title)).text = context.getString(R.string.error_alert_title)
    }

    val builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
    val d = builder.apply {
        setView(customTitle)
        setCancelable(true)
        setNegativeButton(R.string.ok) { dialog, id ->
            // User clicked OK button
        }
        if (showInfoButton) {
            // setPositiveButton(R.string.info, DialogInterface.OnClickListener { dialog, id -> showInfo() })
        }
    }
    d.show()
}
