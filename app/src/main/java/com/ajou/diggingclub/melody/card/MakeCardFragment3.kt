package com.ajou.diggingclub.melody.card

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import codes.side.andcolorpicker.converter.setFromColorInt
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentMakeCard3Binding
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlin.math.abs
import kotlin.math.roundToInt

class MakeCardFragment3 : Fragment() {
    private var _binding: FragmentMakeCard3Binding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakeCard3Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var cardColor : String = resources.getColor(R.color.primaryColor).toString()

        val multiOptions = MultiTransformation(
            CenterCrop(),
            RoundedCorners(10)
        ) // glide 옵션
        val args : MakeCardFragment3Args by navArgs()

        if(args.uri.isNotEmpty()){
            val parsedUri = Uri.parse(args.uri)
            Glide.with(requireActivity())
                .load(parsedUri)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(multiOptions))
                .into(binding.background)
        }

        binding.artist.text = args.music.artist
        binding.title.text = args.music.title

        binding.done.setOnSingleClickListener {
            val action =
                MakeCardFragment3Directions.actionMakeCardFragment3ToSearchLocationFragment(
                    args.uri,
                    args.music,
                    cardColor
                )
            findNavController().navigate(action)
        }

        binding.hueSeekBar.progress = 50

        binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
            it.setFromColorInt(resources.getColor(R.color.primaryColor))
        }

        binding.color1.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#FF0000"))
            }
        }
        binding.color2.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#FF5C00"))
            }
        }
        binding.color3.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#FFD600"))
            }
        }
        binding.color4.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#A9F900"))
            }
        }
        binding.color5.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#00D923"))
            }
        }
        binding.color6.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#00EAB2"))
            }
        }
        binding.color7.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#00C2FF"))
            }
        }
        binding.color8.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#1400FF"))
            }
        }
        binding.color9.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#9E00FF"))
            }
        }
        binding.color10.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#FF006B"))
            }
        }
        binding.color11.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#FBC4E4"))
            }
        }
        binding.color12.setOnSingleClickListener {
            binding.hueSeekBar.pickedColor = IntegerHSLColor().also {
                it.setFromColorInt(android.graphics.Color.parseColor("#000000"))
            }
        }

        val layerDrawable = ContextCompat.getDrawable(mContext!!,R.drawable.playingicon)?.mutate() as LayerDrawable

        binding.hueSeekBar.addListener(object : OnSeekBarChangeListener, ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                val hslColor = hslToHex(color.floatH, color.floatS, color.floatL)
                layerDrawable.getDrawable(0).setColorFilter(android.graphics.Color.parseColor(hslColor),PorterDuff.Mode.SRC_IN)
                binding.playIcon.setImageDrawable(layerDrawable)
                binding.background.setBackgroundColor(android.graphics.Color.parseColor(hslColor))
                cardColor = hslColor
            }

            override fun onColorPicked(picker: ColorSeekBar<IntegerHSLColor>, color: IntegerHSLColor, value: Int, fromUser: Boolean) {
            }
            override fun onColorPicking(picker: ColorSeekBar<IntegerHSLColor>, color: IntegerHSLColor, value: Int, fromUser: Boolean) {
            }
        })
   }
    fun hslToHex(h: Float, s: Float, l: Float): String {
        val c = (1 - abs(2 * l - 1)) * s
        val x = c * (1 - abs((h / 60) % 2 - 1))
        val m = l - c / 2

        var r = 0f
        var g = 0f
        var b = 0f

        when (h) {
            in 0f..60f -> {
                r = c
                g = x
            }
            in 60f..120f -> {
                r = x
                g = c
            }
            in 120f..180f -> {
                g = c
                b = x
            }
            in 180f..240f -> {
                g = x
                b = c
            }
            in 240f..300f -> {
                r = x
                b = c
            }
            in 300f..360f -> {
                r = c
                b = x
            }
        }

        val red = ((r + m) * 255).roundToInt()
        val green = ((g + m) * 255).roundToInt()
        val blue = ((b + m) * 255).roundToInt()

        return String.format("#%02X%02X%02X", red, green, blue)
    }
}