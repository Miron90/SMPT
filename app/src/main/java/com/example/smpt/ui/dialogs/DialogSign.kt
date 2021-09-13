package com.example.smpt.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.Spinner
import com.example.smpt.R

class DialogSign(context: Context) {
    private val dialog = Dialog(context)

    init {
        build()
        dialog.show()
    }

    private fun build() {
        dialog.setContentView(R.layout.dialog_sign)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val items = arrayListOf("sfap________", "shsx________", "sngpu_______", "sugpe_____mo")
        val spinner = dialog.findViewById(R.id.spinnerSigns) as Spinner
        val adapter = SignAdapter(dialog.context, R.layout.item_sign, R.id.txtSign, items)
        val button = dialog.findViewById(R.id.btnDialog) as Button
        var signCode = ""

        spinner.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                signCode = items[p2]
                //dialog.dismiss()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }


        }

        spinner.adapter = adapter

        button.setOnClickListener {
            //wyslac signCode
            dialog.dismiss()
        }

    }
}