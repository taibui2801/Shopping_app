package com.example.storage_shopping_app.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.storage_shopping_app.entity.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product... products);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<Product> getProductsByCategory(int categoryId);

    @Query("SELECT * FROM products WHERE productId = :productId")
    Product getProductById(int productId);
}
