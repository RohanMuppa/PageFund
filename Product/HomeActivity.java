package com.example.app1;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements FetchBook.AsyncResponse {
    private LinearLayout booksLayout;
    private Button depositeRedirectText;
    private Button portfolioRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        booksLayout = findViewById(R.id.booksLayout);

        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=HarryPotter&maxResults=4";
        new FetchBook(this).execute(apiUrl);

        depositeRedirectText = findViewById(R.id.deposit_money_button);
        portfolioRedirectText = findViewById(R.id.go_to_portfolio_button);

        depositeRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddDepositFragment.class));
            }
        });

        portfolioRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PortfolioActivity.class));
            }
        });
    }
    @Override
    public void processFinish(List<Book> books) {
        LinearLayout booksLayout = findViewById(R.id.booksLayout);
        for (Book book : books) {

            // Create a new layout for each book entry
            LinearLayout bookEntryLayout = new LinearLayout(this);
            bookEntryLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textView = new TextView(this);
            textView.setText(book.getTitle() + " - $" + book.getPrice());
            textView.setTextSize(16);

            Button buyButton = new Button(this);
            buyButton.getBackground().setColorFilter(0xFFc2beea, PorterDuff.Mode.MULTIPLY);
            buyButton.setText("Buy " + book.getTitle());

            bookEntryLayout.addView(textView);
            bookEntryLayout.addView(buyButton);

            booksLayout.addView(bookEntryLayout);

            buyButton.setOnClickListener(v -> {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userRef = db.collection("users").document(userId);

                db.runTransaction((Transaction.Function<Void>) transaction -> {

                    DocumentSnapshot snapshot = transaction.get(userRef);
                    Double eCurrency = snapshot.getDouble("eCurrency");
                    if (eCurrency == null) {
                        throw new FirebaseFirestoreException("eCurrency field is missing.",
                                FirebaseFirestoreException.Code.ABORTED);
                    }

                    double price = book.getPrice();
                    if (eCurrency >= price) {

                        transaction.update(userRef, "eCurrency", eCurrency - price);

                        Map<String, Object> currentInvestments = (Map<String, Object>) snapshot.get("investments");
                        if (currentInvestments == null) {
                            currentInvestments = new HashMap<>();
                        }

                        currentInvestments.put(book.getTitle(), price);

                        transaction.update(userRef, "investments", currentInvestments);
                    }
                    else {
                        throw new FirebaseFirestoreException("Not enough funds.",
                                FirebaseFirestoreException.Code.ABORTED);
                    }
                    // Transaction success handling
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Successfully bought " + book.getTitle(), Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    if (e instanceof FirebaseFirestoreException) {
                        FirebaseFirestoreException fe = (FirebaseFirestoreException) e;
                        if (fe.getCode() == FirebaseFirestoreException.Code.ABORTED) {
                            Toast.makeText(HomeActivity.this, "Not enough funds to make the purchase.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Error purchasing investment. Try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Error purchasing investment. Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }
    }
}