package com.camping.admin.web;

import com.camping.admin.exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice("com.camping.admin.web")
public class WebExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri.contains("/campsites")) {
            return "redirect:/console/campsites";
        } else if (requestUri.contains("/products")) {
            return "redirect:/console/products";
        }
        return "redirect:/console/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (requestUri.contains("/campsites")) {
            redirectAttributes.addFlashAttribute("error", "캠프사이트 처리 중 오류가 발생했습니다.");
            return "redirect:/console/campsites";
        } else if (requestUri.contains("/products")) {
            redirectAttributes.addFlashAttribute("error", "상품 처리 중 오류가 발생했습니다.");
            return "redirect:/console/products";
        }

        redirectAttributes.addFlashAttribute("error", "처리 중 오류가 발생했습니다.");
        return "redirect:/console/dashboard";
    }
}
