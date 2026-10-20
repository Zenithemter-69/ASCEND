package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentFitnessBinding
import ascending.com.ascendingg.data.model.Exercise
import ascending.com.ascendingg.ui.adapter.ExerciseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FitnessFragment : Fragment(R.layout.fragment_fitness) {

    private var _binding: FragmentFitnessBinding? = null
    val binding get() = _binding!!
    private lateinit var exerciseAdapter: ExerciseAdapter

    private val sampleExercises = listOf(
        Exercise(
            id = 1,
            name = "Wall Chin Tucks",
            description = "5 sets × 10 reps | 3x/day",
            instructions = "Stand with back against wall. Pull chin straight back horizontally. Feel tension in back of neck.",
            timeOfDay = "Morning",
            category = "Neck & Posture",
            completed = false
        ),
        Exercise(
            id = 2,
            name = "Chewing Protocol",
            description = "30-45 mins | Use Falim gum or raw carrots",
            instructions = "Chew on molars ONLY. Keep teeth slightly apart. Alternate sides every 15 minutes.",
            timeOfDay = "Daytime",
            category = "Jaw Development",
            completed = true
        ),
        Exercise(
            id = 3,
            name = "Buteyko Breathing",
            description = "5 min × 2x/day",
            instructions = "Close mouth. Breathe normally through nose for 5 min. Hold after exhale until slight air hunger.",
            timeOfDay = "Morning & Evening",
            category = "Breathing",
            completed = false
        ),
        Exercise(
            id = 4,
            name = "Sunlight Exposure",
            description = "15-20 mins | Face exposed directly",
            instructions = "Go outside before 10am or after 4pm. No sunscreen on face. No sunglasses.",
            timeOfDay = "Morning",
            category = "Recovery",
            completed = false
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFitnessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter { exerciseId ->
            // TODO: ViewModel call to persist workout state
        }
        binding.recyclerViewExercises.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }
        exerciseAdapter.submitList(sampleExercises)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}