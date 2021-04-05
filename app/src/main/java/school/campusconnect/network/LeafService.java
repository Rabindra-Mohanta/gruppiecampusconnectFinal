package school.campusconnect.network;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.http.Url;
import school.campusconnect.datamodel.LeaveReq;
import school.campusconnect.datamodel.MarkSheetListResponse;
import school.campusconnect.datamodel.NewPassReq;
import school.campusconnect.datamodel.OtpVerifyReq;
import school.campusconnect.datamodel.OtpVerifyRes;
import school.campusconnect.datamodel.ReadUnreadResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceDetailRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.attendance_report.PreSchoolStudentRes;
import school.campusconnect.datamodel.bus.BusResponse;
import school.campusconnect.datamodel.bus.BusStudentRes;
import school.campusconnect.datamodel.calendar.AddEventReq;
import school.campusconnect.datamodel.calendar.EventInDayRes;
import school.campusconnect.datamodel.calendar.EventListRes;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.classs.ParentKidsResponse;
import school.campusconnect.datamodel.ebook.AddEbookReq;
import school.campusconnect.datamodel.ebook.EBooksResponse;
import school.campusconnect.datamodel.ebook.EBooksTeamResponse;
import school.campusconnect.datamodel.marksheet.AddMarkCardReq;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;
import school.campusconnect.datamodel.marksheet.StudentMarkCardListResponse;
import school.campusconnect.datamodel.marksheet.SubjectTeamResponse;
import school.campusconnect.datamodel.marksheet.UploadMarkRequest;
import school.campusconnect.datamodel.notificationList.NotificationListRes;
import school.campusconnect.datamodel.readMore.ReadMoreRes;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.student.StudentRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import school.campusconnect.datamodel.AbsentAttendanceRes;
import school.campusconnect.datamodel.AddCodeOfConductReq;
import school.campusconnect.datamodel.AddGalleryPostRequest;
import school.campusconnect.datamodel.AddLeadRequest;
import school.campusconnect.datamodel.AddPostRequest;
import school.campusconnect.datamodel.AddPostRequestDescription;
import school.campusconnect.datamodel.AddPostRequestFile;
import school.campusconnect.datamodel.AddPostRequestFile_Friend;
import school.campusconnect.datamodel.AddPostRequestVideo_Friend;
import school.campusconnect.datamodel.AddPostResponse;
import school.campusconnect.datamodel.AddStudentReq;
import school.campusconnect.datamodel.AddTeamResponse;
import school.campusconnect.datamodel.AddTimeTableRequest;
import school.campusconnect.datamodel.AddVendorPostRequest;
import school.campusconnect.datamodel.AttendanceListRes;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ChangeNumberRequest;
import school.campusconnect.datamodel.ChangePasswordRequest;
import school.campusconnect.datamodel.ChangePasswordResponse;
import school.campusconnect.datamodel.CodeConductResponse;
import school.campusconnect.datamodel.ContactListResponse;
import school.campusconnect.datamodel.CreateGroupReguest;
import school.campusconnect.datamodel.CreateTeamRequest;
import school.campusconnect.datamodel.EditAttendanceReq;
import school.campusconnect.datamodel.ForgotPasswordRequest;
import school.campusconnect.datamodel.GalleryPostRes;
import school.campusconnect.datamodel.GetLocationRes;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupResponse;
import school.campusconnect.datamodel.GruppieContactListResponse;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.LoginResponse;
import school.campusconnect.datamodel.PersonalSettingRes;
import school.campusconnect.datamodel.PostResponse;
import school.campusconnect.datamodel.ProfileItemUpdate;
import school.campusconnect.datamodel.ProfileResponse;
import school.campusconnect.datamodel.ReadGroupPostResponse;
import school.campusconnect.datamodel.ReadTeamPostResponse;
import school.campusconnect.datamodel.RegisterRequest;
import school.campusconnect.datamodel.SettingRes;
import school.campusconnect.datamodel.SignUpRequest;
import school.campusconnect.datamodel.SignUpResponse;
import school.campusconnect.datamodel.TeamSettingRes;
import school.campusconnect.datamodel.TimeTableRes;
import school.campusconnect.datamodel.UserListResponse;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.datamodel.addgroup.CreateGroupResponse;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserResponse;
import school.campusconnect.datamodel.comments.AddCommentRes;
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
import school.campusconnect.datamodel.subjects.AbsentSubjectReq;
import school.campusconnect.datamodel.subjects.AddSubjectStaffReq;
import school.campusconnect.datamodel.subjects.SubjectResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamsResponse;
import school.campusconnect.datamodel.teamdiscussion.TeamPostGetResponse;
import school.campusconnect.datamodel.time_table.SubStaffTTReq;
import school.campusconnect.datamodel.time_table.SubjectStaffTTResponse;
import school.campusconnect.datamodel.time_table.TimeTableList2Response;
import school.campusconnect.datamodel.versioncheck.VersionCheckResponse;
import school.campusconnect.datamodel.videocall.StartMeetingRes;
import school.campusconnect.datamodel.videocall.VideoClassResponse;
import school.campusconnect.datamodel.youtubetoken.YoutubeTokenResponse;

public interface LeafService {

    @POST("/api/v1/user/exist/category/app")
    @Headers({"Content-Type: application/json"})
    Call<NumberExistResponse> next(@Body NumberExistRequest request, @Query("category") String category);

