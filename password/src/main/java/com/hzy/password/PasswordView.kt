package com.hzy.password

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View

/**
 * Created by ziye_huang on 2017/9/25.
 */
class PasswordView : View {
    //支持密码位数设置
    var passwordCount = 0
    private var strokeColor = 0
    private var mCirclePaint: Paint? = null
    private var mPaint: Paint? = null
    private var symbolColor = 0
    private var mRadius = 0f
    private var inputBoxStroke = 0f
    private var mText: StringBuffer? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.inputBox)
        //支持某些属性设置,比如密码位数,边框颜色、宽度,"●"的颜色、大小
        passwordCount = ta.getInteger(R.styleable.inputBox_passwordCount, 6)
        strokeColor = ta.getColor(R.styleable.inputBox_stokeColor, Color.GRAY)
        symbolColor = ta.getColor(R.styleable.inputBox_symbolColor, Color.BLACK)
        mRadius = ta.getDimension(R.styleable.inputBox_symbolRadius, 12f)
        inputBoxStroke = ta.getDimension(R.styleable.inputBox_inputBoxStroke, 1f)
        //设置输入框圆角边框
        val gd = GradientDrawable()
        gd.setColor(Color.WHITE)
        gd.setStroke(inputBoxStroke.toInt(), strokeColor)
        gd.cornerRadius = 8f
        setBackgroundDrawable(gd)
        ta.recycle()
        if (mPaint == null) {
            mPaint = Paint()
            mPaint!!.color = strokeColor
            mPaint!!.strokeWidth = inputBoxStroke
        }
        if (mCirclePaint == null) {
            mCirclePaint = Paint()
            mCirclePaint!!.color = symbolColor
            mCirclePaint!!.style = Paint.Style.FILL
            mCirclePaint!!.isAntiAlias = true
        }
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val singleWidth = measuredWidth / passwordCount
        val height = measuredHeight
        //绘制每个"●"之间的分割线
        for (i in 1 until passwordCount) {
            canvas.drawLine(
                (singleWidth * i).toFloat(),
                0f,
                (singleWidth * i).toFloat(),
                height.toFloat(),
                mPaint!!
            )
        }
        if (mText != null) {
            //绘制"●"
            val textSize = if (mText!!.length > passwordCount) passwordCount else mText!!.length
            for (i in 1..textSize) {
                canvas.drawCircle(
                    (singleWidth * i - singleWidth / 2).toFloat(),
                    (height / 2).toFloat(),
                    mRadius,
                    mCirclePaint!!
                )
            }
        }
    }

    //密码改变,重新绘制
    fun setPassword(text: CharSequence) {
        mText = text as StringBuffer
        if (text.length > passwordCount) {
            mText!!.delete(mText!!.length - 1, mText!!.length)
            return
        }
        postInvalidate()
    }

    fun clearPassword() {
        if (mText != null) {
            mText!!.delete(0, mText!!.length)
        }
    }

    val password: CharSequence?
        get() = mText
}