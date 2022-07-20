package school.campusconnect.network;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import school.campusconnect.BuildConfig;
import school.campusconnect.datamodel.ConstituencyRes;
import school.campusconnect.datamodel.CoursePostResponse;
import school.campusconnect.datamodel.LeaveErrorResponse;
import school.campusconnect.datamodel.LeaveReq;
import school.campusconnect.datamodel.MarkSheetListResponse;
import school.campusconnect.datamodel.NewPassReq;
import school.campusconnect.datamodel.OtpVerifyReq;
import school.campusconnect.datamodel.OtpVerifyRes;
import school.campusconnect.datamodel.ReadUnreadResponse;
import school.campusconnect.datamodel.TaluksRes;
import school.campusconnect.datamodel.classs.AddClassReq;
import school.campusconnect.datamodel.classs.AddCombinedClass;
import school.campusconnect.datamodel.classs.ClassResV2;
import school.campusconnect.datamodel.profileCaste.ProfessionResponce;
import school.campusconnect.datamodel.register.BoardsData;
import school.campusconnect.datamodel.register.CampusMediumData;
import school.campusconnect.datamodel.register.ClassesListData;
import school.campusconnect.datamodel.register.TypeOfCampusData;
import school.campusconnect.datamodel.attendance_report.ApplyLeaveReq;
import school.campusconnect.datamodel.attendance_report.AttendanceReportParentRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportResv2;
import school.campusconnect.datamodel.attendance_report.AttendenceEditRequest;
import school.campusconnect.datamodel.attendance_report.LeaveRes;
import school.campusconnect.datamodel.banner.BannerAddReq;
import school.campusconnect.datamodel.banner.BannerRes;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.datamodel.booths.MyBoothEventRes;
import school.campusconnect.datamodel.booths.MyTeamSubBoothResponse;
import school.campusconnect.datamodel.booths.SubBoothEventRes;
import school.campusconnect.datamodel.booths.SubBoothWorkerEventRes;
import school.campusconnect.datamodel.booths.VoterProfileResponse;
import school.campusconnect.datamodel.booths.VoterProfileUpdate;
import school.campusconnect.datamodel.comments.AddCommentTaskDetailsReq;
import school.campusconnect.datamodel.comments.CommentTaskDetailsRes;
import school.campusconnect.datamodel.committee.AddCommitteeReq;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.event.TeamEventModelRes;
import school.campusconnect.datamodel.event.TeamPostEventModelRes;
import school.campusconnect.datamodel.feed.AdminFeederResponse;
import school.campusconnect.datamodel.masterList.BoothMasterListModelResponse;
import school.campusconnect.datamodel.masterList.StreetListModelResponse;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;
import school.campusconnect.datamodel.masterList.WorkerListResponse;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.datamodel.register.UniversitiesData;
import school.campusconnect.datamodel.searchUser.SearchUserModel;
import school.campusconnect.datamodel.staff.AddStaffRole;
import school.campusconnect.datamodel.staff.ChangeStaffAttendanceReq;
import school.campusconnect.datamodel.staff.StaffAttendanceRes;
import school.campusconnect.datamodel.staff.TakeAttendanceReq;
import school.campusconnect.datamodel.student.AddMultipleStaffReq;
import school.campusconnect.datamodel.student.AddMultipleStudentReq;
import school.campusconnect.datamodel.subjects.AbsentStudentReq;
import school.campusconnect.datamodel.subjects.SubjectResponsev1;
import school.campusconnect.datamodel.syllabus.ChangeStatusPlanModel;
import school.campusconnect.datamodel.syllabus.EditTopicModelReq;
import school.campusconnect.datamodel.syllabus.StaffAnalysisRes;
import school.campusconnect.datamodel.syllabus.SyllabusListMaster;
import school.campusconnect.datamodel.syllabus.SyllabusListModelRes;
import school.campusconnect.datamodel.syllabus.SyllabusModelReq;
import school.campusconnect.datamodel.syllabus.SyllabusPlanRequest;
import school.campusconnect.datamodel.syllabus.TodaySyllabusPlanRes;
import school.campusconnect.datamodel.ticket.AddTicketRequest;
import school.campusconnect.datamodel.attendance_report.AttendanceDetailRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.attendance_report.OnlineAttendanceRes;
import school.campusconnect.datamodel.attendance_report.PreSchoolStudentRes;
import school.campusconnect.datamodel.booths.BoothData;
import school.campusconnect.datamodel.booths.BoothMemberReq;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.booths.SubBoothResponse;
import school.campusconnect.datamodel.bus.BusResponse;
import school.campusconnect.datamodel.bus.BusStudentRes;
import school.campusconnect.datamodel.calendar.AddEventReq;
import school.campusconnect.datamodel.calendar.EventInDayRes;
import school.campusconnect.datamodel.calendar.EventListRes;
import school.campusconnect.datamodel.chapter.AddChapterPostRequest;
import school.campusconnect.datamodel.chapter.ChapterRes;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.classs.ParentKidsResponse;
import school.campusconnect.datamodel.ebook.AddEbookReq;
import school.campusconnect.datamodel.ebook.AddEbookReq2;
import school.campusconnect.datamodel.ebook.EBooksResponse;
import school.campusconnect.datamodel.ebook.EBooksTeamResponse;
import school.campusconnect.datamodel.event.UpdateDataEventRes;
import school.campusconnect.datamodel.family.FamilyMemberResponse;
import school.campusconnect.datamodel.fees.FeesRes;
import school.campusconnect.datamodel.fees.PaidStudentFeesRes;
import school.campusconnect.datamodel.fees.PayFeesRequest;
import school.campusconnect.datamodel.fees.StudentFeesRes;
import school.campusconnect.datamodel.fees.UpdateStudentFees;
import school.campusconnect.datamodel.gruppiecontacts.SendMsgToStudentReq;
import school.campusconnect.datamodel.homework.AddHwPostRequest;
import school.campusconnect.datamodel.homework.AssignmentRes;
import school.campusconnect.datamodel.homework.HwRes;
import school.campusconnect.datamodel.homework.ReassignReq;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.issue.RegisterIssueReq;
import school.campusconnect.datamodel.markcard2.AddMarksReq;
import school.campusconnect.datamodel.markcard2.MarkCardResponse2;
import school.campusconnect.datamodel.marksheet.AddMarkCardReq;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;
import school.campusconnect.datamodel.marksheet.StudentMarkCardListResponse;
import school.campusconnect.datamodel.marksheet.SubjectTeamResponse;
import school.campusconnect.datamodel.marksheet.UploadMarkRequest;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.datamodel.readMore.ReadMoreRes;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.datamodel.subjects.AbsentSubjectReq;
import school.campusconnect.datamodel.subjects.AddSubjectStaffReq;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.test_exam.AddTestExamPostRequest;
import school.campusconnect.datamodel.test_exam.OfflineTestReq;
import school.campusconnect.datamodel.test_exam.OfflineTestRes;
import school.campusconnect.datamodel.test_exam.TestExamRes;
import school.campusconnect.datamodel.test_exam.TestLiveEventRes;
import school.campusconnect.datamodel.test_exam.TestPaperRes;
import school.campusconnect.datamodel.ticket.TicketEventUpdateResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;
import school.campusconnect.datamodel.time_table.SubStaffTTReq;
import school.campusconnect.datamodel.time_table.SubjectStaffTTResponse;
import school.campusconnect.datamodel.time_table.TimeTableList2Response;
import school.campusconnect.datamodel.videocall.JoinLiveClassReq;
import school.campusconnect.datamodel.videocall.LiveClassEventRes;
import school.campusconnect.datamodel.videocall.MeetingStatusModelApi;
import school.campusconnect.datamodel.videocall.StopMeetingReq;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import retrofit2.Call;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.datamodel.AbsentAttendanceRes;
import school.campusconnect.datamodel.AddCodeOfConductReq;
import school.campusconnect.datamodel.AddGalleryPostRequest;
import school.campusconnect.datamodel.AddPostRequest;
import school.campusconnect.datamodel.AddPostResponse;
import school.campusconnect.datamodel.AddStudentReq;
import school.campusconnect.datamodel.AddTeamResponse;
import school.campusconnect.datamodel.AddTimeTableRequest;
import school.campusconnect.datamodel.AddVendorPostRequest;
import school.campusconnect.datamodel.AttendanceListRes;
import school.campusconnect.datamodel.CodeConductResponse;
import school.campusconnect.datamodel.CreateTeamRequest;
import school.campusconnect.datamodel.EditAttendanceReq;
import school.campusconnect.datamodel.gallery.GalleryPostRes;
import school.campusconnect.datamodel.GetLocationRes;
import school.campusconnect.datamodel.PersonalSettingRes;
import school.campusconnect.datamodel.SettingRes;
import school.campusconnect.datamodel.TeamSettingRes;
import school.campusconnect.datamodel.TimeTableRes;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.datamodel.comments.AddCommentRes;
import school.campusconnect.utils.AppLog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivid.gruppie.model.RegisterRequestData;

import java.lang.reflect.Type;
import java.util.ArrayList;

import school.campusconnect.LeafApplication;
import school.campusconnect.activities.LoginActivity2;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddFriendValidationError;
import school.campusconnect.datamodel.AddLeadRequest;
import school.campusconnect.datamodel.AddLeadValidationError;
import school.campusconnect.datamodel.AddPostRequestDescription;
import school.campusconnect.datamodel.AddPostRequestFile;
import school.campusconnect.datamodel.AddPostRequestFile_Friend;
import school.campusconnect.datamodel.AddPostRequestVideo_Friend;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.BaseValidationError;
import school.campusconnect.datamodel.ChangeNumberRequest;
import school.campusconnect.datamodel.ChangePasswordRequest;
import school.campusconnect.datamodel.ChangePasswordResponse;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.CreateGroupReguest;
import school.campusconnect.datamodel.ErrorResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.ForgotPasswordRequest;
import school.campusconnect.datamodel.ForgotPasswordValidationError;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupResponse;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.GruppieContactListResponse;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.LoginResponse;
import school.campusconnect.datamodel.NumberValidationError;
import school.campusconnect.datamodel.PasswordValidationError;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.profile.ProfileItemUpdate;
import school.campusconnect.datamodel.profile.ProfileResponse;
import school.campusconnect.datamodel.ProfileValidationError;
import school.campusconnect.datamodel.ReadGroupPostResponse;
import school.campusconnect.datamodel.ReadTeamPostResponse;
import school.campusconnect.datamodel.SignUpRequest;
import school.campusconnect.datamodel.SignUpResponse;
import school.campusconnect.datamodel.SignupValidationError;
import school.campusconnect.datamodel.UserListResponse;
import school.campusconnect.datamodel.addgroup.CreateGroupResponse;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserResponse;
import school.campusconnect.datamodel.comments.AddGroupCommentRequest;
import school.campusconnect.datamodel.comments.GroupCommentResponse;
import school.campusconnect.datamodel.gruppiecontacts.InviteResponse;
import school.campusconnect.datamodel.gruppiecontacts.InviteResponseSingle;
import school.campusconnect.datamodel.gruppiecontacts.LeaveResponse;
import school.campusconnect.datamodel.join.JoinListResponse;
import school.campusconnect.datamodel.likelist.LikeListResponse;
import school.campusconnect.datamodel.notifications.NotificationResponse;
import school.campusconnect.datamodel.notingruppie.NotInGruppieResponse;
import school.campusconnect.datamodel.numberexist.NumberExistRequest;
import school.campusconnect.datamodel.numberexist.NumberExistResponse;
import school.campusconnect.datamodel.personalchat.PersonalPostResponse;
import school.campusconnect.datamodel.publicgroup.PublicGroupResponse;
import school.campusconnect.datamodel.question.QuestionResponse;
import school.campusconnect.datamodel.reportlist.ReportResponse;
import school.campusconnect.datamodel.sharepost.ShareGroupResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetResponse;
import school.campusconnect.datamodel.versioncheck.VersionCheckResponse;
import school.campusconnect.datamodel.youtubetoken.YoutubeTokenResponse;
import school.campusconnect.fragments.Fragment_PhoneContacts;


/**
 * Created by techjini on 14/1/16.
 */
public class LeafManager {
    private static final String TAG = "LeafManager";


    private OnCommunicationListener mOnCommunicationListener;
    private OnAddUpdateListener mListener;
    public static final int API_ID_REGISTER = 111;
    public static final int API_ID_LOGIN = 112;
    public static final int API_ID_ADD = 112;
    public static final int API_ID_GROUP_LIST = 113;
    public static final int API_ID_LEAD_LIST = 114;
    public static final int API_ID_MYLEAD_LIST = 115;
    public static final int API_ID_REFERRALS = 116;
    public static final int API_ID_CHANGE_PWD = 118;
    public static final int API_ID_FORGOT_PWD = 119;
    public static final int API_ID_GROUP_DETAIL = 200;
    public static final int API_ID_LEAVE_GROUP = 201;
    public static final int API_ID_CREATE_GROUP = 202;
    public static final int API_ID_EDIT_GROUP = 203;
    public static final int API_ID_DELETE_GROUP = 204;
    public static final int API_ID_GENERAL_POST = 205;
    public static final int API_ID_POST = 208;
    public static final int API_ID_FAV_POST = 210;
    public static final int API_ID_FAV = 206;
    public static final int API_ID_LIKE = 207;
    public static final int API_ID_GET_PROFILE = 208;
    public static final int API_ID_UPDATE_PROFILE = 209;
    public static final int API_ID_DELETE_POST = 555;
    public static final int API_ID_EDIT_NUMBER = 556;
    public static final int API_ID_LEAD_LIST_SEARCH = 561;
    public static final int API_ALL_USERS_LIST = 562;
    public static final int API_DELETE_MY_FRIEND = 564;
    public static final int API_DELETE_USER = 565;
    public static final int API_ALLOW_POST = 566;
    public static final int API_NOT_ALLOW_POST = 567;
    public static final int API_ID_LOGOUT = 569;
    public static final int API_ID_DELETE_PERSONAL_POST = 570;
    public static final int API_ID_DELETE_PROPIC = 571;
    public static final int API_ID_DELETE_GROUPPIC = 572;
    public static final int API_ALL_CONTACT_LIST = 574;
    public static final int API_GRUPPIE_CONTACT_LIST = 575;
    public static final int API_ADD_FRIEND_TOGROUP = 576;
    public static final int API_GRUPPIE_CONTACT_LIST_SEARCH = 577;
    public static final int API_READ_GROUP_REQUEST = 578;
    public static final int API_READ_INDIVIDUAL_REQUEST = 579;
    public static final int API_READ_TEAM_REQUEST = 584;
    public static final int API_ID_ADD_GROUP_COMMENT = 580;
    public static final int API_ID_GET_GROUP_COMMENT = 585;
    public static final int API_MY_TEAM_LIST = 581;
    public static final int API_MY_TEAM_LISTV2 = 581;
    public static final int API_TEAM_POST_LIST = 583;
    public static final int API_GROUP_COMMENT_LIKE = 586;
    public static final int API_REPORT_LIST = 587;
    public static final int API_REPORT = 588;
    public static final int API_SHARE_GROUP_LIST = 589;
    public static final int API_SHARE = 590;
    public static final int API_AUTHOREIZED_USER = 592;
    public static final int API_CHANGE_ADMIN = 593;
    public static final int API_NOTI_LIST = 594;
    public static final int API_PERSONAL_CONTACTS = 595;
    public static final int API_PERSONAL_CHAT = 597;
    public static final int API_ADD_QUE = 599;
    public static final int API_GET_QUE = 600;
    public static final int API_ADD_ANS = 601;
    public static final int API_DELETE_QUE = 602;
    public static final int API_UPDATE_CONTACT_LIST = 604;
    public static final int API_NOTI_SEEN = 605;
    public static final int API_ALLOW_DUPLICATE = 606;
    public static final int API_ALLOW_POST_QUE = 607;
    public static final int API_ALLOW_SHARE = 608;
    public static final int API_VERSION = 610;
    public static final int API_LIKE_LIST = 611;


