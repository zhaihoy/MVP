// Generated by view binder compiler. Do not edit!
package xyz.fycz.myreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.fycz.myreader.R;

public final class DialogHourMinutePickerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final NumberPicker hourPicker;

  @NonNull
  public final NumberPicker minutePicker;

  private DialogHourMinutePickerBinding(@NonNull LinearLayout rootView,
      @NonNull NumberPicker hourPicker, @NonNull NumberPicker minutePicker) {
    this.rootView = rootView;
    this.hourPicker = hourPicker;
    this.minutePicker = minutePicker;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogHourMinutePickerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogHourMinutePickerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_hour_minute_picker, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogHourMinutePickerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.hour_picker;
      NumberPicker hourPicker = rootView.findViewById(id);
      if (hourPicker == null) {
        break missingId;
      }

      id = R.id.minute_picker;
      NumberPicker minutePicker = rootView.findViewById(id);
      if (minutePicker == null) {
        break missingId;
      }

      return new DialogHourMinutePickerBinding((LinearLayout) rootView, hourPicker, minutePicker);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
