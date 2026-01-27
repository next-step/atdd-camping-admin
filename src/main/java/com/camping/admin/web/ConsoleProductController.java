package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.ProductDto;
import com.camping.admin.dto.UpdateProductRequest;
import com.camping.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/console/products")
@RequiredArgsConstructor
public class ConsoleProductController {

    private final ProductService productService;

    @GetMapping
    public String list(Model model) {
        List<ProductDto> products = productService.findAll().stream()
                .map(ProductDto::from)
                .collect(Collectors.toList());
        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/products");
        return "products/form";
    }

    @PostMapping
    public String create(@ModelAttribute CreateProductRequest request, RedirectAttributes redirectAttributes) {
        productService.createProduct(request);
        redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다.");
        return "redirect:/console/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("formAction", "/console/products/" + id);
        model.addAttribute("product", ProductDto.from(product));
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute UpdateProductRequest request, RedirectAttributes redirectAttributes) {
        productService.updateProduct(id, request);
        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


