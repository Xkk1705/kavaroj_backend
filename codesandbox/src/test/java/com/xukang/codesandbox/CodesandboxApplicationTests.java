package com.xukang.codesandbox;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class CodesandboxApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        //创建文件
        File hfsafsdafsafas = FileUtil.writeString("hfsafsdafsafas","/home/kang/codesandbox/usercode/aaa1.java","utf-8");
        Thread.sleep(5000);
    }

}
