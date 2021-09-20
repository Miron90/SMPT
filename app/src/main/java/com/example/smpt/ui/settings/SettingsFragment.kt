package com.example.smpt.ui.settings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.example.smpt.R
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.databinding.FragmentSettingsBinding
import com.example.smpt.models.CustomColor
import com.example.smpt.ui.map.MapFragment
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment() {
    private val sharedPreferences: SharedPreferencesStorage by inject()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var ownColor: CustomColor
    private lateinit var otherColor: CustomColor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = get()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colors = arrayOf(
            CustomColor("Niebieski", R.color.blue),
            CustomColor("Zielony", R.color.greenRGB),
            CustomColor("Czerwony", R.color.red),
            CustomColor("Lazurowy", R.color.lightBlue),
            CustomColor("Żółty", R.color.yellow),
            CustomColor("Fioletowy", R.color.violet),
            CustomColor("Różowy", R.color.pink)
        )

        var adapter1 = SpinnerColorAdapter(requireContext(), R.layout.item_color, R.id.name, colors)
        var adapter2 = SpinnerColorAdapter(requireContext(), R.layout.item_color, R.id.name, colors)
        var ownColorSelection = 0
        var otherColorSelection = 0

        binding.spinnerOtherColor.adapter = adapter1
        binding.spinnerOwnColor.adapter = adapter2

        for (i in 0 until colors.count()) {
            if(sharedPreferences.getOtherMarkerColor() == colors[i].id)
                otherColorSelection = i
            if(sharedPreferences.getOwnMarkerColor() == colors[i].id)
                ownColorSelection = i
        }

        binding.spinnerOtherColor.setSelection(otherColorSelection)
        binding.spinnerOwnColor.setSelection(ownColorSelection)

        binding.spinnerOtherColor.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                otherColor = colors[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.spinnerOwnColor.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                ownColor = colors[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.signSize.setText(sharedPreferences.signSize.toString())

        binding.close.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            sharedPreferences.setOtherMarkerColor(otherColor.id)
            sharedPreferences.setOwnMarkerColor(ownColor.id)
            sharedPreferences.signSize = binding.signSize.text.toString().toInt()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}