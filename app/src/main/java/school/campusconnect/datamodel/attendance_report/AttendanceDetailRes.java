package school.campusconnect.datamodel.attendance_report;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AttendanceDetailRes extends BaseResponse {
    private ArrayList<AttendanceDetailData> data;

    public ArrayList<AttendanceDetailData> getData() {
        return data;
    }

    public void setData(ArrayList<AttendanceDetailData> data) {
        this.data = data;
    }

    public static class AttendanceDetailData {
        private int day;
        private int month;
        private String morningAttendance;
        private String afternoonAttendance;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public String getMorningAttendance() {
            return morningAttendance;
        }

        public void setMorningAttendance(String morningAttendance) {
            this.morningAttendance = morningAttendance;
        }

        public String getAfternoonAttendance() {
            return afternoonAttendance;
        }

        public void setAfternoonAttendance(String afternoonAttendance) {
            this.afternoonAttendance = afternoonAttendance;
        }
    }
}
