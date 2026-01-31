package com.camping.admin.web;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;

import com.camping.admin.service.CampsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/console/campsites")
public class ConsoleCampsiteController {

    private final CampsiteService campsiteService;

    public ConsoleCampsiteController(CampsiteService campsiteService) {
        this.campsiteService = campsiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("campsites", campsiteService.getAll());
        return "campsites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/campsites");
        return "campsites/form";
    }

    @PostMapping
    public String create(@ModelAttribute CampsiteCreateRequest createReq, RedirectAttributes redirectAttributes) {
        campsiteService.create(createReq);

        redirectAttributes.addFlashAttribute("success", "캠프사이트가 등록되었습니다.");
        return "redirect:/console/campsites";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Campsite campsite = campsiteService.get(id);
        model.addAttribute("formAction", "/console/campsites/" + id);
        model.addAttribute("campsite", campsite);
        return "campsites/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute CampsiteUpdateRequest updateReq, RedirectAttributes redirectAttributes) {
        campsiteService.update(id,updateReq);

        redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        return "redirect:/console/campsites";
    }
}
