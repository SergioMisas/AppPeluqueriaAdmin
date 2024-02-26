package dev.kuromiichi.apppeluqueriaadmin.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.kuromiichi.apppeluqueriaadmin.R
import dev.kuromiichi.apppeluqueriaadmin.adapters.RecyclerServicesAdapter
import dev.kuromiichi.apppeluqueriaadmin.databinding.DialogCreateServiceBinding
import dev.kuromiichi.apppeluqueriaadmin.databinding.FragmentServicesBinding
import dev.kuromiichi.apppeluqueriaadmin.listeners.ServiceOnClickListener
import dev.kuromiichi.apppeluqueriaadmin.models.Service

class ServicesFragment : Fragment(), ServiceOnClickListener {
    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    private lateinit var adapter: RecyclerServicesAdapter
    private var services = emptyList<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
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
        adapter = RecyclerServicesAdapter(services, this)
        binding.rvServices.apply {
            adapter = this@ServicesFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
        updateRecycler()
    }

    private fun updateRecycler() {
        db.collection("services").get().addOnSuccessListener { result ->
            services = result.toObjects(Service::class.java)
            adapter.setServices(services)
        }
    }

    private fun setButtons() {
        binding.fabAddService.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                val binding = DialogCreateServiceBinding.inflate(layoutInflater)
                binding.npNumMinutes.apply {
                    minValue = 1
                    maxValue = 20
                    wrapSelectorWheel = false
                    displayedValues = (30..600 step 30).map { it.toString() }.toTypedArray()
                }
                setView(binding.root)
                setTitle("Crear Servicio")
                setPositiveButton("Crear") { _, _ ->
                    if (binding.TilName.editText?.text.toString().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_missing_name),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }
                    db.collection("services").add(
                        Service(
                            name = binding.TilName.editText?.text.toString(),
                            duration = binding.npNumMinutes.value * 30
                        )
                    ).addOnSuccessListener { result ->
                            db.collection("services").document(result.id).update("id", result.id)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.toast_confirm_service_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            updateRecycler()
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_confirm_service_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }.show()
            }
        }
    }

    override fun onServiceClick(service: Service) {
        AlertDialog.Builder(requireContext()).setTitle("Confirmar borrado")
            .setMessage("¿Seguro que quieres borrar este servicio?")
            .setPositiveButton("Borrar") { _, _ ->
                db.collection("services").document(service.id).delete()
                updateRecycler()
            }.setNeutralButton("Cancelar", null).show()
    }
}