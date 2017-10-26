package com.gawk.voicenotes.adapters.logs;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by GAWK on 12.08.2017.
 */

public class CustomLogger {
    private File mFile;
    private File mFolder;
    private FileOutputStream mFileOutputStream;
    private PrintStream mPrintStream;

    public CustomLogger() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceNotes/";
        mFolder = new File(path);
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }
        mFile = new File(mFolder,"log.txt");
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\r\n");
            }
            mFileOutputStream = new FileOutputStream(mFile);
            mPrintStream = new PrintStream(mFileOutputStream);
            mPrintStream.append(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String str) {
        mPrintStream.append(str).append("\r\n");
    }

    public void write(Exception e) {
        File errorFile = new File(mFolder,"log_error"+System.currentTimeMillis()+".txt");
        try {
            FileOutputStream errorFileOutputStream = new FileOutputStream(errorFile);
            PrintStream ps = new PrintStream(errorFileOutputStream);
            e.printStackTrace(ps);
            ps.close();
            errorFileOutputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
