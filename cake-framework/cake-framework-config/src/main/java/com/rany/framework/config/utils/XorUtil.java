package com.rany.framework.config.utils;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:58
 * @slogon 找到银弹
 */
public class XorUtil {

    private static final String KEY = "Configmap-1.0";

    public static String encrypt(final String text) {
        return Base64.encodeBase64String(xor(text.getBytes()));
    }

    public static String decrypt(final String hash) {
        return new String(xor(Base64.decodeBase64(hash.getBytes())), StandardCharsets.UTF_8);
    }

    private static byte[] xor(final byte[] input) {
        final byte[] output = new byte[input.length];
        final byte[] secret = KEY.getBytes();
        int spos = 0;
        for (int pos = 0; pos < input.length; ++pos) {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length) {
                spos = 0;
            }
        }
        return output;
    }

}