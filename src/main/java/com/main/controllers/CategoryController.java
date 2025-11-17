//package com.main.controllers;
//
//import com.main.dtos.CategoryDto;
//import com.main.services.CategoryService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//public class CategoryController {
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/admin/categories")
//    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
//        try {
//            CategoryDto createdCategory = categoryService.createCategory(categoryDto);
//            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error creating category"));
//        }
//    }
//
//    @GetMapping("/categories")
//    public ResponseEntity<List<CategoryDto>> getAllCategories() {
//        List<CategoryDto> categories = categoryService.getAllCategories();
//        return ResponseEntity.ok(categories);
//    }
//
//    @GetMapping("/categories/{id}")
//    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
//        try {
//            CategoryDto category = categoryService.getCategoryById(id);
//            return ResponseEntity.ok(category);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error fetching category"));
//        }
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/admin/categories/{id}")
//    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) throws IllegalArgumentException {
//        try {
//            CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
//            return ResponseEntity.ok(updatedCategory);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error updating category"));
//        }
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/admin/categories/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
//        try {
//            categoryService.deleteCategory(id);
//            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error deleting category"));
//        }
//    }
//}
