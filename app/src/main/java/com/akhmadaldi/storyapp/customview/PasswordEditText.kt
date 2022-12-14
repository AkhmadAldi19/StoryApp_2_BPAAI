package com.akhmadaldi.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText: TextInputEditText {
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
            if (p0.toString().isNotEmpty()) {
                if (p0.toString().length < 6) {
                    setError("min. 6 char", null)
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }
}