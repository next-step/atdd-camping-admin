package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;
import static com.camping.admin.support.ParamParser.*;

@Controller
@RequestMapping("/console/products")
@RequiredArgsConstructor
public class ConsoleProductController {

    private final ProductService productService;

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
    public String create(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String name = params.get("name");
        Integer stockQuantity = parseInteger(params.get("stockQuantity"), 0);
        BigDecimal price = parseBigDecimal(params.get("price"), BigDecimal.ZERO);
        ProductType type = parseProductType(params.get("productType"));

        productService.create(name, stockQuantity, price, type);
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
    public String update(@PathVariable Long id, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String name = params.get("name");
        Integer stockQuantity = parseInteger(params.get("stockQuantity"), 0);
        BigDecimal price = parseBigDecimal(params.get("price"), BigDecimal.ZERO);
        ProductType type = parseProductType(params.get("productType"));

        productService.update(id, name, stockQuantity, price, type);
        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }

}


