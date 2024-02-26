package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        setNumberPicker()
        getSettings()
    }

    private fun setButtons() {
        binding.btnOpeningTime.setOnClickListener {
            timePickerDialog(binding.etOpeningTime)
        }

        binding.btnClosingTime.setOnClickListener {
            timePickerDialog(binding.etClosingTime)
        }

        binding.fabConfirmSettings.setOnClickListener {
            if (binding.etOpeningTime.text.isEmpty() || binding.etClosingTime.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_missing_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            db.collection("settings").document("settings").set(
                hashMapOf(
                    "open_days" to getOpenDays(),
                    "opening_time" to binding.etOpeningTime.text.toString(),
                    "closing_time" to binding.etClosingTime.text.toString(),
                    "max_appointments" to binding.npNumWorkers.value
                )
            ).addOnSuccessListener {
                Toast.makeText(
                    requireContext(), getString(R.string.settings_save_success), Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(), getString(R.string.settings_save_error), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setNumberPicker() {
        binding.npNumWorkers.apply {
            minValue = 1
            maxValue = 100
            wrapSelectorWheel = false
        }
    }

    private fun getOpenDays(): List<Boolean> {
        val openDays = BooleanArray(7)
        openDays[0] = binding.checkboxSunday.isChecked
        openDays[1] = binding.checkboxMonday.isChecked
        openDays[2] = binding.checkboxTuesday.isChecked
        openDays[3] = binding.checkboxWednesday.isChecked
        openDays[4] = binding.checkboxThursday.isChecked
        openDays[5] = binding.checkboxFriday.isChecked
        openDays[6] = binding.checkboxSaturday.isChecked

        return openDays.toList()
    }

    private fun timePickerDialog(editText: EditText) {
        val dialog = MaterialTimePicker.Builder().setTitleText("Escoge una hora")
            .setTimeFormat(TimeFormat.CLOCK_12H).build()

        dialog.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance().apply {
                set(HOUR_OF_DAY, dialog.hour)
                set(MINUTE, dialog.minute)
            }
            editText.setText(timeFormat.format(calendar.time))
        }

        dialog.show(childFragmentManager, "TIME_PICKER")
    }

    private fun getSettings() {
        db.collection("settings").document("settings").get().addOnSuccessListener {
            setOpenDays(it["open_days"] as List<Boolean>)
            binding.etOpeningTime.setText(it["opening_time"].toString())
            binding.etClosingTime.setText(it["closing_time"].toString())
            binding.npNumWorkers.value = it["max_appointments"].toString().toInt()
        }
    }

    private fun setOpenDays(openDays: List<Boolean>) {
        binding.checkboxSunday.isChecked = openDays[0]
        binding.checkboxMonday.isChecked = openDays[1]
        binding.checkboxTuesday.isChecked = openDays[2]
        binding.checkboxWednesday.isChecked = openDays[3]
        binding.checkboxThursday.isChecked = openDays[4]
        binding.checkboxFriday.isChecked = openDays[5]
        binding.checkboxSaturday.isChecked = openDays[6]
    }
}