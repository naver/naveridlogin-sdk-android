package com.navercorp.nid.progress.legacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.navercorp.nid.log.NidLog;
import com.nhn.android.oauth.R;

public class OAuthLoginDialogManager {

    private final String TAG = "OAuthLoginDialogManager";

    public Object progressDialogSync = new Object();
    private ProgressDialog nLoginGlobalDefaultProgressDialog = null;

    /**
     * progress dialog를 보여줌
     *
     * @param context context
     * @param msg
     *            dialog에 출력할 메시지
     * @param onCancelListener
     *            back-key 등으로 cancel 될 경우 실행될 listener. 주로 백그라운드로 처리되던 작업의 중지를 하는 로직이 들어감
     * @return 생성실패하는 경우 false 리턴, 정상적인 경우 true 리턴
     */
    public boolean showProgressDlg(Context context, String msg, DialogInterface.OnCancelListener onCancelListener) {

        synchronized(progressDialogSync) {
            try {
                if (nLoginGlobalDefaultProgressDialog != null) {
                    nLoginGlobalDefaultProgressDialog.hide();
                    nLoginGlobalDefaultProgressDialog.dismiss();
                }
                nLoginGlobalDefaultProgressDialog = new ProgressDialog(context, R.style.Theme_AppCompat_Light_Dialog);
                nLoginGlobalDefaultProgressDialog.setIndeterminate(true);
                nLoginGlobalDefaultProgressDialog.setMessage(msg);
                nLoginGlobalDefaultProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if (onCancelListener != null) {
                    nLoginGlobalDefaultProgressDialog.setOnCancelListener(onCancelListener);
                }
                nLoginGlobalDefaultProgressDialog.setCanceledOnTouchOutside(false);
                nLoginGlobalDefaultProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // dismiss 되는 경우 null로 처리하여 다음 show Progress 하는데 문제
                        // 없도록 함
                        nLoginGlobalDefaultProgressDialog = null;
                    }
                });

                nLoginGlobalDefaultProgressDialog.show();

                return true;
            } catch (Exception e) {
                NidLog.e(TAG, e);
            }
            return false;
        }
    }

    /**
     * showPregressDlg()로 만든 progress dialog를 없앰
     *
     * @return 없거나 실패한경우 false 리턴, 정상적으로 없어진 경우 true 리턴
     */
    public synchronized boolean hideProgressDlg() {
        synchronized(progressDialogSync) {
            if (nLoginGlobalDefaultProgressDialog == null) {
                return false;
            }
            try {
                nLoginGlobalDefaultProgressDialog.hide();
                nLoginGlobalDefaultProgressDialog.dismiss();
                nLoginGlobalDefaultProgressDialog = null;
                return true;
            } catch (Exception e) {
                NidLog.e(TAG, e);
            }
            return false;
        }
    }

}
