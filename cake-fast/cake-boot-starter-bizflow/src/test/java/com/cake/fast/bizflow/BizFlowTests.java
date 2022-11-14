package com.cake.fast.bizflow;

import com.cake.framework.fast.bizflow.BizFlowHub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/9 23:58
 * @email 18668485565163.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BizFlowTests {

    @Autowired
    private BizFlowHub hub;


    @Test
    public void test() {
        System.out.println(hub);
    }
}
