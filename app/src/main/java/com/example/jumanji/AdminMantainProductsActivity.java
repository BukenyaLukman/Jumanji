package com.example.jumanji;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMantainProductsActivity extends AppCompatActivity {

    private Button ApplyChangesBtn,deleteBtn;
    private EditText name,price,description,location;
    private ImageView imageView;

    private String productID = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mantain_products);

        productID = getIntent().getStringExtra("pid");

        productRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MyShop").child("Products").child(productID);

        ApplyChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        location = findViewById(R.id.product_location_maintain);
        imageView = findViewById(R.id.product_image_maintain);
        deleteBtn = findViewById(R.id.delete_product_btn);


        displaySpecificInfo();

        ApplyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });
    }

    private void ApplyChanges() {
        String PName = name.getText().toString();
        String PPrice = price.getText().toString();
        String PDescription = description.getText().toString();
        String PLocation = location.getText().toString();

        if (PName.equals("")){
            Toast.makeText(this, "Please write Product Name", Toast.LENGTH_SHORT).show();
        }else if (PPrice.equals("")){
            Toast.makeText(this, "Please write down Product Price", Toast.LENGTH_SHORT).show();
        }else if(PDescription.equals("")){
            Toast.makeText(this, "Please write Product Description", Toast.LENGTH_SHORT).show();
        }else if (PLocation.equals("")){
            Toast.makeText(this, "Please write Product Location", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",PDescription);
            productMap.put("price",PPrice);
            productMap.put("pname",PName);
            productMap.put("Location",PLocation);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMantainProductsActivity.this, "Changes applied!", Toast.LENGTH_SHORT).show();
                        SendAdminToCategoryActivity();
                    }
                }
            });

        }

    }

    private void deleteProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminMantainProductsActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminMantainProductsActivity.this, "Product deleted Successfully...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendAdminToCategoryActivity() {
        Intent intent = new Intent(AdminMantainProductsActivity.this,AdminCategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void displaySpecificInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String Pname = dataSnapshot.child("pname").getValue().toString();
                    String Pprice = dataSnapshot.child("price").getValue().toString();
                    String Pdescription = dataSnapshot.child("description").getValue().toString();
                    String Plocation = dataSnapshot.child("Location").getValue().toString();
                    final String Pimage = dataSnapshot.child("image").getValue().toString();


                    name.setText(Pname);
                    price.setText(Pprice);
                    description.setText(Pdescription);
                    location.setText(Plocation);


                    Picasso.get().load(Pimage).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(Pimage).into(imageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
