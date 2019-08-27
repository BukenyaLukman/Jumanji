package com.example.jumanji;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jumanji.Model.Users;
import com.example.jumanji.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class UserDetailsActivity extends AppCompatActivity {

    private EditText inputFirstName,inputLastName,inputPhoneNumber;
    private Button registerUserDetailsButtons;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        firebaseAuth = FirebaseAuth.getInstance();

        inputFirstName = (EditText) findViewById(R.id.register_firstname);
        inputLastName = (EditText) findViewById(R.id.register_lastname);
        inputPhoneNumber = (EditText) findViewById(R.id.register_user_phone_number);
        registerUserDetailsButtons = (Button) findViewById(R.id.register_user_details);
        loadingBar = new ProgressDialog(this);



        registerUserDetailsButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUserDetails();
            }
        });

    }

    private void RegisterUserDetails() {
        String firstname = inputFirstName.getText().toString();
        String lastname = inputLastName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();

        if(TextUtils.isEmpty(firstname)){
            Toast.makeText(this, "Please provide your first name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(lastname)){
            Toast.makeText(this, "Please provide your last name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please provide your user phone number", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Registering User");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUserDetails(firstname,lastname,phone);
        }
    }

    private void ValidateUserDetails(final String firstname, final String lastname, final String phone) {



        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child(firebaseAuth.getUid()).exists())){

                    HashMap<String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("Phone",phone);
                    userDataMap.put("FirstName",firstname);
                    userDataMap.put("LastName",lastname);
                    RootRef.child("Users").child(firebaseAuth.getUid()).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserDetailsActivity.this, "Congratulations, your details have been taken", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(UserDetailsActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else{
                                        loadingBar.dismiss();
                                        Toast.makeText(UserDetailsActivity.this, "Network Error. Please ensure that you have connection", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }else{
                    Toast.makeText(UserDetailsActivity.this, "The Phone number " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(UserDetailsActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
