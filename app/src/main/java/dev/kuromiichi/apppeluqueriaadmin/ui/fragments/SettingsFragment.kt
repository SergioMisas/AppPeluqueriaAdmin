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
        setNumberPicker()
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
                    requireContext(),
                    getString(R.string.settings_save_success),
                    Toast.LENGTH_SHORT
                ).show()
                val navController = findNavController()
                navController.popBackStack(R.id.homeFragment, false)
                navController.navigate(R.id.action_settingsFragment_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_save_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setNumberPicker() {
        binding.npNumWorkers.minValue = 1
    }

    private fun getOpenDays(): BooleanArray {
        val openDays = BooleanArray(7)
        openDays[0] = binding.checkboxSunday.isChecked
        openDays[1] = binding.checkboxMonday.isChecked
        openDays[2] = binding.checkboxTuesday.isChecked
        openDays[3] = binding.checkboxWednesday.isChecked
        openDays[4] = binding.checkboxThursday.isChecked
        openDays[5] = binding.checkboxFriday.isChecked
        openDays[6] = binding.checkboxSaturday.isChecked

        return openDays
    }

    private fun timePickerDialog(editText: EditText) {
        val dialog = MaterialTimePicker.Builder()
            .setTitleText("Escoge una hora")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        dialog.addOnPositiveButtonClickListener {
            editText.setText(dialog.hour.toString() + ":" + dialog.minute.toString())
        }
    }
}