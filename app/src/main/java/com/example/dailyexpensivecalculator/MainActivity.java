package com.example.dailyexpensivecalculator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView tvSign;
    public static TextView tvEmpty, tvBalance;
    EditText etAmount, etMessage;
    ImageView ivSend;
    boolean positive = true;
    RecyclerView rvTransactions;
    TransactionAdapter adapter;
    ArrayList<TransactionClass> transactionList;

    // On create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Function to initialize views
        initViews();

        // Function to load data from shared preferences
        loadData();

        // Function to set custom action bar
        setCustomActionBar();

        // To check if there is no transaction
        checkIfEmpty(transactionList.size());

        // Initializing recycler view
        rvTransactions.setHasFixedSize(true);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this,transactionList);
        rvTransactions.setAdapter(adapter);

        // On click sign change
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSign();
            }
        });

        // On click Send
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Input Validation
                if(etAmount.getText().toString().trim().isEmpty())
                {
                    etAmount.setError("Enter Amount!");
                    return;
                }
                if(etMessage.getText().toString().isEmpty())
                {
                    etMessage.setError("Enter a message!");
                    return;
                }
                try {
                    int amt = Integer.parseInt(etAmount.getText().toString().trim());

                    // Adding Transaction to recycler View
                    sendTransaction(amt,etMessage.getText().toString().trim(),positive);
                    checkIfEmpty(transactionList.size());

                    // To update Balance
                    setBalance(transactionList);
                    etAmount.setText("");
                    etMessage.setText("");
                }
                catch (Exception e){
                    etAmount.setError("Amount should be integer greater than zero!");
                }
            }
        });
    }

    // To set custom action bar
    private void setCustomActionBar() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View v = LayoutInflater.from(this).inflate(R.layout.custom_action_bar,null);

        // TextView to show Balance
        tvBalance = v.findViewById(R.id.tvBalance);

        // Setting balance
        setBalance(transactionList);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setElevation(0);
    }

    // To set Balance along with sign (spent(-) or received(+))
    public static void setBalance(ArrayList<TransactionClass> transactionList){
        int bal = calculateBalance(transactionList);
        if(bal<0)
        {
            tvBalance.setText("- ₹"+calculateBalance(transactionList)*-1);
        }
        else {
            tvBalance.setText("+ ₹"+calculateBalance(transactionList));
        }
    }

    // To load data from shared preference
    private void loadData() {
        SharedPreferences pref = getSharedPreferences("com.cs.ec",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("transactions",null);
        Type type = new TypeToken<ArrayList<TransactionClass>>(){}.getType();
        if(json!=null)
        {
            transactionList=gson.fromJson(json,type);
        }
    }

    // To add transaction
    private void sendTransaction(int amt,String msg, boolean positive) {
        transactionList.add(new TransactionClass(amt,msg,positive));
        adapter.notifyDataSetChanged();
        rvTransactions.smoothScrollToPosition(transactionList.size()-1);
    }

    // Function to change sign
    private void changeSign() {
        if(positive)
        {
            tvSign.setText("-₹");
            tvSign.setTextColor(Color.parseColor("#F44336"));
            positive = false;
        }
        else {
            tvSign.setText("+₹");
            tvSign.setTextColor(Color.parseColor("#00c853"));
            positive = true;
        }
    }

    // To check if transaction list is empty
    public static void checkIfEmpty(int size) {
        if (size == 0)
        {
            MainActivity.tvEmpty.setVisibility(View.VISIBLE);
        }
        else {
            MainActivity.tvEmpty.setVisibility(View.GONE);
        }
    }

    // To Calculate Balance by iterating through all transactions
    public static int calculateBalance(ArrayList<TransactionClass> transactionList)
    {
        int bal = 0;
        for(TransactionClass transaction : transactionList)
        {
            if(transaction.isPositive())
            {
                bal+=transaction.getAmount();
            }
            else {
                bal-=transaction.getAmount();
            }
        }
        return bal;
    }

    // Initializing Views
    private void initViews() {
        transactionList = new ArrayList<TransactionClass>();
        tvSign = findViewById(R.id.tvSign);
        rvTransactions = findViewById(R.id.rvTransactions);
        etAmount = findViewById(R.id.etAmount);
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    // Storing data locally
    // using shared preferences
    // in onStop() method
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("com.cs.ec",MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(transactionList);
        editor.putString("transactions",json);
        editor.apply();
    }
}
