package com.gawk.voicenotes.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.windows.OpenFileDialog;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by GAWK on 01.03.2017.
 */

public class ExportImportActivity extends ParentActivity implements View.OnClickListener{
    private Spinner spinnerSelectType;
    private EditText editTextFileName;
    private TextView textViewFullFile, textViewFileSelected;
    private Button buttonSelectFolder, buttonExport, buttonSelectFile, buttonImport;
    private String typeExport = "json", fileName, fullFileName;
    private String[] exportTypes;
    private File directoryFile, importFile;
    private boolean checkSelectFile = false;
    private OpenFileDialog openFileDialog, openDirectoryDialog;
    private View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import);
        mView = findViewById(R.id.export_import);

        openDirectoryDialog = new OpenFileDialog(this)
                .setFileSelectedColor(getColorByAttr(R.attr.primaryColor))
                .setFolderSelectable(true).setOnCloseListener(new OpenFileDialog.OnCloseListener(){
                    public void onCancel(){}

                    @Override
                    public void onOk(String selectedFile) {
                        directoryFile = new File(selectedFile);
                        checkSelectFile = true;
                        changeExport();
                    }
                });
        openFileDialog = new OpenFileDialog(this).setFolderSelectable(true)
                .setFileSelectedColor(getColorByAttr(R.attr.primaryColor))
                .setOnCloseListener(new OpenFileDialog.OnCloseListener(){
                    public void onCancel(){}

                    @Override
                    public void onOk(String selectedFile) {
                        if (selectedFile.length() > 5) {
                            String json = selectedFile.substring(selectedFile.length()-5,selectedFile.length());
                            if (json.equalsIgnoreCase("."+exportTypes[0])) {
                                importFile = new File(selectedFile);
                                buttonImport.setEnabled(true);
                                textViewFileSelected.setText(selectedFile);
                            } else {
                                buttonImport.setEnabled(false);
                                textViewFileSelected.setText(getString(R.string.export_import_select_no));
                            }
                        }
                    }
                });

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
            }
        });

        buttonSelectFolder = (Button) findViewById(R.id.buttonSelectFolder);
        buttonSelectFolder.setOnClickListener(this);

        textViewFullFile = (TextView) findViewById(R.id.textViewFullFile);
        buttonExport = (Button) findViewById(R.id.buttonExport);
        buttonExport.setEnabled(false);
        buttonExport.setOnClickListener(this);

        buttonSelectFile = (Button) findViewById(R.id.buttonSelectFile);
        buttonSelectFile.setOnClickListener(this);

        buttonImport = (Button) findViewById(R.id.buttonImport);
        buttonImport.setOnClickListener(this);
        buttonImport.setEnabled(false);

        textViewFileSelected = (TextView) findViewById(R.id.textViewFileSelected);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPermissions(this,2)) {
            Snackbar.make(mView, getString(R.string.main_permissions_error), Snackbar.LENGTH_LONG).show();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_import_export).setCheckable(true).setChecked(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelectFolder:
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
                Snackbar.make(getCurrentFocus(), getString(R.string.success), Snackbar.LENGTH_LONG).show();
                break;
            case R.id.buttonSelectFile:
                openFileDialog.show();
                break;
            case R.id.buttonImport:
                dbHelper.connection();
                if (dbHelper.importDB(importFile)) {
                    Snackbar.make(getCurrentFocus(), getString(R.string.success), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getCurrentFocus(), getString(R.string.export_import_error_import), Snackbar.LENGTH_LONG).show();
                }
                dbHelper.disconnection();
                break;
        }
    }

    private void changeExport() {
        if (checkSelectFile) {
            buttonExport.setEnabled(directoryFile.isDirectory());
            if (directoryFile.isDirectory()) {
                fullFileName = directoryFile.getPath()+"/"+fileName+"."+typeExport;
                textViewFullFile.setText(fullFileName);
            } else {
                Snackbar.make(getCurrentFocus(), getString(R.string.export_import_folder_error), Snackbar.LENGTH_LONG).show();
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
