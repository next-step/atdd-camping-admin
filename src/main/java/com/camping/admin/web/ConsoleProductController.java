package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductUpdateRequest;

import com.camping.admin.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        model.addAttribute("products", productService.getAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/products");
        return "products/form";
    }

    @PostMapping
    public String create(@RequestParam ProductCreateRequest createReq, RedirectAttributes redirectAttributes) {
        productService.create(createReq);

        redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다.");
        return "redirect:/console/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.get(id);

        model.addAttribute("formAction", "/console/products/" + id);
        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ProductUpdateRequest updateReq, RedirectAttributes redirectAttributes) {
        productService.update(id, updateReq);

        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


