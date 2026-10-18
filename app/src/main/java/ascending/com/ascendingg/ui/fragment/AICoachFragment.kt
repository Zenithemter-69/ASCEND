package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentAicoachBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AICoachFragment : Fragment(R.layout.fragment_aicoach) {
    private var _binding: FragmentAicoachBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAicoachBinding.bind(view)

        binding.btnAnalyze.setOnClickListener {
            // AI Analysis trigger
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
