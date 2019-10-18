package com.adi.exam.common;

import com.adi.exam.utils.TraceUtils;

import java.security.MessageDigest;

public class MixUpValue {

    public String getValues(String id) {

        int length = id.length();

        String val = "";

        for (int i = 1; i <= length; i++) {

            if (i % 2 != 0) {
                val = val + id.charAt(i - 1);
            }

        }

        return val;
    }

    public final String encryption(final String string) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            byte[] hash = digest.digest(string.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
        return "";
    }

}
