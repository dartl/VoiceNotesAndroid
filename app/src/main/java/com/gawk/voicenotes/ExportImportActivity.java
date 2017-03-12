package com.gawk.voicenotes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gawk.voicenotes.adapters.OpenFileDialog;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.UUID;

/**
 * Created by GAWK on 01.03.2017.
 */

public class ExportImportActivity extends ParentActivity implements View.OnClickListener{
    private Spinner spinnerSelectType;
    private EditText editTextFileName;
    private TextView textViewFullFile;
    private Button buttonSelectFolder, buttonExport, buttonSelectFile, buttonImport;
    private String typeExport = "json", fileName, fullFileName;
    private String[] exportTypes;
    private File directoryFile;
    private boolean checkSelectFile = false;
    private OpenFileDialog openFileDialog, openDirectoryDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_import);
        getSupportActionBar().setTitle(getString(R.string.app_name_export_import));

        spinnerSelectType = (Spinner) findViewById(R.id.spinnerSelectType);
        exportTypes = getResources().getStringArray(R.array.export_import_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exportTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSelectType.setAdapter(adapter);
        spinnerSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeExport = exportTypes[position];
                changeExport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextFileName = (EditText) findViewById(R.id.editTextFileName);
        fileName = UUID.randomUUID().toString().substring(0,5)+"dump";
        editTextFileName.setText(fileName);
        editTextFileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fileName = s.toString();
                changeExport();
                Log.e("GAWK_ERR","fileName = "+fileName+"."+typeExport);
            }
        });

        buttonSelectFolder = (Button) findViewById(R.id.buttonSelectFolder);
        buttonSelectFolder.setOnClickListener(this);

        textViewFullFile = (TextView) findViewById(R.id.textViewFullFile);
        buttonExport = (Button) findViewById(R.id.buttonExport);
        buttonExport.setEnabled(false);
        buttonExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelectFolder:
                openDirectoryDialog = new OpenFileDialog(this).setOnCloseListener(new OpenFileDialog.OnCloseListener(){
                    public void onCancel(){}

                    @Override
                    public void onOk(String selectedFile) {
                        directoryFile = new File(selectedFile);
                        changeExport();
                        checkSelectFile = true;
                    }
                }).setFolderSelectable(true);
                openDirectoryDialog.show();
                break;
            case R.id.buttonExport:
                File fileExport = new File(fullFileName);
                try {
                    fileExport.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dbHelper.connection();
                dbHelper.exportDB(fileExport,typeExport,exportTypes);
                dbHelper.disconnection();
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.success), Toast.LENGTH_SHORT);
                toast.show();
        }
    }

    private void changeExport() {
        if (checkSelectFile) {
            buttonExport.setEnabled(directoryFile.isDirectory());
            if (directoryFile.isDirectory()) {
                fullFileName = directoryFile.getPath()+"/"+fileName+"."+typeExport;
                textViewFullFile.setText(fullFileName);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.export_import_folder_error), Toast.LENGTH_SHORT);
                toast.show();
                fullFileName = "";
                textViewFullFile.setText(getResources().getString(R.string.export_import_folder_not));
            }
        } else {
            buttonExport.setEnabled(false);
            fullFileName = "";
            textViewFullFile.setText(getResources().getString(R.string.export_import_folder_not));
        }

    }
}
