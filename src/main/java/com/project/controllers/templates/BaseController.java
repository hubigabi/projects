package com.project.controllers.templates;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {
    @GetMapping("")
    private String getIndex() {
        return "redirect:/projects";
    }

    @GetMapping("/403")
    private String getAccessDenied() {
        return "errors/403";
    }
}
