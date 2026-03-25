package com.example.storage_shopping_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_shopping_app.R;
import com.example.storage_shopping_app.database.AppDatabase;
import com.example.storage_shopping_app.entity.OrderDetail;
import com.example.storage_shopping_app.entity.Product;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderDetail> orderDetails;
    private AppDatabase db;

    public OrderDetailAdapter(List<OrderDetail> orderDetails, AppDatabase db) {
        this.orderDetails = orderDetails;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail detail = orderDetails.get(position);
        Product product = db.productDao().getProductById(detail.productId);
        
        holder.tvProductName.setText(product != null ? product.productName : "Unknown Product");
        holder.tvQuantity.setText("x" + detail.quantity);
        holder.tvPrice.setText(String.format("$%.2f", detail.unitPrice * detail.quantity));
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvDetailProductName);
            tvQuantity = itemView.findViewById(R.id.tvDetailQuantity);
            tvPrice = itemView.findViewById(R.id.tvDetailPrice);
        }
    }
}
