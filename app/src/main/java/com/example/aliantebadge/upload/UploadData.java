package com.example.aliantebadge.upload;

import android.app.AlertDialog;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.example.aliantebadge.R;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.OtherUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class UploadData {

    public AppDatabase db = null;



    public static void uploadUsersOnDB(Task<QuerySnapshot> task, FragmentActivity activity) {

        AppDatabase db = AppDatabase.getDbInstance(activity);

        db.otherUserDao().deleteAll();

        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
            String documentId = document.getId();
            Map<String, Object> documentData = document.getData();

            OtherUser user = new OtherUser();
            user.uid = documentId;
            user.firstName = (String) documentData.get("first_name");
            user.secondName = (String) documentData.get("second_name");
            user.phoneModel = (String) documentData.get("phoneModel");
            user.email = (String) documentData.get("email");
            user.versionApp = (String) documentData.get("versionApp");
            db.otherUserDao().insertUser(user);

        }
    }

    public static void dialogError(FragmentActivity activity, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getText(R.string.pay_attention));
        builder.setMessage(message);
        builder.show();
    }

    public static void dialogOk(FragmentActivity activity, NavController mNav) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getText(R.string.ok));
        builder.setMessage(activity.getText(R.string.data_successfully_changed));
        builder.setPositiveButton(activity.getText(R.string.ok), (dialog, id) -> mNav.navigateUp()).show();
    }
    public static void dialogPrenotedOk(FragmentActivity activity, NavController mNav) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getText(R.string.ok));
        builder.setMessage("Prenotazione effettuata.\nTrovi le tue prenotazioni nel menù in alto a sinistra");
        builder.setPositiveButton(activity.getText(R.string.ok), (dialog, id) -> mNav.navigateUp()).show();
    }


}