    public static final int API_ID_REFERRALS_FILTER = 612;
    public static final int API_ID_GROUP_COMMENT_LIKE_LIST = 613;
    public static final int API_ID_TEAM_COMMENT_LIKE_LIST = 614;
    public static final int API_ID_LEAVE_TEAM = 615;
    public static final int API_ID_PUBLIC_GROUP_LIST = 616;
    public static final int API_JOIN_LIST = 617;
    public static final int API_JOIN_GROUP = 618;
    public static final int API_ALLOW_TO_POST_ALL = 619;
    public static final int API_ID_CREATE_TEAM = 620;
    public static final int API_SETTING_DATA = 621;
    public static final int API_ALLOW_TEAM_POST_ALL = 622;
    public static final int API_ALLOW_TEAM_COMMENT_ALL = 623;
    public static final int API_TEAM_SETTING_DATA = 624;
    public static final int API_ALLOW_PERSONAL_REPLY = 625;
    public static final int API_ALLOW_PERSONAL_COMMENT = 626;
    public static final int API_PERSONAL_SETTING_DATA = 627;
    public static final int API_ID_EDIT_TEAM = 628;
    public static final int API_ID_LIKE_TEAM = 629;
    public static final int API_ALLOW_USER_TO_ADD_MEMBER_GROUP = 630;
    public static final int API_ALLOW_OTHER_TO_ADD = 631;
    public static final int API_ENABLE_DISABLE_GPS = 632;
    public static final int API_START_UPDATE_TRIP = 633;
    public static final int API_END_TRIP = 634;
    public static final int API_GET_LOCATION = 635;
    public static final int API_ENABLE_ATTENANCE = 636;
    public static final int API_GET_ATTENDANCE = 637; //314  get new student attandance with last 5 day
    public static final int API_EDIT_ATTENDANCE = 638;
    public static final int API_REMOVE_ATTENDANCE = 639;
    public static final int API_ABSENTIES_ATTENDANCE = 640;
    public static final int API_IMPORT_STUDENTS = 641;
    public static final int API_ID_LIKE_PERSONAL = 642;
    public static final int API_ID_DELETE_TEAM = 643;
    public static final int API_ID_ARCHIVE_TEAM = 644;
    public static final int API_ID_RESTORE_ARCHIVE_TEAM = 645;
    public static final int API_ID_GET_ARCHIVE_TEAM = 646;
    public static final int API_ALLOW_CHANGE_ADMIN = 647;
    public static final int API_ALLOW_ADD_TEAM_POST = 648;
    public static final int API_ALLOW_ADD_TEAM_POST_COMMENT = 649;
    public static final int API_ID_NESTED_TEAM = 650;
    public static final int API_ADD_STUDENT = 651;
    public static final int API_GALLERY_POST = 652;
    public static final int API_GALLERY_ADD = 653;
    public static final int API_GALLERY_DELETE = 654;
    public static final int API_TIMETABLE_POST = 655;
    public static final int API_TIMETABLE_ADD = 656;
    public static final int API_TIMETABLE_DELETE = 657;
    public static final int API_VENDOR_POST = 658;
    public static final int API_VENDOR_ADD = 659;
    public static final int API_VENDOR_DELETE = 660;
    public static final int API_CODE_CONDUCT_POST = 661;
    public static final int API_CODE_CONDUCT_ADD = 662;
    public static final int API_CODE_CONDUCT_DELETE = 663;
    public static final int API_MY_PEOPLE = 664;
    public static final int API_ID_OTP_VERIFY = 665;
    public static final int API_ID_NEW_PASS = 667;
    public static final int API_NOTIFICATION_LIST = 146;
    public static final int API_READ_MORE_GROUP_POST = 147;
    public static final int API_READ_MORE_TEAM_POST = 149;
    public static final int API_READ_MORE_TEAM_POST_COMMENT = 150;
    public static final int API_READ_MORE_GALLERY = 148;
    public static final int API_READ_MORE_INDIVIDUAL = 151;
    public static final int API_ID_READ_UNREAD_USERS = 152;
    public static final int API_CLASSES = 153;
    public static final int API_ADD_CLASSES = 154;
    public static final int API_STUDENTS = 155;
    public static final int API_ADD_ClASS_STUDENTS = 156;
    public static final int API_EDIT_STUDENTS = 370; // changes in 27.6.22
    public static final int API_DELETE_STUDENTS = 158;
    public static final int API_ATTENDANCE_REPORT = 159;

    public static final int API_ADD_COURSE = 310;
    public static final int API_GET_COURSES = 311;
    public static final int API_EDIT_COURSE = 312;
    public static final int API_DELETE_COURSE = 313;

    public static final int API_ATTENDANCE_DETAIL = 160;
    public static final int API_ADD_EVENT = 161;
    public static final int API_GET_EVENTS = 162;
    public static final int API_GET_EVENTS_IN_DAY = 163;
    public static final int API_DELETE_EVENT = 164;
    public static final int API_LEAVE_REQUEST = 165;
    public static final int API_CHANGE_ADMIN_TEAM = 166;
    public static final int API_LEAVE_REQ_FORM = 167;
    public static final int API_BUS_LIST = 168;
    public static final int API_ADD_BUS = 169;
    public static final int API_BUS_STUDENTS = 170;
    public static final int API_BUS_STUDENTS_ADD = 171;
    public static final int API_BUS_STUDENTS_DELETE = 172;
    public static final int API_STAFF = 173;
    public static final int API_STAFF_ADD = 174;
    public static final int API_STAFF_EDIT = 175;
    public static final int API_STAFF_DELETE = 176;
    public static final int API_STAFF_STUDENT_TEAM = 177;
    public static final int ADD_SCHOOL_ACCOUNTANT =376;
    public static final int API_GET_PRESCHOOL_STUDENTS = 178;
    public static final int API_ATTENDANCE_PRESCHOOL_IN = 179;
    public static final int API_ATTENDANCE_PRESCHOOL_OUT = 180;
    public static final int API_TEACHER_CLASS = 181;
    public static final int API_PARENT_KIDS = 182;
    public static final int API_MARK_SHEET = 183;
    public static final int API_MARK_SHEET_LIST = 184;
    public static final int API_MARK_SHEET_DELETE = 185;
    public static final int API_SUBJECTS = 186;
    public static final int API_SUBJECTS_ADD = 187;
    public static final int API_SUBJECTS_UPDATE = 188;
    public static final int API_SUBJECTS_DELETE = 189;
    public static final int API_MARK_CARD_LIST = 190;
    public static final int API_TEAM_SUBJECT_LIST = 191;
    public static final int API_CREATE_MARK_CARD = 192;
    public static final int API_GALLERY_FILE_DELETE = 193;
    public static final int API_GALLERY_FILE_ADD = 194;
    public static final int API_Video_Class = 195;

    public static final int API_EBOOK_REGISTER = 198;
    public static final int API_EBOOK_LIST = 199;
    public static final int API_EBOOK_DELETE = 200;
    public static final int API_UPDATE_EBOOK_IN_CLASS = 201;
    public static final int API_E_BOOK_FOR_TEAM = 202;
    public static final int API_ADD_JISTI_TOKEN = 203;
    public static final int API_ATTENDANCE_SUBJECT = 206;
    public static final int API_ADD_SUBJECT_STAFF = 209;
    public static final int API_SUBJECT_STAFF = 210;
    public static final int API_TT_ADD = 211;
    public static final int API_TT_ADD_SUB_Staff = 212;
    public static final int API_TT_LIST_2 = 213;
    public static final int API_TT_REMOVE = 214;
    public static final int API_TT_REMOVE_DAY = 2141;
    public static final int API_CHAPTER_LIST = 216;
    public static final int API_CHAPTER_ADD = 215;
    public static final int API_CHAPTER_REMOVE = 218;
    public static final int API_TOPIC_REMOVE = 2181;
    public static final int API_UPDATE_SUBJECT_STAFF = 220;
    public static final int API_DELETE_SUBJECT_STAFF = 221;
    public static final int API_TOPIC_STATUS_CHANGE = 222;
    public static final int API_FEES_CREATE = 2222;
    public static final int API_FEES_RES = 223;
    public static final int API_STUDENT_FEES_LIST = 224;
    public static final int API_STUDENT_FEES_ADD = 225;
    public static final int API_HW_ADD = 226;
    public static final int API_HW_LIST = 227;
    public static final int API_SUBMIT_ASSIGNMENT = 228;
    public static final int API_ASSIGNMENT_LIST = 229;
    public static final int API_VERIFY_ASSIGNMENT = 230;
    public static final int API_REASSIGN_ASSIGNMENT = 2301;
    public static final int API_DELETE_ASSIGNMENT_TEACHER = 231;
    public static final int API_DELETE_ASSIGNMENT_STUDENT = 232;
    public static final int API_JISTI_MEETING_JOIN = 233;
    public static final int API_ONLINE_ATTENDANCE_REPORT = 234;
    public static final int API_ONLINE_ATTENDANCE_PUSH = 236;
    public static final int API_UPDATE_EVENT_LIST = 240;
    public static final int API_TEST_EXAM_LIST = 241;
    public static final int API_TEST_EXAM_ADD = 242;
    public static final int API_TEST_EXAM_REMOVE = 243;
    public static final int API_SUBMIT_TEST_PAPER = 244;
    public static final int API_TEST_EXAM_PAPER_LIST = 245;
    public static final int API_TEST_EXAM_PAPER_DELETE_STUDENT = 246;
    public static final int API_TEST_EXAM_PAPER_VERIFY = 247;
    public static final int API_TEST_PAPER_START_EVENT = 248;
    public static final int API_TEST_PAPER_STOP_EVENT = 249;
    public static final int API_TEST_PAPER_LIVE_EVENTS = 250;
    public static final int API_LIVE_CLASS_START = 2500;
    public static final int API_LIVE_CLASS_EVENTS = 251;
    public static final int API_LIVE_CLASS_JOIN = 252;
    public static final int API_LIVE_CLASS_END = 253;
    public static final int API_SEND_MSG_TO_NOTSUBMITTED_STUDENT = 254;
    public static final int API_TALUKS = 258;
    public static final int API_PAID_STUDENT_LIST = 262;
    public static final int API_PAY_FEES_BY_STUDENT = 261;
    public static final int API_APPROVE_FEES = 263;
    public static final int API_DUE_DATE_STATUS = 264;
    public static final int API_EDIT_STUDENT_FEES = 265;
    public static final int API_CREATE_OFFLINE_TEST = 266;
    public static final int API_OFFLINE_TEST_LIST = 267;
    public static final int API_REMOVE_OFFLINE_TEST = 268;
    public static final int API_MARK_CARD_LIST_2 = 269;
    public static final int API_ADD_OBT_MARK = 270;
    public static final int API_CONSTITUENCY = 271;
    public static final int API_GET_BOOTHS = 2711;
    public static final int API_ADD_BOOTH = 272;
    public static final int API_ADD_BOOTH_MEMEBER = 273;
    public static final int API_UPDATE_BOOTH_MEMEBER = 277;
    public static final int API_GET_BOOTH_MEMEBER = 274;
    public static final int API_BOOTH_VOTER_LIST = 275;
    public static final int API_GET_FAMILY_MEMBER = 275;
    public static final int API_CREATE_FAMILY_MEMBER = 276;
    public static final int API_GET_BOOTH_TEAMS = 280;
    public static final int API_GET_MY_BOOTH = 279;
    public static final int API_ISSUE_LIST = 282;
    public static final int API_ISSUE_REGISTER = 281;
    public static final int API_ADD_TASK_FORCE = 283;
    public static final int API_ISSUE_REMOE = 284;
    public static final int API_GET_COORDINATE = 286;
    public static final int API_ADD_COORDINATE = 286;
    public static final int API_SUB_BOOTH_TEAM_LIST = 289;
    public static final int API_ADD_TICKET = 290;
    public static final int API_DELETE_TICKET = 291;
    public static final int API_LIST_TICKET = 292;
    public static final int APPROVED_TICKET = 293;
    public static final int ADD_COMMENT = 294;
    public static final int LIST_COMMENT = 295;
    public static final int ADD_COMMITTEE = 297;
    public static final int LIST_COMMITTEE = 298;
    public static final int UPDATE_COMMITTEE = 299;
    public static final int REMOVE_COMMITTEE = 300;
    public static final int UPDATED_TICKET_LIST_EVENT = 296; //UPDATE EVENT TICKET AND MASTER LIST Booth LIST 301 CODE USED
    public static final int ADMIN_FEEDER_LIST = 307;
    public static final int MASTER_BOOTH_LIST = 301; //UPDATE EVENT TICKET AND MASTER LIST Booth LIST 301 CODE USED
    public static final int WORKER_LIST = 302;
    public static final int WORKER_STREET_LIST = 303;
    public static final int ADD_VOTER_MASTER_LIST = 304;
    public static final int VOTER_MASTER_LIST = 305;
    public static final int API_VOTER_MASTER_DELETE = 306;
    public static final int UPDATE_PHONE_STAFF = 308;
    public static final int API_UPDATE_PHONE_STUDENT = 309;
    public static final int API_TAKE_ATTENDANCE = 315;
    public static final int API_ATTENDANCE_REPORT_OFFLINE = 316;

    public static final int API_ADD_BANNER_LIST = 321;
    public static final int API_MAKE_ADMIN = 324;
    public static final int API_BANNER_LIST = 322;
    public static final int API_VOTER_PROFILE_GET = 326;
    public static final int API_VOTER_PROFILE_UPDATE = 327;
    public static final int API_CASTE_GET = 328;
    public static final int API_SUB_CASTE_GET = 329;
    public static final int API_RELIGION_GET = 330;

    public static final int API_EVENT_SUB_BOOTH_GET = 331;
    public static final int API_EVENT_MY_BOOTH_GET = 332;
    public static final int API_EVENT_SUB_BOOTH_WORKER_GET = 333;
    public static final int API_EVENT_TEAM_POST_GET = 335;
    public static final int API_EVENT_TEAM_GET = 336;
    public static final int API_ADD_SYLLABUS = 337;
    public static final int API_GET_SYLLABUS = 338;
    public static final int API_SEARCH_USER = 342;
    public static final int API_EDIT_SYLLABUS = 339;
    public static final int API_STATUS_PLAN = 340;
    public static final int API_CHANGE_STATUS_PLAN = 341;
    public static final int API_CLASS_OF_STAFF = 346;
    public static final int API_EDIT_ATTENDANCE_STUDENT =  354;
    public static final int API_ATTENDANCE_REPORT_PARENT =  355;
    public static final int API_APPLY_FOR_LEAVE =  356;
    public static final int API_TODAY_DATE_WISE_SYLLBUS_PLAN =  353;
    public static final int API_STAFF_ANALYSIS =  347;

    public static final int API_ID_TYPE_OF_CAMPUS = 358;
    public static final int API_ID_GET_BOARDS_LIST = 359;
    public static final int API_ID_GET_UNIVERSITY_LIST = 360;
    public static final int API_ID_GET_CAMPUS_MEDIUM = 361;
    public static final int API_ID_GET_CLASSES_LIST = 362;
    public static final int API_ID_DO_REGISTER = 357;

    public static final int API_STAFF_ATTENDACNCE =  352;
   // public static final int API_APPROVAL_STAFF_ATTENDACNCE =  348;
    public static final int API_TAKE_STAFF_ATTENDACNCE =  344;
    public static final int API_CHNAGE_STAFF_ATTENDACNCE =  349;

    public static final int API_GET_LEAVE_ATTENDACNCE =  363;
    public static final int API_ADD_CLASS_V2 = 364;
    public static final int API_ASSIGN_TEACHER = 366;
    public static final int API_COMBINED_CLASS = 123;
    public static final int API_GET_CLASS_LIST_V2 = 365;

    public static final int API_ADD_STUDENT_MULTIPLE = 367;
    public static final int API_ADD_STAFF_MULTIPLE = 368;
    public static final int API_PROFESSION_GET = 369;
    public static final int API_GET_SYLLABUS_MASTER = 375;

    public LeafManager() {

    }

