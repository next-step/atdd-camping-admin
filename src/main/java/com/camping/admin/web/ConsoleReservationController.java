package com.camping.admin.web;

import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/console/reservations")
@RequiredArgsConstructor
public class ConsoleReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String list(Model model) {
        List<ReservationResponse> reservations = reservationService.findAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservations/list";
    }

    @GetMapping("/new")
    public String createForm() {
        return "reservations/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id) {
        return "reservations/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id) {
        return "reservations/form";
    }
}


