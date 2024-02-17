package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.navigation.fragment.findNavController
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.adapters.RecyclerAppointmentAdapter
import dev.kuromiichi.apppeluqueriaadmin.databinding.FragmentHomeBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Appointment
import java.util.Calendar
import java.util.Date

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
    }
}