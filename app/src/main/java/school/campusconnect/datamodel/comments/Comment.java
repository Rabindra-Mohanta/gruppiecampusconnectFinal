package school.campusconnect.datamodel.comments;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

/**
 * Created by frenzin04 on 1/19/2017.
 */
public class Comment {
    private ObservableInt commentEditTextId;
    private ObservableField<String> commentString;
    public Comment() {
        commentEditTextId = new ObservableInt();
        commentString = new ObservableField<>();
    }
    public ObservableInt getCommentEditTextId() {
        return commentEditTextId;
    }
    public void setCommentEditTextId(ObservableInt pSomeNumber) {
        commentEditTextId.set(pSomeNumber.get());
    }
    public ObservableField<String> getCommentString() {
        return commentString;
    }
    public void setCommentString(ObservableField<String> pSomeStr) {
        commentString.set(pSomeStr.get());
    }
}