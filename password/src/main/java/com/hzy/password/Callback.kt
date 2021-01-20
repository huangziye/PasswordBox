package com.hzy.password

/**
 * Created by ziye_huang on 2017/9/25.
 */
interface Callback {
    fun onForgetPassword()
    fun onInputCompleted(password: CharSequence?)
    fun onPasswordCorrectly()
    fun onCancel()
}