package com.cake.framework.cqrs.utis;

import java.util.Collection;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:36
 * @email 18668485565163.com
 */
public final class CheckUtils {

    CheckUtils() {}

    public static boolean isExist(Collection collection, Object target) {
        if (collection == null || collection.isEmpty() || target == null) {
            return false;
        }

        return collection.contains(target);
    }
}
