package school.campusconnect.datamodel.calendar;

import com.google.gson.Gson;

public class AddEventReq {
    private String text;
    private String type;//(Radio Button - 1. Event, 2. Holiday)

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
