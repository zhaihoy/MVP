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

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.weaction.ddsdk.base.DdSdkHelper;
import com.weaction.ddsdk.bean.DDSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import zhy.hongyuan.application.App;
import zhy.hongyuan.common.URLCONST;
import zhy.hongyuan.entity.ad.AdBean;
import zhy.hongyuan.entity.ad.AdConfig;
import zhy.hongyuan.model.user.UserService;
import zhy.hongyuan.util.SharedPreAdUtils;
import zhy.hongyuan.util.SharedPreUtils;
import zhy.hongyuan.util.help.DateHelper;

/**
 * @author fengyue
 * @date 2021/4/22 19:00
 */
public class AdUtils {
    public static final String TAG = AdUtils.class.getSimpleName();
    private static boolean hasInitAd = false;
    private static AdConfig adConfig;
    public static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    static {
        String config = getSp().getString("adConfig");
        adConfig = GsonExtensionsKt.getGSON().fromJson(config, AdConfig.class);
        if (adConfig == null || adConfig.getBackAdTime() == 0) {
            adConfig = new AdConfig(false, false, false, 60, 20,
                    60, 6, 3, 48,
                    true);
        }
    }

    public static SharedPreAdUtils getSp() {
        return SharedPreAdUtils.getInstance();
    }

    public static Single<Boolean> checkHasAd() {
        return checkHasAd(false, true);
    }

