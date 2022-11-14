package com.cake.fast.example.cqrs;

import com.cake.fast.example.crqs.Application;
import com.cake.fast.example.crqs.TestEvent;
import com.cake.framework.cqrs.bus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/2 23:02
 * @email 18668485565163.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BastTests {

    @Resource
    private EventBus eventBus;

    @Test
    public void test() {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                eventBus.dispatch(new TestEvent("AAA"));
            }).start();
        }
    }


}
