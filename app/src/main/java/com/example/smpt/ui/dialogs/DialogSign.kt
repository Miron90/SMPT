package com.example.smpt.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.Spinner
import com.example.smpt.R
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.models.Sign
import com.example.smpt.models.SignUploadDto
import com.example.smpt.remote.ApiInterface
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogSign(
    context: Context,
    private val location: GeoPoint,
    private val apiInterface: ApiInterface,
    private val sharedPreferencesStorage: SharedPreferencesStorage)
{
    private val dialog = Dialog(context)

    init {
        build()
        dialog.show()
    }

    private fun build() {
        dialog.setContentView(R.layout.dialog_sign)
        val signs = sharedPreferencesStorage.getSigns()
        setSigns(signs)
    }

    private fun setSigns(signs: Array<Sign>){
        val spinner = dialog.findViewById(R.id.spinnerSigns) as Spinner
        val adapter = SignAdapter(dialog.context, R.layout.item_sign, R.id.txtSign, signs)
        val button = dialog.findViewById(R.id.btnDialog) as Button
        var signCode = ""

        spinner.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                signCode = signs[p2].signCode
                //dialog.dismiss()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }


        }

        spinner.adapter = adapter

        button.setOnClickListener {
            apiInterface.sendSign(SignUploadDto(location.longitude, location.latitude, signCode)).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.body() != null) Log.d(
                        "API",
                        "send sign" + response.message()
                    )
                    Log.d("API", "send sign" + response.message())
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.d("API", "Error" + t.toString())
                }
            })

            dialog.dismiss()
        }

        button.setOnClickListener {
            apiInterface.sendSign(SignUploadDto(location.longitude, location.latitude, signCode)).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.body() != null) Log.d(
                        "API",
                        "send sign" + response.message()
                    )
                    Log.d("API", "send sign" + response.message())
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.d("API", "Error" + t.toString())
                }
            })

            dialog.dismiss()
        }

        button.setOnClickListener {
            apiInterface.sendSign(SignUploadDto(location.longitude, location.latitude, signCode)).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.body() != null) Log.d(
                        "API",
                        "send sign" + response.message()
                    )
                    Log.d("API", "send sign" + response.message())
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.d("API", "Error" + t.toString())
                }
            })

            dialog.dismiss()
        }

    }
}