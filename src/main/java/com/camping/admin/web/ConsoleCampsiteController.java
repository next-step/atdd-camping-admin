package com.camping.admin.web;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.service.CampsiteService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/console/campsites")
@RequiredArgsConstructor
public class ConsoleCampsiteController {

    private final CampsiteService campsiteService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("campsites", campsiteService.getAllCampsitesForWeb());
        return "campsites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/campsites");
        return "campsites/form";
    }

    @PostMapping
    public String create(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        campsiteService.createCampsiteFromWebForm(params);
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 등록되었습니다.");
        return "redirect:/console/campsites";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            Campsite campsite = campsiteService.findCampsiteById(id);
            model.addAttribute("formAction", "/console/campsites/" + id);
            model.addAttribute("campsite", campsite);
            return "campsites/form";
        } catch (Exception e) {
            return "redirect:/console/campsites";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        try {
            campsiteService.updateCampsiteFromWebForm(id, params);
            redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "캠프사이트 수정에 실패했습니다.");
        }
        return "redirect:/console/campsites";
    }
}
