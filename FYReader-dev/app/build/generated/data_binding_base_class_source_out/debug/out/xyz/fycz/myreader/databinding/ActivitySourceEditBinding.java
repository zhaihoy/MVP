// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.tabs.TabLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class ActivitySourceEditBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AppCompatCheckBox cbSourceEnable;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final AppCompatSpinner sSourceType;

  @NonNull
  public final TabLayout tabLayout;

  private ActivitySourceEditBinding(@NonNull LinearLayout rootView,
      @NonNull AppCompatCheckBox cbSourceEnable, @NonNull RecyclerView recyclerView,
      @NonNull AppCompatSpinner sSourceType, @NonNull TabLayout tabLayout) {
    this.rootView = rootView;
    this.cbSourceEnable = cbSourceEnable;
    this.recyclerView = recyclerView;
    this.sSourceType = sSourceType;
    this.tabLayout = tabLayout;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySourceEditBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySourceEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_source_edit, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySourceEditBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cb_source_enable;
      AppCompatCheckBox cbSourceEnable = rootView.findViewById(id);
      if (cbSourceEnable == null) {
        break missingId;
      }

      id = R.id.recycler_view;
      RecyclerView recyclerView = rootView.findViewById(id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.s_source_type;
      AppCompatSpinner sSourceType = rootView.findViewById(id);
      if (sSourceType == null) {
        break missingId;
      }

      id = R.id.tab_layout;
      TabLayout tabLayout = rootView.findViewById(id);
      if (tabLayout == null) {
        break missingId;
      }

      return new ActivitySourceEditBinding((LinearLayout) rootView, cbSourceEnable, recyclerView,
          sSourceType, tabLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
