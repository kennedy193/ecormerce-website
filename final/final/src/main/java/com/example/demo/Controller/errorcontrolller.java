package com.example.demo.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class errorcontrolller implements ErrorController {
    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }
}