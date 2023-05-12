// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class ListviewChapterTitleItemBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView tvChapterTitle;

  @NonNull
  public final View vLine;

  private ListviewChapterTitleItemBinding(@NonNull LinearLayout rootView,
      @NonNull TextView tvChapterTitle, @NonNull View vLine) {
    this.rootView = rootView;
    this.tvChapterTitle = tvChapterTitle;
    this.vLine = vLine;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ListviewChapterTitleItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListviewChapterTitleItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.listview_chapter_title_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListviewChapterTitleItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.tv_chapter_title;
      TextView tvChapterTitle = rootView.findViewById(id);
      if (tvChapterTitle == null) {
        break missingId;
      }

      id = R.id.v_line;
      View vLine = rootView.findViewById(id);
      if (vLine == null) {
        break missingId;
      }

      return new ListviewChapterTitleItemBinding((LinearLayout) rootView, tvChapterTitle, vLine);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
