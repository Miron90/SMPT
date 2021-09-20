package com.example.smpt.ui.settings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smpt.R
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.databinding.FragmentSettingsBinding
import com.example.smpt.models.CustomColor
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment() {
    private val sharedPreferences: SharedPreferencesStorage by inject()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

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

        var adapter = SpinnerColorAdapter(requireContext(), R.layout.item_color, R.id.name, colors)

        binding.spinnerOtherColor.adapter = adapter
        binding.spinnerOwnColor.adapter = adapter

        binding.signSize.setText(sharedPreferences.signSize.toString())

        binding.close.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            sharedPreferences.signSize = binding.signSize.text.toString().toInt()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}