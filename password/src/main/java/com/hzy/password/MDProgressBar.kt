package com.hzy.password

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.hzy.password.MDProgressBar

/**
 * Created by ziye_huang on 2017/9/25.
 */
class MDProgressBar : View {
    //圆弧颜色
    private var arcColor = DEFAULT_ARC_COLOR
    private var animatorSet: AnimatorSet? = null
    private var mBorderWidth = 0f
    private var mPaint: Paint? = null
    private var arcRectF: RectF? = null
    private var startAngle = -45f
    private var sweepAngle = -19f
    private var incrementAngele = 0f

    //是否需要开始绘制对勾
    private var isNeedTick = false
    private var mResize = 0
    private var mTickAnimation: TickAnimation? = null

    //判断"对勾"动画是否过半,"对勾"由两条线绘制而成。
    private var isAnimationOverHalf = false

    //圆形进度条的半径
    private var mRadius = 0f
    private var startY1 = 0f
    private var startX1 = 0f
    private var stopX1 = 0f
    private var stopY1 = 0f
    private var stopX2 = 0f
    private var stopY2 = 0f
    private var mListener: OnPasswordCorrectlyListener? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.materialStatusProgressAttr)
        arcColor = typedArray.getColor(
            R.styleable.materialStatusProgressAttr_arcColor,
            Color.parseColor("#4a90e2")
        )
        mBorderWidth = typedArray.getDimension(
            R.styleable.materialStatusProgressAttr_progressBarBorderWidth,
            resources.getDimension(R.dimen.material_status_progress_border)
        )
        typedArray.recycle()
        mPaint = Paint()
        mPaint!!.color = arcColor
        mPaint!!.strokeWidth = mBorderWidth
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
        arcRectF = RectF()
        mTickAnimation = TickAnimation()
        mTickAnimation!!.duration = 800
        //对勾动画监听
        mTickAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                //当对勾动画完成后,延迟一秒回掉,不然动画效果不明显
                if (mListener != null) {
                    postDelayed({ mListener!!.onPasswordCorrectly() }, 1000)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun arcPaint() {
        mPaint!!.reset()
        mPaint!!.color = arcColor
        mPaint!!.strokeWidth = mBorderWidth
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
    }

    private fun linePaint() {
        mPaint!!.reset()
        mPaint!!.color = arcColor
        mPaint!!.strokeWidth = mBorderWidth
        mPaint!!.isAntiAlias = true
    }

    //对勾动画完成回调
    fun setOnPasswordCorrectlyListener(listener: OnPasswordCorrectlyListener?) {
        mListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        startY1 = (measuredHeight / 2).toFloat()
        mRadius = measuredHeight / 2 - 2 * mBorderWidth
        startX1 = startY1 - measuredHeight / 5
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        arcPaint()
        canvas.drawArc(arcRectF!!, startAngle + incrementAngele, sweepAngle, false, mPaint!!)
        if (animatorSet == null || !animatorSet!!.isRunning && !isNeedTick) {
            startAnimation()
        }
        if (isNeedTick) {
            //补全圆
            arcPaint()
            canvas.drawArc(
                arcRectF!!,
                startAngle + incrementAngele + sweepAngle,
                360 - sweepAngle,
                false,
                mPaint!!
            )
            linePaint()
            //画第一根线
            canvas.drawLine(startX1, startY1, stopX1, stopY1, mPaint!!)
            if (isAnimationOverHalf) {
                //-2 +2 是为了两根线尽可能靠拢
                canvas.drawLine(stopX1 - 2, stopY1 + 2, stopX2, stopY2, mPaint!!)
            }
        }
    }

    //对勾动画
    private inner class TickAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            if (interpolatedTime <= 0.5f) {
                stopX1 = startX1 + mRadius / 3 * interpolatedTime * 2
                stopY1 = startY1 + mRadius / 3 * interpolatedTime * 2
                isAnimationOverHalf = false
            } else {
                stopX2 = stopX1 + (mRadius - 20) * (interpolatedTime - 0.5f) * 2
                stopY2 = stopY1 - (mRadius - 20) * (interpolatedTime - 0.5f) * 2
                isAnimationOverHalf = true
            }
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mResize = if (w < h) w else h
        setBound()
    }

    private fun setBound() {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        arcRectF!![paddingLeft + mBorderWidth, paddingTop + mBorderWidth, mResize - paddingLeft - mBorderWidth] =
            mResize - paddingTop - mBorderWidth
    }

    fun startAnimation() {
        isNeedTick = false
        if (animatorSet != null && animatorSet!!.isRunning) {
            animatorSet!!.cancel()
        }
        if (animatorSet == null) {
            animatorSet = AnimatorSet()
        }
        val set = loopAnimator()
        animatorSet!!.play(set)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            private var isCancel = false
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (!isCancel) {
                    startAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                isCancel = true
            }
        })
        animatorSet!!.start()
    }

    /**
     * 进度条旋转的动画
     */
    private fun loopAnimator(): AnimatorSet {
        //从小圈到大圈
        val holdAnimator1 =
            ValueAnimator.ofFloat(incrementAngele + DEFAULT_MIN_ANGLE, incrementAngele + 115f)
        holdAnimator1.addUpdateListener { animation ->
            incrementAngele = animation.animatedValue as Float
        }
        holdAnimator1.duration = DEFAULT_DURATION.toLong()
        holdAnimator1.interpolator = LinearInterpolator()
        val expandAnimator = ValueAnimator.ofFloat(DEFAULT_MIN_ANGLE, DEFAULT_MAX_ANGLE)
        expandAnimator.addUpdateListener { animation ->
            sweepAngle = animation.animatedValue as Float
            incrementAngele -= sweepAngle
            invalidate()
        }
        expandAnimator.duration = DEFAULT_DURATION.toLong()
        expandAnimator.interpolator = DecelerateInterpolator(2f)
        //从大圈到小圈
        val holdAnimator = ValueAnimator.ofFloat(startAngle, startAngle + 115f)
        holdAnimator.addUpdateListener { animation ->
            startAngle = animation.animatedValue as Float
        }
        holdAnimator.duration = DEFAULT_DURATION.toLong()
        holdAnimator.interpolator = LinearInterpolator()
        val narrowAnimator = ValueAnimator.ofFloat(DEFAULT_MAX_ANGLE, DEFAULT_MIN_ANGLE)
        narrowAnimator.addUpdateListener { animation ->
            sweepAngle = animation.animatedValue as Float
            invalidate()
        }
        narrowAnimator.duration = DEFAULT_DURATION.toLong()
        narrowAnimator.interpolator = DecelerateInterpolator(2f)
        val set = AnimatorSet()
        set.play(holdAnimator1).with(expandAnimator)
        set.play(holdAnimator).with(narrowAnimator).after(holdAnimator1)
        return set
    }

    //清除动画
    private fun cancelAnimator() {
        if (animatorSet != null) {
            animatorSet!!.cancel()
            isNeedTick = true
        }
    }

    fun setSuccessfullyStatus() {
        if (animatorSet != null) {
            animatorSet!!.cancel()
            isNeedTick = true
            startAnimation(mTickAnimation)
        }
    }

    override fun setVisibility(visibility: Int) {
        when (visibility) {
            VISIBLE -> startAnimation()
            INVISIBLE -> cancelAnimator()
            GONE -> cancelAnimator()
            else -> {
            }
        }
        super.setVisibility(visibility)
    }

    fun setBorderWidth(width: Int) {
        mBorderWidth = width.toFloat()
    }

    fun setArcColor(color: Int) {
        arcColor = color
    }

    interface OnPasswordCorrectlyListener {
        fun onPasswordCorrectly()
    }

    companion object {
        private val TAG = MDProgressBar::class.java.simpleName
        private const val DEFAULT_MAX_ANGLE = -305f
        private const val DEFAULT_MIN_ANGLE = -19f

        //默认的动画时间
        private const val DEFAULT_DURATION = 660
        private const val DEFAULT_ARC_COLOR = Color.BLUE
    }
}