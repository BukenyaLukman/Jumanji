package com.example.jumanji;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.jumanji.Model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addToCartBtn,callSellerBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private String productID = "",state = "Normal";
    private TextView productPrice,productDescription,productName,productLocation;
    private FirebaseUser firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        productID = getIntent().getStringExtra("pid");



        addToCartBtn =  findViewById(R.id.add_product_to_cart);
        callSellerBtn =  findViewById(R.id.button_addc);
        numberButton =  findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_details);
        productPrice =  findViewById(R.id.product_price_details);
        productDescription =  findViewById(R.id.product_description_details);
        productName = findViewById(R.id.product_name_details);
        productLocation = findViewById(R.id.product_location_details);


        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("Order Placed") || state.equals("Order Delivered")){
                    Toast.makeText(ProductDetailsActivity.this, "You can purchase more products once order verified ", Toast.LENGTH_LONG).show();

                }else{
                   addingToCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void getProductDetails(String productID) {
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MyShop").child("Products");
            productRef.keepSynced(true);
            productRef.child(productID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final Products products = dataSnapshot.getValue(Products.class);
                        productName.setText(products.getPname());
                        productPrice.setText(products.getPrice());
                        productLocation.setText(products.getLocation());
                        productDescription.setText(products.getDescription());

                        Picasso.get().load(products.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(productImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(products.getImage()).into(productImage);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    private void addingToCartList(){
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        cartListRef.keepSynced(true);

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
        cartMap.put("location",productLocation.getText().toString());

        cartListRef.child("User View").child(firebaseAuth.getUid()).child("Products")
                .child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("MyBuyers View").child(firebaseAuth.getUid()).child("Products")
                                    .child(productID).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               Toast.makeText(ProductDetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                               Intent intent = new Intent(ProductDetailsActivity.this,MainActivity.class);
                                               startActivity(intent);
                                           }
                                        }
                                    });
                        }
                    }
                });


    }


    private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(firebaseAuth.getUid());
        ordersRef.keepSynced(true);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String deliverystate = dataSnapshot.child("State").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    if(deliverystate.equals("Delivered")){
                        state = "Order Delivered";
                    }else if(deliverystate.equals("not Delivered")){
                        state = "Order Placed";
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
