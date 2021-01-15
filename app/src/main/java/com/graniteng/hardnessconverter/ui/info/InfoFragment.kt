package com.graniteng.hardnessconverter.ui.info

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.utils.setUpHTMLInView
import kotlin.math.roundToInt

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpHTMLInView(view.findViewById<View>(R.id.copyright) as TextView)
        setUpHTMLInView(view.findViewById<View>(R.id.discover) as TextView)

        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = ""
        screenSizeInDp.apply {
            // screen width in dp
            textView.append("Width : $x dp")

            // screen height in dp
            textView.append("\nHeight : $y dp")
        }
        super.onViewCreated(view, savedInstanceState)
    }

}


// extension property to get display metrics instance
val Fragment.displayMetrics: DisplayMetrics
    get() {
        // display metrics is a structure describing general information
        // about a display, such as its size, density, and font scaling
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= 30){
            requireActivity().display?.apply {
                getRealMetrics(displayMetrics)
            }
        }else{
            // getMetrics() method was deprecated in api level 30
            @Suppress("DEPRECATION")
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        return displayMetrics
    }


// extension property to get screen width and height in dp
val Fragment.screenSizeInDp: Point
    get() {
        val point = Point()
        displayMetrics.apply {
            // screen width in dp
            point.x = (widthPixels / density).roundToInt()


            // dp = px / (dpi / 160)
            // screen height in dp
            point.y = (heightPixels / density).roundToInt()
        // heightPixels / ( densityDpi / DisplayMetrics.DENSITY_DEFAULT) // (heightPixels / density).roundToInt()
        }

        return point
    }


/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.getResources()
        .getDisplayMetrics().densityDpi as Float / DisplayMetrics.DENSITY_DEFAULT)
}