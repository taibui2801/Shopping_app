package com.example.storage_shopping_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_shopping_app.adapter.CategoryAdapter;
import com.example.storage_shopping_app.adapter.ProductAdapter;
import com.example.storage_shopping_app.database.AppDatabase;
import com.example.storage_shopping_app.entity.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvCategories, rvProducts;
    private TextView tvWelcome;
    private Button btnLoginMain, btnLogout;
    private AppDatabase db;
    private SessionManager sessionManager;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupDummyData();
        loadCategories();
        loadProducts();
        updateUI();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLoginMain = findViewById(R.id.btnLoginMain);
        btnLogout = findViewById(R.id.btnLogout);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        btnLoginMain.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            updateUI();
        });
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            User user = db.userDao().getUserById(sessionManager.getUserId());
            tvWelcome.setText("Hello, " + (user != null ? user.fullName : "User"));
            btnLoginMain.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvWelcome.setText("Welcome Guest!");
            btnLoginMain.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void setupDummyData() {
        if (db.categoryDao().getAllCategories().isEmpty()) {
            db.categoryDao().insert(new Category("Electronics"), new Category("Clothing"), new Category("Books"));
            db.userDao().insert(new User("admin", "123", "Admin User"));
            
            // "ic_menu_gallery" là ảnh mặc định của Android để demo
            // Trong thực tế bạn nên copy file ảnh vào folder res/drawable và đổi tên lại cho đúng
            db.productDao().insert(
                    new Product("Phone 17", 1000, "Flagship phone", "ip17", 1),
                    new Product("Laptop", 2000, "High-end laptop", "laptop", 1),
                    new Product("T-Shirt", 20, "Cotton T-shirt", "ao", 2),
                    new Product("Novel", 15, "Interesting novel", "sach", 3)
            );
        }
    }

    private void loadCategories() {
        List<Category> categories = db.categoryDao().getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            List<Product> filteredProducts = db.productDao().getProductsByCategory(category.categoryId);
            productAdapter.updateList(filteredProducts);
        });
        rvCategories.setAdapter(adapter);
    }

    private void loadProducts() {
        List<Product> products = db.productDao().getAllProducts();
        productAdapter = new ProductAdapter(products, this::handleAddToCart);
        rvProducts.setAdapter(productAdapter);
    }

    private void handleAddToCart(Product product) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        int userId = sessionManager.getUserId();
        Order pendingOrder = db.orderDao().getPendingOrderByUser(userId);

        if (pendingOrder == null) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            pendingOrder = new Order(userId, date, "Pending", 0);
            long orderId = db.orderDao().insert(pendingOrder);
            pendingOrder.orderId = (int) orderId;
        }

        OrderDetail detail = db.orderDetailDao().getDetailByOrderAndProduct(pendingOrder.orderId, product.productId);
        if (detail == null) {
            db.orderDetailDao().insert(new OrderDetail(pendingOrder.orderId, product.productId, 1, product.price));
        } else {
            detail.quantity += 1;
            db.orderDetailDao().update(detail);
        }

        updateOrderTotal(pendingOrder);

        Toast.makeText(this, "Added " + product.productName + " to cart", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        intent.putExtra("orderId", pendingOrder.orderId);
        startActivity(intent);
    }

    private void updateOrderTotal(Order order) {
        List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrder(order.orderId);
        double total = 0;
        for (OrderDetail d : details) {
            total += d.quantity * d.unitPrice;
        }
        order.totalAmount = total;
        db.orderDao().update(order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
