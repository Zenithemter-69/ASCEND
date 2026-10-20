package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentRoutineBinding
import ascending.com.ascendingg.data.model.RoutineItem
import ascending.com.ascendingg.ui.adapter.RoutineAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RoutineFragment : Fragment(R.layout.fragment_routine) {

    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!
    private lateinit var routineAdapter: RoutineAdapter

    private val sampleRoutineItems = listOf(
        RoutineItem(0, "Morning Routine", "7:00 AM - 8:00 AM", true),
        RoutineItem(1, "Study Session #1", "9:00 AM - 10:30 AM", true),
        RoutineItem(2, "Workout", "11:00 AM - 12:00 PM", false),
        RoutineItem(3, "Lunch Break", "12:30 PM - 1:00 PM", true),
        RoutineItem(4, "Evening Routine", "6:00 PM - 8:00 PM", false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setDateHeader()
    }

    private fun setupRecyclerView() {
        routineAdapter = RoutineAdapter { routineId ->
            // TODO: ViewModel call to persist routine state
        }
        binding.recyclerViewRoutine.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = routineAdapter
        }
        routineAdapter.submitList(sampleRoutineItems)
    }

    private fun setDateHeader() {
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}