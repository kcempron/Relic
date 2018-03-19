package pip.project.relic;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainHandler {
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
