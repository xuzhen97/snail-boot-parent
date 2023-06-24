package fun.easycode.snail.boot.test.adapter.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuzhen97
 */
@RestController
public class TestRestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
