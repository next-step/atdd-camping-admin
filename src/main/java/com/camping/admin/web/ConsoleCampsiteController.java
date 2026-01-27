package com.camping.admin.web;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.service.CampsiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        model.addAttribute("campsites", campsiteService.findAll());
        return "campsites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/campsites");
        return "campsites/form";
    }

    @PostMapping
    public String create(@ModelAttribute CreateCampsiteRequest request, RedirectAttributes redirectAttributes) {
        campsiteService.create(
                request.getSiteNumber(),
                request.getDescription(),
                request.getMaxPeople()
        );
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 등록되었습니다.");
        return "redirect:/console/campsites";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Campsite campsite = campsiteService.findById(id);
        model.addAttribute("formAction", "/console/campsites/" + id);
        model.addAttribute("campsite", campsite);
        return "campsites/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute UpdateCampsiteRequest request, RedirectAttributes redirectAttributes) {
        campsiteService.update(
                id,
                request.getSiteNumber(),
                request.getDescription(),
                request.getMaxPeople()
        );
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        return "redirect:/console/campsites";
    }
}