package dev.kuromiichi.apppeluqueriaadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.databinding.ItemAppointmentBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.AppointmentOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Appointment
import dev.kuromiichi.apppeluqueriaadmin.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerAppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onClickListener: AppointmentOnClickListener
) : RecyclerView.Adapter<RecyclerAppointmentAdapter.ViewHolder>() {

    private val db by lazy { Firebase.firestore }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAppointmentBinding.bind(view)

        fun bind(appointment: Appointment) {
            db.collection("users").document(appointment.userUid).get().addOnSuccessListener {
                binding.tvAppointmentUser.text = it.toObject(User::class.java)?.name
            }
            binding.tvAppointmentServices.text = "Servicios:\n"
            appointment.services.forEach {
                binding.tvAppointmentServices.append(it.name + "\n")
            }
            binding.tvAppointmentDateTime.text =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(appointment.date.time + appointment.time.time))
            binding.tvAppointmentDuration.text = appointment.duration.toString() + " min"
        }

        fun setListener(appointment: Appointment) {
            binding.root.setOnClickListener {
                onClickListener.onAppointmentClick(appointment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_appointment,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
        holder.setListener(appointments[position])
    }

    fun setAppointments(appointments: List<Appointment>) {
        this.appointments = appointments
        notifyDataSetChanged()
    }
}