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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;
import zhy.hongyuan.widget.CoverImageView;

public final class ItemReadRecordBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final CoverImageView ivBookImg;

  @NonNull
  public final AppCompatImageView ivMore;

  @NonNull
  public final LinearLayout llBookItem;

  @NonNull
  public final LinearLayout llBookRead;

  @NonNull
  public final RelativeLayout rlBookItem;

  @NonNull
  public final TextView tvBookAuthor;

  @NonNull
  public final TextView tvBookName;

  @NonNull
  public final TextView tvBookReadRecord;

  private ItemReadRecordBinding(@NonNull RelativeLayout rootView, @NonNull CoverImageView ivBookImg,
      @NonNull AppCompatImageView ivMore, @NonNull LinearLayout llBookItem,
      @NonNull LinearLayout llBookRead, @NonNull RelativeLayout rlBookItem,
      @NonNull TextView tvBookAuthor, @NonNull TextView tvBookName,
      @NonNull TextView tvBookReadRecord) {
    this.rootView = rootView;
    this.ivBookImg = ivBookImg;
    this.ivMore = ivMore;
    this.llBookItem = llBookItem;
    this.llBookRead = llBookRead;
    this.rlBookItem = rlBookItem;
    this.tvBookAuthor = tvBookAuthor;
    this.tvBookName = tvBookName;
    this.tvBookReadRecord = tvBookReadRecord;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemReadRecordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemReadRecordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_read_record, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemReadRecordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_book_img;
      CoverImageView ivBookImg = rootView.findViewById(id);
      if (ivBookImg == null) {
        break missingId;
      }

      id = R.id.iv_more;
      AppCompatImageView ivMore = rootView.findViewById(id);
      if (ivMore == null) {
        break missingId;
      }

      id = R.id.ll_book_item;
      LinearLayout llBookItem = rootView.findViewById(id);
      if (llBookItem == null) {
        break missingId;
      }

      id = R.id.ll_book_read;
      LinearLayout llBookRead = rootView.findViewById(id);
      if (llBookRead == null) {
        break missingId;
      }

      RelativeLayout rlBookItem = (RelativeLayout) rootView;

      id = R.id.tv_book_author;
      TextView tvBookAuthor = rootView.findViewById(id);
      if (tvBookAuthor == null) {
        break missingId;
      }

      id = R.id.tv_book_name;
      TextView tvBookName = rootView.findViewById(id);
      if (tvBookName == null) {
        break missingId;
      }

      id = R.id.tv_book_read_record;
      TextView tvBookReadRecord = rootView.findViewById(id);
      if (tvBookReadRecord == null) {
        break missingId;
      }

      return new ItemReadRecordBinding((RelativeLayout) rootView, ivBookImg, ivMore, llBookItem,
          llBookRead, rlBookItem, tvBookAuthor, tvBookName, tvBookReadRecord);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
