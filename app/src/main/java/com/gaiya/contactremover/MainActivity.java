package com.gaiya.contactremover;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button delete;
    EditText start1;
    EditText start2;
    EditText end1;
    EditText end2;
    EditText country;

    int PERMISSION_READ_WRITE_CONTACTS = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        init();

        country.setText("+94");

        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                deleteInRange();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteInRange() {

        if(start1.getText().equals("") || start2.getText().equals("") || end1.getText().equals("") || end2.getText().equals("")){
            Toast.makeText(this,"Fill Everything",Toast.LENGTH_SHORT).show();
        }

        String start11 = String.valueOf(start1.getText()).replace("\n", "");
        String start22 = String.valueOf(start2.getText()).replace("\n", "");

        String end11 = String.valueOf(end1.getText()).replace("\n", "");
        String end22 = String.valueOf(end2.getText()).replace("\n", "");

        start1.setText("");
        start2.setText("0000");

        end1.setText("");
        end2.setText("0000");

        int starting_number;
        int ending_number;
        try {
            starting_number = Integer.parseInt(start11 + start22);
            ending_number = Integer.parseInt(end11 + end22);
        }catch (Exception e){
            Toast.makeText(this,String.valueOf(e),Toast.LENGTH_LONG).show();
            return;
        }

        int dif = ending_number - starting_number;
        int i=0;
        for (int number = starting_number; number <= ending_number; number++,i++) {
            if(i%100==0){
                Toast.makeText(this,"100 Contacts\n\tDeleted",Toast.LENGTH_SHORT).show();
            }
            String build = String.valueOf(country.getText()) + number;
            Deleter.deleteContact(getContentResolver(),build);
        }
        Toast.makeText(this,"Finished deleted in Range",Toast.LENGTH_SHORT).show();
    }



    private void init() {
        delete = findViewById(R.id.deletebtn);
        start1 = findViewById(R.id.start1);
        start2 = findViewById(R.id.start2);
        end1 = findViewById(R.id.end1);
        end2 = findViewById(R.id.end2);
        country = findViewById(R.id.country);
    }

    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
            }, PERMISSION_READ_WRITE_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Accept Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}