package com.example.storage_shopping_app.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.storage_shopping_app.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category... categories);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
}
