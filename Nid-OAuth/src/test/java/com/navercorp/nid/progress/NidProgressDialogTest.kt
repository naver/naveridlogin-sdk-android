package com.navercorp.nid.progress

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieAnimationView
import com.navercorp.naverid.NaverIdTestCase
import com.nhn.android.oauth.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog


class NidProgressDialogTest: NaverIdTestCase() {

    private lateinit var context: Context
    private lateinit var dialog: AppCompatDialog
    private lateinit var message: AppCompatTextView
    private lateinit var animation: LottieAnimationView

    private lateinit var oldProgressDialog: ProgressDialog

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dialog = AppCompatDialog(context)

        message = Mockito.mock(AppCompatTextView::class.java)
        animation = Mockito.mock(LottieAnimationView::class.java)

        oldProgressDialog = ProgressDialog(context, R.style.Theme_AppCompat_Light_Dialog)

    }

    @Test
    fun init_테스트() {
        dialog.setCancelable(true)

        val shadowAlertDialog = shadowOf(dialog)
        Assert.assertTrue(shadowAlertDialog.isCancelable)
    }

    @Test
    fun show_테스트() {
        val msg = "message"

        val shadowAlertDialog = shadowOf(dialog)
        val cancelListener = Mockito.mock(DialogInterface.OnCancelListener::class.java)

        showProgress(msg, cancelListener)

        Assert.assertTrue(dialog.isShowing)

        Mockito.verify(message).setText(msg)
        Assert.assertEquals(cancelListener, shadowAlertDialog.onCancelListener)
        Mockito.verify(animation).playAnimation()
    }

    fun hide_테스트() {
        hideProgress()

        Assert.assertFalse(dialog.isShowing)

        Mockito.verify(animation).pauseAnimation()
    }

    @Test
    fun init_리그레션_테스트() {
        oldProgressDialog.setCancelable(true)
        dialog.setCancelable(true)

        val oldShadowDialog = shadowOf(oldProgressDialog)
        val nidShadowDialog = shadowOf(dialog)

        Assert.assertEquals(oldShadowDialog.isCancelable, nidShadowDialog.isCancelable)
    }

    @Test
    fun show_리그레션_테스트() {
        val cancelListener = Mockito.mock(DialogInterface.OnCancelListener::class.java)
        val message = "message"

        oldProgressDialog.show()
        oldProgressDialog.setOnCancelListener(cancelListener)
        val oldShadowDialog: ShadowAlertDialog = shadowOf(oldProgressDialog)

        dialog.show()
        dialog.setOnCancelListener(cancelListener)
        val nidShadowDialog = shadowOf(dialog)

        Assert.assertEquals(oldProgressDialog.isShowing, dialog.isShowing)
        Assert.assertEquals(oldShadowDialog.onCancelListener, nidShadowDialog.onCancelListener)
    }

    @Test
    fun hide_리그레션_테스트() {
        oldProgressDialog.hide()
        dialog.hide()

        Assert.assertEquals(oldProgressDialog.isShowing, dialog.isShowing)
    }

    fun showProgress(msg: String, cancelListener: DialogInterface.OnCancelListener? = null) {
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
        if (dialog.isShowing) {
            animation?.pauseAnimation()
            dialog.dismiss()
        }
    }



}