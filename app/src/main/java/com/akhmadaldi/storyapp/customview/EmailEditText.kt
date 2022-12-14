package com.akhmadaldi.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.akhmadaldi.storyapp.utils.EmailValidation.isValidEmail
import com.google.android.material.textfield.TextInputEditText

class EmailEditText: TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(onTextChanged = {p0, _, _, _ ->
            if (!isValidEmail(p0.toString())) {
                error = "Wrong Format"
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}