package ru.lazer.cas.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimpleErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String error(HttpServletRequest req) {
        Object status = req.getAttribute("jakarta.servlet.error.status_code");
        return "<html><body><h3>Ошибка</h3><p>Status: " + status + "</p></body></html>";
    }
}
