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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class LayoutBookDetailContentBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final RelativeLayout bookDetailRlCatalog;

  @NonNull
  public final RecyclerView bookDetailRvCatalog;

  @NonNull
  public final TextView bookDetailTvCatalog;

  @NonNull
  public final TextView bookDetailTvCatalogMore;

  @NonNull
  public final TextView bookDetailTvDesc;

  @NonNull
  public final TextView tvDisclaimer;

  private LayoutBookDetailContentBinding(@NonNull LinearLayout rootView,
      @NonNull RelativeLayout bookDetailRlCatalog, @NonNull RecyclerView bookDetailRvCatalog,
      @NonNull TextView bookDetailTvCatalog, @NonNull TextView bookDetailTvCatalogMore,
      @NonNull TextView bookDetailTvDesc, @NonNull TextView tvDisclaimer) {
    this.rootView = rootView;
    this.bookDetailRlCatalog = bookDetailRlCatalog;
    this.bookDetailRvCatalog = bookDetailRvCatalog;
    this.bookDetailTvCatalog = bookDetailTvCatalog;
    this.bookDetailTvCatalogMore = bookDetailTvCatalogMore;
    this.bookDetailTvDesc = bookDetailTvDesc;
    this.tvDisclaimer = tvDisclaimer;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutBookDetailContentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutBookDetailContentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_book_detail_content, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutBookDetailContentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.book_detail_rl_catalog;
      RelativeLayout bookDetailRlCatalog = rootView.findViewById(id);
      if (bookDetailRlCatalog == null) {
        break missingId;
      }

      id = R.id.book_detail_rv_catalog;
      RecyclerView bookDetailRvCatalog = rootView.findViewById(id);
      if (bookDetailRvCatalog == null) {
        break missingId;
      }

      id = R.id.book_detail_tv_catalog;
      TextView bookDetailTvCatalog = rootView.findViewById(id);
      if (bookDetailTvCatalog == null) {
        break missingId;
      }

      id = R.id.book_detail_tv_catalog_more;
      TextView bookDetailTvCatalogMore = rootView.findViewById(id);
      if (bookDetailTvCatalogMore == null) {
        break missingId;
      }

      id = R.id.book_detail_tv_desc;
      TextView bookDetailTvDesc = rootView.findViewById(id);
      if (bookDetailTvDesc == null) {
        break missingId;
      }

      id = R.id.tv_disclaimer;
      TextView tvDisclaimer = rootView.findViewById(id);
      if (tvDisclaimer == null) {
        break missingId;
      }

      return new LayoutBookDetailContentBinding((LinearLayout) rootView, bookDetailRlCatalog,
          bookDetailRvCatalog, bookDetailTvCatalog, bookDetailTvCatalogMore, bookDetailTvDesc,
          tvDisclaimer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
