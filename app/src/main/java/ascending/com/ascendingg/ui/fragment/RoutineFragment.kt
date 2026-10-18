package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentRoutineBinding
import ascending.com.ascendingg.ui.adapter.RoutineAdapter
import ascending.com.ascendingg.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineFragment : Fragment(R.layout.fragment_routine) {
    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRoutineBinding.bind(view)

        // Setup routine list (Mewing, Skincare, Chin Tucks)
        binding.routineRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // ... adapter implementation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
