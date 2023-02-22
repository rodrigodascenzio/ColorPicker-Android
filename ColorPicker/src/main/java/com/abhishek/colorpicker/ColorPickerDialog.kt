package com.abhishek.colorpicker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.abhishek.colorpicker.databinding.FragmentColorPickerDialogBinding

class ColorPickerDialog : DialogFragment() {
    private lateinit var binding: FragmentColorPickerDialogBinding
    private var currentColor = MutableLiveData(Color.argb(100, 255, 0, 0))
    private var okCancelListener: OkCancelFun? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPickerDialogBinding.inflate(layoutInflater)
        initialize()
        return binding.root
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "ColorPickerDialog")
    }

    fun getColor() = currentColor.value!!
    fun setColor(color: Int) {
        binding.apply {
            //update hue
            val hsv = color.toHSV()
            hueSlider.setHue(hsv[0].toInt())
            //update alpha
            alphaSlider.setAlpha(Color.alpha(color))
            //update color compose
            colorComposer.setColor(color)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        requireDialog().window?.setLayout((6 * width) / 7, height * 80 / 100)
    }
    private fun initialize(){
        binding.apply {
            setColor(currentColor.value!!)
            alphaSlider.setOnChangeListener {
                val newColor=Utils.colorWithAlpha(alphaSlider.getAlphaValue(),currentColor.value!!)
                currentColor.value=newColor
            }
            hueSlider.setOnChangeListener {
                colorComposer.setComposeColor(hueSlider.getColor())
            }
            colorComposer.setColorChangeListener {
                val alpha = Color.alpha(currentColor.value!!)
                val newColor = Utils.colorWithAlpha(alpha, colorComposer.getColor())
                currentColor.value = newColor
                alphaSlider.setColor(newColor)
            }
            currentColor.observe(viewLifecycleOwner) { color ->
                color?.let {
                    colorView.setColor(it)
                }
            }
            //button
            buttonCancel.setOnClickListener {
                onClickButton(false)
            }
            buttonOk.setOnClickListener {
                onClickButton(true)
            }
        }
    }

    private fun onClickButton(isOk: Boolean) {
        okCancelListener?.invoke(isOk, currentColor.value!!)
        dismiss()
    }

    fun setOnOkCancelListener(callback: OkCancelFun) {
        okCancelListener = callback
    }
}

typealias OkCancelFun = (isOk: Boolean, color: Int) -> Unit