package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.service.ProductService;
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

    private final ProductService productService;

    public ConsoleProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/products");
        return "products/form";
    }

    @PostMapping
    public String create(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        productService.createProductFromWebForm(params);
        redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다.");
        return "redirect:/console/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findProductById(id);
            model.addAttribute("formAction", "/console/products/" + id);
            model.addAttribute("product", product);
            return "products/form";
        } catch (Exception e) {
            return "redirect:/console/products";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductFromWebForm(id, params);
            redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정에 실패했습니다.");
        }
        return "redirect:/console/products";
    }
}