    public static Single<Boolean> checkHasAd(boolean noRemove, boolean isUser) {
        if (!noRemove && hasRemoveAdReward()) return Single.just(false);
        initAd();
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            boolean hasAd = false;
//            if (!adConfig.isCloud() || isExpire()) {
//                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                String body = "type=adConfig" + UserService.INSTANCE.makeAuth();
//                if (UserService.INSTANCE.isLogin()) {
//                    User user = UserService.INSTANCE.readConfig();
//                    if (user != null) {
//                        body += "&user=" + user.getUserName();
//                    }
//                }
//                RequestBody requestBody = RequestBody.create(mediaType, body);
//                String jsonStr = OkHttpUtils.getHtml(URLCONST.AD_URL, requestBody, "UTF-8");
//                try {
//                    JSONObject jsonObject = new JSONObject(jsonStr);
//                    int code = jsonObject.getInt("code");
//                    if (code > 200) {
//                        Log.e(TAG, "checkHasAd-->errorCode：" + code);
//                        if (code == 213) {
//                            hasAd = true;
//                        }
//                    } else {
//                        String res = jsonObject.getString("result");
//                        adConfig = GsonExtensionsKt.getGSON().fromJson(res, AdConfig.class);
//                        adConfig.setCloud(true);
//                        hasAd = isUser ? adConfig.isUserHasAd() : adConfig.isHasAd();
//                        getSp().putString("adConfig", res);
//                        getSp().putLong("adConfigTime", System.currentTimeMillis());
//                    }
//                    Log.i(TAG, "adConfig：" + adConfig);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                hasAd = isUser ? adConfig.isUserHasAd() : adConfig.isHasAd();
//            }
            emitter.onSuccess(false); //todo 广告请求
        }).compose(RxUtils::toSimpleSingle);
    }

    public static void adRecord(String type, String name) {
//        Single.create((SingleOnSubscribe<Boolean>) emitter -> {
//            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//            String body = "adType=" + type + "&type=" + name + UserService.INSTANCE.makeAuth();
//            RequestBody requestBody = RequestBody.create(mediaType, body);
//            OkHttpUtils.getHtml(URLCONST.AD_URL, requestBody, "UTF-8");
//            emitter.onSuccess(true);
//        }).compose(RxUtils::toSimpleSingle).subscribe(new MySingleObserver<Boolean>() {
//            @Override
//            public void onSuccess(@NonNull Boolean aBoolean) {
//                Log.i(TAG, name + "上报成功");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, name + "上报失败\n" + e.getLocalizedMessage());
//            }
//        });
    }

    public static Single<int[]> adTimes() {
        return Single.create((SingleOnSubscribe<int[]>) emitter -> {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String body = "type=adTimes" + UserService.INSTANCE.makeAuth();
            RequestBody requestBody = RequestBody.create(mediaType, body);
            String jsonStr = OkHttpUtils.getHtml(URLCONST.AD_URL, requestBody, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonStr);
            int[] adTimes = new int[]{-1, 3, 5};
            try {
                int code = jsonObject.getInt("code");
                JSONArray adTimesArr = jsonObject.getJSONArray("result");
                Log.i(TAG, "adTimesArr：" + adTimesArr.toString());
                if (code > 200) {
                    Log.e(TAG, "adTimes-->errorCode：" + code);
                    if (code == 213) {
                        adTimes = new int[]{-1};
                    }
                } else {
                    adTimes = new int[adTimesArr.length()];
                    for (int i = 0; i < adTimesArr.length(); i++) {
                        adTimes[i] = adTimesArr.getInt(i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            emitter.onSuccess(adTimes);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static boolean checkTodayShowAd() {
        String splashAdCount = SharedPreUtils.getInstance().getString("splashAdCount");
        boolean bookDetailAd = SharedPreUtils.getInstance().getBoolean("bookDetailAd", true);
        int adTimes = SharedPreUtils.getInstance().getInt("curAdTimes", 3);
        String[] splashAdCounts = splashAdCount.split(":");
        String today = DateHelper.getYearMonthDay1();
        int todayAdCount;
        if (today.equals(splashAdCounts[0])) {
            todayAdCount = Integer.parseInt(splashAdCounts[1]);
        } else {
            todayAdCount = 0;
        }
        return adTimes < 0 || todayAdCount < adTimes || bookDetailAd;
    }

    public static void backTime() {
        getSp().putLong("backTime", System.currentTimeMillis());
    }

    public static boolean backSplashAd() {
//        if (!adConfig.isUserHasAd()) return false;
//        boolean adTimeout = getSp().getBoolean("adTimeout");
//        if (adTimeout) return false;
//        long splashAdTime = getSp().getLong("splashAdTime");
//        long backTime = getSp().getLong("backTime");
//        long currentTime = System.currentTimeMillis();
//        Log.d(TAG, "currentTime - splashAdTime=" + (currentTime - splashAdTime));
//        Log.d(TAG, "currentTime - backTime=" + (currentTime - splashAdTime));
//        Log.d(TAG, "adConfig.getIntervalAdTime()=" + (adConfig.getIntervalAdTime() * 60L * 1000));
//        Log.d(TAG, "adConfig.getBackAdTime()=" + (adConfig.getBackAdTime() * 60L * 1000));
//        return currentTime - splashAdTime >= adConfig.getIntervalAdTime() * 60L * 1000 ||
//                currentTime - backTime >= adConfig.getBackAdTime() * 60L * 1000;
        return false;
    }

    private static boolean isExpire() {
        long adConfigTime = getSp().getLong("adConfigTime");
        long currentTime = System.currentTimeMillis();
        return currentTime - adConfigTime >= adConfig.getExpireTime() * 60L * 1000;
    }

    public static boolean adTime(String adTag, AdBean adBean) {
        if (adBean.getStatus() == 0) return false;
        long adTime = getSp().getLong(adTag + "Time");
        long currentTime = System.currentTimeMillis();
        return currentTime - adTime >= adBean.getInterval() * 60L * 1000;
    }

    /**
     * @param activity
     * @param type     1小、4中
     * @param flowAd
     */
    public static void getFlowAd(Activity activity, int type, FlowAd flowAd, String adTag) {
//        try {
//            new DdSdkFlowAd().getFlowViews(activity, type, new DdSdkFlowAd.FlowCallback() {
//                // 信息流广告拉取完毕后返回的 views
//                @Override
//                public void getFlowView(View view) {
//                    flowAd.getView(view);
//                }
//
//                // 信息流广告展示后调用
//                @Override
//                public void show() {
//                    AdUtils.adRecord("flow", "adShow");
//                    Log.d(TAG, "信息流广告展示成功");
//                    if (adTag != null) {
//                        getSp().putLong(adTag + "Time", System.currentTimeMillis());
//                    }
//                }
//
//                // 广告拉取失败调用
//                @Override
//                public void error(String msg) {
//                    Log.d(TAG, "广告拉取失败\n" + msg);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void showInterAd(Activity activity, String adTag) {
//        /*
//         * 参数 1  activity
//         * 参数 2  marginDp (float)，插屏默认 margin 全屏幕的 24dp，此处允许开发者手动调节 margin 大小，单位为 dp，允许范围为 0dp (全屏) ~ 48dp，请开发者按需填写
//         */
//        try {
//            DdSdkInterAd.show(activity, 48f, new DdSdkInterAd.Callback() {
//                @Override
//                public void show() {
//                    Log.i(TAG, "插屏广告展示成功");
//                    AdUtils.adRecord("inter", "adShow");
//                    if (adTag != null) {
//                        getSp().putLong(adTag + "Time", System.currentTimeMillis());
//                    }
//                }
//
//                @Override
//                public void click() {
//                    Log.i(TAG, "插屏广告");
//                    AdUtils.adRecord("inter", "adClick");
//                }
//
//                @Override
//                public void error(String msg) {
//                }
//
//                @Override
//                public void close() {
//                    Log.i(TAG, "插屏广告被关闭");
//                    AdUtils.adRecord("inter", "adClose");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void showRewardVideoAd(Activity activity, RewardAd rewardAd) {
//        try {
//            DdSdkRewardAd.show(activity, new DdSdkRewardAd.DdSdkRewardCallback() {
//                @Override
//                public void show() {
//                    Log.i(TAG, "激励视频展示成功");
//                    AdUtils.adRecord("rewardVideo", "adShow");
//                }
//
//                @Override
//                public void click() {
//                    Log.i(TAG, "激励视频被点击");
//                    AdUtils.adRecord("rewardVideo", "adClick");
//                }
//
//                @Override
//                public void error(String msg) {
//                }
//
//                @Override
//                public void skip() {
//                    Log.i(TAG, "激励视频被跳过");
//                    AdUtils.adRecord("rewardVideo", "adSkip");
//                }
//
//                @Override
//                public void reward() {
//                    if (rewardAd != null) {
//                        rewardAd.reward();
//                    }
//                    Log.i(TAG, "激励视频计时完成");
//                    AdUtils.adRecord("rewardVideo", "adFinishCount");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void removeAdReward() {
//        if (canAddReward()) {
//            try {
//                String rewardTime = getSp().getString("rewardLastTime");
//                long current = System.currentTimeMillis();
//                long rewardLastTime = System.currentTimeMillis();
//                if (!TextUtils.isEmpty(rewardTime)) {
//                    rewardLastTime = SDF.parse(rewardTime).getTime();
//                }
//                if (rewardLastTime < current) rewardLastTime = current;
//                rewardLastTime += adConfig.getRemoveAdTime() * 60L * 60 * 1000;
//                getSp().putString("rewardLastTime", SDF.format(rewardLastTime));
//                rewardCountPlus();
//            } catch (ParseException e) {
//                e.printStackTrace();
//                ToastUtils.showError("" + e.getLocalizedMessage());
//            }
//            ToastUtils.showSuccess("奖励获取成功");
//        } else {
//            ToastUtils.showWarring("已达到单日最大获得奖励次数");
//        }
    }

    private static void rewardCountPlus() {
        String today = DateHelper.getYearMonthDay1();
        String[] rewardCount = getSp().getString("rewardCount").split(":");
        int count;
        if (today.equals(rewardCount[0])) {
            count = Integer.parseInt(rewardCount[1]);
        } else {
            count = 0;
        }
        count++;
        getSp().putString("rewardCount", today + ":" + count);
    }

    public static boolean canAddReward() {
        String today = DateHelper.getYearMonthDay1();
        String[] rewardCount = getSp().getString("rewardCount").split(":");
        if (today.equals(rewardCount[0]) && rewardCount.length > 1) {
            return Integer.parseInt(rewardCount[1]) < adConfig.getMaxRemove();
        }
        return true;
    }

    public static boolean hasRemoveAdReward() {
//        String rewardTime = getSp().getString("rewardLastTime");
//        long rewardLastTime = 0;
//        try {
//            if (!TextUtils.isEmpty(rewardTime)) {
//                rewardLastTime = SDF.parse(rewardTime).getTime();
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return rewardLastTime > System.currentTimeMillis();
        return true;
    }

    public static void resetPangleId() {
        String root = App.getmContext().getFilesDir().getParent();
        String sharePath = root + File.separator + "shared_prefs";
        String c1 = root + File.separator + "cache216data.dat";
        String c2 = root + File.separator + "cache216statistics.dat";
        FileUtils.deleteFile(c1);
        FileUtils.deleteFile(c2);
        File shared = new File(sharePath);
        if (shared.exists() && shared.isDirectory()) {
            File[] files = shared.listFiles((dir, name) -> !name.startsWith("FYReader"));
            for (File file : files) {
                file.delete();
            }
        }
    }

    public static AdConfig getAdConfig() {
        return adConfig;
    }

    public static void initAd() {
        if (!hasInitAd) {
            hasInitAd = true;
            DdSdkHelper.init(new DDSDK.Builder()
                    .setUserId("1234")
                    .setAppId("216")
                    .setAppKey("51716a16fbdf50905704b6575b1b3b60")
                    .setCsjAppId("5273043")
                    .setApp(App.getApplication())
                    .setShowLog(App.isDebug())
                    .setCustomRequestPermission(true)
                    .create()
            );
        }
    }

    public interface FlowAd {
        void getView(View view);
    }

    public interface RewardAd {
        void reward();
    }
}
