package com.android.calendar.vcalendar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.calendar.R;
import com.android.calendar.Utils;
//import android.app.AlertDialog;
import com.hct.gios.widget.AlertDialog;

public class ImportActivity extends Activity {

    Activity mActivity;
    AlertDialog mImportDialog;
    String mFilePath = null;

    private DialogInterface.OnClickListener mImportDialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int button) {
            if (Utils.checkAndRequestPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE)) {
                startImportVCal();
            }
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int button) {
            mActivity.finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        Intent intent = getIntent();
        Uri uri = intent.getData();
        // mFilePath = uri.getPath().toString();
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getString("Drmfilepath") != null) {
            mFilePath = bundle.getString("Drmfilepath", "");
        } else {
            mFilePath = uri.toString();
        }

        Log.d("ImportActivity", "zxj FilePath = " + mFilePath);
        mImportDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.import_file)
                .setPositiveButton(android.R.string.ok, mImportDialogListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false).show();
    }

    public void startImportVCal() {
        VCalImporter VCalProcess = new VCalImporter(mActivity);
        if (!VCalProcess.SaveImportCalendar(mFilePath, -1)) {
            Toast.makeText(mActivity, R.string.import_fail, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(mActivity, R.string.import_success,
                    Toast.LENGTH_SHORT).show();
        }
        mActivity.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE:
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImportVCal();
                } else {
                    String permissionName = getResources().getString(
                            R.string.permissions_name_storage);
                    Toast.makeText(
                            this,
                            this.getString(R.string.permissions_denied_tip,
                                    permissionName), Toast.LENGTH_LONG).show();
                    mActivity.finish();
                }
            }
            break;
        default:
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
            break;
        }
    }

}
