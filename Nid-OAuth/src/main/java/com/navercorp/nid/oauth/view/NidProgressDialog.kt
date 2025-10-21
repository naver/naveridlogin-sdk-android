package com.navercorp.nid.oauth.view

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.nhn.android.oauth.R

/**
 *
 * Created on 2021.10.22
 * Updated on 2021.10.22
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네이버 공통 Nid Progress Dialog
 */
class NidProgressDialog {
    private var context: Context
    private var dialog: AppCompatDialog
    private var message: AppCompatTextView? = null
    private var animation: LottieAnimationView? = null

    constructor(context: Context) {
        this.context = context
        this.dialog = AppCompatDialog(context)

        init(null)
    }


    private fun init(cancelListener: DialogInterface.OnCancelListener?) {
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.nid_progress_dialog)

        message = dialog.findViewById(R.id.nid_progress_dialog_message)
        animation = dialog.findViewById(R.id.nid_progress_dialog_animation)
    }

    fun showProgress(resourceId: Int) {
        showProgress(context.resources.getString(resourceId))
    }

    fun showProgress(resourceId: Int, cancelListener: DialogInterface.OnCancelListener? = null) {
        showProgress(context.resources.getString(resourceId), cancelListener)
    }

    fun showProgress(msg: String) {
        showProgress(msg, null)
    }

    fun showProgress(msg: String, cancelListener: DialogInterface.OnCancelListener? = null) {
        if (context.isFinishing()) {
            return
        }

        message?.let {
            it.text = msg
        }

        cancelListener?.let {
            dialog.setOnCancelListener(cancelListener)
        }

        animation?.playAnimation()
        dialog.show()
    }

    fun hideProgress() {
        if (context.isFinishing()) {
            return
        }

        if (dialog.isShowing) {
            animation?.pauseAnimation()
            dialog.dismiss()
        }
    }
}

fun Context.isFinishing() = (this as? Activity)?.isFinishing == true