    public void doLogin(OnCommunicationListener listListener, LoginRequest request) {
        AppLog.e(TAG, "doLogin->> send data" + new Gson().toJson(request));
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<LoginResponse> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            if (BuildConfig.AppName.equals("Gruppie Campus")){
                model = service.login(request, BuildConfig.APP_ID, request.deviceToken, request.deviceType);
            } else {
                model = service.login(request, BuildConfig.APP_ID, BuildConfig.AppName, request.deviceToken, request.deviceType);
            }
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.loginConstituency(request, BuildConfig.AppName, request.deviceToken, request.deviceType);
        } else {
            model = service.loginIndividual(request, BuildConfig.APP_ID, request.deviceToken, request.deviceType);
        }


        ResponseWrapper<LoginResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LOGIN, new ResponseWrapper.ResponseHandler<LoginResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LoginResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void otpVerify(OnCommunicationListener listListener, OtpVerifyReq request) {
        AppLog.e(TAG, "otpVerify->> send data" + new Gson().toJson(request));
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<OtpVerifyRes> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.otpVerify(request, BuildConfig.APP_ID);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.otpVerifyConstituency(request, BuildConfig.AppName);
        } else {
            model = service.otpVerifyIndividual(request, BuildConfig.APP_ID);
        }

        ResponseWrapper<OtpVerifyRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_OTP_VERIFY, new ResponseWrapper.ResponseHandler<OtpVerifyRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, OtpVerifyRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void newPass(OnCommunicationListener listListener, NewPassReq request) {
        AppLog.e(TAG, "newPass->> send data" + new Gson().toJson(request));
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LoginResponse> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.newPass(request, BuildConfig.APP_ID);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.newPassConstituency(request, BuildConfig.AppName);
        } else {
            model = service.newPassIndividual(request, BuildConfig.APP_ID);
        }

        ResponseWrapper<LoginResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_NEW_PASS, new ResponseWrapper.ResponseHandler<LoginResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LoginResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void doSignUp(OnAddUpdateListener<SignupValidationError> listListener, SignUpRequest request) {
        AppLog.e(TAG, "doSignUp->> send data" + new Gson().toJson(request));
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SignUpResponse> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.signup(request, BuildConfig.APP_ID);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.signupConstituency(request, BuildConfig.AppName);
        } else {
            model = service.signupIndividual(request, BuildConfig.APP_ID);
        }

