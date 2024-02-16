package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.databinding.FragmentSettingsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtons()
    }

    private fun setButtons() {
        binding.btnOpeningTime.setOnClickListener {
            timePickerDialog(binding.etOpeningTime)
        }

        binding.btnClosingTime.setOnClickListener {
            timePickerDialog(binding.etClosingTime)
        }

        binding.fabConfirmSettings.setOnClickListener {
            db.collection("settings").document("settings").set(
                hashMapOf(
                    "open_days" to getOpenDays(),
                    "opening_time" to binding.etOpeningTime.text.toString(),
                    "closing_time" to binding.etClosingTime.text.toString()
                )
            )
        }

        binding.btnSetServices.setOnClickListener {
            findNavController().navigate(R.id.servicesFragment)
        }
    }

    private fun getOpenDays(): BooleanArray {
        val openDays = BooleanArray(7)
        openDays[0] = binding.checkboxMonday.isChecked
        openDays[1] = binding.checkboxTuesday.isChecked
        openDays[2] = binding.checkboxWednesday.isChecked
        openDays[3] = binding.checkboxThursday.isChecked
        openDays[4] = binding.checkboxFriday.isChecked
        openDays[5] = binding.checkboxSaturday.isChecked
        openDays[6] = binding.checkboxSunday.isChecked

        return openDays
    }

    private fun timePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar[HOUR_OF_DAY] = hour
                calendar[MINUTE] = minute
                editText.setText(
                    SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).format(calendar.time)
                )
            },
            calendar[HOUR_OF_DAY],
            calendar[MINUTE],
            true
        ).show()
    }
}