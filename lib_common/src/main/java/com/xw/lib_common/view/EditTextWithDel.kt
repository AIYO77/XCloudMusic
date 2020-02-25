package com.xw.lib_common.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.xw.lib_common.R
import com.xw.lib_common.ext.*
import kotlinx.android.synthetic.main.custom_edittext_withdel.view.*
import kotlinx.android.synthetic.main.notification.view.*


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class EditTextWithDel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_edittext_withdel, this)

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.EditTextWithDel, defStyleAttr, 0)

        try {
            etWithDel?.apply {
                setText(typedArray.getString(R.styleable.EditTextWithDel_android_text))
                setTextColor(
                    typedArray.getColor(
                        R.styleable.EditTextWithDel_android_textColor,
                        getColor(R.color.color_343434)
                    )
                )
                hint = typedArray.getString(R.styleable.EditTextWithDel_android_hint)
                highlightColor = typedArray.getColor(
                    R.styleable.EditTextWithDel_android_textColorHint,
                    getColor(R.color.color_cbcbcb)
                )
                textSize =
                    typedArray.getDimension(R.styleable.EditTextWithDel_android_textSize, 16f)
                inputType = typedArray.getInt(
                    R.styleable.EditTextWithDel_android_inputType,
                    EditorInfo.TYPE_NULL
                )
                val maxlength = typedArray.getInt(R.styleable.EditTextWithDel_android_maxLength, 1)
                filters = if (maxlength >= 0) {
                    arrayOf<InputFilter>(LengthFilter(maxlength))
                } else {
                    arrayOfNulls<InputFilter>(0)
                }
            }
        } finally {
            typedArray.recycle()
        }
        initEvent()
    }

    private fun initEvent() {
        etDel.setOnClickListener {
            etWithDel.text = null
        }
        etWithDel.apply {
            onTextChanged {
                it.isNotEmpty().yes {
                    etDel.show()
                }.no {
                    etDel.invisible()
                }
            }
        }

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (this.text.length() > 0) {
                    etDel.show()
                }
            } else {
                etDel.invisible()
            }
        }
    }

    /**
     * 返回EditText中的文本数据
     */
    fun getText(): Editable? {
        return etWithDel.text
    }

    /**
     * 设置文本到EditText中
     */
    fun setText(charSequence: CharSequence?) {
        etWithDel.setText(charSequence)
    }

}