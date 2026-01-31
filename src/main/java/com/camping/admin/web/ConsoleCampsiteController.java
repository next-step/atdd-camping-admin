package com.camping.admin.web;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.repository.CampsiteRepository;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/console/campsites")
public class ConsoleCampsiteController {

    private final CampsiteRepository campsiteRepository;

    public ConsoleCampsiteController(CampsiteRepository campsiteRepository) {
        this.campsiteRepository = campsiteRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("campsites", campsiteRepository.findAll());
        return "campsites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formAction", "/console/campsites");
        return "campsites/form";
    }

    @PostMapping
    public String create(@ModelAttribute CampsiteCreateRequest createReq, RedirectAttributes redirectAttributes) {
        String siteNumber = createReq.siteNumber();
        String description = Objects.requireNonNull(createReq.description(), "");
        Integer maxPeople = createReq.maxPeople();

        Campsite entity = new Campsite(siteNumber, description, maxPeople);
        campsiteRepository.save(entity);
        redirectAttributes.addFlashAttribute("success", "캠프사이트가 등록되었습니다.");
        return "redirect:/console/campsites";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + id));
        model.addAttribute("formAction", "/console/campsites/" + id);
        model.addAttribute("campsite", campsite);
        return "campsites/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute CampsiteUpdateRequest updateReq, RedirectAttributes redirectAttributes) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + id));

        String siteNumber = Objects.requireNonNullElse(updateReq.siteNumber(), campsite.getSiteNumber());
        String description = Objects.requireNonNullElse(updateReq.description(), campsite.getDescription());
        Integer maxPeople = Objects.requireNonNullElse(updateReq.maxPeople(), campsite.getMaxPeople());

        campsite.setSiteNumber(siteNumber);
        campsite.setDescription(description);
        campsite.setMaxPeople(maxPeople);

        campsiteRepository.save(campsite);

        redirectAttributes.addFlashAttribute("success", "캠프사이트가 수정되었습니다.");
        return "redirect:/console/campsites";
    }
}


