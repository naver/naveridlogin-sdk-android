package com.navercorp.nid.oauth.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nhn.android.oauth.R


/**
 *
 * Created on 2021.10.22
 * Updated on 2021.10.22
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * TODO : Write a role for this class.
 */
class DownloadBanner : LinearLayout {

    private var density: Float = context.resources.displayMetrics.density
    private var densityDpi : Int = context.resources.displayMetrics.densityDpi

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setBackgroundColor(Color.rgb(254, 252, 227))
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)

        addView(createIcon())
        addView(createDescription())
        addView(createCloseButton())
    }

    private fun createIcon(): ImageView {
        val view = ImageView(context)
        val padding = 10.toDp()
        view.apply {
            layoutParams = LayoutParams(70.toDp(), 70.toDp())
            setPadding(padding, padding, 0, padding)
            setImageDrawable(context.getDrawable(R.drawable.naver_icon))
            setOnClickListener { downloadNaverapp() }
        }
        return view
    }

    private fun createDescription(): LinearLayout {
        val layout = LinearLayout(context)
        val padding = 10.toDp()
        layout.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(padding, padding, 0, padding)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            setOnClickListener { downloadNaverapp() }
        }

        val description = TextView(context)
        val tvPadding = 4.toDp()
        description.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, tvPadding, 0, tvPadding)
            setTextColor(Color.rgb(51, 51, 51))
            text = context.getString(R.string.naveroauthlogin_string_msg_naverapp_download_desc)
            setTypeface(null, Typeface.BOLD)
            textSize = getTextSizeUpper()
        }
        layout.addView(description)

        val link = TextView(context)
        link.apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, tvPadding)
            text = context.getString(R.string.naveroauthlogin_string_msg_naverapp_download_link)
            setTextColor(Color.rgb(45, 100, 0)) // #2db400
            paintFlags = link.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            textSize = getTextSizeUpper()
        }
        layout.addView(link)

        return layout
    }

    private fun createCloseButton(): RelativeLayout {
        val button = ImageView(context)
        val padding = 10.toDp()

        button.apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            setPadding(padding, padding, padding, padding)
            setImageDrawable(context.getDrawable(R.drawable.close_btn_img_black))
            scaleType = ImageView.ScaleType.FIT_START
        }


        val params = button.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        button.layoutParams = params
        button.setOnClickListener {
            visibility = View.GONE
        }

        val layout = RelativeLayout(context)
        layout.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        layout.addView(button)
        return layout
    }

    private fun getTextSizeUpper(): Float {
        return when {
            isXhdpi(densityDpi) -> { 14f }
            isHdpi(densityDpi) -> { 13f }
            else -> { 12f }
        }
    }

    private fun isXhdpi(densityDpi: Int): Boolean {
        if (isMdpi(densityDpi)) {
            return false
        }
        if (isHdpi(densityDpi)) {
            return false
        }
        return true
    }

    private fun isHdpi(densityDpi: Int): Boolean {
        if (isMdpi(densityDpi)) {
            return false
        }
        return densityDpi <= 240
    }

    private fun isMdpi(densityDpi: Int): Boolean {
        return densityDpi <= 160
    }

    private fun downloadNaverapp() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.search"))
        (context as Activity).startActivity(intent)
        (context as Activity).finish()
    }

    private fun Int.toDp(): Int = (this * density).toInt()
}

