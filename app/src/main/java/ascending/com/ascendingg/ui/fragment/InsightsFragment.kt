package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.FragmentInsightsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InsightsFragment : Fragment(R.layout.fragment_insights) {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setDateRange()
    }

    private fun setupCharts() {
        setupHabitCompletionChart()
        setupStudyHoursChart()
        setupProgressPhotos()
    }

    private fun setupHabitCompletionChart() {
        val entries = listOf(
            BarEntry(0f, 80f), BarEntry(1f, 90f), BarEntry(2f, 75f),
            BarEntry(3f, 85f), BarEntry(4f, 95f), BarEntry(5f, 70f), BarEntry(6f, 88f)
        )

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
            animateY(700, Easing.EaseInOutQuad)
            data = BarData(dataSet)
            invalidate()
        }
    }

    private fun setupStudyHoursChart() {
        val entries = listOf(
            Entry(0f, 2f), Entry(1f, 3f), Entry(2f, 2.5f),
            Entry(3f, 4f), Entry(4f, 3.5f), Entry(5f, 2f), Entry(6f, 3f)
        )

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