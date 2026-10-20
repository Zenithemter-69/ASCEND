package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentStudyBinding
import ascending.com.ascendingg.data.model.Subject
import ascending.com.ascendingg.ui.adapter.SubjectAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyFragment : Fragment(R.layout.fragment_study) {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private lateinit var subjectAdapter: SubjectAdapter

    private val sampleSubjects = listOf(
        Subject(0, "Math", 3, 120, "#4D9FFF"), // 3 chapters, 120 mins today
        Subject(1, "Physics", 2, 90, "#C5F36A"),
        Subject(2, "Chemistry", 4, 60, "#FF6B6B"),
        Subject(3, "Biology", 2, 45, "#9B5DE5"),
        Subject(4, "English", 1, 30, "#F15AEA")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTimerButton()
    }

    private fun setupRecyclerView() {
        subjectAdapter = SubjectAdapter()
        binding.recyclerViewSubjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subjectAdapter
        }
        subjectAdapter.submitList(sampleSubjects)
    }

    private fun setupTimerButton() {
        binding.btnStartFocus.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Starting 50-minute focus session...\nBreak after 10 minutes",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}