package com.graniteng.hardnessconverter.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.conversions.Values
import com.graniteng.hardnessconverter.conversions.Values.getScale
import com.graniteng.hardnessconverter.databinding.MainFragmentBinding
import com.graniteng.hardnessconverter.utils.NoFilterAdapter
import com.graniteng.hardnessconverter.utils.alert
import com.graniteng.hardnessconverter.utils.hideKeyboard
import com.graniteng.hardnessconverter.utils.setUpHTMLInView

class MainFragment : Fragment() {

    private var fromScaleSelectedIndex: Int = -1
    private var toScaleSelectedIndex: Int = -1

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelListeners()
        setupScaleDropdown(binding.fromScale) { selectedScale ->
            fromScaleSelectedIndex = selectedScale
        }
        setupScaleDropdown(binding.toScale) { selectedScale ->
            toScaleSelectedIndex = selectedScale
        }

        setUpHTMLInView(binding.footerInclude.copyright)

        setUpCalculateClickListener()

        setUpShareClickListener()

        setupValueChangeListener()

        setHasOptionsMenu(true)
    }

    private fun setUpShareClickListener() {
        binding.shareButton.setOnClickListener {
            shareIt()
        }
    }

    private fun setupViewModelListeners() {
        viewModel.showAlert.observe(viewLifecycleOwner, { message ->
            alert(
                getString(message.messageId, *(message.arguments).toTypedArray()),
                message.showInfo,
                ::showInfo
            )
        })

        viewModel.convertedValue.observe(viewLifecycleOwner, { convertedValue ->
            binding.result.setText(convertedValue)
        })

        viewModel.fromScaleSelectedIndex.observe(viewLifecycleOwner, {
            this.fromScaleSelectedIndex = it
            binding.fromScale.editText?.setText(getScale(it))
        })
        viewModel.toScaleSelectedIndex.observe(viewLifecycleOwner, {
            this.toScaleSelectedIndex = it
            binding.toScale.editText?.setText(getScale(it))
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.actionbar_info -> {
                showInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showInfo() {
        findNavController().navigate(R.id.action_mainFragment_to_infoFragment) // , Bundle.EMPTY, navOptions)
    }

    private fun setupValueChangeListener() {
        binding.value.doAfterTextChanged {
            binding.result.setText("")
        }
    }

    private fun setUpCalculateClickListener() {
        binding.calculate.setOnClickListener { _ ->
            val valueToConvert = binding.value.text.toString()
            viewModel.calculate(fromScaleSelectedIndex, toScaleSelectedIndex, valueToConvert)
            hideKeyboard()
        }
    }

    private fun setupScaleDropdown(scaleInputLayout: TextInputLayout, setParam: (Int) -> Unit) {
        val items = Values.scales
        val adapter = NoFilterAdapter(
            requireContext(),
            R.layout.drop_down_item,
            items
        )

        (scaleInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                println("**** selected position $position")
                setParam(position)
            }
        }
    }

    private fun shareIt() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))

        // I'm building the message here, instead of using string resources,
        // because newlines and empty spaces are not preserved properly
        val body = """
            ~ Hardness Converter ~

            ${getScale(fromScaleSelectedIndex)} : ${binding.value.text}
                        ↓
            ${getScale(toScaleSelectedIndex)} : ${binding.result.text}

            ~~
            ${getString(R.string.share_discover_tools)}
        """.trimIndent()

        sharingIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)))
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("fromScaleSelectedIndex", fromScaleSelectedIndex)
        outState.putInt("toScaleSelectedIndex", toScaleSelectedIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        fromScaleSelectedIndex = savedInstanceState?.getInt("fromScaleSelectedIndex") ?: -1
        toScaleSelectedIndex = savedInstanceState?.getInt("toScaleSelectedIndex") ?: -1
        super.onViewStateRestored(savedInstanceState)
    }
}


