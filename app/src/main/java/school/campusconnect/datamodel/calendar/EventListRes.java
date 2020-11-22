package school.campusconnect.datamodel.calendar;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class EventListRes extends BaseResponse {
    public ArrayList<EventData> data;

    public ArrayList<EventData> getData() {
        return data;
    }

    public void setData(ArrayList<EventData> data) {
        this.data = data;
    }

    public static class EventData {
        int year;
        String type;
        int month;
        int day;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }
}
