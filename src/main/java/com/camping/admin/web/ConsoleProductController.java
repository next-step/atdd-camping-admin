package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.service.ProductService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/console/products")
public class ConsoleProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ConsoleProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/products");
        return "products/form";
    }

    @PostMapping
    public String create(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String name = params.getOrDefault("name", null);
        Integer stockQuantity;
        try {
            stockQuantity = params.containsKey("stockQuantity") ? Integer.valueOf(params.get("stockQuantity")) : 0;
        } catch (Exception e) {
            stockQuantity = 0;
        }
        BigDecimal price;
        try {
            price = params.containsKey("price") ? new BigDecimal(params.get("price")) : BigDecimal.ZERO;
        } catch (Exception e) {
            price = BigDecimal.ZERO;
        }
        ProductType type;
        try {
            type = params.containsKey("productType") ? ProductType.valueOf(params.get("productType")) : ProductType.SALE;
        } catch (Exception e) {
            type = ProductType.SALE;
        }

        Product entity = new Product(name, stockQuantity, price, type);
        productRepository.save(entity);
        redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다.");
        return "redirect:/console/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + id));
        model.addAttribute("formAction", "/console/products/" + id);
        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String name = params.get("name");

        Integer stockQuantity = null;
        try {
            stockQuantity = params.containsKey("stockQuantity") ? Integer.valueOf(params.get("stockQuantity")) : null;
        } catch (Exception ignore) {}

        BigDecimal price = null;
        try {
            price = params.containsKey("price") ? new BigDecimal(params.get("price")) : null;
        } catch (Exception ignore) {}

        ProductType productType = null;
        try {
            productType = params.containsKey("productType") ? ProductType.valueOf(params.get("productType")) : null;
        } catch (Exception ignore) {}

        productService.update(id, name, stockQuantity, price, productType);

        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


