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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class ActivityRemoveAdBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout llSplashAdTimes;

  @NonNull
  public final RelativeLayout rlRewardVideo;

  @NonNull
  public final TextView tvCurRemoveAdTime;

  @NonNull
  public final AppCompatTextView tvTip;

  private ActivityRemoveAdBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout llSplashAdTimes, @NonNull RelativeLayout rlRewardVideo,
      @NonNull TextView tvCurRemoveAdTime, @NonNull AppCompatTextView tvTip) {
    this.rootView = rootView;
    this.llSplashAdTimes = llSplashAdTimes;
    this.rlRewardVideo = rlRewardVideo;
    this.tvCurRemoveAdTime = tvCurRemoveAdTime;
    this.tvTip = tvTip;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRemoveAdBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRemoveAdBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_remove_ad, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRemoveAdBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ll_splash_ad_times;
      LinearLayout llSplashAdTimes = rootView.findViewById(id);
      if (llSplashAdTimes == null) {
        break missingId;
      }

      id = R.id.rl_reward_video;
      RelativeLayout rlRewardVideo = rootView.findViewById(id);
      if (rlRewardVideo == null) {
        break missingId;
      }

      id = R.id.tv_cur_remove_ad_time;
      TextView tvCurRemoveAdTime = rootView.findViewById(id);
      if (tvCurRemoveAdTime == null) {
        break missingId;
      }

      id = R.id.tv_tip;
      AppCompatTextView tvTip = rootView.findViewById(id);
      if (tvTip == null) {
        break missingId;
      }

      return new ActivityRemoveAdBinding((LinearLayout) rootView, llSplashAdTimes, rlRewardVideo,
          tvCurRemoveAdTime, tvTip);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
