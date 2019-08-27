package com.example.jumanji;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jumanji.Model.Cart;
import com.example.jumanji.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount,txtMsg1;

    private int overTotalPrice = 0;

    private FirebaseUser firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button)findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView)findViewById(R.id.total_price);
        txtMsg1 = (TextView)findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtTotalAmount.setText(String.valueOf("Total Price:UGX"+overTotalPrice));
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(firebaseAuth.getUid()).child("Products"),Cart.class).build();



        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                      holder.txtProductQuantity.setText(model.getQuantity());
                      holder.txtProductPrice.setText("Price UGX"+ model.getPrice());
                      holder.txtProductName.setText(model.getPname());

                      int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                      overTotalPrice = overTotalPrice + oneTypeProductTPrice;


                      holder.itemView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              CharSequence options[] = new CharSequence[]
                                      {
                                              "Edit",
                                              "Remove"
                                      };
                              AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                              builder.setTitle("Cart Options");

                              builder.setItems(options, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int i) {
                                      if(i == 0){
                                          Intent intent =  new Intent(CartActivity.this,ProductDetailsActivity.class);
                                          intent.putExtra("pid",model.getPid());
                                          startActivity(intent);
                                      }
                                      if(i == 1){
                                          cartListRef.child("User View")
                                                  .child(firebaseAuth.getUid())
                                                  .child("Products")
                                                  .child(model.getPid())
                                                  .removeValue()
                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {
                                                         if (task.isSuccessful()){
                                                             Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                                                             Intent intent =  new Intent(CartActivity.this,CartActivity.class);
                                                             startActivity(intent);
                                                         }
                                                      }
                                                  });
                                      }
                                  }
                              });
                              builder.show();
                          }
                      });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(firebaseAuth.getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String deliverystate = dataSnapshot.child("State").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    if(deliverystate.equals("Delivered")){
                         txtTotalAmount.setText("Dear " + username + "\n order is in delivery");
                         recyclerView.setVisibility(View.GONE);

                         txtMsg1.setVisibility(View.VISIBLE);
                         txtMsg1.setText("Congratulations,your final order is in delivery...");
                         NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products once you receive your last order", Toast.LENGTH_SHORT).show();

                    }else if(deliverystate.equals("not Delivered")){
                        txtTotalAmount.setText("Delivery State - Not Delivered");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products once you receive your last order", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
