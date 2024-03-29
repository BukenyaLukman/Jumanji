package com.example.jumanji;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount = "";
    private FirebaseUser firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Amount is UGX"+ totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.delivery_name);
        phoneEditText = (EditText) findViewById(R.id.delivery_phone_number);
        addressEditText = (EditText) findViewById(R.id.delivery_address);
        cityEditText = (EditText) findViewById(R.id.delivery_city);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please provide your full name..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please provide your phone number..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please provide your delivery address..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(this, "Please provide your city name..", Toast.LENGTH_SHORT).show();
        }else{
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(firebaseAuth.getUid());
        HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("City",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("State","not Delivered");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               FirebaseDatabase.getInstance().getReference().child("Cart List")
                       .child("User View")
                       .child(firebaseAuth.getUid())
                       .removeValue()
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(ConfirmFinalOrderActivity.this, "Your Final Order has been Placed successfully", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                                   finish();
                               }
                           }
                       });
            }
        });

    }
}
