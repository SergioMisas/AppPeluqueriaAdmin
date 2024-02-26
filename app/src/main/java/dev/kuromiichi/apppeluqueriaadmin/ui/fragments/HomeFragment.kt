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
import dev.kuromiichi.apppeluqueriaadmin.models.User
import java.text.SimpleDateFormat
import java.util.Calendar.MONDAY
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(), AppointmentOnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    private lateinit var adapter: RecyclerAppointmentAdapter
    private var appointments: List<Appointment> = emptyList()
    private var beginDate: Date? = null
    private var endDate: Date? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
        updateRecycler()
    }

    private fun updateRecycler() {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                appointments = result.toObjects(Appointment::class.java)
                filterAppointments()
                adapter.setAppointments(appointments)
            }
    }

    private fun filterAppointments() {
        appointments = when {
            beginDate == null && endDate == null -> {
                appointments.filter { it.date >= Date(MaterialDatePicker.todayInUtcMilliseconds()) }
            }
            beginDate != null && endDate == null -> {
                appointments.filter { it.date >= beginDate }
            }
            beginDate == null && endDate != null -> {
                appointments.filter { it.date <= endDate }
            }
            else -> appointments.filter { it.date >= beginDate && it.date <= endDate }
        }
    }

    override fun onAppointmentClick(appointment: Appointment) {
        val navController = findNavController()
        var user: User?
        db.collection("users").whereEqualTo("uid", appointment.userUid).get()
            .addOnSuccessListener {
                user = it.toObjects(User::class.java).first()
                val action = HomeFragmentDirections.actionHomeFragmentToUsersFragment(user!!)
                navController.navigate(action)
            }
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
                editText.setText(dateFormat.format(dialog.selection))
                updateDates()
            }
        }
    }

    private fun updateDates() {
        if (binding.etDateFrom.text.toString().isNotEmpty()) beginDate =
            dateFormat.parse(binding.etDateFrom.text.toString())
        if (binding.etDateTo.text.toString().isNotEmpty()) endDate =
            dateFormat.parse(binding.etDateTo.text.toString())

        if (beginDate != null && endDate != null && beginDate!! > endDate!!) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_date),
                Toast.LENGTH_SHORT
            ).show()
            resetFilters()
            return
        }

        updateRecycler()
    }

    private fun resetFilters() {
        beginDate = null
        endDate = null
        binding.etDateFrom.setText("")
        binding.etDateTo.setText("")

        updateRecycler()
    }
}