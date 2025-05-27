package com.example.app1;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PortfolioActivity extends AppCompatActivity {

    private static final String TAG = "PortfolioActivity";
    private LinearLayout investmentsLayout;
    private TextView totalInvestmentTextView;
    private Button goBackButton;
    TextView accountBalanceTextView;

    private Map<String, Double> updatedPrices = new HashMap<>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        investmentsLayout = findViewById(R.id.investmentsLayout);
        totalInvestmentTextView = findViewById(R.id.totalInvestmentTextView);
        goBackButton = findViewById(R.id.goBackButton);
        accountBalanceTextView = findViewById(R.id.accountBalanceTextView);


        String userId = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        updatePortfolioUI(docRef);
        goBackButton.setOnClickListener(view -> {
            startActivity(new Intent(PortfolioActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void fetchAccountBalance() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double balance = documentSnapshot.getDouble("eCurrency");
                if (balance != null) {
                    accountBalanceTextView.setText(String.format(Locale.getDefault(), "Account Balance: $%.2f", balance));
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching account balance", e));
    }

    private void updatePortfolioUI(DocumentReference docRef) {
        fetchAccountBalance();
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> investments = (Map<String, Object>) document.get("investments");
                    if (investments != null) {
                        double totalInvestment = 0;
                        investmentsLayout.removeAllViews();
                        for (Map.Entry<String, Object> entry : investments.entrySet()) {
                            String title = entry.getKey();
                            Double price = (Double) entry.getValue();

                            // Investment details TextView
                            TextView investmentView = new TextView(PortfolioActivity.this);
                            investmentView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            investmentView.setTextSize(16);
                            investmentView.setPadding(8, 8, 8, 8);
                            investmentView.setText(String.format(Locale.getDefault(), "%s: $%.2f", title, price));
                            investmentsLayout.addView(investmentView);

                            // Sell Button
                            Button sellButton = new Button(PortfolioActivity.this);
                            sellButton.getBackground().setColorFilter(0xFFc2beea, PorterDuff.Mode.MULTIPLY);
                            sellButton.setText(String.format(Locale.getDefault(), "Sell %s", title));
                            sellButton.setOnClickListener(view -> sellInvestment(title, price, docRef));
                            investmentsLayout.addView(sellButton);
                            fetchAccountBalance();

                            totalInvestment += price;
                        }
                        totalInvestmentTextView.setText(String.format(Locale.getDefault(), "Total Investment: $%.2f", totalInvestment));
                    } else {
                        totalInvestmentTextView.setText("Total Investment: $0.00");
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.w(TAG, "Error getting document: ", task.getException());
            }
        });
    }

    private void sellInvestment(String bookTitle, Double price, DocumentReference userRef) {
        Log.d(TAG, "Attempting to sell investment: " + bookTitle + " for $" + price);

        // Fetch the current balance before starting the transaction
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double currentBalance = documentSnapshot.getDouble("eCurrency");
                if (currentBalance != null) {
                    double updatedBalance = currentBalance + price;

                    db.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(userRef);
                        Map<String, Object> investments = (Map<String, Object>) snapshot.get("investments");
                        if (investments != null && investments.containsKey(bookTitle)) {
                            investments.remove(bookTitle); // Remove the investment
                            transaction.update(userRef, "investments", investments); // Update the user document
                            transaction.update(userRef, "eCurrency", updatedBalance); // Update the balance
                            Log.d(TAG, bookTitle + " sold successfully. Updated balance: $" + updatedBalance);
                        } else {
                            Log.d(TAG, "Investment not found or already sold.");
                        }
                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Investment sold and balance updated.");
                        fetchAccountBalance();
                        updatePortfolioUI(userRef);
                    }).addOnFailureListener(e -> Log.e(TAG, "Transaction failure: ", e));
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching account balance", e));
    }
}