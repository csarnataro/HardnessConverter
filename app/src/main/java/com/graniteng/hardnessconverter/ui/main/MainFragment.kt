package com.graniteng.hardnessconverter.ui.main

import android.os.Bundle
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.graniteng.hardnessconverter.R
import com.graniteng.hardnessconverter.conversions.Values
import com.graniteng.hardnessconverter.databinding.MainFragmentBinding
import com.graniteng.hardnessconverter.isTablet
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

        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.showAlert.observe(viewLifecycleOwner, { message ->
            alert(getString(message.first, *(message.second).toTypedArray()), true)
        })

        viewModel.convertedValue.observe(viewLifecycleOwner, { convertedValue ->
            binding.result?.setText(convertedValue)
        })

        setupScaleDropdown(binding.fromScale) { selectedScale ->
            fromScaleSelectedIndex = selectedScale
        }
        setupScaleDropdown(binding.toScale) { selectedScale ->
            toScaleSelectedIndex = selectedScale
        }

        setUpHTMLInView(view.findViewById<View>(R.id.copyright) as TextView)

        setUpCalculateClickListener()

        setupValueChangeListener()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        if (isTablet()) {
            menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Share")
                .setIcon(R.drawable.ic_share)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.actionbar_info -> {
                println("****** NAVIGATING TO INFO")
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
        binding.calculate.setOnClickListener { view ->
            val fromScale = fromScaleSelectedIndex
            val toScale = toScaleSelectedIndex
            val valueToConvert = binding.value?.text.toString()
            viewModel.calculate(fromScale, toScale, valueToConvert)
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

        (scaleInputLayout.editText as? AutoCompleteTextView)?.apply{
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                println("**** selected position $position")
                setParam(position)
            }
        }

    }

}


