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
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentInsightsBinding
import ascending.com.ascendingg.ui.viewmodel.InsightsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InsightsFragment : Fragment(R.layout.fragment_insights) {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InsightsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setDateRange()
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitCompletionData.collect { habitData ->
                    setupHabitCompletionChart(habitData)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.studyHoursData.collect { studyData ->
                    setupStudyHoursChart(studyData)
                }
            }
        }
    }

    private fun setupHabitCompletionChart(habitData: List<InsightsViewModel.BarEntryData>) {
        val entries = habitData.map { data ->
            BarEntry(data.x, data.y)
        }

        val dataSet = BarDataSet(entries, "Habit Completion (%)").apply {
            color = android.graphics.Color.parseColor("#C5F36A")
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.WHITE
        }

        binding.barChartHabits.apply {
            description.isEnabled = false
            xAxis.granularity = 1f
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisRight.isEnabled = false
            // Set x-axis labels
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val entry = habitData.firstOrNull { it.x == value }
                    return entry?.label ?: ""
                }
            }
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            animateY(700, Easing.EaseInOutQuad)
            data = BarData(dataSet)
            invalidate()
        }
    }

    private fun setupStudyHoursChart(studyData: List<InsightsViewModel.LineEntryData>) {
        val entries = studyData.map { data ->
            Entry(data.x, data.y)
        }

        val dataSet = LineDataSet(entries, "Study Hours").apply {
            color = android.graphics.Color.parseColor("#4D9FFF")
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.WHITE
            setDrawCircles(true)
            circleRadius = 4f
            lineWidth = 2f
        }

        binding.lineChartStudy.apply {
            description.isEnabled = false
            xAxis.granularity = 1f
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            // Set x-axis labels
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val entry = studyData.firstOrNull { it.x == value }
                    return entry?.label ?: ""
                }
            }
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            animateXY(700, 700, Easing.EaseInOutQuad)
            data = LineData(dataSet)
            invalidate()
        }
    }

    private fun setupProgressPhotos() {
        binding.tvPhotoCount.text = "5 photos saved"
    }

    private fun setDateRange() {
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        val start = sdf.format(Date(System.currentTimeMillis() - 7*24*60*60*1000L))
        val end = sdf.format(Date())
        binding.tvDateRange.text = "$start - $end"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}