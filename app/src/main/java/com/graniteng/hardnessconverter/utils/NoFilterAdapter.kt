package com.graniteng.hardnessconverter.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

/**
 * This adapter is heavily inspired by this post:
 * https://blog.usejournal.com/there-is-no-material-design-spinner-for-android-3261b7c77da8
 *
 * The reason we have to subclass ArrayAdapter is because we need
 * the AutoCompleteTextView to act like a proper Spinner.  Thus we have to
 * override the AutoCompleteTextView's Filter so that it NEVER performs
 * filtering of the dropdown menu items.
 */
class NoFilterAdapter(context: Context, layout: Int, var values: Array<String>) :
    ArrayAdapter<String>(context, layout, values) {
    private val noOpFilter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return noOpFilter
    }

}