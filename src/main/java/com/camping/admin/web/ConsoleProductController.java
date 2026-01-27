package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.UpdateProductRequest;
import com.camping.admin.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/console/products")
public class ConsoleProductController {

    private final ProductService productService;

    public ConsoleProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/products");
        return "products/form";
    }

    @PostMapping
    public String create(@ModelAttribute CreateProductRequest request, RedirectAttributes redirectAttributes) {
        productService.create(
                request.getName(),
                request.getStockQuantity(),
                request.getPrice(),
                request.getProductType()
        );
        redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다.");
        return "redirect:/console/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("formAction", "/console/products/" + id);
        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute UpdateProductRequest request, RedirectAttributes redirectAttributes) {
        productService.update(
                id,
                request.getName(),
                request.getStockQuantity(),
                request.getPrice(),
                request.getProductType()
        );
        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


