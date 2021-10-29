package com.example.tejasvedantham.pttmobile2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EditUserActivity extends AppCompatActivity {

    public EditText firstNameEdit;
    public EditText lastNameEdit;
    public TextView emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        firstNameEdit = (EditText) findViewById(R.id.userFirstNameEdit);
        lastNameEdit= (EditText) findViewById(R.id.userLastNameEdit);
        emailEdit = (TextView) findViewById(R.id.userEmailEdit);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String firstName = extras.getString("firstName");
            String lastName = extras.getString("lastName");
            String email = extras.getString("email");

            firstNameEdit.setText(firstName);
            lastNameEdit.setText(lastName);
            emailEdit.setText(email);
        }


    }

    public void submitEditUser(View view) {

        String newFirstName = firstNameEdit.getText().toString();
        String newLastName = lastNameEdit.getText().toString();

        //TODO: Submit edited user info here using the two variables above

        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);
    }

}