    @POST("/api/v1/login/category/app")
    @Headers({"Content-Type: application/json"})
    Call<LoginResponse> login(@Body LoginRequest request, @Query("category") String category, @Query("deviceToken") String deviceToken, @Query("deviceType") String deviceType);

    @POST("/api/v1/verify/otp/category/app")
    @Headers({"Content-Type: application/json"})
    Call<OtpVerifyRes> otpVerify(@Body OtpVerifyReq request, @Query("category") String category);

    @PUT("/api/v1/create/password/category/app")
    @Headers({"Content-Type: application/json"})
    Call<LoginResponse> newPass(@Body NewPassReq request, @Query("category") String category);

    @PUT("/api/v1/forgot/password/category/app")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> forgotPassword(@Body ForgotPasswordRequest request, @Query("category") String category, @Query("sms") int count);

    @PUT("/api/v1/password/change/category/app")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request, @Query("category") String category);

    @PUT("/api/v1/number/change")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> changeNumber(@Body ChangeNumberRequest request);


    @POST("/api/v1/register/category/app")
    @Headers({"Content-Type: application/json"})
    Call<SignUpResponse> signup(@Body SignUpRequest request, @Query("category") String category);


    @PUT("/api/v1/logout")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> logout();

    @POST("/api/v1/groups/{group_id}/users")
    @Headers({"Content-Type: application/json"})
    Call<InviteResponseSingle> inviteInGroup(@Path("group_id") String group_id, @Body AddLeadRequest request);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/user/add")
    @Headers({"Content-Type: application/json"})
    Call<InviteResponseSingle> inviteInTeam(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body AddLeadRequest request);


    @POST("/api/v1/groups/{id}/post/add")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> addPostGroupFile(@Path("id") String id, @Body AddPostRequestFile request);

    @POST("/api/v1/groups/{id}/post/add")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> addPostGroupDesc(@Path("id") String id, @Body AddPostRequestDescription request);

    @POST("/api/v1/groups/{group_id}/posts/add")
    @Headers({"Content-Type: application/json"})
    Call<AddPostResponse> addPostGroup(@Path("group_id") String group_id, @Body AddPostRequest request);

    @PUT("api/v1/groups/{id}/teamposts/{post_id}/message/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deleteTeamPost(@Path("id") String id, @Path("post_id") String post_id);

    @PUT("api/v1/groups/{id}/posts/{post_id}/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deleteGroupPost(@Path("id") String id, @Path("post_id") String post_id);

    @PUT("/api/v2/groups/{id}/personalpost/{post_id}/sender/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deletePersonalInboxPost(@Path("id") String id, @Path("post_id") String post_id);

    @PUT("/api/v2/groups/{id}/personalpost/{post_id}/receiver/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deletePersonalOutboxPost(@Path("id") String id, @Path("post_id") String post_id);

    @PUT("/api/v1/profile/pic/remove")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deletePropic();

    @PUT("/api/v1/admin/groups/{group_id}/profile/remove")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteGrouppic(@Path("group_id") String group_id);

    @PUT("/api/v1/profile/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> updateProfile(@Body ProfileItemUpdate request);

    @GET("/api/v1/groups")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupResponse> getGroupList();

    @GET("/api/v1/admin/groups/{group_id}/all/users")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<UserListResponse> getAllUsersList(@Path("group_id") String group_id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{id}/leads")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<UserListResponse> getAllUsersListBySearch(@Path("id") String id, @Query("filter") String pagenumber);


    @GET("/api/v1/groups/{group_id}/my/people")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ContactListResponse> getAllContactListNoPaginate(@Path("group_id") String groupId);

    @GET("/api/v1/gruppie/all")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ContactListResponse> getAllContactListBySearch(@Query("filter") String pagenumber);

    @GET("/api/v1/groups/{id}/notfriends")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GruppieContactListResponse> getGruppieContactList(@Path("id") String id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{id}/notfriends")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GruppieContactListResponse> getGruppieContactListBySearch(@Path("id") String id, @Query("filter") String title);

    @GET("/api/v1/groups/{id}/leads/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getLeadsList(@Path("id") String id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{id}/leads/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getLeadsListBySearch(@Path("id") String id, @Query("filter") String title);

    @GET("/api/v1/groups/{id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupDetailResponse> getGroupDetail(@Path("id") String id);

    @GET("/api/v1/profile/show")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ProfileResponse> getProfileDetails();

    @DELETE("/api/v1/admin/groups/{group_id}/users/{user_id}/delete")
    @Headers({"Content-Type: application/json"})
    Call<LeaveResponse> deleteUser(@Path("group_id") String group_id, @Path("user_id") String uid);

    @GET("/api/v1/groups/{id}/posts/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getGeneralPosts(@Path("id") String id, @Query("page") int pagenumber);


    @GET("/api/v1/groups/{group_id}/posts/saved")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getFavPosts(@Path("group_id") String id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{id}/team/messages/inbox")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getTeamInboxPosts(@Path("id") String id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{id}/team/messages/outbox")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getTeamOutboxPosts(@Path("id") String id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{group_id}/friend/{idd}/users")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getNestedFriends(@Path("group_id") String groupId, @Path("idd") String leadId, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/post/{post_id}/read/unread")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadUnreadResponse> getReadUnreadUser(@Path("group_id") String groupId, @Path("team_id") String teamId, @Path("post_id") String post_id);


    @GET("api/v1/groups/{id}/users/{idd}/referrals")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getReferrersFilter(@Path("id") String groupId, @Path("idd") String leadId, @Query("filter") String searchStr);


    @PUT("/api/v1/groups/{group_id}/posts/{post_id}/save")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> setFavourite(@Path("group_id") String groupId, @Path("post_id") String postId);

    @PUT("/api/v1/groups/{group_id}/posts/{post_id}/like")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> setLike(@Path("group_id") String groupId, @Path("post_id") String postId);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/like")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> setLikeTeam(@Path("group_id") String groupId, @Path("team_id") String team_id, @Path("post_id") String post_id);

    @PUT("/api/v1/groups/{group_id}/post/{post_id}/individual/post/like")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> setPersonalLike(@Path("group_id") String groupId, @Path("post_id") String post_id);


    @PUT("/api/v1/admin/groups/{group_id}/users/{user_id}/allow/post")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowPost(@Path("group_id") String groupId, @Path("user_id") String uid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/user/add/disallow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowAddOtherMember(@Path("group_id") String groupId, @Path("team_id") String team_id, @Path("user_id") String uid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/team/post/allow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowTeamPost(@Path("group_id") String groupId, @Path("team_id") String team_id, @Path("user_id") String uid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/team/comment/allow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowTeamPostComment(@Path("group_id") String groupId, @Path("team_id") String team_id, @Path("user_id") String uid);


    @PUT("/api/v1/admin/groups/{group_id}/users/{user_id}/remove/post")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> notAllowPost(@Path("group_id") String groupId, @Path("user_id") String uid);

    @POST("/api/v1/groups/{id}/add/multiple")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addMultipleFriendToGroup(@Path("id") String groupId, @Query("userId") String uid);

    @GET("/api/v1/groups/{id}/myleads/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getMyLeads(@Path("id") String id, @Query("page") int pagenumber);

    @PUT("/api/v1/groups/{id}/leave")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeaveResponse> leaveGroup(@Path("id") String id);

    @PUT("/api/v1/groups/{id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteGRoup(@Path("id") String id);

    @PUT("/api/v1/admin/groups/{group_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editGroup(@Path("group_id") String group_id, @Body CreateGroupReguest reguest);

    @POST("api/v1/groups/create")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<CreateGroupResponse> createGroup(@Body CreateGroupReguest reguest);

    @POST("/api/v1/groups/{group_id}/team/create")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddTeamResponse> createTeam(@Path("group_id") String group_id, @Body CreateTeamRequest reguest);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editTeam(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body CreateTeamRequest reguest);

    @POST("/api/v1/groups/{group_id}/class/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddTeamResponse> addClass(@Path("group_id") String group_id, @Body ClassResponse.ClassData reguest);

    @POST("/api/v1/groups/{group_id}/subjects/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddTeamResponse> addSubject(@Path("group_id") String group_id, @Body SubjectResponse.SubjectData reguest);

    @PUT("/api/v1/groups/{group_id}/subject/{subject_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editSubject(@Path("group_id") String group_id, @Path("subject_id") String subject_id, @Body SubjectResponse.SubjectData reguest);

    @DELETE("/api/v1/groups/{group_id}/subject/{subject_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteSubject(@Path("group_id") String group_id, @Path("subject_id") String subject_id);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteTeam(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/archive")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> archiveTeam(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/archive/restore")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> restoreArchiveTeam(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/archive")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MyTeamsResponse> getArchiveTeams(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{groupId}/team/{teamId}/user/{userId}/teams")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MyTeamsResponse> getNestedTeams(@Path("groupId") String group_id, @Path("teamId") String teamId, @Path("userId") String userId);


    @PUT("/api/v1/groups/{group_id}/team/{team_id}/leave")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> leaveTeam(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/remove")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> removeTeamUser(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id);

    @GET("/api/v1/groups/{id}/post/{post_id}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadGroupPostResponse> readGroupRequest(@Path("id") String group_id, @Path("post_id") String post_id);

    @GET("/api/v2/groups/{id}/team/{team_id}/post/{post_id}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadTeamPostResponse> readTeamRequest(@Path("id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id);

    // Team Posts

    @GET("/api/v1/groups/{id}/teams")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MyTeamsResponse> getMyTeams(@Path("id") String group_id);

    @GET("/api/v1/groups/{group_id}/my/teams")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MyTeamsResponse> getMyTeamsNew(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/my/people")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getMyPeople(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/class/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ClassResponse> getClasses(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/class/video/conference")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<VideoClassResponse> getVideoClasses(@Path("group_id") String group_id);

    @PUT("/api/v1/groups/{group_id}/team/{teamId}/jitsi/start")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<StartMeetingRes> startMeeting(@Path("group_id") String group_id, @Path("teamId") String teamId);

    @PUT("/api/v1/groups/{group_id}/team/{teamId}/jitsi/stop")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<StartMeetingRes> stopMeeting(@Path("group_id") String group_id, @Path("teamId") String teamId);



    @GET("/api/v1/groups/{group_id}/ebooks/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<EBooksResponse> getEBooks(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/ebooks/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<EBooksTeamResponse> getEBooksForTeam(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @POST("/api/v1/groups/{group_id}/ebooks/register")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addEBook(@Path("group_id") String group_id,@Body AddEbookReq addEbookReq);

    @POST("/api/v1/groups/{group_id}/team/{teamId}/jitsi/token/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addJitiToken(@Path("group_id") String group_id,@Path("teamId") String teamId);


    @PUT("/api/v1/groups/{group_id}/ebook/{book_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteEBook(@Path("group_id") String group_id,@Path("book_id") String book_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/ebook/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> updateEBookInClass(@Path("group_id") String group_id,@Path("team_id") String team_id,@Query("ebookId") String ebookId);


    @GET("/api/v1/groups/{group_id}/subjects/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SubjectResponse> getSubjects(@Path("group_id") String group_id);


    @GET("/api/v1/groups/{group_id}/team/{team_id}/subject/staff/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SubjectStaffResponse> getSubjectStaff(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/year/timetable/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<TimeTableList2Response> getTTNew(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/year/timetable/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<TimeTableList2Response> getTTNewDayWise(@Path("group_id") String group_id, @Path("team_id") String team_id,@Query("day") String day);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/subject/staff/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addSubjectStaff(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body AddSubjectStaffReq addSubjectStaffReq);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/subject/{subject_with_staff_id}/staff/{staff_id}/year/timetable/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addSubjectStaffTT(@Path("group_id") String group_id, @Path("team_id") String team_id,
                                         @Path("subject_with_staff_id") String subject_with_staff_id, @Path("staff_id") String staff_id,
                                         @Body SubStaffTTReq addSubjectStaffReq);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/markscard/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MarkCardListResponse> getMarkCardList(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/markscard/{markscard_id}/students/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<StudentMarkCardListResponse> getMarkCardStudents(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("markscard_id") String markscard_id);



    @GET("/api/v1/groups/{group_id}/team/{team_id}/subjects/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SubjectTeamResponse> getTeamSubjects(@Path("group_id") String group_id,@Path("team_id") String team_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/markscard/create")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddTeamResponse> createMarkCard(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body AddMarkCardReq req);


    @GET("/api/v1/groups/{group_id}/my/class/teams")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ClassResponse> getTeacherClasses(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/my/kids")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ParentKidsResponse> getParentKids(@Path("group_id") String group_id);


    @GET("/api/v1/groups/{group_id}/staff/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<StaffResponse> getStaff(@Path("group_id") String group_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/year/timetable/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SubjectStaffTTResponse> getSubjectStaffTT(@Path("group_id") String group_id, @Path("team_id") String team_id,
                                                   @Body SubStaffTTReq subStaffTTReq);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/students/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<StudentRes> getStudents(@Path("group_id") String group_id, @Path("team_id") String team_id);


    @GET("/api/v1/groups/{group_id}/user/{user_id}/people")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getNestPeople(@Path("group_id") String group_id, @Path("user_id") String user_id);


    @GET("/api/v1/groups/{group_id}/team/{team_id}/posts/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<TeamPostGetResponse> teamPostGet(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("page") int page);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/post/{post_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteTeamPost(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id);

    @POST("/api/v2/groups/{id}/team/{team_id}/posts/add")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> addPostTeamFileNew(@Path("id") String group_id, @Path("team_id") String team_id, @Body AddPostRequestFile request);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/posts/add")
    @Headers({"Content-Type: application/json"})
    Call<AddPostResponse> addPostTeam(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body AddPostRequest request);

    @POST("/api/v2/groups/{id}/team/{team_id}/posts/add")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> addPostTeamDescNew(@Path("id") String group_id, @Path("team_id") String team_id, @Body AddPostRequestDescription request);

    // Group Comments

    @POST("/api/v1/groups/{group_id}/posts/{post_id}/comments/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addGroupComment(@Path("group_id") String group_id, @Path("post_id") String post_id, @Body AddGroupCommentRequest request);

    @GET("api/v1/groups/{group_id}/posts/{post_id}/comments/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllGroupComments(@Path("group_id") String group_id, @Path("post_id") String post_id, @Query("page") int pagenumber);

    @POST("/api/v1/groups/{group_id}/posts/{post_id}/comments/{comment_id}/reply/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addGroupCommentReply(@Path("group_id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @GET("/api/v1/groups/{group_id}/posts/{post_id}/comments/{comment_id}/reply/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllGroupCommentReplies(@Path("group_id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Query("page") int pagenumber);

    @PUT("/api/v2/groups/{id}/posts/{post_id}/comments/{comment_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> editGroupComment(@Path("id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @DELETE("/api/v1/groups/{group_id}/posts/{post_id}/comment/{comment_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> deleteComment(@Path("group_id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    @PUT("/api/v1/groups/{group_id}/posts/{post_id}/comments/{comment_id}/like")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> likeComment(@Path("group_id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    // Team Comments

    @POST("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addTeamComment(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Body AddGroupCommentRequest request);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllTeamComments(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("page") int pagenumber);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/{comment_id}/reply/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addTeamCommentReply(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/{comment_id}/reply/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllTeamCommentReplies(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Query("page") int pagenumber);

    @PUT("/api/v2/groups/{id}/team/{team_id}/post/{post_id}/comment/{comment_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> editTeamComment(@Path("id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comment/{comment_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> deleteTeamComment(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/{comment_id}/like")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> likeTeamComment(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    // Individual Comments

    @POST("/api/v1/groups/{group_id}/user/{user_id}/posts/{post_id}/comments/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addPersonalComment(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Body AddGroupCommentRequest request);

    @GET("/api/v1/groups/{group_id}/user/{user_id}/posts/{post_id}/comments/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllPersonalComments(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Query("page") int pagenumber);

    @PUT("/api/v2/groups/{id}/individualpost/{post_id}/comment/{comment_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> editPersonalComment(@Path("id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @DELETE("/api/v1/groups/{group_id}/user/{user_id}/posts/{post_id}/comment/{comment_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> deletePersonalComment(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    @POST("/api/v1/groups/{group_id}/user/{user_id}/posts/{post_id}/comments/{comment_id}/reply/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddCommentRes> addPersonalCommentReply(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Body AddGroupCommentRequest request);

    @GET("/api/v1/groups/{group_id}/user/{user_id}/posts/{post_id}/comments/{comment_id}/reply/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupCommentResponse> getAllPersonalCommentReplies(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Query("page") int pagenumber);

    // Report Apis

    @GET("/api/v1/post/report/reasons")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReportResponse> getReportList();

    @POST("/api/v1/groups/{group_id}/post/{post_id}/report")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> reportGroupPost(@Path("group_id") String group_id, @Path("post_id") String post_id, @Query("type") int type);

    @POST("/api/v2/groups/{id}/team/{team_id}/post/{post_id}/report/{reason_id}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> reportTeamPost(@Path("id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("reason_id") int reason_id);

    // Share Apis

    // Groups Apis

    @GET("/api/v2/groups/post")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ShareGroupResponse> getGroupListShare();

    @POST("/api/v2/groups/{id}/post/{post_id}/group/post/share")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareGroupPost(@Path("id") String group_id, @Path("post_id") String post_id, @Query("groupsid") String groupsid);

    @PUT("/api/v1/groups/{id}/posts/{post_id}/share/group")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedGroupPost(@Body AddPostRequestDescription request, @Path("id") String group_id, @Path("post_id") String post_id, @Query("groupId") String groupsid);

    @GET("/api/v2/groups/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ShareGroupResponse> getTeamGroupListShare(@Query("page") int pagenumber);

    // Team Share

    @GET("/api/v2/groups/{id}/team/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ShareGroupResponse> getTeamSelectListShare(@Path("id") String group_id, @Query("page") int pagenumber);

    @POST("/api/v2/groups/{id}/post/{post_id}/group/post/share/team")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareTeamPost(@Path("id") String group_id, @Path("post_id") String post_id, @Query("groupid") String groupid, @Query("teamid") String teamid);

    @PUT("/api/v1/groups/{id}/posts/{post_id}/share/team")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedTeamPost(@Body AddPostRequestDescription request, @Path("id") String group_id, @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("teamId") String teamid);

    @POST("/api/v2/groups/{id}/team/{team_id}/post/{post_id}/team/post/share")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareTeamPostToGroup(@Path("id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("groupsid") String groupsid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/post/{post_id}/share/group")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedTeamPostToGroup(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("groupId") String groupsid);

    @POST("/api/v2/groups/{id}/team/{team_id}/post/{post_id}/team/post/share/team")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareTeamPostToTeam(@Path("id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("groupid") String groupid, @Query("teamid") String teamid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/post/{post_id}/share/team")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedTeamPostToTeam(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("teamId") String teamid);


    @PUT("/api/v1/groups/{group_id}/posts/{post_id}/share/friend")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedPersonalPost(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("friendsId") String friendid);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/post/{post_id}/share/friend")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedPersonalPost(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("team_id") String team_id,
                                               @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("friendsId") String friendid);


    @PUT("/api/v1/groups/{group_id}/user/{user_id}/post/{post_id}/share/group")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedPersonalPostToGroup(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Query("groupId") String groupIds);


    @PUT("/api/v1/groups/{group_id}/user/{user_id}/post/{post_id}/share/team")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedPersonalPostToTeam(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("teamId") String teamid);

    @PUT("/api/v1/groups/{group_id}/user/{user_id}/post/{post_id}/share/friend")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> shareEditedPersonalPostToFriends(@Body AddPostRequestDescription request, @Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id, @Query("groupId") String groupid, @Query("friendsId") String friendid);


    @GET("/api/v1/groups/{group_id}/team/{team_id}/users")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getTeamMember(@Path("group_id") String group_id, @Path("team_id") String team_id/*, @Query("page") int pagenumber*/);

    @GET("/api/v1/groups/{group_id}/chat/contacts")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getTeamMemberFromChat(@Path("group_id") String group_id);


    @GET("/api/v2/groups/{id}/team/{team_id}/members")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> getPersonalBySearch(@Path("id") String group_id, @Path("team_id") String team_id, @Query("filter") String title);

    @GET("/api/v2/groups/{id}/personal/outbox")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getOutbox(@Path("id") String group_id, @Query("page") int pagenumber);


    @GET("/api/v1/admin/groups/{group_id}/users/authorised")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AuthorizedUserResponse> getAuthorizedList(@Path("group_id") String group_id, @Query("page") int pagenumber);

    @GET("/api/v2/groups/{id}/posts/users/list")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AuthorizedUserResponse> getAuthorizedListBySearch(@Path("id") String group_id, @Query("filter") String pagenumber);

    @PUT("/api/v1/admin/groups/{group_id}/users/{user_id}/admin/change")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> changeAdmin(@Path("group_id") String group_id, @Path("user_id") String user_id);

    // Notification Api

    @GET("/api/v2/groups/notification")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<NotificationResponse> getNotificationsGroup(@Query("page") int pagenumber);

    @GET("/api/v2/team/notification")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<NotificationResponse> getNotificationsTeam(@Query("page") int pagenumber);

    @GET("/api/v2/user/notification")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<NotificationResponse> getNotificationsPersonal(@Query("page") int pagenumber);

    // Personal chat like whatsApp

    @GET("/api/v1/groups/{group_id}/chat/inbox")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PersonalPostResponse> getInbox(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/user/{user_id}/posts/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostResponse> getPersonalListChat(@Path("group_id") String group_id, @Path("user_id") String user_id, @Query("page") int pagenumber);

    @PUT("/api/v1/groups/{group_id}/user/{user_id}/post/{post_id}/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deletePersonalChatPost(@Path("group_id") String group_id, @Path("user_id") String user_id, @Path("post_id") String post_id);

    @POST("/api/v1/groups/{group_id}/user/{user_id}/post/add")
    @Headers({"Content-Type: application/json"})
    Call<AddPostResponse> addPersonalPostFromChat(@Path("group_id") String group_id, @Path("user_id") String user_id, @Body AddPostRequest request);

    // Questions

    @GET("/api/v2/groups/{id}/questions/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<QuestionResponse> getQuestions(@Path("id") String group_id, @Query("page") int pagenumber);

    @POST("/api/v2/groups/{id}/post/{post_id}/question/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addQueImage(@Body AddPostRequestFile_Friend request, @Path("id") String group_id, @Path("post_id") String post_id);

    @POST("/api/v2/groups/{id}/post/{post_id}/question/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addQueVideo(@Body AddPostRequestVideo_Friend request, @Path("id") String group_id, @Path("post_id") String post_id);

    @POST("/api/v2/groups/{id}/question/{question_id}/answer")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addAnsImage(@Body AddPostRequestFile_Friend request, @Path("id") String group_id, @Path("question_id") String question_id);

    @POST("/api/v2/groups/{id}/question/{question_id}/answer")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addAnsVideo(@Body AddPostRequestVideo_Friend request, @Path("id") String group_id, @Path("question_id") String question_id);

    @PUT("/api/v2/groups/{id}/question/{question_id}/delete")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> deleteQue(@Path("id") String id, @Path("question_id") String question_id);


    // Update Contacts https://gruppie.in/api/v2/gruppie/friends/updated/data?friendsId=12,3

    @GET("/api/v1/gruppie/friends/data")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ContactListResponse> updateContactList(@Query("friendsId") String friendsId);

    @GET("/api/v1/groups/{group_id}/friend/data")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ContactListResponse> updateFriendList(@Path("group_id") String group_id, @Query("friendsId") String friendsId);

    // Notification seen

    @PUT("/api/v2/groups/notification/seen")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> groupNotiSeen(@Query("notificationId") String notificationId);

    @PUT("/api/v2/team/notification/seen")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> teamNotiSeen(@Query("notificationId") String notificationId);

    @PUT("/api/v2/user/notification/seen")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> personalNotiSeen(@Query("notificationId") String notificationId);

    @POST("/api/v1/groups/{group_id}/invite/multiple")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<InviteResponse> inviteMultipleFriendsInGroup(@Path("group_id") String group_id, @Query(value = "user[]", encoded = true) ArrayList<String> users);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/user/add/contact")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<InviteResponse> inviteMultipleFriendsInTeam(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query(value = "user[]", encoded = true) ArrayList<String> users);

    @GET("/api/v1/youtube/token")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<YoutubeTokenResponse> youtubeToken();

    @PUT("/api/v2/groups/{id}/allow/duplicate")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowDuplicate(@Path("id") String group_id);

    @PUT("/api/v2/groups/{group_id}/allow/post/question")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowPostQue(@Path("group_id") String group_id);

    @PUT("/api/v1/admin/groups/{group_id}/post/share/allow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowShare(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/notfriends")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<NotInGruppieResponse> notInGruppie(@Path("group_id") String group_id);

    @GET("/api/v1/app/version")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<VersionCheckResponse> versionCheck();

    @GET("/api/v1/groups/{group_id}/posts/{post_id}/likes/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LikeListResponse> likeList(@Path("group_id") String group_id, @Path("post_id") String post_id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/likes/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LikeListResponse> likeListTeam(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Query("page") int pagenumber);


    @GET("/api/v1/groups/{group_id}/posts/{post_id}/comments/{comment_id}/likes/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LikeListResponse> groupCommentLikeList(@Path("group_id") String group_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Query("page") int pagenumber);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/posts/{post_id}/comments/{comment_id}/likes/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LikeListResponse> teamCommentLikeList(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("post_id") String post_id, @Path("comment_id") String comment_id, @Query("page") int pagenumber);


//    @Field("lost_project_reasons[]")

    @GET("/api/v1/groups/public")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PublicGroupResponse> getPublicGroupList();

    @POST("/api/v1/groups/{id}/join")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<JoinListResponse> getJoinList(@Path("id") String group_id);

    @POST("/api/v1/groups/{id}/join/submit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> joinGroup(@Path("id") String group_id, @Query("friendsId") String friendIds);

    @POST("/api/v1/groups/{id}/join/submit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> joinGroupDirect(@Path("id") String group_id);

    @PUT("/api/v1/admin/groups/{group_id}/allow/post/all")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowToPostAll(@Path("group_id") String group_id);

    @PUT("/api/v1/admin/groups/{group_id}/admin/change/allow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowChangeAdmin(@Path("group_id") String group_id);


    @GET("/api/v1/admin/groups/{group_id}/settings")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SettingRes> getSettingData(@Path("group_id") String group_id);

    //team settings
    @PUT("/api/v1/groups/{group_id}/team/{team_id}/allow/post/all")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowTeamPostAll(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/allow/comment/all")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowTeamCommentAll(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/add/allow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowUsersToAddTeamMember(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/add/disallow")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> disAllowUsersToAddTeamMember(@Path("group_id") String group_id, @Path("team_id") String team_id);


    @PUT("/api/v1/groups/{group_id}/team/{team_id}/gps/enable")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> enableDisableGps(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/attendance/enable")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> enableDisableAttendance(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/settings")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<TeamSettingRes> getTeamSettingData(@Path("group_id") String group_id, @Path("team_id") String team_id);


    //Personal Settings
    @PUT("/api/v1/groups/{group_id}/user/{user_id}/allow/chat/reply")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowPersonalReply(@Path("group_id") String group_id, @Path("user_id") String user_id);

    @PUT("/api/v1/groups/{group_id}/user/{user_id}/allow/comment")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> allowPersonalComment(@Path("group_id") String group_id, @Path("user_id") String user_id);

    @GET("/api/v1/groups/{group_id}/user/{user_id}/settings")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PersonalSettingRes> getPersonalSettingData(@Path("group_id") String group_id, @Path("user_id") String user_id);


    //Gps track
    @POST("/api/v1/groups/{group_id}/team/{team_id}/trip/start")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> startUpdateTrip(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("lat") double lat, @Query("long") double lng);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/trip/end")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> endTrip(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/trip/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GetLocationRes> getLocation(@Path("group_id") String group_id, @Path("team_id") String team_id);


    //Attendance
    @GET("/api/v1/groups/{group_id}/team/{team_id}/subjects/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SubjectResponse> getAttendanceSubject(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/attendance/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AttendanceListRes> getAttendance(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/attendance/import")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> importStudent(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/attendance/{attendance_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editAttendance(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("attendance_id") String attendance_id, @Body() EditAttendanceReq editAttendanceReq);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/attendance/{attendance_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> removeAttendance(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("attendance_id") String attendance_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/message/absenties")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AbsentAttendanceRes> sendAbsenties(@Path("group_id") String group_id, @Path("team_id") String team_id,
                                            @Query(value = "userIds[]", encoded = true) ArrayList<String> userIds, @Body AbsentSubjectReq absentSubjectReq);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/attendance/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body AddStudentReq addStudentReq);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/student/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addClassStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body StudentRes.StudentData addStudentReq);

    @POST("/api/v1/groups/{group_id}/staff/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addStaff(@Path("group_id") String group_id, @Body StaffResponse.StaffData staffData);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/school/user/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addTeamStaffOrStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("userId") String userIds);


    @PUT("/api/v1/groups/{group_id}/staff/{user_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editStaff(@Path("group_id") String group_id, @Path("user_id") String user_id, @Body StaffResponse.StaffData staffData);


    @PUT("/api/v1/groups/{group_id}/team/{team_id}/student/{user_id}/edit")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> editClassStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id, @Body StudentRes.StudentData addStudentReq);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/student/{user_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteClassStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id);

    @DELETE("/api/v1/groups/{group_id}/staff/{user_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteStaff(@Path("group_id") String group_id, @Path("user_id") String user_id);


    @GET("/api/v1/groups/{group_id}/team/{team_id}/attendance/report/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AttendanceReportRes> getAttendanceReport(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("month") int month, @Query("year") int year);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/attendance/report/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AttendanceDetailRes> getAttendanceDetail(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id, @Query("rollNumber") String rollNumber, @Query("month") int month, @Query("year") int year);


    @GET("/api/v1/groups/{group_id}/gallery/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GalleryPostRes> getGalleryPost(@Path("group_id") String group_id, @Query("page") int page);

    @PUT("/api/v1/groups/{group_id}/album/{album_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteGalleryPost(@Path("group_id") String groupId, @Path("album_id") String album_id);

    @PUT("/api/v1/groups/{group_id}/album/{album_id}/remove")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteGalleryFile(@Path("group_id") String groupId, @Path("album_id") String album_id,@Query("fileName") String fileName);

    @POST("/api/v1/groups/{group_id}/gallery/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addGalleryPost(@Path("group_id") String groupId, @Body AddGalleryPostRequest addGalleryPostRequest);

    @PUT("/api/v1/groups/{group_id}/album/{album_id}/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addGalleryFile(@Path("group_id") String groupId,@Path("album_id") String album_id, @Body AddGalleryPostRequest addGalleryPostRequest);

    @GET("/api/v1/groups/{group_id}/timetable/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<TimeTableRes> getTimeTablePost(@Path("group_id") String group_id, @Query("page") int page);

    @PUT("/api/v1/groups/{group_id}/timetable/{time_table_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteTimeTablePost(@Path("group_id") String groupId, @Path("time_table_id") String time_table_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/timetable/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addTimeTablePost(@Path("group_id") String groupId, @Path("team_id") String team_id, @Body AddTimeTableRequest addTimeTableRequest);

    @GET("/api/v1/groups/{group_id}/vendors/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<VendorPostResponse> getVendorPost(@Path("group_id") String group_id, @Query("page") int page);

    @PUT("/api/v1/groups/{group_id}/vendor/{vendor_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteVendorPost(@Path("group_id") String groupId, @Path("vendor_id") String vendor_id);

    @POST("/api/v1/groups/{group_id}/vendors/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addVendorPost(@Path("group_id") String groupId, @Body AddVendorPostRequest addVendorPostRequest);

    @GET("/api/v1/groups/{group_id}/coc/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<CodeConductResponse> getCodeOfConductPost(@Path("group_id") String group_id, @Query("page") int page);

    @PUT("/api/v1/groups/{group_id}/coc/{coc_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteCodeConduct(@Path("group_id") String groupId, @Path("coc_id") String coc_id);

    @POST("/api/v1/groups/{group_id}/coc/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addCodeOfConduct(@Path("group_id") String groupId, @Body AddCodeOfConductReq addCodeOfConductReq);

//    @GET("/api/v1/groups/{group_id}/my/people")
//    @Headers({"Content-Type: application/json", "Accept: application/json"})
//    Call<LeadResponse> getMyPeople(@Path("group_id") String group_id);

    @GET("/api/v1/groups/{group_id}/notifications/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<NotificationListRes> getNotificationList(@Path("group_id") String groupId);

    @GET("/api/v1/groups/{groupId}/post/{postId}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadMoreRes> readMore_GroupPost(@Path("groupId") String groupId, @Path("postId") String postId);

    @GET("/api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadMoreRes> readMore_TeamPost(@Path("groupId") String groupId, @Path("postId") String postId, @Path("teamId") String teamId);

    @GET("/api/v1/groups/{groupId}/team/{teamId}/post/{postId}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadMoreRes> readMore_TeamPostComment(@Path("groupId") String groupId, @Path("postId") String postId, @Path("teamId") String teamId);

    @GET("/api/v1/groups/{groupId}/album/{albumId}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadMoreRes> readMore_Gallery(@Path("groupId") String groupId, @Path("albumId") String postId);

    @GET("/api/v1/groups/{groupId}/user/{userId}/post/{postId}/read")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReadMoreRes> readMore_Individual(@Path("groupId") String groupId, @Path("postId") String postId, @Path("userId") String userId);


    @POST("/api/v1/groups/{group_id}/school/calendar/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addEvent(@Body AddEventReq addEventReq, @Path("group_id") String group_id, @Query("day") int day, @Query("month") int month, @Query("year") int year);

    @GET("/api/v1/groups/{group_id}/school/calendar/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<EventListRes> getEventList(@Path("group_id") String group_id, @Query("month") int month, @Query("year") int year);

    @GET("/api/v1/groups/{group_id}/school/calendar/event/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<EventInDayRes> getEventSelectedDay(@Path("group_id") String group_id, @Query("day") int day, @Query("month") int month, @Query("year") int year);

    @DELETE("/api/v1/groups/{group_id}/event/{event_id}/delete")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteEvent(@Path("group_id") String group_id, @Path("event_id") String event_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/leave/request")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddPostResponse> leaveRequest(@Body LeaveReq leaveReq, @Path("group_id") String group_id, @Path("team_id") String team_id, @Query("name") String names);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/user/{user_id}/change/admin")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> changeTeamAdmin(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id);


    @GET("/api/v1/groups/{group_id}/team/{team_id}/leave/request/form")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LeadResponse> leaveForm(@Path("group_id") String group_id, @Path("team_id") String teamId);


    @POST("/api/v1/groups/{group_id}/bus/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddTeamResponse> addBus(@Path("group_id") String group_id, @Body BusResponse.BusData reguest);

    @DELETE("/api/v1/groups/{group_id}/team/{team_id}/student/{user_id}/delete/bus")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteBusStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Path("user_id") String user_id);

    @GET("/api/v1/groups/{group_id}/bus/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BusResponse> getBusList(@Path("group_id") String group_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/student/add/bus")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> addBusStudent(@Path("group_id") String group_id, @Path("team_id") String team_id, @Body BusStudentRes.StudentData addStudentReq);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/bus/students/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BusStudentRes> getBusStudents(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/attendance/get/preschool")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PreSchoolStudentRes> getPreSchoolStudent(@Path("group_id") String group_id, @Path("team_id") String team_id);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/student/in?")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> attendanceIN(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("userId") String userId);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/student/out?")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> attendanceOUT(@Path("group_id") String group_id, @Path("team_id") String team_id, @Query("userId") String userId);

    @POST("/api/v1/groups/{group_id}/team/{team_id}/markscard/{markscard_id}/student/{student_id}/marks/add")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<AddPostResponse> addMarksheet(@Path("group_id") String group_id,
                                       @Path("team_id") String team_id,
                                       @Path("markscard_id") String markscard_id,
                                       @Path("student_id") String userId,
                                       @Query("rollNo") String rollNumber, @Body UploadMarkRequest addMarkSheetReq);

    @GET("/api/v1/groups/{group_id}/team/{team_id}/student/{student_id}/markscard/{markscard_id}/get")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<MarkSheetListResponse> getMarkSheetList(@Path("group_id") String group_id,
                                                 @Path("team_id") String team_id,
                                                 @Path("markscard_id") String markscard_id,
                                                 @Path("student_id") String user_id,
                                                 @Query("rollNo") String rollNumber);

    @PUT("/api/v1/groups/{group_id}/team/{team_id}/student/{student_id}/markscard/{markscard_id}/remove")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<BaseResponse> deleteMarkCart(@Path("group_id") String group_id,
                                                 @Path("team_id") String team_id,
                                                 @Path("markscard_id") String markscard_id,
                                                 @Path("student_id") String student_id,
                                                 @Query("rollNo") String rollNumber);
    @GET
    Call<ResponseBody> downloadFile(@Url String url);


    @GET("/api/v1/groups")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<GroupResponse> getGroups(@Query("category") String category);
}