/*
 * This file is part of FYReader.
 * FYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package zhy.hongyuan.util.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import zhy.hongyuan.util.ToastUtils;

/**
 * @author fengyue
 * @date 2021/1/9 15:13
 */
public class FingerprintUtils {
    private static final String DEFAULT_KEY_NAME = "fyreader";

    static KeyStore keyStore;

    public static boolean supportFingerprint(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            ToastUtils.showWarring("您的系统版本过低，不支持指纹功能");
            return false;
        } else {
            KeyguardManager keyguardManager = activity.getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = activity.getSystemService(FingerprintManager.class);
            if (!fingerprintManager.isHardwareDetected()) {
                ToastUtils.showWarring("您的手机不支持指纹功能");
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                ToastUtils.showWarring("您至少需要在系统设置中添加一个指纹");
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    private static void initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(23)
    public static Cipher initCipher() {
        try {
            initKey();
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
