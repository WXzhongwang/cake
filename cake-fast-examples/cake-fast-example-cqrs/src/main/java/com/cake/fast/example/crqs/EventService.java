package com.cake.fast.example.crqs;

import com.cake.framework.cqrs.base.EventHandler;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/2 23:06
 * @email 18668485565163.com
 */
@Component
public class EventService {

    @EventHandler(name = "处理测试时间")
    public void process(TestEvent testEvent) {
        try {
            System.out.println(testEvent.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
