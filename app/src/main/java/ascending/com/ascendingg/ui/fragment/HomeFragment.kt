package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import ascending.com.ascendingg.R
import ascending.com.ascendingg.data.model.Habit
import ascending.com.ascendingg.data.tracking.LocationTracker
import ascending.com.ascendingg.databinding.FragmentHomeBinding
import ascending.com.ascendingg.ui.adapter.HabitAdapter
import ascending.com.ascendingg.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var habitAdapter: HabitAdapter
    private val locationTracker: LocationTracker by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        bindViewModel()
        setGreeting()
        startStepTracking()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter { habitId ->
            viewModel.incrementStreak(habitId, System.currentTimeMillis())
        }
        binding.habitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todaysHabits.collect { habits ->
                    habitAdapter.submitList(habits)
                    updateProgress(habits)
                }
            }
        }
    }

    private fun startStepTracking() {
        // Start location tracking for step counting
        lifecycleScope.launch {
            locationTracker.startTracking().collect()
        }

        // Update step count display periodically
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationTracker.currentLocation.collect { location ->
                    updateStepDisplay()
                }
            }
        }
    }

    private fun updateStepDisplay() {
        val steps = locationTracker.stepCount
        val distanceKm = locationTracker.totalDistanceMeters / 1000f
        binding.tvStepCount.text = "$steps steps"
        binding.tvDistance.text = String.format("%.2f km", distanceKm)
    }

    private fun updateProgress(habits: List<Habit>) {
        val completed = habits.count { it.lastCompleted > 0 }
        val percentage = if (habits.isNotEmpty()) {
            (completed * 100) / habits.size
        } else 0

        binding.progressRing.progress = percentage.toFloat()
        binding.tvProgressText.text = "$percentage%"
        binding.tvProgressLabel.text = "Habit Completion"
    }

    private fun setGreeting() {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())
        binding.tvName.text = "Alex" // Replace with actual username
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}