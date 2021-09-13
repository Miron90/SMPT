package com.example.smpt.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.smpt.R


class SignAdapter(context: Context, resource: Int, textResource: Int, objects: ArrayList<String>)
    : ArrayAdapter<String>(context, resource, textResource, objects) {

    //String[] objects;
    var signs = arrayListOf("")

    init {
        signs = objects
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView = LayoutInflater.from(context).inflate(R.layout.item_sign, parent, false);
        val txtSign = itemView.findViewById<View>(R.id.txtSign) as TextView
        txtSign.text = signs[position]
        val img = itemView.findViewById<View>(R.id.imgSign) as ImageView
        when {
            signs[position] == "sfap________" -> img.setImageResource(R.drawable.sfap________)
            signs[position] == "shsx________" -> img.setImageResource(R.drawable.shsx________)
            signs[position] == "sngpu_______" -> img.setImageResource(R.drawable.sngpu_______)
            signs[position] == "sugpe_____mo" -> img.setImageResource(R.drawable.sugpe_____mo)
        }
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