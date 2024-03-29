package net.iessochoa.ignaciodiazmirete.practica5.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import net.iessochoa.ignaciodiazmirete.practica5.R
import net.iessochoa.ignaciodiazmirete.practica5.databinding.FragmentListaBinding
import net.iessochoa.ignaciodiazmirete.practica5.viewmodel.AppViewModel

@Suppress("DEPRECATION")
class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val viewModel: AppViewModel by activityViewModels()
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/

        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
            actualizaLista(lista)
        })
        binding.fabNuevo.setOnClickListener {
            //creamos acción enviamos argumento nulo porque queremos crear NuevaTarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)

        }
        //para prueba, editamos una tarea aleatoria
        binding.btPruebaEdicion.setOnClickListener{
            //cogemos la lista actual de Tareas que tenemos en el ViewModel. No es lo más correcto
            val lista= viewModel.tareasLiveData.value
            //buscamos una tarea aleatoriamente
            val tarea=lista?.get((0..lista.lastIndex).random())
            //se la enviamos a TareaFragment para su edición
            val action=ListaFragmentDirections.actionEditar(tarea)
            findNavController().navigate(action)
        }
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
        iniciaFiltros()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun iniciaFiltros() {
        binding.swSinPagar.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSoloSinPagar(isChecked)
        }

        binding.rgEstados.setOnCheckedChangeListener { _, checkedId ->
            // Traducir el ID del RadioButton a un valor de estado (0, 1, 2, 3)
            val estadoFiltrado = when (checkedId) {
                R.id.rbAbierta -> 0
                R.id.rbEnCurso -> 1
                R.id.rbCerrada -> 2
                R.id.rbTodas -> 3
                else -> -1 // Valor por defecto o manejo de error si es necesario
            }

            // Llamar a setEstado en el ViewModel para aplicar el filtro
            if (estadoFiltrado != -1) {
                viewModel.setEstado(estadoFiltrado)
            }
        }
        // Inicializar los filtros según el estado inicial del Fragmento.
        val idRadioButtonSeleccionado = binding.rgEstados.checkedRadioButtonId
        val estadoFiltradoInicial = when (idRadioButtonSeleccionado) {
            R.id.rbAbierta -> 0
            R.id.rbEnCurso -> 1
            R.id.rbCerrada -> 2
            R.id.rbTodas -> 3
            else -> -1 // Valor por defecto o manejo de error si es necesario
        }

        // Llamar a setEstado en el ViewModel para aplicar el filtro inicial
        if (estadoFiltradoInicial != -1) {
            viewModel.setEstado(estadoFiltradoInicial)
        }
    }

    private fun actualizaLista(lista: List<Tarea>?) {
        //creamos un string modificable
        val listaString = buildString {
            lista?.forEach() {
                //añadimos al final del string
                append(
                    "${it.id}-${it.tecnico}-${
                        //mostramos un trozo de la descripción
                        if (it.descripcion.length < 21) it.descripcion
                        else
                            it.descripcion.subSequence(0, 20)
                    }-${
                        if (it.pagado) "SI-PAGADO" else
                            "NO-PAGADO"
                    }-" + when (it.estado) {
                        0 -> "ABIERTA"
                        1 -> "EN_CURSO"
                        2 -> "CERRADA"
                        else -> "TODAS"
                    } + "\n"
                )
            }
        }
        binding.tvLista.setText(listaString)
    }
}