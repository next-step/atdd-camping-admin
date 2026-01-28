package com.camping.admin.web;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.service.CampsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static com.camping.admin.support.ParamParser.*;

@Controller
@RequestMapping("/console/campsites")
@RequiredArgsConstructor
public class ConsoleCampsiteController {

    private final CampsiteService campsiteService;


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
    public String create(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String siteNumber = params.get("siteNumber");
        String description = params.get("description");
        Integer maxPeople = parseInteger(params.get("maxPeople"));

        campsiteService.create(siteNumber, description, maxPeople);
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
    public String update(@PathVariable Long id, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String siteNumber = params.get("siteNumber");
        String description = params.get("description");
        Integer maxPeople = parseInteger(params.get("maxPeople"));

        campsiteService.update(id, siteNumber, description, maxPeople);
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        return "redirect:/console/campsites";
    }

}


