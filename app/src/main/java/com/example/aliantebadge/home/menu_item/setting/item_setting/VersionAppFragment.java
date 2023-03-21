package com.example.aliantebadge.home.menu_item.setting.item_setting;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.example.aliantebadge.BuildConfig;
import com.example.aliantebadge.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class VersionAppFragment extends Fragment {
    TextView actualVersion;
    TextView lastVersion;
    Button updateVersion;
    FirebaseFirestore mFirestore;
    String url;
    String fileName = "aliante.apk";
    String destination;
    String uriString;
    Uri uri;
    View progressBar ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = requireActivity().findViewById(R.id.progress_bar);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_version_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actualVersion = view.findViewById(R.id.actualVersion);
        lastVersion = view.findViewById(R.id.lastVersion);
        updateVersion = view.findViewById(R.id.updateButton);


        destination = requireActivity().getExternalCacheDir() + "/aliante/" + fileName;
        uriString = "file://" + destination;
        uri = Uri.parse(uriString);
        mFirestore = FirebaseFirestore.getInstance();

        String act;


        actualVersion.setText(BuildConfig.VERSION_NAME);
        act = String.valueOf(actualVersion.getText());

        mFirestore.collection("Version").document("versionApp").get().addOnCompleteListener( task -> {
               if( task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String last;

                        Map<String, Object> documentData = document.getData();
                        assert documentData != null;
                        lastVersion.setText(String.valueOf( documentData.get("newVersionApp")));
                        last = String.valueOf(lastVersion.getText());

                        url = (String) documentData.get("urlNewVersion");


                        if(act.compareTo(last) != 0){
                           updateVersion.setVisibility(View.VISIBLE);
                        }
                        if(act.compareTo(last) == 0){
                            updateVersion.setVisibility(View.GONE);
                        }
                    }
                }});

        updateVersion.setOnClickListener(view1 -> {

            requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            //installAfterQ();
            downloadApk();
        });

    }

    public void installAfterQ() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                InputStream inputStream = requireActivity().getApplication().getContentResolver().openInputStream(uri);

                long length = DocumentFile.fromSingleUri(requireActivity().getApplication(), uri).length();
                PackageInstaller installer = requireActivity().getApplication().getPackageManager().getPackageInstaller();
                PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                int sessionId = installer.createSession(params);
                PackageInstaller.Session session = installer.openSession(sessionId);

                File file = new File(uri.getPath());
                OutputStream outputStream = session.openWrite(file.getName(), 0, length);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0,  len);
                }


                session.fsync(outputStream);

                outputStream.close();
                inputStream.close();

                if (file.isFile())
                    file.delete();

                Intent intent = new Intent(requireActivity().getApplication(), UpdateReceiver.class);
                PendingIntent pi;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pi = PendingIntent.getBroadcast(requireActivity().getApplication(),
                            3439,
                            intent,
                            PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                }else{
                    pi = PendingIntent.getBroadcast(requireActivity().getApplication(),
                            3439,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }
                session.commit(pi.getIntentSender());
                session.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void downloadApk() {
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment


        //Delete update file if exists
        File file = new File(destination);
        if (file.isFile())
            file.delete();



        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download new version");
        request.setTitle(requireActivity().getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                //requirePermissionReadFileSystem();
                progressBar.setVisibility(View.GONE);
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                requirePermissionInstall();
                ctxt.unregisterReceiver(this);
            }
        };
        //register receiver for when .apk download is complete
        requireContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void requirePermissionInstall() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!requireActivity().getPackageManager().canRequestPackageInstalls()) {
                startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + requireContext().getPackageName())));
            } else
                installAfterQ();

        }else
            installApk();


    }

    private void installApk() {

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addCategory("android.intent.category.DEFAULT");
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(install);
    }

}
