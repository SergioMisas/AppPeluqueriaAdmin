package dev.kuromiichi.apppeluqueriaadmin.listeners

import dev.kuromiichi.apppeluqueriaadmin.models.Service

interface ServiceOnClickListener {
    fun onServiceClick(service: Service)
}