        ResponseWrapper<SignUpResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<SignupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_REGISTER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<SignupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<SignupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void forgetPassword(OnAddUpdateListener<ForgotPasswordValidationError> listListener, ForgotPasswordRequest request, int count) {
        AppLog.e(TAG, "forgetPassword->> send data" + new Gson().toJson(request));

        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.forgotPassword(request, BuildConfig.APP_ID, count);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.forgotPasswordConstituency(request, BuildConfig.AppName, count);
        } else {
            model = service.forgotPasswordIndividual(request, BuildConfig.APP_ID, count);
        }

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ForgotPasswordValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_FORGOT_PWD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ForgotPasswordValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ForgotPasswordValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addPostFile(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String friendId, AddPostRequestFile request, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        if (type.equals("team")) {
            final Call<BaseResponse> model = service.addPostTeamFileNew(groupId, team_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("group")) {
            final Call<BaseResponse> model = service.addPostGroupFile(groupId, request);
            wrapper = new ResponseWrapper<>(model);
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addPostDesc(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, AddPostRequestDescription request, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        if (type.equals("team")) {
            final Call<BaseResponse> model = service.addPostTeamDescNew(groupId, team_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("group")) {
            final Call<BaseResponse> model = service.addPostGroupDesc(groupId, request);
            wrapper = new ResponseWrapper<>(model);
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void addPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, AddPostRequest request, String type, String friendId, boolean isFromChat) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        AppLog.e(TAG, "Add Group Post: " + request.toString());

        ResponseWrapper<AddPostResponse> wrapper = null;
        if (type.equals("group")) {
            final Call<AddPostResponse> model = service.addPostGroup(groupId, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("team")) {
            final Call<AddPostResponse> model = service.addPostTeam(groupId, team_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else {
            /*if (isFromChat) {*/
            final Call<AddPostResponse> model = service.addPersonalPostFromChat(groupId, friendId, request);
            wrapper = new ResponseWrapper<>(model);
            /*} else {
                final Call<AddPostResponse> model = service.addPersonalPostFromTeam(groupId, team_id, friendId, request);
                wrapper = new ResponseWrapper<>(model);
            }*/
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_POST, new ResponseWrapper.ResponseHandler<AddPostResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, AddPostResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deletePost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String postId, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<BaseResponse> wrapper = null;

        if (type.equals("group")) {
            final Call<BaseResponse> model = service.deleteGroupPost(groupId, postId);
            wrapper = new ResponseWrapper<>(model);
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTeamPost(OnCommunicationListener listListener, String groupId, String team_id, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteTeamPost(groupId, team_id, postId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_ID_DELETE_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deletePersonalPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String postId, boolean isInbox) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        if (isInbox) {
            final Call<BaseResponse> model = service.deletePersonalOutboxPost(groupId, postId);
            wrapper = new ResponseWrapper<>(model);
        } else {
            final Call<BaseResponse> model = service.deletePersonalInboxPost(groupId, postId);
            wrapper = new ResponseWrapper<>(model);
        }


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_PERSONAL_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void removeTeamUser(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String teamId, String uid) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.removeTeamUser(groupId, teamId, uid);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_DELETE_MY_FRIEND, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<BaseValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<BaseValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteUser(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String uid) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<LeaveResponse> wrapper = null;

        final Call<LeaveResponse> model = service.deleteUser(groupId, uid);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_DELETE_USER, new ResponseWrapper.ResponseHandler<LeaveResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, LeaveResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteProPic(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.deletePropic();
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<BaseValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_PROPIC, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<BaseValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<BaseValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void deleteGroupPic(OnCommunicationListener listListener, String groupId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.deleteGrouppic(groupId);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<BaseValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_GROUPPIC, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<BaseValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<BaseValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void changePassword(OnAddUpdateListener<PasswordValidationError> listListener, ChangePasswordRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<ChangePasswordResponse> model;

        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.changePassword(request, BuildConfig.APP_ID);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.changePasswordConstituency(request, BuildConfig.AppName);
        } else {
            model = service.changePasswordIndividual(request, BuildConfig.APP_ID);
        }


        ResponseWrapper<ChangePasswordResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<PasswordValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_CHANGE_PWD, new ResponseWrapper.ResponseHandler<ChangePasswordResponse, ErrorResponseModel<PasswordValidationError>>() {
            @Override
            public void handle200(int apiId, ChangePasswordResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<PasswordValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void changeNumber(OnAddUpdateListener<NumberValidationError> listListener, ChangeNumberRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.changeNumber(request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<NumberValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_EDIT_NUMBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<NumberValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<NumberValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void deleteGroup(OnCommunicationListener listListener, String id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteGRoup(id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_DELETE_GROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }


    public void leaveGroup(OnCommunicationListener listListener, String id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeaveResponse> model = service.leaveGroup(id);
        ResponseWrapper<LeaveResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAVE_GROUP, new ResponseWrapper.ResponseHandler<LeaveResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeaveResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void leaveTeam(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.leaveTeam(group_id, team_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAVE_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }


    public void inviteFriend(OnAddUpdateListener<AddFriendValidationError> listListener, String groupId, String teamId, boolean isFromTeam, AddLeadRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<InviteResponseSingle> model;
        if (isFromTeam) {
            model = service.inviteInTeam(groupId, teamId, request);
        } else {
            model = service.inviteInGroup(groupId, request);
        }

        ResponseWrapper<InviteResponseSingle> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddFriendValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_ADD, new ResponseWrapper.ResponseHandler<InviteResponseSingle, ErrorResponseModel<AddFriendValidationError>>() {
            @Override
            public void handle200(int apiId, InviteResponseSingle response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddFriendValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void addGroup(OnAddUpdateListener<GroupValidationError> listListener, CreateGroupReguest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<CreateGroupResponse> model = service.createGroup(request);
        ResponseWrapper<CreateGroupResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_CREATE_GROUP, new ResponseWrapper.ResponseHandler<CreateGroupResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, CreateGroupResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addTeam(OnAddUpdateListener<GroupValidationError> listListener, String group_id, CreateTeamRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddTeamResponse> model = service.createTeam(group_id, request);
        ResponseWrapper<AddTeamResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_CREATE_TEAM, new ResponseWrapper.ResponseHandler<AddTeamResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddTeamResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addEvent(OnCommunicationListener listListener, String group_id, AddEventReq request, int day, int month, int year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addEvent(request, group_id, day, month, year);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_EVENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getEventList(OnCommunicationListener listListener, String group_id, int month, int year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<EventListRes> model = service.getEventList(group_id, month, year);
        ResponseWrapper<EventListRes> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_GET_EVENTS, new ResponseWrapper.ResponseHandler<EventListRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, EventListRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getUpdateEventList(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<UpdateDataEventRes> model = null;

        if (BuildConfig.AppCategory.equalsIgnoreCase("constituency"))
        {
            model = service.getUpdateEventListConstituency(group_id);
        }else
        {
            model = service.getUpdateEventList(group_id);
        }
        ResponseWrapper<UpdateDataEventRes> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_UPDATE_EVENT_LIST, new ResponseWrapper.ResponseHandler<UpdateDataEventRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, UpdateDataEventRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getEventInDay(OnCommunicationListener listListener, String group_id, int day, int month, int year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<EventInDayRes> model = service.getEventSelectedDay(group_id, day, month, year);
        ResponseWrapper<EventInDayRes> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_GET_EVENTS_IN_DAY, new ResponseWrapper.ResponseHandler<EventInDayRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, EventInDayRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteEvent(OnCommunicationListener listListener, String group_id, String eventId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteEvent(group_id, eventId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_DELETE_EVENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void leaveRequest(OnAddUpdateListener listListener, String group_id, String team_id, LeaveReq leaveReq, String selectedKids) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddPostResponse> model = service.leaveRequest(leaveReq, group_id, team_id, selectedKids);
        ResponseWrapper<AddPostResponse> wrapper = new ResponseWrapper<>(model);
        AppLog.e(TAG, "data : " + leaveReq);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<LeaveErrorResponse>>() {
        }.getType();
        wrapper.execute(API_LEAVE_REQUEST, new ResponseWrapper.ResponseHandler<AddPostResponse, ErrorResponseModel<LeaveErrorResponse>>() {
            @Override
            public void handle200(int apiId, AddPostResponse response) {
                AppLog.e(TAG, "handle200 : " + response);
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<LeaveErrorResponse> error) {
                AppLog.e(TAG, "handle200 : code" + code + ",error : " + error);
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                AppLog.e(TAG, "handleException : Exception" + e);
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void changeTeamAdmin(OnCommunicationListener listListener, String group_id, String team_id, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.changeTeamAdmin(group_id, team_id, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        AppLog.e(TAG, "data : " + "");

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<LeaveErrorResponse>>() {
        }.getType();
        wrapper.execute(API_CHANGE_ADMIN_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<LeaveErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e(TAG, "handle200 : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<LeaveErrorResponse> error) {
                AppLog.e(TAG, "handle200 : code" + code + ",error : " + error);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                AppLog.e(TAG, "handleException : Exception" + e);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void addClass(OnAddUpdateListener<GroupValidationError> listListener, String group_id, ClassResponse.ClassData request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddTeamResponse> model = service.addClass(group_id, request);
        ResponseWrapper<AddTeamResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_CLASSES, new ResponseWrapper.ResponseHandler<AddTeamResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddTeamResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addSubject(OnAddUpdateListener<GroupValidationError> listListener, String group_id, SubjectResponse.SubjectData request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddTeamResponse> model = service.addSubject(group_id, request);
        ResponseWrapper<AddTeamResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_SUBJECTS_ADD, new ResponseWrapper.ResponseHandler<AddTeamResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddTeamResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void editSubject(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String subjectId, SubjectResponse.SubjectData request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editSubject(group_id, subjectId, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_SUBJECTS_UPDATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteSubjects(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String subjectId) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteSubject(group_id, subjectId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_SUBJECTS_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void editTeam(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String team_id, CreateTeamRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editTeam(group_id, team_id, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_EDIT_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTeam(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String team_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteTeam(group_id, team_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void archiveTeam(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.archiveTeam(group_id, team_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_ID_ARCHIVE_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void restoreArchiveTeam(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.restoreArchiveTeam(group_id, team_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_ID_RESTORE_ARCHIVE_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void getArchiveTeams(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MyTeamsResponse> model = service.getArchiveTeams(group_id);
        ResponseWrapper<MyTeamsResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_ID_ARCHIVE_TEAM, new ResponseWrapper.ResponseHandler<MyTeamsResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, MyTeamsResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void getNestedTeams(OnCommunicationListener listListener, String group_id, String teamId, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MyTeamsResponse> model = service.getNestedTeams(group_id, teamId, userId);
        ResponseWrapper<MyTeamsResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<GroupValidationError>() {
        }.getType();
        wrapper.execute(API_ID_NESTED_TEAM, new ResponseWrapper.ResponseHandler<MyTeamsResponse, GroupValidationError>() {
            @Override
            public void handle200(int apiId, MyTeamsResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, GroupValidationError error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void updateProfileDetails(OnAddUpdateListener<ProfileValidationError> listListener, ProfileItemUpdate request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateProfile(request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ProfileValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_UPDATE_PROFILE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ProfileValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ProfileValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void editGroup(OnAddUpdateListener<GroupValidationError> listListener, CreateGroupReguest request, String groupId) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editGroup(groupId, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_EDIT_GROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void logout(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.logout();
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LOGOUT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "Logout:" + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void getGroupList(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupResponse> model = service.getGroupList();
        ResponseWrapper<GroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GROUP_LIST, new ResponseWrapper.ResponseHandler<GroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getPublicGroupList(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PublicGroupResponse> model = service.getPublicGroupList();
        ResponseWrapper<PublicGroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_PUBLIC_GROUP_LIST, new ResponseWrapper.ResponseHandler<PublicGroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PublicGroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void getProfileDetails(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ProfileResponse> model = service.getProfileDetails();
        ResponseWrapper<ProfileResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GET_PROFILE, new ResponseWrapper.ResponseHandler<ProfileResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ProfileResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void getGroupDetail(final OnCommunicationListener listListener, String id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupDetailResponse> model = service.getGroupDetail(id);
        ResponseWrapper<GroupDetailResponse> wrapper = new ResponseWrapper<>(model);
        wrapper.execute(API_ID_GROUP_DETAIL, new ResponseWrapper.ResponseHandler<GroupDetailResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GroupDetailResponse response) {
                AppLog.e(TAG, "response: " + response);
                AppLog.e(TAG, "response.body(): " + response.data);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    private void logoutApp(Context mContext) {
        AppLog.e("Logout", "onSuccessCalled");
        LeafPreference.getInstance(mContext).clearData();
        Intent intent = new Intent(mContext, LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }


    public void getNestedFriends(OnCommunicationListener listListener, String groupId, String leadId, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getNestedFriends(groupId, leadId, page);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_REFERRALS, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getReadUnreadUser(OnCommunicationListener listListener, String groupId, String teamId, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadUnreadResponse> model = service.getReadUnreadUser(groupId, teamId, postId);
        ResponseWrapper<ReadUnreadResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_READ_UNREAD_USERS, new ResponseWrapper.ResponseHandler<ReadUnreadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadUnreadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getRefferalFilter(OnCommunicationListener listListener, String groupId, String leadId, String searchStr) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getReferrersFilter(groupId, leadId, searchStr);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_REFERRALS_FILTER, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void getAllUsersList(OnCommunicationListener listListener, String id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<UserListResponse> model = service.getAllUsersList(id, page);
        ResponseWrapper<UserListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALL_USERS_LIST, new ResponseWrapper.ResponseHandler<UserListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, UserListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getAllUsersListBySearch(OnCommunicationListener listListener, String id, String page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<UserListResponse> model = service.getAllUsersListBySearch(id, page);
        ResponseWrapper<UserListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALL_USERS_LIST, new ResponseWrapper.ResponseHandler<UserListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, UserListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getAllContactsList(OnCommunicationListener listListener/*, int page*/) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ContactListResponse> model = service.getAllContactListNoPaginate(GroupDashboardActivityNew.groupId);
        ResponseWrapper<ContactListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALL_CONTACT_LIST, new ResponseWrapper.ResponseHandler<ContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getAllContactsListBySearch(OnCommunicationListener listListener, String page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ContactListResponse> model = service.getAllContactListBySearch(page);
        ResponseWrapper<ContactListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALL_CONTACT_LIST, new ResponseWrapper.ResponseHandler<ContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void getGruppieContactList(OnCommunicationListener listListener, String groupId, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GruppieContactListResponse> model = service.getGruppieContactList(groupId, page);
        ResponseWrapper<GruppieContactListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GRUPPIE_CONTACT_LIST, new ResponseWrapper.ResponseHandler<GruppieContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GruppieContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void getGruppieContactListBySearch(OnCommunicationListener listListener, String groupId, String page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GruppieContactListResponse> model = service.getGruppieContactListBySearch(groupId, page);
        ResponseWrapper<GruppieContactListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GRUPPIE_CONTACT_LIST_SEARCH, new ResponseWrapper.ResponseHandler<GruppieContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GruppieContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void getLeadsList(OnCommunicationListener listListener, String id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getLeadsList(id, page);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAD_LIST, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getLeadsListBySearch(OnCommunicationListener listListener, String id, String page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getLeadsListBySearch(id, page);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAD_LIST_SEARCH, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getMyLeadsList(OnCommunicationListener listListener, String id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getMyLeads(id, 1);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_MYLEAD_LIST, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void getGeneralPosts(OnCommunicationListener listListener, String id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PostResponse> model = service.getGeneralPosts(id, page);
        ResponseWrapper<PostResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GENERAL_POST, new ResponseWrapper.ResponseHandler<PostResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getFavPosts(OnCommunicationListener listListener, String id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PostResponse> model = service.getFavPosts(id, page);
        ResponseWrapper<PostResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_FAV_POST, new ResponseWrapper.ResponseHandler<PostResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getTeamPosts(OnCommunicationListener listListener, String id, boolean isInbox, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PostResponse> model;
        if (isInbox) {
            model = service.getTeamInboxPosts(id, page);
        } else {
            model = service.getTeamOutboxPosts(id, page);
        }

        ResponseWrapper<PostResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_POST, new ResponseWrapper.ResponseHandler<PostResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void setFav(OnCommunicationListener listListener, String groupId, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.setFavourite(groupId, postId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_FAV, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void setLikes(OnCommunicationListener listListener, String groupId, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.setLike(groupId, postId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LIKE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void setTeamLike(OnCommunicationListener listListener, String groupId, String teamId, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.setLikeTeam(groupId, teamId, postId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LIKE_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void setPersonalLike(OnCommunicationListener listListener, String groupId, String postId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.setPersonalLike(groupId, postId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LIKE_PERSONAL, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void allowPost(OnCommunicationListener listListener, String groupId, String uId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowPost(groupId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ALLOW_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowAddOtherMember(OnCommunicationListener listListener, String groupId, String teamId, String uId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowAddOtherMember(groupId, teamId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ALLOW_OTHER_TO_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void allowTeamPostComment(OnCommunicationListener listListener, String groupId, String teamId, String uId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowTeamPostComment(groupId, teamId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ALLOW_ADD_TEAM_POST_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void allowTeamPost(OnCommunicationListener listListener, String groupId, String teamId, String uId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowTeamPost(groupId, teamId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ALLOW_ADD_TEAM_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void notAllowPost(OnCommunicationListener listListener, String groupId, String uId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.notAllowPost(groupId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_NOT_ALLOW_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addMultipleFriendToGroup(OnCommunicationListener listListener, String groupId, String uId, Context context) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addMultipleFriendToGroup(groupId, uId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_FRIEND_TOGROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void readGroupRequest(OnCommunicationListener listListener, String group_id, String post_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadGroupPostResponse> model = service.readGroupRequest(group_id, post_id/*, request*/);
        ResponseWrapper<ReadGroupPostResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ForgotPasswordValidationError>>() {
        }.getType();

        wrapper.execute(API_READ_GROUP_REQUEST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ForgotPasswordValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ForgotPasswordValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void readPostRequest(OnCommunicationListener listListener, String group_id, String team_id, String post_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadTeamPostResponse> model = service.readTeamRequest(group_id, team_id, post_id/*, request*/);
        ResponseWrapper<ReadTeamPostResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();

        wrapper.execute(API_READ_TEAM_REQUEST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Team Discussion Posts

    public void myTeamList(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MyTeamsResponse> model = service.getMyTeams(group_id);
        ResponseWrapper<MyTeamsResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MY_TEAM_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Team Upadte UI Posts

    public void myTeamListV2(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseTeamv2Response> model = service.getMyTeamsv2(group_id);
        ResponseWrapper<BaseTeamv2Response> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MY_TEAM_LISTV2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // use for view discussion
    public void myTeamListNew(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MyTeamsResponse> model = service.getMyTeamsNew(group_id);
        ResponseWrapper<MyTeamsResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MY_TEAM_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMyPeople(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getMyPeople(group_id);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MY_PEOPLE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getClasses(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassResponse> model = service.getClasses(group_id);
        ResponseWrapper<ClassResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_CLASSES, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }



    public void getVideoClasses(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<VideoClassResponse> model = service.getVideoClasses(group_id);
        ResponseWrapper<VideoClassResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_Video_Class, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void attendancePush(OnCommunicationListener listListener, String group_id, String teamId, MeetingStatusModelApi req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.attendancePush(group_id, teamId, req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<BaseResponse>>() {
        }.getType();

        wrapper.execute(API_ONLINE_ATTENDANCE_PUSH, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getEBooks(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<EBooksResponse> model = service.getEBooks(group_id);
        ResponseWrapper<EBooksResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EBOOK_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getEBooksForTeam(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<EBooksTeamResponse> model = service.getEBooksForTeam(group_id, teamId);
        ResponseWrapper<EBooksTeamResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_E_BOOK_FOR_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateEBookInClass(OnCommunicationListener listListener, String group_id, String teamId, String eBookId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateEBookInClass(group_id, teamId, eBookId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_UPDATE_EBOOK_IN_CLASS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addEBook(OnCommunicationListener listListener, String groupId, AddEbookReq addEbookReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addEBook(groupId, addEbookReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EBOOK_REGISTER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addEBook2(OnCommunicationListener listListener, String groupId, String teamId, AddEbookReq2 addEbookReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addEBook2(groupId, teamId, addEbookReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EBOOK_REGISTER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }




    public void addJitiToken(OnCommunicationListener listListener, String groupId, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addJitiToken(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_JISTI_TOKEN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteEBook(OnCommunicationListener listListener, String groupId, String bookId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteEBook(groupId, bookId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EBOOK_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteEBookTeam(OnCommunicationListener listListener, String groupId, String teamId, String bookId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteEBookTeam(groupId, teamId, bookId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EBOOK_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getSubjects(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubjectResponse> model = service.getSubjects(group_id);
        ResponseWrapper<SubjectResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SUBJECTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getSubjectStaff(OnCommunicationListener listListener, String group_id, String team_id, String option) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<SubjectStaffResponse> model;
        if (!TextUtils.isEmpty(option)) {
            model = service.getSubjectStaffMore(group_id, team_id, option);
        } else {
            model = service.getSubjectStaff(group_id, team_id);
        }
        ResponseWrapper<SubjectStaffResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SUBJECT_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }


            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getTTNew(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TimeTableList2Response> model = service.getTTNew(group_id, team_id);
        ResponseWrapper<TimeTableList2Response> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TT_LIST_2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getTTNewDayWise(OnCommunicationListener listListener, String group_id, String team_id, String day) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TimeTableList2Response> model = service.getTTNewDayWise(group_id, team_id, day);
        ResponseWrapper<TimeTableList2Response> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TT_LIST_2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addSubjectStaff(OnCommunicationListener listListener, String group_id, String team_id, AddSubjectStaffReq addSubjectStaffReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addSubjectStaff(group_id, team_id, addSubjectStaffReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_SUBJECT_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateSubjectStaff(OnCommunicationListener listListener, String group_id, String team_id, String subject_id, AddSubjectStaffReq addSubjectStaffReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateSubjectStaff(group_id, team_id, subject_id, addSubjectStaffReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_UPDATE_SUBJECT_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteSubjectStaff(OnCommunicationListener listListener, String group_id, String team_id, String subject_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteSubjectStaff(group_id, team_id, subject_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_DELETE_SUBJECT_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addSubjectStaffTT(OnCommunicationListener listListener, String group_id, String team_id, String subject_with_staff_id, String staff_id, SubStaffTTReq addSubjectStaffReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addSubjectStaffTT(group_id, team_id, subject_with_staff_id, staff_id, addSubjectStaffReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TT_ADD_SUB_Staff, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMarkCardList(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MarkCardListResponse> model = service.getMarkCardList(group_id, teamId);
        ResponseWrapper<MarkCardListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MARK_CARD_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMarkCardStudents(OnCommunicationListener listListener, String group_id, String teamId, String markCardId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StudentMarkCardListResponse> model = service.getMarkCardStudents(group_id, teamId, markCardId);
        ResponseWrapper<StudentMarkCardListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getTeamSubjects(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubjectTeamResponse> model = service.getTeamSubjects(group_id, teamId);
        ResponseWrapper<SubjectTeamResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TEAM_SUBJECT_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void createMarkCard(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String teamId, AddMarkCardReq request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddTeamResponse> model = service.createMarkCard(group_id, teamId, request);
        ResponseWrapper<AddTeamResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_CREATE_MARK_CARD, new ResponseWrapper.ResponseHandler<AddTeamResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddTeamResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getTeacherClasses(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassResponse> model = service.getTeacherClasses(group_id);
        ResponseWrapper<ClassResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TEACHER_CLASS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getParentKids(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ParentKidsResponse> model = service.getParentKids(group_id);
        ResponseWrapper<ParentKidsResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_PARENT_KIDS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getParentKidsNew(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassResponse> model = service.getParentKidsNew(group_id);
        ResponseWrapper<ClassResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_PARENT_KIDS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getStaff(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StaffResponse> model = service.getStaff(group_id);
        ResponseWrapper<StaffResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }


            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getSubjectStaffTT(OnCommunicationListener listListener, String group_id, String team_id, SubStaffTTReq subStaffTTReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubjectStaffTTResponse> model = service.getSubjectStaffTT(group_id, team_id, subStaffTTReq);
        ResponseWrapper<SubjectStaffTTResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TT_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getStudents(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StudentRes> model = service.getStudents(group_id, teamId);
        ResponseWrapper<StudentRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getNotificationList(OnCommunicationListener listener, String groupId,String Page) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<NotificationListRes> model = service.getNotificationList(groupId,Page);
        ResponseWrapper<NotificationListRes> wrapper = new ResponseWrapper<>(model);

        AppLog.e(TAG, model.request().url().toString());

        wrapper.execute(API_NOTIFICATION_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

//        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
//        }.getType();
//
//        wrapper.execute(API_NOTIFICATION_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
//
//
//
//
//            @Override
//            public void handle200(int apiId, BaseResponse response) {
//                if (mOnCommunicationListener != null) {
//                    mOnCommunicationListener.onSuccess(apiId, response);
//                }
//            }
//
//            @Override
//            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
//                if (mOnCommunicationListener != null) {
//                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
//
//                }
//            }
//
//            @Override
//            public void handleException(int apiId, Exception e) {
//                if (mOnCommunicationListener != null) {
//                    mOnCommunicationListener.onException(apiId, e.getMessage());
//                }
//            }
//        }, serviceErrorType);


    }

    public void getNestPeople(OnCommunicationListener listListener, String group_id, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getNestPeople(group_id, userId);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MY_PEOPLE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void teamPostGetApi(OnCommunicationListener listListener, String group_id, String team_id, Context context, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TeamPostGetResponse> model = service.teamPostGet(group_id, team_id, page);
        ResponseWrapper<TeamPostGetResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_TEAM_POST_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Group Comments

    public void addGroupComment(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String post_id, String comment_id, AddGroupCommentRequest request, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<AddCommentRes> wrapper = null;
        AppLog.e(TAG, "send_data : " + request);

        if (type.equals("comment_add")) {
            final Call<AddCommentRes> model = service.addGroupComment(group_id, post_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("comment_edit")) {
            final Call<AddCommentRes> model = service.editGroupComment(group_id, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_add")) {
            final Call<AddCommentRes> model = service.addGroupCommentReply(group_id, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_delete")) {
            final Call<AddCommentRes> model = service.deleteComment(group_id, post_id, comment_id);
            wrapper = new ResponseWrapper<>(model);
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();

        wrapper.execute(API_ID_ADD_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<AddCommentRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddCommentRes response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getGroupComment(OnCommunicationListener listListener, String group_id, String post_id, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllGroupComments(group_id, post_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getGroupReply(OnCommunicationListener listListener, String group_id, String post_id, String comment_id, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllGroupCommentReplies(group_id, post_id, comment_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void likeUnlikeGroupComment(OnCommunicationListener listListener, String group_id, String post_id, String comment_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.likeComment(group_id, post_id, comment_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_GROUP_COMMENT_LIKE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Team Comments

    public void addTeamComment(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String team_id, String post_id, String comment_id, AddGroupCommentRequest request, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<AddCommentRes> wrapper = null;

        if (type.equals("comment_add")) {
            final Call<AddCommentRes> model = service.addTeamComment(group_id, team_id, post_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("comment_edit")) {
            final Call<AddCommentRes> model = service.editTeamComment(group_id, team_id, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_add")) {
            final Call<AddCommentRes> model = service.addTeamCommentReply(group_id, team_id, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_delete")) {
            final Call<AddCommentRes> model = service.deleteTeamComment(group_id, team_id, post_id, comment_id);
            wrapper = new ResponseWrapper<>(model);
        }


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_ADD_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<AddCommentRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddCommentRes response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getTeamComment(OnCommunicationListener listListener, String group_id, String team_id, String post_id, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllTeamComments(group_id, team_id, post_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getTeamReply(OnCommunicationListener listListener, String group_id, String team_id, String post_id, String comment_id, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllTeamCommentReplies(group_id, team_id, post_id, comment_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void likeUnlikeTeamComment(OnCommunicationListener listListener, String group_id, String team_id, String post_id, String comment_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.likeTeamComment(group_id, team_id, post_id, comment_id);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_GROUP_COMMENT_LIKE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Personal Comments

    public void addPersonalComment(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String post_id, String comment_id, String selectedUserId, AddGroupCommentRequest request, String type) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<AddCommentRes> wrapper = null;

        if (type.equals("comment_add")) {
            final Call<AddCommentRes> model = service.addPersonalComment(group_id, selectedUserId, post_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("comment_edit")) {
            final Call<AddCommentRes> model = service.editPersonalComment(group_id, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_add")) {
            final Call<AddCommentRes> model = service.addPersonalCommentReply(group_id, selectedUserId, post_id, comment_id, request);
            wrapper = new ResponseWrapper<>(model);
        } else if (type.equals("reply_delete")) {
            final Call<AddCommentRes> model = service.deletePersonalComment(group_id, selectedUserId, post_id, comment_id);
            wrapper = new ResponseWrapper<>(model);
        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();

        wrapper.execute(API_ID_ADD_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<AddCommentRes, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddCommentRes response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getPersonalComment(OnCommunicationListener listListener, String group_id, String post_id, String userId, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllPersonalComments(group_id, userId, post_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getPersonalReply(OnCommunicationListener listListener, String group_id, String post_id, String comment_id, String userId, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupCommentResponse> model = service.getAllPersonalCommentReplies(group_id, userId, post_id, comment_id, page_num);
        ResponseWrapper<GroupCommentResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ID_GET_GROUP_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getReportList(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReportResponse> model = service.getReportList();
        ResponseWrapper<ReportResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_REPORT_LIST, new ResponseWrapper.ResponseHandler<ReportResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReportResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void reportPost(OnCommunicationListener listListener, String group_id, String post_id, int reason_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.reportGroupPost(group_id, post_id, reason_id);
        wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_REPORT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    // Share APIS

    public void getGroupListShare(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ShareGroupResponse> model = service.getGroupListShare();
        ResponseWrapper<ShareGroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_SHARE_GROUP_LIST, new ResponseWrapper.ResponseHandler<ShareGroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ShareGroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void shareGroupPost(OnCommunicationListener listListener, AddPostRequestDescription request, String group_id, String post_id, String selected_ids, String selected_group_id, String type) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        final Call<BaseResponse> model;
        switch (type) {
            case "team":
                AppLog.e("report", "if is + " + type);
                model = service.shareTeamPost(group_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "team_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedTeamPost(request, group_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "group":
                AppLog.e("report", "else is + " + type);
                model = service.shareGroupPost(group_id, post_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "group_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedGroupPost(request, group_id, post_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "personal_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedPersonalPost(request, group_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_SHARE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getGroupTeamListShare(final OnCommunicationListener listListener, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ShareGroupResponse> model = service.getTeamGroupListShare(page);
        ResponseWrapper<ShareGroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_SHARE_GROUP_LIST, new ResponseWrapper.ResponseHandler<ShareGroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ShareGroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getSelectTeamListShare(final OnCommunicationListener listListener, String group_id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ShareGroupResponse> model = service.getTeamSelectListShare(group_id, page);
        ResponseWrapper<ShareGroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_SHARE_GROUP_LIST, new ResponseWrapper.ResponseHandler<ShareGroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ShareGroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void shareTeamPost(OnCommunicationListener listListener, AddPostRequestDescription request, String group_id, String team_id, String post_id, String selected_ids, String selected_group_id, String type) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        final Call<BaseResponse> model;
        switch (type) {
            case "team":
                AppLog.e("report", "if is + " + type);
                model = service.shareTeamPostToTeam(group_id, team_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "team_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedTeamPostToTeam(request, group_id, team_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "group":
                AppLog.e("report", "else is + " + type);
                model = service.shareTeamPostToGroup(group_id, team_id, post_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "group_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedTeamPostToGroup(request, group_id, team_id, post_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "personal_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedPersonalPost(request, group_id, team_id, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_SHARE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void sharePersonalPost(OnCommunicationListener listListener, AddPostRequestDescription request, String group_id, String post_id, String selected_ids, String selected_group_id, String type) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        final Call<BaseResponse> model;
        switch (type) {
            case "team_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedPersonalPostToTeam(request, group_id, GroupDashboardActivityNew.selectedUserInChat, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "group_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedPersonalPostToGroup(request, group_id, GroupDashboardActivityNew.selectedUserInChat, post_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

            case "personal_edit":
                AppLog.e("report", "else is + " + type);
                model = service.shareEditedPersonalPostToFriends(request, group_id, GroupDashboardActivityNew.selectedUserInChat, post_id, selected_group_id, selected_ids);
                wrapper = new ResponseWrapper<>(model);
                break;

        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_SHARE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void joinList(final OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<JoinListResponse> model = service.getJoinList(group_id);
        ResponseWrapper<JoinListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_JOIN_LIST, new ResponseWrapper.ResponseHandler<JoinListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, JoinListResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void joinGroup(final OnCommunicationListener listListener, String group_id, String friendIds) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.joinGroup(group_id, friendIds);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_JOIN_GROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void joinGroupDirect(final OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.joinGroupDirect(group_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_JOIN_GROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getTeamMember(OnCommunicationListener listListener, String group_id, String team_id, boolean fromChat) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model;

        if (fromChat) {
            model = service.getTeamMemberFromChat(group_id);
        } else {
            model = service.getTeamMember(group_id, team_id);
        }


        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAD_LIST, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void leaveForm(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model;
        model = service.leaveForm(group_id, team_id);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_LEAVE_REQ_FORM, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getLeadsListBySearch_new(OnCommunicationListener listListener, String group_id, String team_id, String page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeadResponse> model = service.getPersonalBySearch(group_id, team_id, page);
        ResponseWrapper<LeadResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_ID_LEAD_LIST_SEARCH, new ResponseWrapper.ResponseHandler<LeadResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LeadResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    // Authorized users list

    public void getAuthorizedList(OnCommunicationListener listListener, String group_id, int page_num) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AuthorizedUserResponse> model = service.getAuthorizedList(group_id, page_num);
        ResponseWrapper<AuthorizedUserResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_AUTHOREIZED_USER, new ResponseWrapper.ResponseHandler<AuthorizedUserResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, AuthorizedUserResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getAuthorizedListFromSearch(OnCommunicationListener listListener, String group_id, String text) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AuthorizedUserResponse> model = service.getAuthorizedListBySearch(group_id, text);
        ResponseWrapper<AuthorizedUserResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_AUTHOREIZED_USER, new ResponseWrapper.ResponseHandler<AuthorizedUserResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, AuthorizedUserResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void changeAdmin(OnCommunicationListener listListener, String group_id, String user_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper;

        final Call<BaseResponse> model = service.changeAdmin(group_id, user_id);
        wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_CHANGE_ADMIN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + " : " + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getNotifications(OnCommunicationListener listListener, int page, int notiType) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<NotificationResponse> model = null;// = service.getNotifications(page);
        switch (notiType) {
            case 1:
                model = service.getNotificationsGroup(page);
                break;

            case 2:
                model = service.getNotificationsTeam(page);
                break;

            case 3:
                model = service.getNotificationsPersonal(page);
                break;
        }

//        final Call<NotificationResponse> model = service.getNotifications(page);
        ResponseWrapper<NotificationResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_NOTI_LIST, new ResponseWrapper.ResponseHandler<NotificationResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, NotificationResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getPersonalContacts(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<PersonalPostResponse> model = service.getInbox(group_id);
        ResponseWrapper<PersonalPostResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_PERSONAL_CONTACTS, new ResponseWrapper.ResponseHandler<PersonalPostResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, PersonalPostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getPersonalChat(OnCommunicationListener listListener, String group_id, String friend_id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<PostResponse> model = service.getPersonalListChat(group_id, friend_id, page);
        ResponseWrapper<PostResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_PERSONAL_CHAT, new ResponseWrapper.ResponseHandler<PostResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, PostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deletePersonalPostChat(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String friend_id, String postId) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper;
        final Call<BaseResponse> model = service.deletePersonalChatPost(groupId, friend_id, postId);
        wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ID_DELETE_PERSONAL_POST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    // Questions Apis

    public void getQuestions(OnCommunicationListener listListener, String group_id, int page) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<QuestionResponse> model = service.getQuestions(group_id, page);
//        final Call<NotificationResponse> model = service.getNotifications(page);
        ResponseWrapper<QuestionResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_QUE, new ResponseWrapper.ResponseHandler<QuestionResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, QuestionResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addQueImage(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String post_id, AddPostRequestFile_Friend request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.addQueImage(request, groupId, post_id);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_QUE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addQueVideo(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String post_id, AddPostRequestVideo_Friend request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.addQueVideo(request, groupId, post_id);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_QUE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addAnsImage(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String que_id, AddPostRequestFile_Friend request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.addAnsImage(request, groupId, que_id);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_ANS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addAnsVideo(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String post_id, AddPostRequestVideo_Friend request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<BaseResponse> wrapper = null;

        final Call<BaseResponse> model = service.addAnsVideo(request, groupId, post_id);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_ANS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteQue(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String question_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper;
        final Call<BaseResponse> model = service.deleteQue(groupId, question_id);
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_DELETE_QUE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateContactsList(OnCommunicationListener listListener, String friendId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ContactListResponse> model = service.updateContactList(friendId);
        ResponseWrapper<ContactListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_UPDATE_CONTACT_LIST, new ResponseWrapper.ResponseHandler<ContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void updatedLeadsList(OnCommunicationListener listListener, String id, String friendIds) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ContactListResponse> model = service.updateFriendList(id, friendIds);
        ResponseWrapper<ContactListResponse> wrapper = new ResponseWrapper<>(model);


        wrapper.execute(API_UPDATE_CONTACT_LIST, new ResponseWrapper.ResponseHandler<ContactListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ContactListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void seeNotifications(OnCommunicationListener listListener, String notificationId, int type) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        ResponseWrapper<BaseResponse> wrapper = null;
        final Call<BaseResponse> model;
        switch (type) {
            case 1:
                AppLog.e("report", "if is + " + type);
                model = service.groupNotiSeen(notificationId);
                wrapper = new ResponseWrapper<>(model);
                break;

            case 2:
                AppLog.e("report", "else is + " + type);
                model = service.teamNotiSeen(notificationId);
                wrapper = new ResponseWrapper<>(model);
                break;

            case 3:
                AppLog.e("report", "else is + " + type);
                model = service.personalNotiSeen(notificationId);
                wrapper = new ResponseWrapper<>(model);
                break;

        }

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_NOTI_SEEN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void inviteMultipleFriends(Fragment_PhoneContacts listListener, String groupId, ArrayList<String> users, boolean isFromTeam, String teamId) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        ResponseWrapper<InviteResponse> wrapper = null;
        Call<InviteResponse> model;
        if (isFromTeam) {
            model = service.inviteMultipleFriendsInTeam(groupId, teamId, users);
        } else {
            model = service.inviteMultipleFriendsInGroup(groupId, users);
        }
        wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<ErrorResponse>>() {
        }.getType();
        wrapper.execute(API_ADD_ANS, new ResponseWrapper.ResponseHandler<InviteResponse, ErrorResponseModel<ErrorResponse>>() {
            @Override
            public void handle200(int apiId, InviteResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<ErrorResponse> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void youtubeToken(OnCommunicationListener listListener) {
        AppLog.e("YOTU", "youtubeToken");
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClientYoutube();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<YoutubeTokenResponse> model = service.youtubeToken();
        ResponseWrapper<YoutubeTokenResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_NOTI_SEEN, new ResponseWrapper.ResponseHandler<YoutubeTokenResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, YoutubeTokenResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void allowDuplicate(OnCommunicationListener listListener, String groupId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.allowDuplicate(groupId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ALLOW_DUPLICATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void allowPostQue(OnCommunicationListener listListener, String groupId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.allowPostQue(groupId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ALLOW_POST_QUE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void allowShare(OnCommunicationListener listListener, String groupId) {
        AppLog.e("YOTU", "youtubeToken");
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.allowShare(groupId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_ALLOW_SHARE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void doNext(OnCommunicationListener listener, NumberExistRequest request) {
        AppLog.e(TAG, "send data ->" + new Gson().toJson(request));
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<NumberExistResponse> model;
        if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.next(request, BuildConfig.APP_ID);
        } else if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            model = service.nextConstituency(request, BuildConfig.AppName);
        } else {
            model = service.nextIndividual(request, BuildConfig.APP_ID);
        }


        ResponseWrapper<NumberExistResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LOGIN, new ResponseWrapper.ResponseHandler<NumberExistResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, NumberExistResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void notInGruppie(OnCommunicationListener listener, String group_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<NotInGruppieResponse> model = service.notInGruppie(group_id);
        ResponseWrapper<NotInGruppieResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_LOGIN, new ResponseWrapper.ResponseHandler<NotInGruppieResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, NotInGruppieResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void versionCheck(OnCommunicationListener listener) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<VersionCheckResponse> model = service.versionCheck();
        ResponseWrapper<VersionCheckResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_VERSION, new ResponseWrapper.ResponseHandler<VersionCheckResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, VersionCheckResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void likeList(OnCommunicationListener listener, String group_id, String post_id, int page) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LikeListResponse> model = service.likeList(group_id, post_id, page);
        ResponseWrapper<LikeListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_LIKE_LIST, new ResponseWrapper.ResponseHandler<LikeListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LikeListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void likeListTeam(OnCommunicationListener listener, String group_id, String team_id, String post_id, int page) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LikeListResponse> model = service.likeListTeam(group_id, team_id, post_id, page);
        ResponseWrapper<LikeListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_LIKE_LIST, new ResponseWrapper.ResponseHandler<LikeListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LikeListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void groupCommentLikeList(OnCommunicationListener listener, String group_id, String post_id, String comment_id, int page) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LikeListResponse> model = service.groupCommentLikeList(group_id, post_id, comment_id, page);
        ResponseWrapper<LikeListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GROUP_COMMENT_LIKE_LIST, new ResponseWrapper.ResponseHandler<LikeListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LikeListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getSettingsData(OnCommunicationListener listener, String group_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SettingRes> model = service.getSettingData(group_id);
        ResponseWrapper<SettingRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_SETTING_DATA, new ResponseWrapper.ResponseHandler<SettingRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, SettingRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void teamCommentLikeList(OnCommunicationListener listener, String group_id, String team_id, String post_id, String comment_id, int page) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LikeListResponse> model = service.teamCommentLikeList(group_id, team_id, post_id, comment_id, page);
        ResponseWrapper<LikeListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_TEAM_COMMENT_LIKE_LIST, new ResponseWrapper.ResponseHandler<LikeListResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LikeListResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowPostToAll(OnCommunicationListener listener, String groupId) {
        AppLog.e(TAG, "doNext->> group id ->" + groupId);
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowToPostAll(groupId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_TO_POST_ALL, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowChangeAdmin(OnCommunicationListener listener, String groupId) {
        AppLog.e(TAG, "doNext->> group id ->" + groupId);
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowChangeAdmin(groupId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_CHANGE_ADMIN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowPersonalReply(OnCommunicationListener listener, String groupId, String userId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowPersonalReply(groupId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_PERSONAL_REPLY, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowPersonalComment(OnCommunicationListener listener, String groupId, String userId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowPersonalComment(groupId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_PERSONAL_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getPersonalSettingsData(OnCommunicationListener listener, String group_id, String userId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PersonalSettingRes> model = service.getPersonalSettingData(group_id, userId);
        ResponseWrapper<PersonalSettingRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_PERSONAL_SETTING_DATA, new ResponseWrapper.ResponseHandler<PersonalSettingRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PersonalSettingRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowTeamPostAll(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowTeamPostAll(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_TEAM_POST_ALL, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void allowTeamCommentAll(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.allowTeamCommentAll(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_TEAM_COMMENT_ALL, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void allowUsersToAddTeamMember(OnCommunicationListener listener, String groupId, String teamId, boolean isAllow) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        Call<BaseResponse> model;

        if (isAllow) {
            model = service.allowUsersToAddTeamMember(groupId, teamId);
        } else {
            model = service.disAllowUsersToAddTeamMember(groupId, teamId);
        }

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ALLOW_USER_TO_ADD_MEMBER_GROUP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void enableDisableGps(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.enableDisableGps(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ENABLE_DISABLE_GPS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void enableDisableAttendance(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.enableDisableAttendance(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ENABLE_DISABLE_GPS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getTeamSettingsData(OnCommunicationListener listener, String group_id, String teamId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TeamSettingRes> model = service.getTeamSettingData(group_id, teamId);
        ResponseWrapper<TeamSettingRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TEAM_SETTING_DATA, new ResponseWrapper.ResponseHandler<TeamSettingRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TeamSettingRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void startUpdateTrip(OnCommunicationListener listener, String groupId, String teamId, double lat, double lng) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.startUpdateTrip(groupId, teamId, lat, lng);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_START_UPDATE_TRIP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void endTrip(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.endTrip(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_END_TRIP, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getLocation(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GetLocationRes> model = service.getLocation(groupId, teamId);
        ResponseWrapper<GetLocationRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GET_LOCATION, new ResponseWrapper.ResponseHandler<GetLocationRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GetLocationRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getAttendance(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AttendanceListRes> model = service.getAttendance(groupId, teamId);
        ResponseWrapper<AttendanceListRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GET_ATTENDANCE, new ResponseWrapper.ResponseHandler<AttendanceListRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AttendanceListRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }


            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getAttendanceSubject(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubjectResponsev1> model = service.getAttendanceSubjectv2(groupId, teamId);
        ResponseWrapper<SubjectResponsev1> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ATTENDANCE_SUBJECT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void importStudent(OnCommunicationListener listener, String groupId, String teamId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.importStudent(groupId, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_IMPORT_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void editAttendance(OnCommunicationListener listener, String groupId, String teamId, String attendanceId, EditAttendanceReq editAttendanceReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editAttendance(groupId, teamId, attendanceId, editAttendanceReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_EDIT_ATTENDANCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void removeAttendance(OnCommunicationListener listener, String groupId, String teamId, String attendanceId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.removeAttendance(groupId, teamId, attendanceId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_REMOVE_ATTENDANCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void sendAbsenties(OnCommunicationListener listener, String groupId, String teamId, ArrayList<String> sudentIds, AbsentSubjectReq absentSubjectReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<AbsentAttendanceRes> model = service.sendAbsenties(groupId, teamId, sudentIds, absentSubjectReq);
        ResponseWrapper<AbsentAttendanceRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ABSENTIES_ATTENDANCE, new ResponseWrapper.ResponseHandler<AbsentAttendanceRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AbsentAttendanceRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void sendAbsentiesv1(OnCommunicationListener listener, String groupId, String teamId, AbsentStudentReq absentStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.sendAbsentiesv1(groupId, teamId, absentStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TAKE_ATTENDANCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addStudent(OnCommunicationListener listener, String groupId, String teamId, AddStudentReq addStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addStudent(groupId, teamId, addStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addClassStudent(OnCommunicationListener listener, String groupId, String teamId, StudentRes.StudentData addStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addClassStudent(groupId, teamId, addStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_ClASS_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addStaff(OnCommunicationListener listener, String groupId, StaffResponse.StaffData data) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addStaff(groupId, data);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_STAFF_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addTeamStaffOrStudent(OnCommunicationListener listener, String groupId, String teamId,String role, String selectedUserIds) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addTeamStaffOrStudent(groupId, teamId, selectedUserIds,role);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_STAFF_STUDENT_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }





    public void addSchoolStaffRole(OnCommunicationListener listener, String groupId, String role, AddStaffRole addStaffRole) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addSchoolStaffRole(groupId,role,addStaffRole);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(ADD_SCHOOL_ACCOUNTANT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }











    public void addTeamStaffOrStudent1(OnCommunicationListener listener, String groupId, String teamId,String role, String selectedUserIds,String teamid) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addTeamStaffOrStudent1(groupId, teamId, selectedUserIds,role,teamid);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_STAFF_STUDENT_TEAM, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.message);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void editClassStudent(OnCommunicationListener listener, String groupId, String teamId, String userId,String grpRollNo, StudentRes.StudentData addStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editClassStudent(groupId, teamId, userId, grpRollNo,addStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_EDIT_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void editClassStudentPhone(OnCommunicationListener listener, String groupId, String userId, StudentRes.StudentData addStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editClassStudentPhone(groupId, userId, addStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_UPDATE_PHONE_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void editStaff(OnCommunicationListener listener, String groupId, String userId, StaffResponse.StaffData data) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editStaff(groupId, userId, data);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        wrapper.execute(API_STAFF_EDIT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void editStaffPhone(OnCommunicationListener listener, String groupId, String userId, StaffResponse.StaffData data) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editStaffPhone(groupId, userId, data);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        wrapper.execute(UPDATE_PHONE_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteClassStudent(OnCommunicationListener listener, String groupId, String teamId, String userId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteClassStudent(groupId, teamId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_DELETE_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteStaff(OnCommunicationListener listener, String groupId, String userId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteStaff(groupId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_STAFF_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getAttendanceReport(OnCommunicationListener listener, String groupId, String teamId, int month, int year) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AttendanceReportRes> model = service.getAttendanceReport(groupId, teamId, month, year);
        ResponseWrapper<AttendanceReportRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ATTENDANCE_REPORT, new ResponseWrapper.ResponseHandler<AttendanceReportRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AttendanceReportRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }
    public void getAttendanceReportOffline(OnCommunicationListener listener, String groupId, String teamId, int month, int year,int startDate,int endDate) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AttendanceReportResv2> model = service.getAttendanceReportOffline(groupId, teamId, String.valueOf(month), String.valueOf(year),String.valueOf(startDate),String.valueOf(endDate));
        ResponseWrapper<AttendanceReportResv2> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ATTENDANCE_REPORT_OFFLINE, new ResponseWrapper.ResponseHandler<AttendanceReportResv2, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AttendanceReportResv2 response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getAttendanceReportOnline(OnCommunicationListener listener, String groupId, String teamId, int month, int year) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<OnlineAttendanceRes> model = service.getAttendanceReportOnline(groupId, teamId, month, year);
        ResponseWrapper<OnlineAttendanceRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ONLINE_ATTENDANCE_REPORT, new ResponseWrapper.ResponseHandler<OnlineAttendanceRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, OnlineAttendanceRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getAttendanceDetail(OnCommunicationListener listener, String groupId, String teamId, String userId, String rollNo, int month, int year) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AttendanceDetailRes> model = service.getAttendanceDetail(groupId, teamId, userId, rollNo, month, year);
        ResponseWrapper<AttendanceDetailRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ATTENDANCE_DETAIL, new ResponseWrapper.ResponseHandler<AttendanceDetailRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AttendanceDetailRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getGalleryPost(OnCommunicationListener listener, String groupId, int page) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GalleryPostRes> model = service.getGalleryPost(groupId, page);
        ResponseWrapper<GalleryPostRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GALLERY_POST, new ResponseWrapper.ResponseHandler<GalleryPostRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GalleryPostRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteGalleryPost(OnCommunicationListener listener, String groupId, String album_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteGalleryPost(groupId, album_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GALLERY_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteGalleryFile(OnCommunicationListener listener, String groupId, String album_id, String fileName) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteGalleryFile(groupId, album_id, fileName);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GALLERY_FILE_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addGalleryPost(OnCommunicationListener listener, String groupId, AddGalleryPostRequest addGalleryPostRequest) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addGalleryPost(groupId, addGalleryPostRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GALLERY_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addGalleryFile(OnCommunicationListener listener, String groupId, String albumId, AddGalleryPostRequest addGalleryPostRequest) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addGalleryFile(groupId, albumId, addGalleryPostRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GALLERY_FILE_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void getTimeTablePost(OnCommunicationListener listener, String groupId, int page) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TimeTableRes> model = service.getTimeTablePost(groupId, page);
        ResponseWrapper<TimeTableRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TIMETABLE_POST, new ResponseWrapper.ResponseHandler<TimeTableRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TimeTableRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteTimeTablePost(OnCommunicationListener listener, String groupId, String time_table_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteTimeTablePost(groupId, time_table_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TIMETABLE_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addTimeTablePost(OnCommunicationListener listener, String groupId, String teamId, AddTimeTableRequest addTimeTableRequest) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addTimeTablePost(groupId, teamId, addTimeTableRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TIMETABLE_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getVendorPost(OnCommunicationListener listener, String groupId, int page) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<VendorPostResponse> model = service.getVendorPost(groupId, page);
        ResponseWrapper<VendorPostResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_VENDOR_POST, new ResponseWrapper.ResponseHandler<VendorPostResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, VendorPostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void getCourses(OnCommunicationListener listener, String groupId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<CoursePostResponse> model = service.getCourses(groupId);
        ResponseWrapper<CoursePostResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GET_COURSES, new ResponseWrapper.ResponseHandler<CoursePostResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, CoursePostResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }



    public void deleteCourse(OnCommunicationListener listener, String groupId, String course_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteCourse(groupId, course_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_DELETE_COURSE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addCourse(OnCommunicationListener listener, String groupId, CoursePostResponse.CoursePostData coursePostData) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addCourse(groupId, coursePostData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_COURSE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }


    public void editCourse(OnCommunicationListener listener, String groupId, String courseId,CoursePostResponse.CoursePostData coursePostData) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editCourse(groupId, courseId, coursePostData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_EDIT_COURSE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }




    public void MarkSheetListResponse(OnCommunicationListener listener, String groupId, String teamId, String mark_card_id, String userId, String rollNo, int page) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MarkSheetListResponse> model = service.getMarkSheetList(groupId, teamId, mark_card_id, userId, rollNo);
        ResponseWrapper<MarkSheetListResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_MARK_SHEET_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteMarkCart(OnCommunicationListener listener, String groupId, String teamId, String markCardId, String userId, String rollNo) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteMarkCart(groupId, teamId, markCardId, userId, rollNo);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_MARK_SHEET_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteVendorPost(OnCommunicationListener listener, String groupId, String time_table_id) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteVendorPost(groupId, time_table_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_VENDOR_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addVendorPost(OnCommunicationListener listener, String groupId, AddVendorPostRequest addVendorPostRequest) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addVendorPost(groupId, addVendorPostRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_VENDOR_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getCodeOfConductPost(OnCommunicationListener listener, String groupId, int page) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<CodeConductResponse> model = service.getCodeOfConductPost(groupId, page);
        ResponseWrapper<CodeConductResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CODE_CONDUCT_POST, new ResponseWrapper.ResponseHandler<CodeConductResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, CodeConductResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void deleteCodeConduct(OnCommunicationListener listener, String groupId, String codId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteCodeConduct(groupId, codId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CODE_CONDUCT_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void addCodeOfConduct(OnCommunicationListener listener, String groupId, AddCodeOfConductReq addCodeOfConductReq) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addCodeOfConduct(groupId, addCodeOfConductReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CODE_CONDUCT_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void readMore_GroupPost(OnCommunicationListener listener, String groupId, String postId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadMoreRes> model = service.readMore_GroupPost(groupId, postId);
        ResponseWrapper<ReadMoreRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_READ_MORE_GROUP_POST, new ResponseWrapper.ResponseHandler<ReadMoreRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadMoreRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void readMore_TeamPost(OnCommunicationListener listener, String groupId, String teamId, String postId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadMoreRes> model = service.readMore_TeamPost(groupId, postId, teamId);
        ResponseWrapper<ReadMoreRes> wrapper = new ResponseWrapper<>(model);

        AppLog.e(TAG, model.request().url().toString());

        wrapper.execute(API_READ_MORE_TEAM_POST, new ResponseWrapper.ResponseHandler<ReadMoreRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadMoreRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void readMore_TeamPostComment(OnCommunicationListener listener, String groupId, String teamId, String postId) {


        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadMoreRes> model = service.readMore_TeamPostComment(groupId, postId, teamId);
        ResponseWrapper<ReadMoreRes> wrapper = new ResponseWrapper<>(model);

        AppLog.e(TAG, model.request().url().toString());

        wrapper.execute(API_READ_MORE_TEAM_POST_COMMENT, new ResponseWrapper.ResponseHandler<ReadMoreRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadMoreRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);


    }

    public void readMore_Gallery(OnCommunicationListener listener, String mGroupId, String postId) {


        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadMoreRes> model = service.readMore_Gallery(mGroupId, postId);
        ResponseWrapper<ReadMoreRes> wrapper = new ResponseWrapper<>(model);

        AppLog.e(TAG, model.request().url().toString());

        wrapper.execute(API_READ_MORE_GALLERY, new ResponseWrapper.ResponseHandler<ReadMoreRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadMoreRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void readMore_Individual(OnCommunicationListener listener, String mGroupId, String userId, String postId) {

        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReadMoreRes> model = service.readMore_Individual(mGroupId, postId, userId);
        ResponseWrapper<ReadMoreRes> wrapper = new ResponseWrapper<>(model);

        AppLog.e(TAG, model.request().url().toString());

        wrapper.execute(API_READ_MORE_INDIVIDUAL, new ResponseWrapper.ResponseHandler<ReadMoreRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ReadMoreRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addBus(OnAddUpdateListener<GroupValidationError> listListener, String group_id, BusResponse.BusData request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddTeamResponse> model = service.addBus(group_id, request);
        ResponseWrapper<AddTeamResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_ADD_BUS, new ResponseWrapper.ResponseHandler<AddTeamResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, AddTeamResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteBusStudent(OnCommunicationListener listener, String groupId, String teamId, String userId) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteBusStudent(groupId, teamId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_BUS_STUDENTS_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getBusList(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BusResponse> model = service.getBusList(group_id);
        ResponseWrapper<BusResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_BUS_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addBusStudent(OnCommunicationListener listener, String groupId, String teamId, BusStudentRes.StudentData addStudentReq) {
        mOnCommunicationListener = listener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addBusStudent(groupId, teamId, addStudentReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_BUS_STUDENTS_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);
    }

    public void getBusStudents(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BusStudentRes> model = service.getBusStudents(group_id, teamId);
        ResponseWrapper<BusStudentRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_BUS_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getPreSchoolStudent(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PreSchoolStudentRes> model = service.getPreSchoolStudent(group_id, teamId);
        ResponseWrapper<PreSchoolStudentRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_PRESCHOOL_STUDENTS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void attendanceIN(OnCommunicationListener listListener, String group_id, String teamId, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.attendanceIN(group_id, teamId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ATTENDANCE_PRESCHOOL_IN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void attendanceOUT(OnCommunicationListener listListener, String group_id, String teamId, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.attendanceOUT(group_id, teamId, userId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ATTENDANCE_PRESCHOOL_OUT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addMarksheet(OnCommunicationListener listListener, String group_id, String teamId, String markcardId, String userId, String rollNo, UploadMarkRequest addMarkSheetReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AddPostResponse> model = service.addMarksheet(group_id, teamId, markcardId, userId, rollNo, addMarkSheetReq);
        ResponseWrapper<AddPostResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MARK_SHEET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getGroups(final OnCommunicationListener listListener, String from, String talukName, String category, String categoryName) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<GroupResponse> model;
        if ("CONSTITUENCY".equalsIgnoreCase(from)) {
            model = service.getGroupsConstituency(category, categoryName);
        } else {
            if (!TextUtils.isEmpty(talukName)) {
                model = service.getGroupsTaluks(BuildConfig.APP_ID, talukName);
            } else {
                model = service.getGroups(BuildConfig.APP_ID, BuildConfig.AppName);
            }
        }

        ResponseWrapper<GroupResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GROUP_LIST, new ResponseWrapper.ResponseHandler<GroupResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, GroupResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getTaluks(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TaluksRes> model = service.getTaluks();
        ResponseWrapper<TaluksRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TALUKS, new ResponseWrapper.ResponseHandler<TaluksRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TaluksRes response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getConstituencyList(final OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ConstituencyRes> model = service.getConstituencyList(BuildConfig.AppName);
        ResponseWrapper<ConstituencyRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CONSTITUENCY, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "GetGroupList : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getChapterList(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ChapterRes> model = service.getChapterList(groupId, teamId, subjectId);
        ResponseWrapper<ChapterRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CHAPTER_LIST, new ResponseWrapper.ResponseHandler<ChapterRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ChapterRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    /*public void getChapter(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SyllabusListRes> model = service.getChapter(groupId, teamId, subjectId);

        ResponseWrapper<SyllabusListRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CHAPTER_LIST, new ResponseWrapper.ResponseHandler<SyllabusListRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, SyllabusListRes response) {
                AppLog.e("LeafManager", "SyllabusListRes123 : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }*/





    public void addChapterPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, AddGalleryPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        AppLog.e(TAG, "addChapterPost: " + request.toString());
        AddChapterPostRequest addChapterPostRequest = new AddChapterPostRequest();
        addChapterPostRequest.chapterName = request.albumName;
        addChapterPostRequest.topicName = request.topicName;
        addChapterPostRequest.fileName = request.fileName;
        addChapterPostRequest.fileType = request.fileType;
        addChapterPostRequest.thumbnailImage = request.thumbnailImage;
        addChapterPostRequest.video = request.video;

        Call<BaseResponse> model = service.addChapterPost(groupId, team_id, subject_id, addChapterPostRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_CHAPTER_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addChapterTopicPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String chapter_id, AddGalleryPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        AppLog.e(TAG, "addChapterPost: " + request.toString());
        AddChapterPostRequest addChapterPostRequest = new AddChapterPostRequest();
        addChapterPostRequest.chapterName = request.albumName;
        addChapterPostRequest.topicName = request.topicName;
        addChapterPostRequest.fileName = request.fileName;
        addChapterPostRequest.fileType = request.fileType;
        addChapterPostRequest.thumbnailImage = request.thumbnailImage;
        addChapterPostRequest.video = request.video;
        Call<BaseResponse> model = service.addChapterTopicPost(groupId, team_id, subject_id, chapter_id, addChapterPostRequest);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_CHAPTER_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTopic(OnCommunicationListener listListener, String groupId, String team_id, String subject_id, String chapter_id, String topicId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteTopic(groupId, team_id, subject_id, chapter_id, topicId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_TOPIC_REMOVE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteChapter(OnCommunicationListener listListener, String groupId, String team_id, String subject_id, String chapter_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteChapter(groupId, team_id, subject_id, chapter_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_CHAPTER_REMOVE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void topicCompleteStatus(OnCommunicationListener listListener, String groupId, String team_id, String subject_id, String chapter_id, String topic_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.topicCompleteStatus(groupId, team_id, subject_id, chapter_id, topic_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_TOPIC_STATUS_CHANGE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTTNew(OnCommunicationListener listListener, String groupId, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteTTNew(groupId, team_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_TT_REMOVE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTTNewByDay(OnCommunicationListener listListener, String groupId, String team_id, String day) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteTTNewByDay(groupId, team_id, day);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_TT_REMOVE_DAY, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getFeesDetails(OnCommunicationListener listListener, String groupId, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<FeesRes> model = service.getFeesDetails(groupId, team_id);
        ResponseWrapper<FeesRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_FEES_RES, new ResponseWrapper.ResponseHandler<FeesRes, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, FeesRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getStudentFeesList(OnCommunicationListener listListener, String groupId, String team_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<StudentFeesRes> model = service.getStudentFeesList(groupId, team_id);
        ResponseWrapper<StudentFeesRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_STUDENT_FEES_LIST, new ResponseWrapper.ResponseHandler<StudentFeesRes, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, StudentFeesRes response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void createFees(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String teamId, FeesRes.Fees request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.createFees(group_id, teamId, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_FEES_CREATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void addStudentPaidFees(OnAddUpdateListener<GroupValidationError> listListener, String group_id, String teamId, String studentId, UpdateStudentFees request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addStudentPaidFees(group_id, teamId, studentId, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<GroupValidationError>>() {
        }.getType();
        wrapper.execute(API_STUDENT_FEES_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<GroupValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<GroupValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void getHwList(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<HwRes> model = service.getHwList(groupId, teamId, subjectId);
        ResponseWrapper<HwRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_HW_LIST, new ResponseWrapper.ResponseHandler<HwRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, HwRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addHwPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, AddHwPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.addHwPost(groupId, team_id, subject_id, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_HW_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void submitAssignmentPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id, AddHwPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.submitAssignmentPost(groupId, team_id, subject_id, assignment_id, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_SUBMIT_ASSIGNMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getAssignment(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId, String assignment_id, String param) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AssignmentRes> model;
        if (!TextUtils.isEmpty(param)) {
            model = service.getAssignmentForTeacher(groupId, teamId, subjectId, assignment_id, param);
        } else {
            model = service.getAssignment(groupId, teamId, subjectId, assignment_id);
        }

        ResponseWrapper<AssignmentRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ASSIGNMENT_LIST, new ResponseWrapper.ResponseHandler<AssignmentRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, AssignmentRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void verifyAssignment(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id, String student_assignment_id, boolean verify, ReassignReq reassignReq) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.verifyAssignment(groupId, team_id, subject_id, assignment_id, student_assignment_id, verify, reassignReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_VERIFY_ASSIGNMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void reassignAssignment(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id, String student_assignment_id, boolean reassign, ReassignReq reassignReq) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.reassignAssignment(groupId, team_id, subject_id, assignment_id, student_assignment_id, reassign, reassignReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_REASSIGN_ASSIGNMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteAssignmentTeacher(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.deleteAssignmentTeacher(groupId, team_id, subject_id, assignment_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_DELETE_ASSIGNMENT_TEACHER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteAssignmentStudent(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id, String student_assignment_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.deleteAssignmentStudent(groupId, team_id, subject_id, assignment_id, student_assignment_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_DELETE_ASSIGNMENT_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void getTestExamList(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TestExamRes> model = service.getTestExamList(groupId, teamId, subjectId);
        ResponseWrapper<TestExamRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TEST_EXAM_LIST, new ResponseWrapper.ResponseHandler<TestExamRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TestExamRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addTestExam(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, AddTestExamPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.addTestExam(groupId, team_id, subject_id, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_TEST_EXAM_ADD, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void deleteTestExam(OnCommunicationListener listListener, String groupId, String team_id, String subject_id, String testexam_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteTestExam(groupId, team_id, subject_id, testexam_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();

        wrapper.execute(API_TEST_EXAM_REMOVE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void getTestPaper(final OnCommunicationListener listListener, String groupId, String teamId, String subjectId, String testexam_id, String param) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TestPaperRes> model;
        if (!TextUtils.isEmpty(param)) {
            model = service.getTestPaperForTeacher(groupId, teamId, subjectId, testexam_id, param);
        } else {
            model = service.getTestPaper(groupId, teamId, subjectId, testexam_id);
        }

        ResponseWrapper<TestPaperRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TEST_EXAM_PAPER_LIST, new ResponseWrapper.ResponseHandler<TestPaperRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TestPaperRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void deleteTestPaperStudent(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String testexam_id, String studentTestExamId) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.deleteTestPaperStudent(groupId, team_id, subject_id, testexam_id, studentTestExamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_TEST_EXAM_PAPER_DELETE_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void verifyTestPaper(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String testexam_id, String studentTestExam_id, boolean verify, ReassignReq reassignReq) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.verifyTestPaper(groupId, team_id, subject_id, testexam_id, studentTestExam_id, verify, reassignReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_TEST_EXAM_PAPER_VERIFY, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void submitTestPaperPost(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id, AddHwPostRequest request) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.submitTestPaperPost(groupId, team_id, subject_id, assignment_id, request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_SUBMIT_TEST_PAPER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }


    public void startTestPaperEvent(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.startTestPaperLive(groupId, team_id, subject_id, assignment_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_TEST_PAPER_START_EVENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void stopTestPaperEvent(OnAddUpdateListener<AddPostValidationError> listListener, String groupId, String team_id, String subject_id, String assignment_id) {
        mListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        Call<BaseResponse> model = service.stopTestPaperLive(groupId, team_id, subject_id, assignment_id);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<AddPostValidationError>>() {
        }.getType();
        wrapper.execute(API_TEST_PAPER_STOP_EVENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<AddLeadValidationError>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mListener != null) {
                    mListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<AddLeadValidationError> error) {
                if (mListener != null) {
                    mListener.onFailure(apiId, error);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mListener != null) {
                    mListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);


    }

    public void getTestLiveEvents(final OnCommunicationListener listListener, String groupId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TestLiveEventRes> model;

        model = service.getLivePaperEvents(groupId);


        ResponseWrapper<TestLiveEventRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_TEST_PAPER_LIVE_EVENTS, new ResponseWrapper.ResponseHandler<TestLiveEventRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TestLiveEventRes response) {
                AppLog.e("LeafManager", "ChapterRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void startLiveClass(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.startLiveClass(group_id, teamId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_LIVE_CLASS_START, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getLiveClassEvents(final OnCommunicationListener listListener, String groupId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LiveClassEventRes> model = service.getLiveClassEvents(groupId);


        ResponseWrapper<LiveClassEventRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_LIVE_CLASS_EVENTS, new ResponseWrapper.ResponseHandler<LiveClassEventRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, LiveClassEventRes response) {
                AppLog.e("LeafManager", "LiveClassEventRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void joinLiveClass(OnCommunicationListener listListener, String group_id, String teamId, JoinLiveClassReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.joinLiveClass(group_id, teamId, req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_LIVE_CLASS_JOIN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void endLiveClass(OnCommunicationListener listListener, String group_id, String teamId, StopMeetingReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.endLiveClass(group_id, teamId, req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_LIVE_CLASS_END, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void sendMsgToNotSubmittedStudents(OnCommunicationListener listListener, String group_id, String userIds, SendMsgToStudentReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.sendMsgToNotSubmittedStudents(req, group_id, userIds);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SEND_MSG_TO_NOTSUBMITTED_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getPaidStudentList(final OnCommunicationListener listListener, String groupId, String status, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<PaidStudentFeesRes> model = !TextUtils.isEmpty(teamId) ? service.getPaidStudentList(groupId, status, teamId) : service.getPaidStudentList(groupId, status);

        ResponseWrapper<PaidStudentFeesRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_PAID_STUDENT_LIST, new ResponseWrapper.ResponseHandler<PaidStudentFeesRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, PaidStudentFeesRes response) {
                AppLog.e("LeafManager", "LiveClassEventRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getStudentFees(final OnCommunicationListener listListener, String groupId, String teamId, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StudentFeesRes> model = service.getStudentFees(groupId, teamId, userId);


        ResponseWrapper<StudentFeesRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_STUDENT_FEES_LIST, new ResponseWrapper.ResponseHandler<StudentFeesRes, ErrorResponse>() {
            @Override
            public void handle200(int apiId, StudentFeesRes response) {
                AppLog.e("LeafManager", "LiveClassEventRes : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void payFeesByStudent(final OnCommunicationListener listListener, String groupId, String teamId, String userId, PayFeesRequest payFeesRequest) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.payFeesByStudent(groupId, teamId, userId, payFeesRequest);


        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_PAY_FEES_BY_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void approveOrHoldFees(final OnCommunicationListener listListener, String groupId, String teamId, String studentId, String paymentID, String status) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.approveOrHoldFees(groupId, teamId, studentId, paymentID, status);


        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_APPROVE_FEES, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    /*public void updateStatusDueDate(final OnCommunicationListener listListener, String groupId, String teamId, String userID, String status) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateStatusDueDate(groupId, teamId, userID, status);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_DUE_DATE_STATUS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }
    */
    public void editStudentFees(final OnCommunicationListener listListener, String groupId, String teamId, String userID, FeesRes.Fees req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editStudentFees(groupId, teamId, userID, req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_EDIT_STUDENT_FEES, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void createOfflineTest(final OnCommunicationListener listListener, String groupId, String teamId, OfflineTestReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.createOfflineTest(groupId, teamId, req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_CREATE_OFFLINE_TEST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getOfflineTestList(final OnCommunicationListener listListener, String groupId, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<OfflineTestRes> model = service.getOfflineTestList(groupId, teamId);

        ResponseWrapper<OfflineTestRes> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_OFFLINE_TEST_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void deleteOfflineTestList(final OnCommunicationListener listListener, String groupId, String teamId, String examID) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteOfflineTestList(groupId, teamId, examID);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_REMOVE_OFFLINE_TEST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getMarkCard2List(final OnCommunicationListener listListener, String groupId, String teamId, String offlineTestExamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MarkCardResponse2> model = service.getMarkCard2List(groupId, teamId, offlineTestExamId);

        ResponseWrapper<MarkCardResponse2> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_MARK_CARD_LIST_2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getMarkCard2ListForStudent(final OnCommunicationListener listListener, String groupId, String teamId, String offlineTestExamId, String userID) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MarkCardResponse2> model = service.getMarkCard2ListForStudent(groupId, teamId, offlineTestExamId, userID);

        ResponseWrapper<MarkCardResponse2> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_MARK_CARD_LIST_2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addObtainMark(final OnCommunicationListener listListener, String groupId, String teamId, String offlineTestExamId, String userID, AddMarksReq addMarksReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addObtainMark(groupId, teamId, offlineTestExamId, userID, addMarksReq);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_OBT_MARK, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                AppLog.e("LeafManager", "payFeesByStudent : " + response);
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    AppLog.e("GroupList", "handle Error : " + error.status);
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                    // mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getBooths(OnCommunicationListener listListener, String group_id,String option) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);


    //    final Call<BoothResponse> model = !TextUtils.isEmpty(option)?service.getBoothsCoordinator(group_id,option):service.getBooths(group_id); //remove booth cordinator in boothListfragmentV2


        final Call<BoothResponse> model = service.getBooths(group_id);

        ResponseWrapper<BoothResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_BOOTHS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMyBooths(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BoothResponse> model = service.getMyBooths(group_id);
        ResponseWrapper<BoothResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_MY_BOOTH, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getBoothTeams(OnCommunicationListener listListener, String group_id, String booth_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BoothResponse> model = service.getBoothTeams(group_id, booth_id);
        ResponseWrapper<BoothResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_BOOTH_TEAMS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addBooths(OnCommunicationListener listListener, String group_id, BoothData boothData) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addBooths(group_id, boothData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_BOOTH, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getBoothMember(OnCommunicationListener listListener, String group_id, String teamId,String committeeID) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BoothMemberResponse> model = service.getBoothsMember(group_id, teamId,committeeID);
        ResponseWrapper<BoothMemberResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_BOOTH_MEMEBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getCoordinateMember(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BoothMemberResponse> model = service.getCoordinateMember(group_id, teamId);
        ResponseWrapper<BoothMemberResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_COORDINATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getFamilyMember(OnCommunicationListener listListener, String group_id, String userId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<FamilyMemberResponse> model = service.getFamilyMember(group_id, userId);
        ResponseWrapper<FamilyMemberResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_FAMILY_MEMBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addFamilyMember(OnCommunicationListener listListener, String group_id, String userId, FamilyMemberResponse req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addFamilyMember(group_id, userId, req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_CREATE_FAMILY_MEMBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addBoothsMember(OnCommunicationListener listListener, String group_id, String teamId, String category, BoothMemberReq boothData) {


        Log.e(TAG,"Request"+new Gson().toJson(boothData));

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addBoothsMember(group_id, teamId, category, boothData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_BOOTH_MEMEBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateBoothsMember(OnCommunicationListener listListener, String group_id, String teamId, String studentId, BoothMemberResponse.BoothMemberData boothData) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateBoothsMember(group_id, teamId, studentId, boothData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_UPDATE_BOOTH_MEMEBER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addCoordinateBooth(OnCommunicationListener listListener, String group_id, String teamId, BoothMemberResponse.BoothMemberData boothData) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addCoordinateBooth(group_id, teamId, boothData);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_COORDINATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void addIssue(OnCommunicationListener listListener, String group_id, RegisterIssueReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addIssue(group_id, req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ISSUE_REGISTER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void editIssue(OnCommunicationListener listListener, String group_id, String issueId, IssueListResponse.IssueData req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editIssue(group_id, issueId, req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_TASK_FORCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteIssue(OnCommunicationListener listListener, String group_id, String issueId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteIssue(group_id, issueId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ISSUE_REMOE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getIssues(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<IssueListResponse> model = service.getIssues(group_id);
        ResponseWrapper<IssueListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ISSUE_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }



    public void getSubBooth(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubBoothResponse> model = service.getMySubBooths(group_id);

        ResponseWrapper<SubBoothResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SUB_BOOTH_TEAM_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }
    public void addTicket(OnCommunicationListener listListener, String groupId, String teamId,String issueId, AddTicketRequest addTicketReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addTicket(groupId, teamId, issueId,addTicketReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_TICKET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void deleteTicket(OnCommunicationListener listListener, String group_id, String issueId) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.deleteTicket(group_id, issueId);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_DELETE_TICKET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }
    public void getTickets(OnCommunicationListener listListener, String group_id,String role,String option,String page) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<TicketListResponse> model = service.getTickets(group_id,role,option,page);

       // final Call<TicketListResponse> model = service.getTicketWithoutRole(group_id,option,page);

        ResponseWrapper<TicketListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_LIST_TICKET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void setApprovedTicket(OnCommunicationListener listListener, String group_id,String issuePostID,String status) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.ticketApproved(group_id,issuePostID,status);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(APPROVED_TICKET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void setApprovedTicketByAdmin(OnCommunicationListener listListener, String group_id,String issuePostID,String status) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.ticketApprovedByAdmin(group_id,issuePostID,status);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(APPROVED_TICKET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void setAddCommentTaskDetails(OnCommunicationListener listListener, String group_id, String issuePostID, AddCommentTaskDetailsReq req) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.addCommentTaskDetails(group_id,issuePostID,req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(ADD_COMMENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getCommentTaskDetails(OnCommunicationListener listListener, String group_id, String issuePostID) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<CommentTaskDetailsRes> model = service.getCommentTaskDetails(group_id,issuePostID);

        ResponseWrapper<CommentTaskDetailsRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(LIST_COMMENT, new ResponseWrapper.ResponseHandler<CommentTaskDetailsRes, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, CommentTaskDetailsRes response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addCommittee(OnCommunicationListener listListener, String group_id, String teamId, AddCommitteeReq req) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.addCommittee(group_id,teamId,req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(ADD_COMMITTEE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }





    public void getCommittee(OnCommunicationListener listListener, String group_id, String teamId) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<committeeResponse> model = service.getCommittee(group_id,teamId);

        ResponseWrapper<committeeResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(LIST_COMMITTEE, new ResponseWrapper.ResponseHandler<committeeResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, committeeResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateCommittee(OnCommunicationListener listListener, String group_id, String teamId, String committeeID, AddCommitteeReq req) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.updateCommittee(group_id,teamId,committeeID,req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(UPDATE_COMMITTEE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void removeCommittee(OnCommunicationListener listListener, String group_id, String teamId, String committeeID) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.removeCommittee(group_id,teamId,committeeID);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(REMOVE_COMMITTEE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getTicketsUpdateEvent(OnCommunicationListener listListener, String group_id,String role,String option) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<TicketEventUpdateResponse> model = service.getUpdatedTickets(group_id,role,option);


        ResponseWrapper<TicketEventUpdateResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(UPDATED_TICKET_LIST_EVENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getAdminFeederList(OnCommunicationListener listListener, String group_id,String role) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<AdminFeederResponse> model = service.getAdminFeed(group_id,role);


        ResponseWrapper<AdminFeederResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(ADMIN_FEEDER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMasterBoothList(OnCommunicationListener listListener, String group_id,String type) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BoothMasterListModelResponse> model = service.getMasterListBooth(group_id,type);


        ResponseWrapper<BoothMasterListModelResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(MASTER_BOOTH_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getWorkerList(OnCommunicationListener listListener, String group_id,String team_id,String commiteeID) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<WorkerListResponse> model = service.getWorkerList(group_id,team_id,commiteeID);


        ResponseWrapper<WorkerListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(WORKER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getWorkerStreetList(OnCommunicationListener listListener, String group_id,String team_id,String type) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<StreetListModelResponse> model = service.getWorkerStreetList(group_id,team_id,type);


        ResponseWrapper<StreetListModelResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(WORKER_STREET_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void addVoterMasterList(OnCommunicationListener listListener, String group_id, String team_id, VoterListModelResponse.AddVoterReq req) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.addVoter(group_id,team_id,req);


        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(ADD_VOTER_MASTER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void deleteVoterMaster(OnCommunicationListener listListener, String group_id, String team_id, String voter_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.deleteVoterMaster(group_id,team_id,voter_id);


        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_VOTER_MASTER_DELETE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void voterMasterList(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<VoterListModelResponse.VoterListRes> model = service.getVoterList(group_id,team_id);

        ResponseWrapper<VoterListModelResponse.VoterListRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(VOTER_MASTER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void addBannerList(OnCommunicationListener listListener, String group_id, BannerAddReq req) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.addBannerList(group_id,req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_BANNER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }
































    public void getBannerList(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BannerRes> model = service.getBannerList(group_id);

        ResponseWrapper<BannerRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_BANNER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getBoothVoterList(OnCommunicationListener listListener, String group_id,String boothID) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BoothVotersListResponse> model = service.getBoothVoters(group_id,boothID);

        ResponseWrapper<BoothVotersListResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_BOOTH_VOTER_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getMyTeamSubBooth(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<MyTeamSubBoothResponse> model = service.getMyTeamSubBooths(group_id);

        ResponseWrapper<MyTeamSubBoothResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SUB_BOOTH_TEAM_LIST, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getVoterProfile(OnCommunicationListener listListener, String group_id,String user_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<VoterProfileResponse> model = service.getVoterProfile(group_id,user_id);

        ResponseWrapper<VoterProfileResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_VOTER_PROFILE_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void updateProfileVoter(OnCommunicationListener listListener,String group_id,String user_id, VoterProfileUpdate request) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.updateVoterProfile(group_id,user_id,request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);


        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();
        wrapper.execute(API_VOTER_PROFILE_UPDATE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {

            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }







    public void getProfession(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ProfessionResponce> model = service.getProfession();
        ResponseWrapper<ProfessionResponce> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();
        wrapper.execute(API_PROFESSION_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {

            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }







    public void getReligion(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ReligionResponse> model = service.getReligion();
        ResponseWrapper<ReligionResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();
        wrapper.execute(API_RELIGION_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {

            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void getCaste(OnCommunicationListener listListener,String caste) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<CasteResponse> model = service.getCaste(caste);
        ResponseWrapper<CasteResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();
        wrapper.execute(API_CASTE_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {

            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void getSubCaste(OnCommunicationListener listListener,String casteID) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<SubCasteResponse> model = service.getSubCaste(casteID);
        ResponseWrapper<SubCasteResponse> wrapper = new ResponseWrapper<>(model);
        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();
        wrapper.execute(API_SUB_CASTE_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {

            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void makeAppAdmin(OnCommunicationListener listListener, String group_id, String user_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.makeAppAdmin(group_id,user_id);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_MAKE_ADMIN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getSubBoothEvent(OnCommunicationListener listListener, String group_id, String team_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<SubBoothEventRes> model = service.getSubBoothEvent(group_id,team_id);

        ResponseWrapper<SubBoothEventRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EVENT_SUB_BOOTH_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getMyBoothEvent(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<MyBoothEventRes> model = service.getMyBoothEvent(group_id);

        ResponseWrapper<MyBoothEventRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EVENT_MY_BOOTH_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getSubBoothWorkerEvent(OnCommunicationListener listListener, String group_id) {
        mOnCommunicationListener = listListener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<SubBoothWorkerEventRes> model = service.getSubBoothWorkerEvent(group_id);

        ResponseWrapper<SubBoothWorkerEventRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EVENT_SUB_BOOTH_WORKER_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getTeamPostEvent(OnCommunicationListener listener,String group_id,String team_id)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<TeamPostEventModelRes> model = service.getTeamPostEvent(group_id,team_id);

        ResponseWrapper<TeamPostEventModelRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EVENT_TEAM_POST_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void getTeamEvent(OnCommunicationListener listener,String group_id,String team_id)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<TeamEventModelRes> model = service.getTeamEvent(group_id,team_id);

        ResponseWrapper<TeamEventModelRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EVENT_TEAM_GET, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void addSyllabus(OnCommunicationListener listener, String group_id, String team_id, String subject_id, SyllabusModelReq req)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.addSyllabus(group_id,team_id,subject_id,req);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_ADD_SYLLABUS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }






    public void getSyllabusMaster(OnCommunicationListener listener, String group_id, String team_id, String subject_id)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<SyllabusListMaster> model = service.getSyllabusMaster(group_id,team_id,subject_id);

        ResponseWrapper<SyllabusListMaster> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_SYLLABUS_MASTER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {


                Log.e("Leafmanger","====>"+response);
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }








    public void getSyllabus(OnCommunicationListener listener, String group_id, String team_id, String subject_id)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<SyllabusListModelRes> model = service.getSyllabus(group_id,team_id,subject_id);

        ResponseWrapper<SyllabusListModelRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_GET_SYLLABUS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void getSearchUser(OnCommunicationListener listener, String group_id,String text)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<SearchUserModel> model = service.getSearch(group_id,text);

        ResponseWrapper<SearchUserModel> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_SEARCH_USER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void changeStatusPlan(OnCommunicationListener listener, String group_id, String team_id, String subject_id, String topicId, ChangeStatusPlanModel.ChangeStatusModelReq modelReq)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.ChangeStatusPlan(group_id,team_id,subject_id,topicId,modelReq);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_CHANGE_STATUS_PLAN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }




    public void statusPlan(OnCommunicationListener listener, String group_id, String team_id, String subject_id, String chapter_id, SyllabusPlanRequest modelReq)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.SyllabusPlan(group_id,team_id,subject_id,chapter_id,modelReq);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_STATUS_PLAN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }


    public void EditSyllabus(OnCommunicationListener listener, String group_id, String team_id, String subject_id, String chapter_id, EditTopicModelReq modelReq)
    {
        mOnCommunicationListener = listener;

        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);

        final Call<BaseResponse> model = service.EditSyllabus(group_id,team_id,subject_id,chapter_id,modelReq);

        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EDIT_SYLLABUS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }
            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }
            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {

                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);
    }

    public void getClassesOfStaff(OnCommunicationListener listListener, String group_id,String staff_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassResponse> model = service.getClassesOfStaff(group_id,staff_id);
        ResponseWrapper<ClassResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_CLASS_OF_STAFF, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void editAttendanceStudent(OnCommunicationListener listListener, String group_id, String team_id, AttendenceEditRequest request) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.editAttendanceForSelectedStudent(group_id,team_id,request);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnAddUpdateListener>>() {
        }.getType();

        wrapper.execute(API_EDIT_ATTENDANCE_STUDENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnAddUpdateListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnAddUpdateListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getAttendanceReportParent(OnCommunicationListener listListener, String group_id, String team_id, int month,int year,String user_id) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<AttendanceReportParentRes> model = service.getOfflineAttendReportParent(group_id,team_id,month,year,user_id);
        ResponseWrapper<AttendanceReportParentRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_ATTENDANCE_REPORT_PARENT, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void applyForLeave(OnCommunicationListener listListener, String group_id, String team_id, ApplyLeaveReq req) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.applyForLeave(group_id,team_id,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_APPLY_FOR_LEAVE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }

    public void getTodaySyllabusPlanList(OnCommunicationListener listListener, String group_id, String date) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TodaySyllabusPlanRes> model = service.getTodayDateSyllabus(group_id,date);
        ResponseWrapper<TodaySyllabusPlanRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_TODAY_DATE_WISE_SYLLBUS_PLAN, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void getStaffAnalysis(OnCommunicationListener listListener, String group_id, String staffid) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StaffAnalysisRes> model = service.getAnalysisOfStaff(group_id,staffid);
        ResponseWrapper<StaffAnalysisRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_STAFF_ANALYSIS, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }



    public void getStaffAttendance(OnCommunicationListener listListener, String group_id,int day,int month,int year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<StaffAttendanceRes> model = service.getStaffAttendance(group_id,day,month,year);
        ResponseWrapper<StaffAttendanceRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_STAFF_ATTENDACNCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


   /* public void getStaffAttendance(OnCommunicationListener listListener, String group_id,String day,String month,String year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ApprovalStaffAttendanceRes> model = service.getApprovalStaffAttendance(group_id,day,month,year);
        ResponseWrapper<ApprovalStaffAttendanceRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_APPROVAL_STAFF_ATTENDACNCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }*/








    public void takeStaffAttendance(OnCommunicationListener listListener, String group_id, TakeAttendanceReq takeAttendanceReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.takeStaffAttendance(group_id,takeAttendanceReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_TAKE_STAFF_ATTENDACNCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    public void changeStaffAttendance(OnCommunicationListener listListener, String group_id, ChangeStaffAttendanceReq attendanceReq) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.changeStaffAttendance(group_id,attendanceReq);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_CHNAGE_STAFF_ATTENDACNCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }



    public void getLeave(OnCommunicationListener listListener, String group_id, String team_id,String user_id,String date,int year) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<LeaveRes> model = service.getLeave(group_id,team_id,user_id,date,year);
        ResponseWrapper<LeaveRes> wrapper = new ResponseWrapper<>(model);

        final Type serviceErrorType = new TypeToken<ErrorResponseModel<OnCommunicationListener>>() {
        }.getType();

        wrapper.execute(API_GET_LEAVE_ATTENDACNCE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponseModel<OnCommunicationListener>>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponseModel<OnCommunicationListener> error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);

                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, serviceErrorType);

    }


    /* Registration Form APIs */

    public void getTypeOfCampus(OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<TypeOfCampusData> model;

        model = service.getTypeOfCampus();

        ResponseWrapper<TypeOfCampusData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_TYPE_OF_CAMPUS, new ResponseWrapper.ResponseHandler<TypeOfCampusData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, TypeOfCampusData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getBoardsListForCampus(String campusType, OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BoardsData> model;

        model = service.getBoardsListForCampus(campusType);

        ResponseWrapper<BoardsData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GET_BOARDS_LIST, new ResponseWrapper.ResponseHandler<BoardsData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BoardsData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getUniversitiesListForBoard(String board, OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<UniversitiesData> model;

        model = service.getUniversitiesForBoard(board);

        ResponseWrapper<UniversitiesData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GET_UNIVERSITY_LIST, new ResponseWrapper.ResponseHandler<UniversitiesData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, UniversitiesData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getCampusMedium(String board, OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<CampusMediumData> model;

        model = service.getCampusMedium(board);

        ResponseWrapper<CampusMediumData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GET_CAMPUS_MEDIUM, new ResponseWrapper.ResponseHandler<CampusMediumData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, CampusMediumData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void getClassesList(String subCategory, String board, OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassesListData> model;

        model = service.getClassesList(board, subCategory);

        ResponseWrapper<ClassesListData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_GET_CLASSES_LIST, new ResponseWrapper.ResponseHandler<ClassesListData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ClassesListData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void doRegister(String userId, RegisterRequestData registerRequest, OnCommunicationListener listListener) {
        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassesListData> model;

        model = service.doRegister(userId, registerRequest);

        ResponseWrapper<ClassesListData> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ID_DO_REGISTER, new ResponseWrapper.ResponseHandler<ClassesListData, ErrorResponse>() {
            @Override
            public void handle200(int apiId, ClassesListData response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }





















    public void assignTeacher(OnCommunicationListener listListener,String groupId, String teamID, AddSubjectStaffReq req) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.assignSubject(groupId,teamID,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ASSIGN_TEACHER, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }



















    public void AddCombinedClass(OnCommunicationListener listListener,String groupId, AddCombinedClass req) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.AddCombinedClass(groupId,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_COMBINED_CLASS , new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }



    public void getClassListV2(OnCommunicationListener listListener,String groupId) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<ClassResV2> model = service.getClassesListV2(groupId);
        ResponseWrapper<ClassResV2> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_GET_CLASS_LIST_V2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void addClassListV2(OnCommunicationListener listListener,String groupId, AddClassReq req) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addClass(groupId,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_CLASS_V2, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }


    public void addMultipleStudent(OnCommunicationListener listListener,String groupId,String teamId, AddMultipleStudentReq req) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addMultipleStudent(groupId,teamId,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_STUDENT_MULTIPLE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public void addMultipleStaff(OnCommunicationListener listListener, String groupId, AddMultipleStaffReq req) {

        mOnCommunicationListener = listListener;
        LeafApiClient apiClient = LeafApplication.getInstance().getApiClient();
        LeafService service = apiClient.getService(LeafService.class);
        final Call<BaseResponse> model = service.addMultipleStaff(groupId,req);
        ResponseWrapper<BaseResponse> wrapper = new ResponseWrapper<>(model);

        wrapper.execute(API_ADD_STAFF_MULTIPLE, new ResponseWrapper.ResponseHandler<BaseResponse, ErrorResponse>() {
            @Override
            public void handle200(int apiId, BaseResponse response) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onSuccess(apiId, response);
                }
            }

            @Override
            public void handleError(int apiId, int code, ErrorResponse error) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onFailure(apiId, error.status + ":" + error.title);
                }
            }

            @Override
            public void handleException(int apiId, Exception e) {
                if (mOnCommunicationListener != null) {
                    mOnCommunicationListener.onException(apiId, e.getMessage());
                }
            }
        }, ErrorResponse.class);

    }

    public interface OnCommunicationListener {
        void onSuccess(int apiId, BaseResponse response);

        void onFailure(int apiId, String msg);

        void onException(int apiId, String msg);
    }

    public interface OnAddUpdateListener<T> {

        void onSuccess(int apiId, BaseResponse response);

        void onFailure(int apiId, ErrorResponseModel<T> error);

        void onException(int apiId, String error);
    }

}