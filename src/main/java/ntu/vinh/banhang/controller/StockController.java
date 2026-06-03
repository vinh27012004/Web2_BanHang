package ntu.vinh.banhang.controller;

import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.entity.StockEntry;
import ntu.vinh.banhang.service.ProductService;
import ntu.vinh.banhang.service.StockEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockEntryService stockEntryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String list(Model model) {
        List<StockEntry> entries = stockEntryService.getAllEntries();
        List<Product> products = productService.getAllProducts();
        
        // Tính toán thống kê
        long totalProducts = products.size();
        long totalQuantity = products.stream()
                .mapToLong(Product::getQuantity)
                .sum();
        long lowStockProducts = products.stream()
                .filter(p -> p.getQuantity() <= 10)
                .count();

        model.addAttribute("entries", entries);
        model.addAttribute("products", products);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        return "stock/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("entry", new StockEntry());
        return "stock/form";
    }

    @PostMapping("/add")
    public String addStockEntry(@Valid @ModelAttribute StockEntry stockEntry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "stock/form";
        }
        stockEntryService.addStockEntry(stockEntry);
        return "redirect:/stock";
    }

    @GetMapping("/product/{id}")
    public String showProductHistory(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<StockEntry> entries = stockEntryService.getEntriesByProduct(product);
        
        model.addAttribute("product", product);
        model.addAttribute("entries", entries);
        
        return "stock/product-history";
    }
} 