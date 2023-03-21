package com.example.aliantebadge.home.menu_item.setting.item_setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.widget.Toast;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1);

        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            Intent activityIntent =intent.getParcelableExtra(Intent.EXTRA_INTENT);
            context.startActivity(activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (status == PackageInstaller.STATUS_SUCCESS) {
            Toast.makeText(context, "Successfully Installed", Toast.LENGTH_SHORT).show();
        } else {

            String msg =intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
            Toast.makeText(context, "Error while installing" + msg, Toast.LENGTH_SHORT).show();

        }


    }
}