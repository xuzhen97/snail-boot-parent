package fun.easycode.snail.boot.test.adapter.api;

import fun.easycode.snail.boot.util.JacksonUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuzhen97
 */
@RestController
public class TestRestController {

    @GetMapping("/test")
    public TestDto test() {

        TestDto dto = new TestDto();
        dto.setContent("test");
        dto.setUserEnum(UserEnum.STATE1);

        return dto;
    }

    @PostMapping("/test2")
    public String test2(@RequestBody TestCmd cmd){
        System.out.println(JacksonUtil.toJson(cmd));
        return "hello";
    }
}
