package com.example.homework3doubletapp.screens

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.homework3doubletapp.R
import com.example.homework3doubletapp.model.Habit
import com.example.homework3doubletapp.model.HabitType
import com.example.homework3doubletapp.model.Repository
import kotlinx.android.synthetic.main.activity_details.*


class DetailsActivity : AppCompatActivity(), View.OnClickListener{

    var habit : Habit? = null
    var selectedColor = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        save.setOnClickListener(this)

        addImages()
        setValues()
    }

    private fun addImages(){
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val squareSize = resources.getDimension(R.dimen.square_size).toInt()

        val marginSide = resources.getDimension(R.dimen.margin_side).toInt()
        val marginTop = resources.getDimension(R.dimen.padding_vertical).toInt()

        val spectrumHeight = resources.getDimension(R.dimen.height).toInt()
        val spectrumWidth = (resources.getDimension(R.dimen.square_size).toInt() + 2 * marginSide) * 16

        val bitmap = ResourcesCompat
            .getDrawable(resources, R.drawable.hue2, null)!!
            .toBitmap(width = spectrumWidth, height = spectrumHeight)

        lp.width = squareSize
        lp.height = squareSize
        lp.marginEnd = marginSide
        lp.marginStart = marginSide

        val halfSize = squareSize / 2

        for(i in 0..15){

            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.square)
            imageView.layoutParams = lp
            imageView.setBackgroundResource(R.drawable.border_black)

            linear_layout.addView(imageView)

            val xPosition = (marginSide * 2 + squareSize) * i + marginSide + halfSize
            val yPosition = marginTop + halfSize

            val pixel = bitmap.getPixel(xPosition, yPosition)

            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            val color = Color.rgb(r, g, b)

            imageView.setColorFilter(color)

            imageView.setOnClickListener {
                selected_color.colorFilter = imageView.colorFilter
                selectedColor = color
                val a = FloatArray(3)
                Color.colorToHSV(color, a)
                rgb.text = resources.getString(R.string.rgb_formatted, r, g, b)
                hsv.text = resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
            }
        }
    }
    
    private fun setValues(){
        habit = Repository.getHabit(intent.getStringExtra("habit"))

        if(habit != null){
            name_edit.setText(habit?.name)
            description_edit.setText(habit?.description)
            priority_spinner.setSelection(habit?.priority!! - 1)
            when (habit?.type) {
                HabitType.GOOD -> good_radio.isChecked = true
                HabitType.BAD -> bad_radio.isChecked = true
            }
            quantity_edit.setText(habit?.quantity!!.toString())
            period_edit.setText(habit?.period!!.toString())
            selected_color.setColorFilter(habit?.color!!)
            selectedColor = habit?.color!!
        }
    }

    override fun onClick(v: View?) {
        if(name_edit.text.isEmpty()
            || selectedColor == -1
            || quantity_edit.text.isEmpty()
            || period_edit.text.isEmpty()
            || description_edit.text.isEmpty()
            || (!bad_radio.isChecked && !good_radio.isChecked)){
            Toast
                .makeText(this, "Fill in all the fields!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val type = when {
            bad_radio.isChecked -> HabitType.BAD
            good_radio.isChecked -> HabitType.GOOD
            else -> throw IllegalStateException("No type selected")
        }

        if(habit == null){
            val habit = Habit(
                name_edit.text.toString(),
                description_edit.text.toString(),
                priority_spinner.selectedItem.toString().toInt(),
                type,
                period_edit.text.toString().toInt(),
                quantity_edit.text.toString().toInt(),
                selectedColor
            )

            Repository.addHabit(habit)
        } else {
            habit?.name = name_edit.text.toString()
            habit?.description = description_edit.text.toString()
            habit?.priority = priority_spinner.selectedItem.toString().toInt()
            habit?.type = type
            habit?.period =  period_edit.text.toString().toInt()
            habit?.quantity = quantity_edit.text.toString().toInt()
            habit?.color = selectedColor
        }

        finish()
    }
}