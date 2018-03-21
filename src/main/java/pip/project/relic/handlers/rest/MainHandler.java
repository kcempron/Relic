package pip.project.relic.handlers.rest;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainHandler implements ErrorController{

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/error")
    public String error() {
        return "There seems to be an error.";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
