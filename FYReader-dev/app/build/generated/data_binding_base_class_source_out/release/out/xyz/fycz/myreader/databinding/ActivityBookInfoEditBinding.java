// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;
import zhy.hongyuan.widget.CoverImageView;

public final class ActivityBookInfoEditBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AppCompatButton btSelectLocalPic;

  @NonNull
  public final CoverImageView ivCover;

  @NonNull
  public final TextInputEditText tieBookAuthor;

  @NonNull
  public final TextInputEditText tieBookDesc;

  @NonNull
  public final TextInputEditText tieBookName;

  @NonNull
  public final TextInputEditText tieCoverUrl;

  @NonNull
  public final TextInputLayout tilBookAuthor;

  @NonNull
  public final TextInputLayout tilBookJj;

  @NonNull
  public final TextInputLayout tilBookName;

  @NonNull
  public final TextInputLayout tilCoverUrl;

  private ActivityBookInfoEditBinding(@NonNull LinearLayout rootView,
      @NonNull AppCompatButton btSelectLocalPic, @NonNull CoverImageView ivCover,
      @NonNull TextInputEditText tieBookAuthor, @NonNull TextInputEditText tieBookDesc,
      @NonNull TextInputEditText tieBookName, @NonNull TextInputEditText tieCoverUrl,
      @NonNull TextInputLayout tilBookAuthor, @NonNull TextInputLayout tilBookJj,
      @NonNull TextInputLayout tilBookName, @NonNull TextInputLayout tilCoverUrl) {
    this.rootView = rootView;
    this.btSelectLocalPic = btSelectLocalPic;
    this.ivCover = ivCover;
    this.tieBookAuthor = tieBookAuthor;
    this.tieBookDesc = tieBookDesc;
    this.tieBookName = tieBookName;
    this.tieCoverUrl = tieCoverUrl;
    this.tilBookAuthor = tilBookAuthor;
    this.tilBookJj = tilBookJj;
    this.tilBookName = tilBookName;
    this.tilCoverUrl = tilCoverUrl;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityBookInfoEditBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityBookInfoEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_book_info_edit, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityBookInfoEditBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bt_select_local_pic;
      AppCompatButton btSelectLocalPic = rootView.findViewById(id);
      if (btSelectLocalPic == null) {
        break missingId;
      }

      id = R.id.iv_cover;
      CoverImageView ivCover = rootView.findViewById(id);
      if (ivCover == null) {
        break missingId;
      }

      id = R.id.tie_book_author;
      TextInputEditText tieBookAuthor = rootView.findViewById(id);
      if (tieBookAuthor == null) {
        break missingId;
      }

      id = R.id.tie_book_desc;
      TextInputEditText tieBookDesc = rootView.findViewById(id);
      if (tieBookDesc == null) {
        break missingId;
      }

      id = R.id.tie_book_name;
      TextInputEditText tieBookName = rootView.findViewById(id);
      if (tieBookName == null) {
        break missingId;
      }

      id = R.id.tie_cover_url;
      TextInputEditText tieCoverUrl = rootView.findViewById(id);
      if (tieCoverUrl == null) {
        break missingId;
      }

      id = R.id.til_book_author;
      TextInputLayout tilBookAuthor = rootView.findViewById(id);
      if (tilBookAuthor == null) {
        break missingId;
      }

      id = R.id.til_book_jj;
      TextInputLayout tilBookJj = rootView.findViewById(id);
      if (tilBookJj == null) {
        break missingId;
      }

      id = R.id.til_book_name;
      TextInputLayout tilBookName = rootView.findViewById(id);
      if (tilBookName == null) {
        break missingId;
      }

      id = R.id.til_cover_url;
      TextInputLayout tilCoverUrl = rootView.findViewById(id);
      if (tilCoverUrl == null) {
        break missingId;
      }

      return new ActivityBookInfoEditBinding((LinearLayout) rootView, btSelectLocalPic, ivCover,
          tieBookAuthor, tieBookDesc, tieBookName, tieCoverUrl, tilBookAuthor, tilBookJj,
          tilBookName, tilCoverUrl);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
