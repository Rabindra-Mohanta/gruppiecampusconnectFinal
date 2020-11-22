package school.campusconnect.Assymetric;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private final int padding;

  public SpacesItemDecoration(int padding) {
    this.padding = padding;
  }

  @Override
  public void getItemOffsets(
          Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.bottom = padding;
  }
}