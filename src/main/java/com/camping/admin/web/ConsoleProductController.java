package com.camping.admin.web;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.repository.ProductRepository;
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

        Product entity = new Product(createReq.name(), stockQuantity, price, productType);
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + id));

        if (params.containsKey("name")) {
            product.setName(params.get("name"));
        }
        if (params.containsKey("stockQuantity")) {
            try {
                product.setStockQuantity(Integer.valueOf(params.get("stockQuantity")));
            } catch (Exception ignore) {}
        }
        if (params.containsKey("price")) {
            try {
                product.setPrice(new BigDecimal(params.get("price")));
            } catch (Exception ignore) {}
        }
        if (params.containsKey("productType")) {
            try {
                product.setProductType(ProductType.valueOf(params.get("productType")));
            } catch (Exception ignore) {}
        }

        redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다.");
        return "redirect:/console/products";
    }
}


