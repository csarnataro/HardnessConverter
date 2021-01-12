package com.graniteng.hardnessconverter.ui.info

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.utils.setUpHTMLInView

class InfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpHTMLInView(view.findViewById<View>(R.id.copyright) as TextView)
        setUpHTMLInView(view.findViewById<View>(R.id.discover) as TextView)

        super.onViewCreated(view, savedInstanceState)
    }

}