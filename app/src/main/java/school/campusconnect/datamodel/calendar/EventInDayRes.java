package school.campusconnect.datamodel.calendar;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class EventInDayRes extends BaseResponse {
    public ArrayList<EventInDayData> data;

    public ArrayList<EventInDayData> getData() {
        return data;
    }

    public void setData(ArrayList<EventInDayData> data) {
        this.data = data;
    }

    public static class EventInDayData {
        String type;
        String text;
        String eventId;
        boolean canEdit;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }
    }
}
