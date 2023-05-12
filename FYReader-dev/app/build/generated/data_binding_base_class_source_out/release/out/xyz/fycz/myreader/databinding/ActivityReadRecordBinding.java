// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class ActivityReadRecordBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView ivAllRecord;

  @NonNull
  public final AppCompatImageView ivMore;

  @NonNull
  public final RelativeLayout rlAllRecord;

  @NonNull
  public final RecyclerView rvRecords;

  @NonNull
  public final TextView tvAllRecord;

  private ActivityReadRecordBinding(@NonNull LinearLayout rootView, @NonNull ImageView ivAllRecord,
      @NonNull AppCompatImageView ivMore, @NonNull RelativeLayout rlAllRecord,
      @NonNull RecyclerView rvRecords, @NonNull TextView tvAllRecord) {
    this.rootView = rootView;
    this.ivAllRecord = ivAllRecord;
    this.ivMore = ivMore;
    this.rlAllRecord = rlAllRecord;
    this.rvRecords = rvRecords;
    this.tvAllRecord = tvAllRecord;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityReadRecordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityReadRecordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_read_record, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityReadRecordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_all_record;
      ImageView ivAllRecord = rootView.findViewById(id);
      if (ivAllRecord == null) {
        break missingId;
      }

      id = R.id.iv_more;
      AppCompatImageView ivMore = rootView.findViewById(id);
      if (ivMore == null) {
        break missingId;
      }

      id = R.id.rl_all_record;
      RelativeLayout rlAllRecord = rootView.findViewById(id);
      if (rlAllRecord == null) {
        break missingId;
      }

      id = R.id.rv_records;
      RecyclerView rvRecords = rootView.findViewById(id);
      if (rvRecords == null) {
        break missingId;
      }

      id = R.id.tv_all_record;
      TextView tvAllRecord = rootView.findViewById(id);
      if (tvAllRecord == null) {
        break missingId;
      }

      return new ActivityReadRecordBinding((LinearLayout) rootView, ivAllRecord, ivMore,
          rlAllRecord, rvRecords, tvAllRecord);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
