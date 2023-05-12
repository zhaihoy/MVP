// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class DialogReplaceBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btSelectBook;

  @NonNull
  public final Button btSelectSource;

  @NonNull
  public final CheckBox cbUseRegex;

  @NonNull
  public final AppCompatEditText etRuleBook;

  @NonNull
  public final AppCompatEditText etRuleDesc;

  @NonNull
  public final AppCompatEditText etRuleNew;

  @NonNull
  public final AppCompatEditText etRuleOld;

  @NonNull
  public final AppCompatEditText etRuleSource;

  @NonNull
  public final TextView tvCancel;

  @NonNull
  public final TextView tvConfirm;

  private DialogReplaceBinding(@NonNull LinearLayout rootView, @NonNull Button btSelectBook,
      @NonNull Button btSelectSource, @NonNull CheckBox cbUseRegex,
      @NonNull AppCompatEditText etRuleBook, @NonNull AppCompatEditText etRuleDesc,
      @NonNull AppCompatEditText etRuleNew, @NonNull AppCompatEditText etRuleOld,
      @NonNull AppCompatEditText etRuleSource, @NonNull TextView tvCancel,
      @NonNull TextView tvConfirm) {
    this.rootView = rootView;
    this.btSelectBook = btSelectBook;
    this.btSelectSource = btSelectSource;
    this.cbUseRegex = cbUseRegex;
    this.etRuleBook = etRuleBook;
    this.etRuleDesc = etRuleDesc;
    this.etRuleNew = etRuleNew;
    this.etRuleOld = etRuleOld;
    this.etRuleSource = etRuleSource;
    this.tvCancel = tvCancel;
    this.tvConfirm = tvConfirm;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogReplaceBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogReplaceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_replace, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogReplaceBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bt_select_book;
      Button btSelectBook = rootView.findViewById(id);
      if (btSelectBook == null) {
        break missingId;
      }

      id = R.id.bt_select_source;
      Button btSelectSource = rootView.findViewById(id);
      if (btSelectSource == null) {
        break missingId;
      }

      id = R.id.cb_use_regex;
      CheckBox cbUseRegex = rootView.findViewById(id);
      if (cbUseRegex == null) {
        break missingId;
      }

      id = R.id.et_rule_book;
      AppCompatEditText etRuleBook = rootView.findViewById(id);
      if (etRuleBook == null) {
        break missingId;
      }

      id = R.id.et_rule_desc;
      AppCompatEditText etRuleDesc = rootView.findViewById(id);
      if (etRuleDesc == null) {
        break missingId;
      }

      id = R.id.et_rule_new;
      AppCompatEditText etRuleNew = rootView.findViewById(id);
      if (etRuleNew == null) {
        break missingId;
      }

      id = R.id.et_rule_old;
      AppCompatEditText etRuleOld = rootView.findViewById(id);
      if (etRuleOld == null) {
        break missingId;
      }

      id = R.id.et_rule_source;
      AppCompatEditText etRuleSource = rootView.findViewById(id);
      if (etRuleSource == null) {
        break missingId;
      }

      id = R.id.tv_cancel;
      TextView tvCancel = rootView.findViewById(id);
      if (tvCancel == null) {
        break missingId;
      }

      id = R.id.tv_confirm;
      TextView tvConfirm = rootView.findViewById(id);
      if (tvConfirm == null) {
        break missingId;
      }

      return new DialogReplaceBinding((LinearLayout) rootView, btSelectBook, btSelectSource,
          cbUseRegex, etRuleBook, etRuleDesc, etRuleNew, etRuleOld, etRuleSource, tvCancel,
          tvConfirm);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
