package com.hzy.password

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.Button
import android.widget.GridLayout
import java.util.*

/**
 * 密码键盘
 */
class PasswordKeyboard : GridLayout, View.OnClickListener, View.OnTouchListener {

    @JvmField
    val DEL = "删除"

    @JvmField
    val DONE = "OK"


    private val DELETE = 1
    private var paint: Paint? = null

    /**
     * 因为UED给的是iphone的设计稿，所以是按照等比的思想设置键盘key的高度和宽度
     */
    private val IPHONE = 779

    /**
     * 每个键盘key的宽度，为屏幕宽度的三分之一
     */
    private val keyWidth =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width / 3

    /**
     * 每个键盘key的高度
     */
    private val keyHeight =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height * 59 / IPHONE

    /**
     * 屏幕的宽度
     */
    private val screenWidth =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width

    /**
     * List集合存储key，方便每次输错都能再次随机数字键盘
     */
    private val keyButtons = arrayListOf<Button>()

    private var workHandler: WorkHandler? = null
    private var inputListener: OnPasswordInputListener? = null

    /**
     * WorkHandler 用于处理长按"删除"Key时,执行重复删除操作。
     */
    inner class WorkHandler : Handler() {

        private var index = 0
        private var diffTime: Long = 100

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                DELETE -> {
                    if (msg.obj is PasswordKeyboard) {
                        val keyboard = msg.obj
                        handlerClick(DEL)
                        removeMessages(DELETE)
                        val msg = obtainMessage(DELETE)
                        msg.obj = keyboard
                        if (diffTime > 40) {
                            diffTime -= index
                        }
                        sendMessageDelayed(msg, diffTime)
                        index++
                    }
                }
            }
        }

        fun reset() {
            index = 0
            diffTime = 100
        }
    }


    constructor(context: Context, attrs: AttributeSet? = null) : super(
        context,
        attrs
    ) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        //必须设置调用该方法,不然onDraw方法不执行。如果ViewGroup没有背景,则其onDraw方法不执行
        setWillNotDraw(false)
        if (childCount > 0) {
            keyButtons.clear()
            removeAllViews()
        }
        //获取随机键盘数字的字符串
        val keyList = randomKeys(10)
        //填充键盘Key,用Button来完成Key功能
        for (i in keyList.indices) {
            val item = Button(context)
            //            TextView item = new TextView(getContext());
            val params = LayoutParams(keyWidth, keyHeight)
            item.layoutParams = params
            item.isClickable = true
            item.setOnClickListener(this)
            item.text = keyList[i]
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            item.setTextColor(resources.getColor(android.R.color.black))
            item.setBackgroundDrawable(resources.getDrawable(R.drawable.dialog_key_selector))
            //监听"删除"的长按监听事件,完成重复删除操作
            if (DEL == keyList[i]) {
                item.setOnTouchListener(this)
            }
            item.tag = keyList[i]
            addView(item)
            keyButtons.add(item)
        }
        if (paint == null) {
            paint = Paint()
            paint!!.color = Color.parseColor("#cccccc")
            paint!!.strokeWidth = 1f
        }
    }

    override fun onClick(v: View?) {
        val character = v?.tag.toString()
        handlerClick(character)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (workHandler == null) {
            workHandler = WorkHandler()
        }
        when {
            MotionEvent.ACTION_DOWN == event!!.action -> {
                val msg: Message = workHandler!!.obtainMessage(DELETE)
                msg.obj = this
                workHandler!!.sendMessageDelayed(msg, 500)
            }
            MotionEvent.ACTION_UP == event.action -> {
                workHandler!!.removeMessages(DELETE)
                workHandler!!.reset()
            }
            MotionEvent.ACTION_CANCEL == event.action -> {
                workHandler!!.removeMessages(DELETE)
                workHandler!!.reset()
            }
            MotionEvent.ACTION_MOVE == event.action -> {
            }
            else -> {
                //do nothing
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制分割线
        canvas!!.drawLine(0f, 0f, measuredWidth.toFloat(), 0f, paint!!)
        canvas.drawLine(
            0f,
            (measuredHeight / 4).toFloat(),
            measuredWidth.toFloat(),
            (measuredHeight / 4).toFloat(),
            paint!!
        )
        canvas.drawLine(
            0f,
            (2 * measuredHeight / 4).toFloat(),
            measuredWidth.toFloat(),
            (2 * measuredHeight / 4).toFloat(),
            paint!!
        )
        canvas.drawLine(
            0f,
            (3 * measuredHeight / 4).toFloat(),
            measuredWidth.toFloat(),
            (3 * measuredHeight / 4).toFloat(),
            paint!!
        )
        canvas.drawLine(
            (measuredWidth / 3).toFloat(),
            0f,
            (measuredWidth / 3).toFloat(),
            measuredHeight.toFloat(),
            paint!!
        )
        canvas.drawLine(
            (2 * measuredWidth / 3).toFloat(),
            0f,
            (2 * measuredWidth / 3).toFloat(),
            measuredHeight.toFloat(),
            paint!!
        )
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        setMeasuredDimension(screenWidth, keyHeight * 4)
    }

    private fun handlerClick(character: String) {
        //密码字符输出回调
        inputListener.let {
            when {
                DONE == character -> {
                    inputListener?.onInput(DONE)
                }
                DEL == character -> {
                    inputListener?.onInput(DEL)
                }
                else -> {
                    inputListener?.onInput(character)
                }
            }
        }
    }


    /**
     * 重新设置键盘key位置
     */
    fun resetKeyboard() {
        val keyList = randomKeys(10)
        for (i in keyList.indices) {
            keyButtons[i].text = keyList[i]
            keyButtons[i].tag = keyList[i]
        }
    }

    /**
     * 随机生成键盘key数字
     */
    fun randomKeys(num: Int): List<String> {
        val keys = IntArray(num)
        for (i in 0 until num) {
            keys[i] = i
        }
        val random = Random()
        for (i in 0 until num) {
            val p = random.nextInt(num)
            val tmp = keys[i]
            keys[i] = keys[p]
            keys[p] = tmp
        }
        val keyList: MutableList<String> = ArrayList()
        for (key in keys) {
            keyList.add(key.toString())
        }
        //将空字符串插入到第10个位置,是个无操作的Key
        keyList.add(9, "")
        //将删除字符串插入最后
        keyList.add(DEL)
        return keyList
    }

    fun setOnPasswordInputListener(listener: OnPasswordInputListener) {
        this.inputListener = listener
    }

    /**
     * 监听密码输入
     */
    interface OnPasswordInputListener {
        fun onInput(number: String)
    }
}