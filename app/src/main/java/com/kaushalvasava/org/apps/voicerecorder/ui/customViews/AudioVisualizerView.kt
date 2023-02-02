package com.kaushalvasava.org.apps.voicerecorder.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.kaushalvasava.org.apps.voicerecorder.databinding.AudioVisualizerViewBinding

class AudioVisualizerView : FrameLayout {

    private lateinit var binding: AudioVisualizerViewBinding

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AudioVisualizerViewBinding.inflate(inflater, this, true)
    }

    fun setColor() {
        binding.visualizer.setColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
    }

    fun setDensityValue(value: Float = 32f) {
        binding.visualizer.setDensity(value)
    }

    fun setPlayerId(id: Int?) {
        if (id != null) {
            binding.visualizer.setPlayer(id)
        }
    }
}