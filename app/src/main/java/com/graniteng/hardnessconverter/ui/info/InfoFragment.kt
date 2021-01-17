package com.graniteng.hardnessconverter.ui.info

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.graniteng.hardnessconverter.databinding.InfoFragmentBinding
import com.graniteng.hardnessconverter.utils.screenSizeInDp
import com.graniteng.hardnessconverter.utils.setUpHTMLInView


class InfoFragment : Fragment() {

    private lateinit var binding: InfoFragmentBinding

    private var screenSizeDebugMessage = ""
    private var timesFooterClicked = 0
    private var countDownFooterClicks = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // do nothing
        }
        override fun onFinish() {
            timesFooterClicked = 0
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InfoFragmentBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpHTMLInView(binding.footerInclude.copyright)
        setUpHTMLInView(binding.discover)

        createMessageWithScreenSize()
        setupFooterClickListener()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun createMessageWithScreenSize() {
        screenSizeDebugMessage = "Width: ${screenSizeInDp.x}\nHeight: ${screenSizeInDp.y}"
    }

    private fun setupFooterClickListener() {
        binding.footerInclude.footer.setOnClickListener {
            if (timesFooterClicked == 0) {
                countDownFooterClicks.start()
            }
            if (timesFooterClicked == 2) {
                Toast.makeText(requireContext(), screenSizeDebugMessage, Toast.LENGTH_SHORT).show()
                timesFooterClicked = -1
            }
            timesFooterClicked++

        }
    }
}
