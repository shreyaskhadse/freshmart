package com.blogspot.sarthak.freshmart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.grocery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VerifyEmail extends AppCompatActivity {

    private ImageButton backBtn;
    private FirebaseAuth firebaseAuth;
    private EditText email_ver;
    private TextView status_ver;
    FirebaseUser currentUser;
    String email,role;
    private Button verify_send, verify_confirm, proceed_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        verify_send=(Button)findViewById(R.id.button_verify_email);
        verify_confirm=(Button)findViewById(R.id.button_verify_confirm);
        proceed_user=(Button)findViewById(R.id.button_proceed_user);
        email_ver=(EditText)findViewById(R.id.editTextVerifyEmail);
        status_ver=(TextView)findViewById(R.id.textView_verification_status);

        //accountType=getIntent().getStringExtra("accountType");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            role = ""+ds.child("role").getValue();
                            email = ""+ds.child("email").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        email=getIntent().getStringExtra("email");

        email_ver.setText(email);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        verify_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_email_verification();
            }
        });

        verify_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmVerification();
            }
        });

        proceed_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProceedUser();
            }
        });
    }

    private void ProceedUser() {
        if(currentUser.isEmailVerified())
        {
            if(role.equals("User"))
            {
                Intent intent=new Intent(VerifyEmail.this,MainUserActivity.class);
                startActivity(intent);
                finish();
            }
            else if(role.equals("Retailer"))
            {
                Intent intent=new Intent(VerifyEmail.this,MainSellerActivity.class);
                startActivity(intent);
                finish();
            }
            else if(role.equals("Wholesaler")){
                Intent intent=new Intent(VerifyEmail.this,MainWholesalerActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(VerifyEmail.this,"Invalid Account Type", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                Intent intent=new Intent(VerifyEmail.this,SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            Toast.makeText(VerifyEmail.this,"Verify your email to proceed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void confirmVerification() {
        currentUser.reload();
        if(currentUser.isEmailVerified())
        {
            Toast.makeText(VerifyEmail.this,"Your Email is now Verified", Toast.LENGTH_SHORT).show();
            status_ver.setText("Email Verified");
            status_ver.setTextColor(Color.rgb(35,144,35));
        }
        else
        {
            Toast.makeText(VerifyEmail.this,"Your Email is still not Verified", Toast.LENGTH_SHORT).show();
            status_ver.setText("Email still not verified");
            status_ver.setTextColor(Color.rgb(226,213,36));
        }
    }

    private void send_email_verification() {
        currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VerifyEmail.this,"Verification Email has been sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag","onFailure:Email on sent:" + e.getMessage());
            }
        });
    }
}