package com.example.storage_shopping_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storage_shopping_app.R;
import com.example.storage_shopping_app.entity.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private OnAddToCartClickListener listener;

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnAddToCartClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvProductName.setText(product.productName);
        holder.tvProductPrice.setText(String.format("$%.2f", product.price));
        
        // Hiển thị ảnh từ drawable dựa trên tên lưu trong database
        if (product.imageUrl != null) {
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    product.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.ivProduct.setImageResource(resId);
            } else {
                holder.ivProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateList(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice;
        ImageView ivProduct;
        Button btnAddToCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
