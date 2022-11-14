package com.cake.framework.log.agent;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:31
 * @email 18668485565163.com
 */
@Slf4j
public class CakeAgent {

    private static final Instrumentation instrumentation = ByteBuddyAgent.install();

    private static AgentBuilder agentBuilder = new AgentBuilder.Default();

    private static AtomicBoolean installed = new AtomicBoolean(false);

    public static void init() {
        if (!installed.compareAndSet(false, true)) {
            return;
        }
        agentBuilder.installOn(instrumentation);
        log.info("【CakeLogAgent】install done....");
    }


    public static void installAgent(ElementMatcher<? super TypeDescription> type, ElementMatcher<? super MethodDescription> method, Class interceptor) {
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) ->
                builder.method(method).intercept(MethodDelegation.to(interceptor));

        agentBuilder = agentBuilder.type(type).transform(transformer);
    }
}
