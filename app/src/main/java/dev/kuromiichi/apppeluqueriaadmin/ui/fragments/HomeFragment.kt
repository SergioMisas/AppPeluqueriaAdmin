package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.adapters.RecyclerAppointmentAdapter
import dev.kuromiichi.apppeluqueriaadmin.databinding.FragmentHomeBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Appointment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.MONDAY
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), AppointmentOnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    private lateinit var appointments: List<Appointment>
    private lateinit var adapter: RecyclerAppointmentAdapter
    private var beginDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        setButtons()
    }

    private fun setRecycler() {
        adapter = RecyclerAppointmentAdapter(appointments, this)
        binding.rvAppointments.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                appointments = result.toObjects(Appointment::class.java)
                updateRecycler()
            }
    }

    private fun updateRecycler() {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                appointments = result.toObjects(Appointment::class.java)
            }
        filterAppointments()
    }

    private fun filterAppointments() {
        when {
            beginDate == null && endDate == null -> {
                appointments = appointments.filter {
                    val today = Calendar.getInstance().apply {
                        timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
                    }
                    val tomorrow = today.clone() as Calendar
                    tomorrow.add(Calendar.DAY_OF_MONTH, 1)

                    it.date.after(today.time) && it.date.before(tomorrow.time)
                }
            }

            beginDate == null && endDate != null -> {
                appointments = appointments.filter {
                    it.date.before(endDate)
                }
            }

            beginDate != null && endDate == null -> {
                appointments = appointments.filter {
                    it.date.after(beginDate)
                }
            }

            else -> {
                appointments = appointments.filter {
                    it.date.after(beginDate) && it.date.before(endDate)
                }
            }
        }
    }

    override fun onAppointmentClick(appointment: Appointment) {
        TODO("Not yet implemented")
    }

    private fun setButtons() {
        binding.fabSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.btnDateFrom.setOnClickListener {
            datePickerDialog(binding.etDateFrom)
        }

        binding.btnDateTo.setOnClickListener {
            datePickerDialog(binding.etDateTo)
        }

        binding.btnFilterOff.setOnClickListener {
            resetFilters()
        }
    }

    private fun datePickerDialog(editText: EditText) {
        val dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.choose_date))
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setFirstDayOfWeek(MONDAY)
                    .build()
            ).build()
        dialog.show(childFragmentManager, "DATE_PICKER")

        dialog.addOnPositiveButtonClickListener {
            if (dialog.selection != null) {
                editText.setText(
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(dialog.selection)
                )
                updateDates()
            }
        }
    }

    private fun updateDates() {
        if (binding.etDateFrom.text.toString().isNotEmpty()) beginDate = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).parse(binding.etDateFrom.text.toString())
        if (binding.etDateTo.text.toString().isNotEmpty()) endDate = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).parse(binding.etDateTo.text.toString())

        if (beginDate != null && endDate != null && beginDate!! > endDate!!) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_date),
                Toast.LENGTH_SHORT
            ).show()
            resetFilters()
        }
    }

    private fun resetFilters() {
        beginDate = null
        endDate = null
        binding.etDateFrom.setText("")
        binding.etDateTo.setText("")
    }
}