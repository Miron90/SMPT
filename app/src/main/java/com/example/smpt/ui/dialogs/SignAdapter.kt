package com.example.smpt.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.caverock.androidsvg.SVG
import com.example.smpt.R
import com.example.smpt.models.Sign


class SignAdapter(context: Context, resource: Int, textResource: Int, objects: Array<Sign>)
    : ArrayAdapter<Sign>(context, resource, textResource, objects) {

    var signs: Array<Sign>

    init {
        signs = objects
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView = LayoutInflater.from(context).inflate(R.layout.item_sign, parent, false);
        val txtSign = itemView.findViewById<View>(R.id.txtSign) as TextView
        txtSign.text = signs[position].signCode
        val img = itemView.findViewById<View>(R.id.imgSign) as ImageView
        img.setImageDrawable(
            PictureDrawable(SVG.getFromString(signs[position].signSVG).renderToPicture()))
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