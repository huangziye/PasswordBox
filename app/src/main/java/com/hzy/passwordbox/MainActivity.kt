package com.hzy.passwordbox

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hzy.password.Callback
import com.hzy.password.PasswordKeypad
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowDialog.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnShowDialog -> showInputPasswordDialog()
        }
    }

    private fun showInputPasswordDialog() {
        val dialog = PasswordKeypad()
        dialog.setPasswordCount(6)
        dialog.isCancelable = false
        dialog.setInputPasswordTimes(2)
        dialog.setTitleList(listOf("请输入旧密码", "请输入新密码"))
        dialog.setCallback(object : Callback {
            override fun onForgetPassword() {
                dialog.setPasswordState(true)
                dialog.dismiss()
            }

            override fun onInputCompleted(password: MutableList<CharSequence>) {
                Toast.makeText(this@MainActivity, password.toString(), Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    dialog.setPasswordState(true)
                    dialog.dismiss()
                }, 5000)
            }

            override fun onPasswordCorrectly() {

            }

            override fun onCancel() {

            }

        })
        dialog.show(supportFragmentManager, "payDetail")
    }
}