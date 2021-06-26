package promind.cleaner.app.ui.activities.deepClean;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.EnvironmentCompat;

import com.airbnb.lottie.LottieAnimationView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import promind.cleaner.app.R;
import promind.cleaner.app.core.data.model.Cont;
import promind.cleaner.app.core.data.model.DataModel;
import promind.cleaner.app.core.data.model.Duplicate;
import promind.cleaner.app.core.data.model.TypeFile;
import promind.cleaner.app.core.utils.utilts.Config;
import promind.cleaner.app.core.utils.utilts.LocalListenerKt;
import promind.cleaner.app.core.utils.utilts.Utils;

public class DeepScanActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    public static ArrayList<DataModel> mListData = new ArrayList<DataModel>();
    LottieAnimationView animationView;
    TextView tvProgress;
    ScanImagesAsyncTask mScanImagesAsyncTask;
    HashMap<String, ArrayList<File>> mListDoc = new HashMap<>();
    HashMap<String, ArrayList<File>> mListImage = new HashMap<>();
    HashMap<String, ArrayList<File>> mListVideo = new HashMap<>();
    HashMap<String, ArrayList<File>> mListAudio = new HashMap<>();
    HashMap<String, ArrayList<File>> mListOtherFile = new HashMap<>();
    private ArrayList<String> arrPermission;
    private HashMap<String, ArrayList<File>> requiredContent;
    private int scanStep = 1;
    private boolean isCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocalListenerKt.setListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                mListData.clear();
                finish();
                return null;
            }
        });
        mListData.clear();
        super.onCreate(savedInstanceState);
        Utils.setLocale(this);
        setContentView(R.layout.activity_dupicate_main);
        intView();
        checkPermission();
        requiredContent = new HashMap();
        scanAll();
    }

    private void scanAll() {

        switch (scanStep) {
            case 1: {
                Toast.makeText(DeepScanActivity.this, getString(R.string.scan_wait), Toast.LENGTH_LONG).show();
                scanFile(Cont.IMAGE);
            }
            break;
            case 2:
                scanFile(Cont.AUDIO);
                break;
            case 3:
                scanFile(Cont.VIDEO);
                break;
            case 4:
                scanFile(Cont.DOCCUNMENT);
                break;
            case 5:
                scanFile(Cont.OTHERFILE);
                break;
            default:
                animationView.pauseAnimation();
                intentAfterScan();
        }
    }

    private void intentAfterScan() {
        if (mListData.size() == 0) {
            Toast.makeText(DeepScanActivity.this, getString(R.string.no_file_found), Toast.LENGTH_LONG).show();
            try {
                Intent intent = new Intent(DeepScanActivity.this, DeepScanActivity.class);
                intent.putExtra("result deep clean data", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
            Intent intent = new Intent(getApplicationContext(), DuplicateActivity.class);
            startActivity(intent);
        }
    }


    public void intView() {
        animationView = findViewById(R.id.av_clean_file);
        animationView.playAnimation();
        tvProgress = findViewById(R.id.tv_progress);
    }


    private void collectRequiredFilesContent() {
        requiredContent.put(getString(R.string.apk), new ArrayList());
        requiredContent.put(getString(R.string.zip), new ArrayList());
        requiredContent.put(getString(R.string.vcf), new ArrayList());
        requiredContent.put(getString(R.string.mp3), new ArrayList());
        requiredContent.put(getString(R.string.aac), new ArrayList());
        requiredContent.put(getString(R.string.amr), new ArrayList());
        requiredContent.put(getString(R.string.m4a), new ArrayList());
        requiredContent.put(getString(R.string.ogg), new ArrayList());
        requiredContent.put(getString(R.string.wav), new ArrayList());
        requiredContent.put(getString(R.string.flac), new ArrayList());
        requiredContent.put(getString(R.string._3gp), new ArrayList());
        requiredContent.put(getString(R.string.mp4), new ArrayList());
        requiredContent.put(getString(R.string.mkv), new ArrayList());
        requiredContent.put(getString(R.string.webm), new ArrayList());
        requiredContent.put(getString(R.string.jpg), new ArrayList());
        requiredContent.put(getString(R.string.jpeg), new ArrayList());
        requiredContent.put(getString(R.string.png), new ArrayList());
        requiredContent.put(getString(R.string.bmp), new ArrayList());
        requiredContent.put(getString(R.string.gif), new ArrayList());
        requiredContent.put(getString(R.string.doc), new ArrayList());
        requiredContent.put(getString(R.string.docx), new ArrayList());
        requiredContent.put(getString(R.string.html), new ArrayList());
        requiredContent.put(getString(R.string.pdf), new ArrayList());
        requiredContent.put(getString(R.string.txt), new ArrayList());
        requiredContent.put(getString(R.string.xml), new ArrayList());
        requiredContent.put(getString(R.string.xlsx), new ArrayList());
        requiredContent.put(getString(R.string.js), new ArrayList());
        requiredContent.put(getString(R.string.css), new ArrayList());
        requiredContent.put(getString(R.string.dat), new ArrayList());
        requiredContent.put(getString(R.string.cache), new ArrayList());
        requiredContent.put(getString(R.string.nomedia), new ArrayList());
        requiredContent.put(getString(R.string.emptyshow), new ArrayList());

    }

    private void detectFileTypeAndAddInCategory(File file) {

        String fileName = file.getName();
        ArrayList audios;
        ArrayList videos;
        ArrayList images;
        ArrayList documents;
        ArrayList files;

        if (fileName.endsWith(".apk")) {
            ArrayList<File> apk = requiredContent.get(getString(R.string.apk));
            if (apk != null) {
                apk.add(file);
            }
        } else if (fileName.endsWith(".zip")) {
            ArrayList<File> zip = requiredContent.get(getString(R.string.zip));
            if (zip != null) {
                zip.add(file);
            }
        } else if (fileName.endsWith(".vcf")) {
            ArrayList<File> vcf = requiredContent.get(getString(R.string.vcf));
            if (vcf != null) {
                vcf.add(file);
            }
        } else if (fileName.endsWith(".mp3")) {
            audios = requiredContent.get(getString(R.string.mp3));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".aac")) {
            audios = requiredContent.get(getString(R.string.aac));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".amr")) {
            audios = requiredContent.get(getString(R.string.amr));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".m4a")) {
            audios = requiredContent.get(getString(R.string.m4a));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".ogg")) {
            audios = requiredContent.get(getString(R.string.ogg));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".wav")) {
            audios = requiredContent.get(getString(R.string.wav));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".flac")) {
            audios = requiredContent.get(getString(R.string.flac));
            if (audios != null) {
                audios.add(file);
            }
        } else if (fileName.endsWith(".3gp")) {
            videos = requiredContent.get(getString(R.string._3gp));
            if (videos != null) {
                videos.add(file);
            }
        } else if (fileName.endsWith(".mp4")) {
            videos = requiredContent.get(getString(R.string.mp4));
            if (videos != null) {
                videos.add(file);
            }
        } else if (fileName.endsWith(".mkv")) {
            videos = requiredContent.get(getString(R.string.mkv));
            if (videos != null) {
                videos.add(file);
            }
        } else if (fileName.endsWith(".webm")) {
            videos = requiredContent.get(getString(R.string.webm));
            if (videos != null) {
                videos.add(file);
            }
        } else if (fileName.endsWith(".jpg")) {
            images = requiredContent.get(getString(R.string.jpg));
            if (images != null) {
                images.add(file);
            }
        } else if (fileName.endsWith(".jpeg")) {
            images = requiredContent.get(getString(R.string.jpeg));
            if (images != null) {
                images.add(file);
            }
        } else if (fileName.endsWith(".png")) {
            images = requiredContent.get(getString(R.string.png));
            if (images != null) {
                images.add(file);
            }
        } else if (fileName.endsWith(".bmp")) {
            images = requiredContent.get(getString(R.string.bmp));
            if (images != null) {
                images.add(file);
            }
        } else if (fileName.endsWith(".gif")) {
            images = requiredContent.get(getString(R.string.gif));
            if (images != null) {
                images.add(file);
            }
        } else if (fileName.endsWith(".doc")) {
            documents = requiredContent.get(getString(R.string.doc));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".docx")) {
            documents = requiredContent.get(getString(R.string.docx));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".html")) {
            documents = requiredContent.get(getString(R.string.html));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".pdf")) {
            documents = requiredContent.get(getString(R.string.pdf));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".txt")) {
            documents = requiredContent.get(getString(R.string.txt));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".xml")) {
            documents = requiredContent.get(getString(R.string.xml));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".xlsx")) {
            documents = requiredContent.get(getString(R.string.xlsx));
            if (documents != null) {
                documents.add(file);
            }
        } else if (fileName.endsWith(".js")) {
            files = requiredContent.get(getString(R.string.js));
            if (files != null) {
                files.add(file);
            }
        } else if (fileName.endsWith(".css")) {
            files = requiredContent.get(getString(R.string.css));
            if (files != null) {
                files.add(file);
            }
        } else if (fileName.endsWith(".dat")) {
            files = requiredContent.get(getString(R.string.dat));
            if (files != null) {
                files.add(file);
            }
        } else if (fileName.endsWith(".cache")) {
            files = requiredContent.get(getString(R.string.cache));
            if (files != null) {
                files.add(file);
            }
        } else if (fileName.endsWith(".nomedia")) {
            files = requiredContent.get(getString(R.string.nomedia));
            if (files != null) {
                files.add(file);
            }
        } else if (fileName.endsWith(".emptyshow")) {
            files = requiredContent.get(getString(R.string.emptyshow));
            if (files != null) {
                files.add(file);
            }
        }
    }


    public void scanFile(int type) {
        mScanImagesAsyncTask = new ScanImagesAsyncTask(type);
        mScanImagesAsyncTask.execute();
    }

    public String[] getExternalStorageDirectories() {
        List<String> results = new ArrayList();
        if (Build.VERSION.SDK_INT >= 19) {
            File[] externalDirs = getExternalFilesDirs(null);
            if (externalDirs != null && externalDirs.length > 0) {
                for (File file : externalDirs) {
                    if (file != null) {
                        String[] paths = file.getPath().split("/Android");
                        if (paths != null && paths.length > 0) {
                            boolean addPath;
                            String path = paths[0];
                            if (Build.VERSION.SDK_INT >= 21) {
                                addPath = Environment.isExternalStorageRemovable(file);
                            } else {
                                addPath = "mounted".equals(EnvironmentCompat.getStorageState(file));
                            }
                            if (addPath) {
                                results.add(path);
                            }
                        }
                    }
                }
            }
        }

        if (results.isEmpty()) {
            String output = "";
            InputStream is = null;
            try {
                Process process = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                process.waitFor();
                is = process.getInputStream();
                byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (Exception e) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {

                    }
                }

            }
            if (!output.trim().isEmpty()) {
                String[] devicePoints = output.split(IOUtils.LINE_SEPARATOR_UNIX);
                if (devicePoints.length > 0) {
                    for (String voldPoint : devicePoints) {
                        results.add(voldPoint.split(" ")[2]);
                    }
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            storageDirectories[i] = results.get(i);
        }
        return storageDirectories;
    }

    public void checkFileOfDirectory(File[] fileArr) {
        if (fileArr != null) {
            for (int i = 0; i < fileArr.length; i++) {
                if (fileArr[i].isDirectory()) {
                    checkFileOfDirectory(Utils.getFileList(fileArr[i].getPath()));
                } else {
                    detectFileTypeAndAddInCategory(fileArr[i]);

                }
            }
        }


    }

    private HashMap<Long, ArrayList<File>> findExactDuplicates(ArrayList<File> files, IProgressListener progressListener) {
        HashMap<Long, ArrayList<File>> exactDuplicates = new HashMap();
        if (files != null) {
            HashMap<Long, ArrayList<File>> duplicatesBySize = findDuplicatesBySize(files);
            ArrayList<Long> keys = new ArrayList(duplicatesBySize.keySet());
            for (int i = 0; i < keys.size(); i++) {
                ArrayList<File> sameLengthFiles = duplicatesBySize.get(keys.get(i));
                int countOfSameLengthFiles = sameLengthFiles.size();
                int j = 0;

                while (j < countOfSameLengthFiles) {

                    int k = 0;

                    while (k < countOfSameLengthFiles) {

                        if (j != k && j < countOfSameLengthFiles && k < countOfSameLengthFiles) {

                            try {
                                if (contentEquals(sameLengthFiles.get(j), sameLengthFiles.get(k))) {

                                    File candidateFile = sameLengthFiles.get(j);
                                    ArrayList<File> candidateFiles;

                                    if (exactDuplicates.containsKey(Long.valueOf(candidateFile.length()))) {

                                        candidateFiles = exactDuplicates.get(Long.valueOf(candidateFile.length()));

                                        if (!candidateFiles.contains(candidateFile)) {
                                            candidateFiles.add(candidateFile);
                                        }

                                    } else {

                                        candidateFiles = new ArrayList();
                                        candidateFiles.add(candidateFile);
                                        exactDuplicates.put(Long.valueOf(candidateFile.length()), candidateFiles);
                                        progressListener.onProgress(candidateFile);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                        k++;
                    }
                    j++;
                }
            }
        }
        return exactDuplicates;
    }

    interface IProgressListener {
        void onProgress(File file);
    }

    private HashMap<Long, ArrayList<File>> findDuplicatesBySize(ArrayList<File> files) {
        HashMap<Long, ArrayList<File>> duplicatesBySize = new HashMap();
        Iterator it = files.iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            long length = file.length();
            if (duplicatesBySize.containsKey(Long.valueOf(length))) {
                duplicatesBySize.get(Long.valueOf(length)).add(file);
            } else {
                ArrayList<File> candidateFiles = new ArrayList();
                candidateFiles.add(file);
                duplicatesBySize.put(Long.valueOf(length), candidateFiles);
            }
        }
        ArrayList<Long> keys = new ArrayList(duplicatesBySize.keySet());
        for (int i = 0; i < keys.size(); i++) {
            try {
                if (duplicatesBySize.get(keys.get(i)).size() == 1) {
                    duplicatesBySize.remove(keys.get(i));

                }
            } catch (Exception e) {

            }
        }
        return duplicatesBySize;
    }

    private boolean contentEquals(File file1, File file2) throws IOException {
        if (!file1.exists()) {
            return false;
        }
        if (!file2.exists()) {
            return false;
        }
        if (file1.length() != file2.length()) {
            return false;
        }
        if (file1.length() <= 3000) {
            try {
                return FileUtils.contentEquals(file1, file2);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            if (file1.exists()) {
                InputStream input1 = new FileInputStream(file1);
                InputStream input2 = new FileInputStream(file2);

                try {
                    byte[] startBufferFile1 = new byte[512];
                    IOUtils.read(input1, startBufferFile1, 0, 512);
                    String str = new String(startBufferFile1);
                    IOUtils.skip(input1, (file1.length() / 2) - 256);
                    byte[] midBufferFile1 = new byte[512];
                    IOUtils.read(input1, midBufferFile1, 0, 512);
                    String midTextFile1 = new String(midBufferFile1);
                    IOUtils.skip(input1, file1.length() - 512);
                    byte[] endBufferFile1 = new byte[512];
                    IOUtils.read(input1, endBufferFile1, 0, 512);
                    String endTextFile1 = new String(endBufferFile1);
                    byte[] startBufferFile2 = new byte[512];
                    IOUtils.read(input2, startBufferFile2, 0, 512);
                    str = new String(startBufferFile2);
                    IOUtils.skip(input2, (file2.length() / 2) - 256);
                    byte[] midBufferFile2 = new byte[512];
                    IOUtils.read(input2, midBufferFile2, 0, 512);
                    str = new String(midBufferFile2);
                    IOUtils.skip(input2, file2.length() - 512);
                    byte[] endBufferFile2 = new byte[512];
                    IOUtils.read(input2, endBufferFile2, 0, 512);
                    String endTextFile2 = new String(endBufferFile2);
                    if (str.equals(str) && midTextFile1.equals(str) && endTextFile1.equals(endTextFile2)) {

                        return true;
                    }

                    input1.close();
                    input2.close();
                    input1.close();
                    input2.close();
                    return false;
                } catch (IOException e2) {

                } finally {
                    input1.close();
                    input2.close();
                }
            }
        } catch (FileNotFoundException e3) {

        }
        return false;
    }

    public HashMap<String, ArrayList<File>> getMediaGroup(int typeGroup) {
        switch (typeGroup) {
            case Cont.IMAGE:
                mListImage.put(getString(R.string.jpg), (ArrayList) requiredContent.get(getString(R.string.jpg)));
                mListImage.put(getString(R.string.jpeg), (ArrayList) requiredContent.get(getString(R.string.jpeg)));
                mListImage.put(getString(R.string.png), (ArrayList) requiredContent.get(getString(R.string.png)));
                mListImage.put(getString(R.string.bmp), (ArrayList) requiredContent.get(getString(R.string.bmp)));
                mListImage.put(getString(R.string.gif), (ArrayList) requiredContent.get(getString(R.string.gif)));
                return mListImage;

            case Cont.AUDIO:
                mListAudio.put(getString(R.string.mp3), (ArrayList) requiredContent.get(getString(R.string.mp3)));
                mListAudio.put(getString(R.string.aac), (ArrayList) requiredContent.get(getString(R.string.aac)));
                mListAudio.put(getString(R.string.amr), (ArrayList) requiredContent.get(getString(R.string.amr)));
                mListAudio.put(getString(R.string.m4a), (ArrayList) requiredContent.get(getString(R.string.m4a)));
                mListAudio.put(getString(R.string.ogg), (ArrayList) requiredContent.get(getString(R.string.ogg)));
                mListAudio.put(getString(R.string.wav), (ArrayList) requiredContent.get(getString(R.string.wav)));
                mListAudio.put(getString(R.string.flac), (ArrayList) requiredContent.get(getString(R.string.flac)));
                return mListAudio;
            case Cont.DOCCUNMENT:
                mListDoc.put(getString(R.string.doc), (ArrayList) requiredContent.get(getString(R.string.doc)));
                mListDoc.put(getString(R.string.docx), (ArrayList) requiredContent.get(getString(R.string.docx)));
                mListDoc.put(getString(R.string.html), (ArrayList) requiredContent.get(getString(R.string.html)));
                mListDoc.put(getString(R.string.pdf), (ArrayList) requiredContent.get(getString(R.string.pdf)));
                mListDoc.put(getString(R.string.txt), (ArrayList) requiredContent.get(getString(R.string.txt)));
                mListDoc.put(getString(R.string.xml), (ArrayList) requiredContent.get(getString(R.string.xml)));
                mListDoc.put(getString(R.string.xlsx), (ArrayList) requiredContent.get(getString(R.string.xlsx)));
                return mListDoc;
            case Cont.OTHERFILE:
                mListOtherFile.put(getString(R.string.zip), (ArrayList) requiredContent.get(getString(R.string.zip)));
                mListOtherFile.put(getString(R.string.apk), (ArrayList) requiredContent.get(getString(R.string.apk)));
                mListOtherFile.put(getString(R.string.vcf), (ArrayList) requiredContent.get(getString(R.string.vcf)));

                mListOtherFile.put(getString(R.string.js), (ArrayList) requiredContent.get(getString(R.string.js)));
                mListOtherFile.put(getString(R.string.css), (ArrayList) requiredContent.get(getString(R.string.css)));
                mListOtherFile.put(getString(R.string.dat), (ArrayList) requiredContent.get(getString(R.string.dat)));
                mListOtherFile.put(getString(R.string.cache), (ArrayList) requiredContent.get(getString(R.string.cache)));
                return mListOtherFile;
            case Cont.VIDEO:
                mListVideo.put(getString(R.string._3gp), (ArrayList) requiredContent.get(getString(R.string._3gp)));
                mListVideo.put(getString(R.string.mp4), (ArrayList) requiredContent.get(getString(R.string.mp4)));
                mListVideo.put(getString(R.string.mkv), (ArrayList) requiredContent.get(getString(R.string.mkv)));
                mListVideo.put(getString(R.string.webm), (ArrayList) requiredContent.get(getString(R.string.webm)));
                return mListVideo;
            default:
                return requiredContent;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        File fileDirectory = new File(Config.IMAGE_RECOVER_DIRECTORY);
                        if (!fileDirectory.exists()) {
                            fileDirectory.mkdirs();
                        }
                    } else {
                        Toast.makeText(DeepScanActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }


    private void checkPermission() {
        arrPermission = new ArrayList();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Utils.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                arrPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!Utils.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                arrPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!arrPermission.isEmpty()) {
                requestPermissions(arrPermission.toArray(new String[0]), REQUEST_PERMISSIONS);
            }
        }
    }

    public void getSdCard() {

        String[] externalStoragePaths = getExternalStorageDirectories();

        if (externalStoragePaths != null && externalStoragePaths.length > 0) {

            for (String path : externalStoragePaths) {
                File file = new File(path);
                if (file.exists()) {
                    File[] subFiles = file.listFiles();
                    checkFileOfDirectory(subFiles);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mScanImagesAsyncTask = null;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isCanceled = true;
        if (mScanImagesAsyncTask != null) {
            mScanImagesAsyncTask.cancel(true);
            Log.e("RRR", "OnCancel asynctask ");
        }
        Log.e("RRR", "onStop: " + isCanceled);
        finish();
    }

    public class ScanImagesAsyncTask extends AsyncTask<String, File, ArrayList<DataModel>> {

        int typeScan = 0;

        public ScanImagesAsyncTask(int type) {
            typeScan = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<DataModel> model_images) {
            super.onPostExecute(model_images);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanStep++;
                    scanAll();
                }
            }, 1000);

        }

        @Override
        protected void onProgressUpdate(File... values) {
            super.onProgressUpdate(values);
            if (values.length > 0) {
                String[] list = values[0].getAbsolutePath().split("/");
                tvProgress.setText(list[list.length - 1]);
            }
        }

        @Override
        protected ArrayList<DataModel> doInBackground(String... strings) {

            if (isCancelled()) return null;

            String strArr;
            strArr = Environment.getExternalStorageDirectory().getAbsolutePath();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("root = ");
            stringBuilder.append(strArr);
            collectRequiredFilesContent();

            checkFileOfDirectory(Utils.getFileList(strArr));
            getSdCard();
            HashMap<String, ArrayList<File>> duplicateResult = getMediaGroup(typeScan);
            ArrayList<String> keys = new ArrayList(duplicateResult.keySet());
            int group = 1;
            for (int i = 0; i < keys.size(); i++) {

                publishProgress();

                ArrayList<File> mListFile = duplicateResult.get(keys.get(i));

                HashMap<Long, ArrayList<File>> extractDouble = findExactDuplicates(mListFile, file -> {
                    publishProgress(file);
                });


                if (extractDouble != null && extractDouble.size() > 0) {

                    Iterator it = new ArrayList(extractDouble.keySet()).iterator();

                    while (it.hasNext()) {
                        DataModel mDataModel = new DataModel();
                        ArrayList<File> files = extractDouble.get(it.next());
                        if (files != null && files.size() > 0) {
                            mDataModel.setTitleGroup("Group: " + Integer.valueOf(group));
                            ArrayList<Duplicate> list = new ArrayList();
                            for (int j = 0; j < files.size(); j++) {

                                Duplicate duplicate = new Duplicate();
                                duplicate.setFile(files.get(j));
                                duplicate.setTypeFile(TypeFile.getType(files.get(j).getPath()));
                                if (j == 0) {
                                    duplicate.setChecked(false);
                                }
                                list.add(duplicate);
                            }
                            mDataModel.setListDuplicate(list);
                            group++;
                        }
                        mListData.add(mDataModel);
                    }


                }


            }


            return null;
        }


    }
}
