package com.example.storage_shopping_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_shopping_app.adapter.OrderDetailAdapter;
import com.example.storage_shopping_app.database.AppDatabase;
import com.example.storage_shopping_app.entity.Order;
import com.example.storage_shopping_app.entity.OrderDetail;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvOrderDetails;
    private TextView tvTotalAmount;
    private Button btnContinueShopping, btnCheckout;
    private AppDatabase db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        db = AppDatabase.getInstance(this);
        orderId = getIntent().getIntExtra("orderId", -1);

        if (orderId == -1) {
            finish();
            return;
        }

        initViews();
        loadOrderDetails();
    }

    private void initViews() {
        rvOrderDetails = findViewById(R.id.rvOrderDetails);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvOrderDetails.setLayoutManager(new LinearLayoutManager(this));

        btnContinueShopping.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            Order order = db.orderDao().getOrderById(orderId);
            if (order != null) {
                order.status = "Paid";
                db.orderDao().update(order);
                Toast.makeText(this, "Order Paid Successfully!", Toast.LENGTH_SHORT).show();
                
                // Chuyển sang màn hình Hóa đơn (Invoice)
                Intent intent = new Intent(OrderActivity.this, InvoiceActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadOrderDetails() {
        Order order = db.orderDao().getOrderById(orderId);
        if (order != null) {
            tvTotalAmount.setText(String.format("Total: $%.2f", order.totalAmount));
            List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrder(orderId);
            OrderDetailAdapter adapter = new OrderDetailAdapter(details, db);
            rvOrderDetails.setAdapter(adapter);

            if ("Paid".equals(order.status)) {
                btnCheckout.setEnabled(false);
                btnContinueShopping.setText("Back to Home");
            }
        }
    }
}
