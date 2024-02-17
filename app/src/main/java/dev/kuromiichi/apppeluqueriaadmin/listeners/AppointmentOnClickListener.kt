package dev.kuromiichi.apppeluqueriaadmin.listeners

import dev.kuromiichi.apppeluqueriaadmin.models.Appointment

interface AppointmentOnClickListener {
    fun onAppointmentClick(appointment: Appointment)
}