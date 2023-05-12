// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class ActivityAdSettingBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout llAdSetting;

  @NonNull
  public final LinearLayout llSplashAdTimes;

  @NonNull
  public final RelativeLayout rlAd;

  @NonNull
  public final RelativeLayout rlBookDetailAd;

  @NonNull
  public final RelativeLayout rlDeleteAdFile;

  @NonNull
  public final RelativeLayout rlFlowAdCount;

  @NonNull
  public final SwitchCompat scAd;

  @NonNull
  public final SwitchCompat scBookDetailAd;

  @NonNull
  public final TextView tvSplashCurAdTimes;

  private ActivityAdSettingBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout llAdSetting, @NonNull LinearLayout llSplashAdTimes,
      @NonNull RelativeLayout rlAd, @NonNull RelativeLayout rlBookDetailAd,
      @NonNull RelativeLayout rlDeleteAdFile, @NonNull RelativeLayout rlFlowAdCount,
      @NonNull SwitchCompat scAd, @NonNull SwitchCompat scBookDetailAd,
      @NonNull TextView tvSplashCurAdTimes) {
    this.rootView = rootView;
    this.llAdSetting = llAdSetting;
    this.llSplashAdTimes = llSplashAdTimes;
    this.rlAd = rlAd;
    this.rlBookDetailAd = rlBookDetailAd;
    this.rlDeleteAdFile = rlDeleteAdFile;
    this.rlFlowAdCount = rlFlowAdCount;
    this.scAd = scAd;
    this.scBookDetailAd = scBookDetailAd;
    this.tvSplashCurAdTimes = tvSplashCurAdTimes;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAdSettingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAdSettingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_ad_setting, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAdSettingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ll_ad_setting;
      LinearLayout llAdSetting = rootView.findViewById(id);
      if (llAdSetting == null) {
        break missingId;
      }

      id = R.id.ll_splash_ad_times;
      LinearLayout llSplashAdTimes = rootView.findViewById(id);
      if (llSplashAdTimes == null) {
        break missingId;
      }

      id = R.id.rl_ad;
      RelativeLayout rlAd = rootView.findViewById(id);
      if (rlAd == null) {
        break missingId;
      }

      id = R.id.rl_book_detail_ad;
      RelativeLayout rlBookDetailAd = rootView.findViewById(id);
      if (rlBookDetailAd == null) {
        break missingId;
      }

      id = R.id.rl_delete_ad_file;
      RelativeLayout rlDeleteAdFile = rootView.findViewById(id);
      if (rlDeleteAdFile == null) {
        break missingId;
      }

      id = R.id.rl_flow_ad_count;
      RelativeLayout rlFlowAdCount = rootView.findViewById(id);
      if (rlFlowAdCount == null) {
        break missingId;
      }

      id = R.id.sc_ad;
      SwitchCompat scAd = rootView.findViewById(id);
      if (scAd == null) {
        break missingId;
      }

      id = R.id.sc_book_detail_ad;
      SwitchCompat scBookDetailAd = rootView.findViewById(id);
      if (scBookDetailAd == null) {
        break missingId;
      }

      id = R.id.tv_splash_cur_ad_times;
      TextView tvSplashCurAdTimes = rootView.findViewById(id);
      if (tvSplashCurAdTimes == null) {
        break missingId;
      }

      return new ActivityAdSettingBinding((LinearLayout) rootView, llAdSetting, llSplashAdTimes,
          rlAd, rlBookDetailAd, rlDeleteAdFile, rlFlowAdCount, scAd, scBookDetailAd,
          tvSplashCurAdTimes);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
