package com.example.smpt.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.smpt.R
import com.example.smpt.models.CustomColor


class SpinnerColorAdapter(context: Context, resource: Int, textResource: Int, objects: Array<CustomColor>)
    : ArrayAdapter<CustomColor>(context, resource, textResource, objects) {

    private var colors = objects

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        val txtName = itemView.findViewById<View>(R.id.name) as TextView
        txtName.text = colors[position].name
        val img = itemView.findViewById<View>(R.id.frame) as FrameLayout
        img.background = ContextCompat.getDrawable(context, colors[position].id)

        return itemView
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }
}