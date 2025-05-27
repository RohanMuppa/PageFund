package com.example.app1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//THIS IS FOR REGISTERING
public class SignUpFragment extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private FirebaseFirestore db;

    public String email;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupEmail = findViewById(R.id.signupEmailEditText);
        signupPassword = findViewById(R.id.signupPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.gotoSignInTextView);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = signupEmail.getText().toString().trim();
                final String pass = signupPassword.getText().toString().trim();

                setEmail(email);

                if (email.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                    return;
                }

                if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                    return;
                }
                // Create a user with Firebase Auth
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get Firebase user ID
                            String userId = auth.getCurrentUser().getUid();
                            setUserID(auth.getCurrentUser().getUid());

                            // Create a map to hold user data
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);

                            // Add a new document with a generated ID to Firestore
                            db.collection("users").document(userID)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "User added with ID: " + userId);
                                            Toast.makeText(SignUpFragment.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpFragment.this, SignInFragment.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpFragment.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpFragment.this, SignInFragment.class));
            }
        });
    }
    public void setUserID(String usersID){
        this.userID = usersID;
    }

    public void setEmail(String email1){
        this.email = email1;
    }

    public String getUserID(){
        return this.userID;
    }

    public String getEmail(){
        return this.userID;
    }
}