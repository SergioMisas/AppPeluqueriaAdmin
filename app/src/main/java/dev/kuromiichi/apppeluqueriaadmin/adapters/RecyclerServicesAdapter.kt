package dev.kuromiichi.apppeluqueriaadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.databinding.ItemServiceBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.ServiceOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Service

class RecyclerServicesAdapter(
    private var services: List<Service>,
    private val onClickListener: ServiceOnClickListener
) : RecyclerView.Adapter<RecyclerServicesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ItemServiceBinding.bind(view)

        fun bind(service: Service) {
            binding.tvServiceName.text = service.name
            binding.tvServiceTime.text = "${service.duration} min"
        }

        fun setListener(service: Service) {
            binding.root.setOnClickListener {
                onClickListener.onServiceClick(service)
            }
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerServicesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_service,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerServicesAdapter.ViewHolder, position: Int) {
        holder.bind(services[position])
        holder.setListener(services[position])
    }

    override fun getItemCount(): Int {
        return services.size
    }

    fun setServices(services: List<Service>) {
        this.services = services
        notifyDataSetChanged()
    }
}