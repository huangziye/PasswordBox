package com.hzy.password

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 防京东密码键盘
 * Created by ziye_huang on 2018/3/8.
 */
class Keyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
) {
    private var gvKeyboard: GridView? = null
    private var key: Array<String> = arrayOf()
    private var onClickKeyboardListener: OnClickKeyboardListener? = null

    /**
     * 初始化键盘的点击事件
     */
    private fun initEvent() {
        gvKeyboard!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (onClickKeyboardListener != null && position >= 0) {
                onClickKeyboardListener!!.onKeyClick(position, key[position])
            }
        }
    }

    /**
     * 初始化KeyboardView
     */
    private fun initKeyboardView() {
        val view = inflate(context, R.layout.view_keyboard, this)
        gvKeyboard = view.findViewById(R.id.gv_keyboard)
        gvKeyboard?.adapter = keyboardAdapter
        initEvent()
    }

    interface OnClickKeyboardListener {
        fun onKeyClick(position: Int, value: String?)
    }

    /**
     * 对外开放的方法
     *
     * @param onClickKeyboardListener
     */
    fun setOnClickKeyboardListener(onClickKeyboardListener: OnClickKeyboardListener?) {
        this.onClickKeyboardListener = onClickKeyboardListener
    }

    /**
     * 设置键盘所显示的内容
     *
     * @param key
     */
    fun setKeyboardKeys(key: Array<String>) {
        this.key = key
        initKeyboardView()
    }

    private val keyboardAdapter: BaseAdapter = object : BaseAdapter() {
        private val KEY_NINE = 9
        override fun getCount(): Int {
            return key.size
        }

        override fun getItem(position: Int): Any {
            return key[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getViewTypeCount(): Int {
            return 2
        }

        override fun getItemViewType(position: Int): Int {
            return if (getItemId(position) == KEY_NINE.toLong()) 2 else 1
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            var viewHolder: ViewHolder? = null
            if (convertView == null) {
                if (getItemViewType(position) == 1) {
                    //数字键
                    convertView = LayoutInflater.from(context)
                        .inflate(R.layout.grid_item_keyboard, parent, false)
                    viewHolder = ViewHolder(convertView)
                } else {
                    //删除键
                    convertView = LayoutInflater.from(context)
                        .inflate(R.layout.grid_item_keyboard_delete, parent, false)
                }
            }
            if (getItemViewType(position) == 1) {
                viewHolder = convertView.tag as ViewHolder
                viewHolder.tvKey.text = key[position]
            }
            return convertView
        }
    }

    /**
     * ViewHolder,view缓存
     */
    internal class ViewHolder(view: View) {
        val tvKey: TextView = view.findViewById(R.id.tv_keyboard_keys)

        init {
            view.tag = this
        }
    }
}