package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductUpdateRequest;
import com.camping.admin.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.util.Objects.*;

@Controller
@RequestMapping("/console/products")
public class ConsoleProductController {

    private final ProductRepository productRepository;

    public ConsoleProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
    public String create(@RequestParam ProductCreateRequest createReq, RedirectAttributes redirectAttributes) {
        Integer stockQuantity = requireNonNullElse(createReq.stockQuantity(), 0);
        BigDecimal price = requireNonNullElse(createReq.price(), BigDecimal.ZERO);
        ProductType productType = requireNonNullElse(createReq.productType(), ProductType.SALE);

        Product newProduct = new Product(createReq.name(), stockQuantity, price, productType);
        productRepository.save(newProduct);

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
    public String update(@PathVariable Long id, @ModelAttribute ProductUpdateRequest updateReq, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + id));

        String name = requireNonNullElse(updateReq.name(), product.getName());
        Integer stockQuantity = requireNonNullElse(updateReq.stockQuantity(), product.getStockQuantity());
        BigDecimal price = requireNonNullElse(updateReq.price(), product.getPrice());
        ProductType productType = requireNonNullElse(updateReq.productType(), product.getProductType());

        product.setName(name);
        product.setStockQuantity(stockQuantity);
        product.setPrice(price);
        product.setProductType(productType);

        productRepository.save(product);

        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


