package com.camping.admin.web;

import com.camping.admin.dto.CampsiteRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.service.CampsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/console/campsites")
@RequiredArgsConstructor
public class ConsoleCampsiteController {

    private final CampsiteService campsiteService;

    @GetMapping
    public String list(Model model) {
        List<CampsiteResponse> campsites = campsiteService.getAllCampsites();
        model.addAttribute("campsites", campsites);
        return "campsites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/campsites");
        return "campsites/form";
    }

    @PostMapping
    public String create(CampsiteRequest request, RedirectAttributes redirectAttributes) {
        campsiteService.createCampsite(request);
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 등록되었습니다.");
        return "redirect:/console/campsites";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CampsiteResponse campsite = campsiteService.getAllCampsites().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + id));
        model.addAttribute("formAction", "/console/campsites/" + id);
        model.addAttribute("campsite", campsite);
        return "campsites/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, CampsiteRequest request, RedirectAttributes redirectAttributes) {
        campsiteService.updateCampsite(id, request);
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        return "redirect:/console/campsites";
    }
}


