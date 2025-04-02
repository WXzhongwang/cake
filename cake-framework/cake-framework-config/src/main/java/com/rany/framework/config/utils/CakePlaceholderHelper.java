package com.rany.framework.config.utils;

import com.rany.framework.config.Config;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:50
 * @slogon 找到银弹
 */
public class CakePlaceholderHelper {
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String SIMPLE_PREFIX = "{";

    /**
     * 尝试解析value中的placeholder
     *
     * @param value               value
     * @param visitedPlaceholders 已经访问过的placeholder
     * @return placeholder所对应的值
     */
    public static String resolvePlaceholder(String value, Set<String> visitedPlaceholders) {

        if (value == null) {
            return null;
        }

        int startIndex = value.indexOf(PLACEHOLDER_PREFIX);
        if (startIndex == -1) {
            return value;
        }

        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = resolvePlaceholder(placeholder, visitedPlaceholders);
                // Now obtain the value for the fully resolved key...
                String propVal = Config.getData(placeholder);
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal = resolvePlaceholder(propVal, visitedPlaceholders);
                    result.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                    System.out.println("Resolved placeholder '" + placeholder + "':" + propVal);
                    startIndex = result.indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
                } else {
                    // Proceed with unprocessed value.
                    startIndex = result.indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_SUFFIX.length());
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + PLACEHOLDER_PREFIX.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + PLACEHOLDER_SUFFIX.length();
                } else {
                    return index;
                }
            } else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
                withinNestedPlaceholder++;
                index = index + SIMPLE_PREFIX.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

}
