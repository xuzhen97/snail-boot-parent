package fun.easycode.snail.boot.test;


import cn.hutool.json.JSONUtil;
import fun.easycode.snail.boot.core.R;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestApplicationTest {

    @Test
    public void test(){
        R<?> r = R.message("操作成功！");
        R<String> r1 = R.success("操作成功!");

        System.out.println(JSONUtil.toJsonStr(r));
        System.out.println(JSONUtil.toJsonStr(r1));
    }
}
