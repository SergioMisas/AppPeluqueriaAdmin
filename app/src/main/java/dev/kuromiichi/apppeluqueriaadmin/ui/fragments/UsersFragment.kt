package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueriaadmin.adapters.RecyclerAppointmentAdapter
import dev.kuromiichi.apppeluqueriaadmin.databinding.FragmentUsersBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Appointment
import kotlinx.coroutines.runBlocking

class UsersFragment : Fragment(), AppointmentOnClickListener {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerAppointmentAdapter
    private val args by navArgs<UsersFragmentArgs>()
    private val db by lazy { Firebase.firestore }
    private var appointments: List<Appointment> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
    }

    private fun setRecycler() {
        adapter = RecyclerAppointmentAdapter(appointments, this)
        binding.rvUserAppointments.apply {
            adapter = this@UsersFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        runBlocking {
            db.collection("appointments").whereEqualTo("userUid", args.user.uid).get()
                .addOnSuccessListener { result ->
                    appointments = result.toObjects(Appointment::class.java)
                    appointments = appointments.sortedBy { it.date }
                    adapter.setAppointments(appointments)
                }
        }
    }

    override fun onAppointmentClick(appointment: Appointment) {
        // Does nothing
    }


}