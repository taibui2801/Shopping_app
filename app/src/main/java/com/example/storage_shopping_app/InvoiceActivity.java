package com.example.storage_shopping_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_shopping_app.adapter.OrderDetailAdapter;
import com.example.storage_shopping_app.database.AppDatabase;
import com.example.storage_shopping_app.entity.Order;
import com.example.storage_shopping_app.entity.OrderDetail;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private TextView tvInvoiceId, tvInvoiceDate, tvInvoiceTotal;
    private RecyclerView rvInvoiceDetails;
    private Button btnDone;
    private AppDatabase db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        db = AppDatabase.getInstance(this);
        orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId == -1) {
            finish();
            return;
        }

        initViews();
        displayInvoice();
    }

    private void initViews() {
        tvInvoiceId = findViewById(R.id.tvInvoiceId);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        rvInvoiceDetails = findViewById(R.id.rvInvoiceDetails);
        btnDone = findViewById(R.id.btnDone);

        rvInvoiceDetails.setLayoutManager(new LinearLayoutManager(this));
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayInvoice() {
        Order order = db.orderDao().getOrderById(orderId);
        if (order != null) {
            tvInvoiceId.setText("Order ID: #" + order.orderId);
            tvInvoiceDate.setText("Date: " + order.orderDate);
            tvInvoiceTotal.setText(String.format("$%.2f", order.totalAmount));

            List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrder(orderId);
            OrderDetailAdapter adapter = new OrderDetailAdapter(details, db);
            rvInvoiceDetails.setAdapter(adapter);
        }
    }
}
