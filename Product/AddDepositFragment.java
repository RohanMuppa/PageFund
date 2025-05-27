package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddDepositFragment extends AppCompatActivity {

    private Button depositButton;
    private EditText depositText;

    private Button goBackButton;
    int money;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_deposit);

        depositButton = findViewById(R.id.deposit_button);
        depositText = findViewById(R.id.deposit_amount);
        goBackButton = findViewById(R.id.go_back_button);

        depositButton.setOnClickListener(view -> {
            String depositAmountStr = depositText.getText().toString();
            if (!depositAmountStr.isEmpty()) {
                double depositAmount = Double.parseDouble(depositAmountStr);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(userId);

                userRef.update("eCurrency", FieldValue.increment(depositAmount))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddDepositFragment.this, "Deposit added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddDepositFragment.this, "Error adding deposit", Toast.LENGTH_SHORT).show());
            }
        });

        goBackButton.setOnClickListener(view -> {
            startActivity(new Intent(AddDepositFragment.this, HomeActivity.class));
            finish();
        });
    };
}
