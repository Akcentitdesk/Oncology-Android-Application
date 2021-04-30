package com.indiaoncology.service;


import com.indiaoncology.model.PrescriptionResponse;
import com.indiaoncology.model.address.CheckPincodeBaseResponse;
import com.indiaoncology.model.blogDetail.BlogDetailResponse;
import com.indiaoncology.model.dashboard.IndexResponse;
import com.indiaoncology.model.doctor.DoctorResponse;
import com.indiaoncology.model.doctor.ListResponse;
import com.indiaoncology.model.doctor.location.LocationResponse;
import com.indiaoncology.model.feedback.FeedbackResponse;
import com.indiaoncology.model.medicine.MedicineResponse;
import com.indiaoncology.model.myAppointment.AppointmentListResponse;
import com.indiaoncology.model.myAppointment.AppointmentResponse;
import com.indiaoncology.model.order.OrderDetailResponse;
import com.indiaoncology.model.patient.PatientBaseResponse;
import com.indiaoncology.model.review.ReviewResponse;
import com.indiaoncology.model.order.SaveOrderResponse;
import com.indiaoncology.model.search.SearchingResponse;
import com.indiaoncology.model.user.LoginData;
import com.indiaoncology.model.type.TypeResponse;
import com.indiaoncology.model.user.UserBaseResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    // signup
    @FormUrlEncoded
    @POST("sign.php")
    Call<UserBaseResponse> getSignup(@Field("full_name") String name, @Field("email") String email, @Field("mobile") String mobile, @Field("password") String password);

    //verify otp for signup process
    @FormUrlEncoded
    @POST("checkotp.php")
    Call<LoginData> verifyOtpSignup(@FieldMap HashMap<String, String> map);

    // check user for login
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<UserBaseResponse> checkUser(@FieldMap HashMap<String, String> map);

    // login through password|login through OTP
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginData> login(@FieldMap HashMap<String, String> map);

    // get OTP for forgot password OR resend OTP
    @FormUrlEncoded
    @POST("sendotp.php")
    Call<UserBaseResponse> getOTP(@FieldMap HashMap<String, String> map);

    // reset password
    @FormUrlEncoded
    @POST("resetpassword.php")
    Call<UserBaseResponse> resetPassword(@Field("user_id") String user_id, @Field("otp") String otp, @Field("mobile") String mobile, @Field("new_password") String password);

    // social login
    @FormUrlEncoded
    @POST("sociallogin.php")
    Call<LoginData> getSocialLogin(@Field("social_token") String token, @Field("social_user_id") String social_user_id, @Field("email") String email, @Field("full_name") String name, @Field("device_token") String device_token, @Field("social_type") String social_type);


    // user|company data
    @FormUrlEncoded
    @POST("getuser.php")
    Call<LoginData> getuserdata(@Field("user_id") String user_id);


    // contact form
    @FormUrlEncoded
    @POST("contact.php")
    Call<BaseResponse> contact(@Field("name") String name, @Field("email") String email, @Field("mobile") String mobile, @Field("message") String message);

    //user logout
    @FormUrlEncoded
    @POST("logout.php")
    Call<BaseResponse> logout(@FieldMap HashMap<String, String> map);

    //Doctors list (category wise | top doctors)
    @FormUrlEncoded
    @POST("getdoctors.php")
    Call<ListResponse> getDoctorsList(@FieldMap HashMap<String, Object> map);

    // cancer type listing
    @GET("getdoctorcategory.php")
    Call<TypeResponse> getCategory();

    // cancer type listing
    @FormUrlEncoded
    @POST("getcancertypelist.php")
    Call<TypeResponse> getTypeList(@Field("page") int page);

    // article listing
    @FormUrlEncoded
    @POST("getarticle.php")
    Call<TypeResponse> getBlogList(@FieldMap HashMap<String, Object> map);

    // doctor profile
    @FormUrlEncoded
    @POST("getdoctorprofile.php")
    Call<DoctorResponse> getDoctorProfile(@Field("doctor_id") String doctor_id);

    // Time slot
    @FormUrlEncoded
    @POST("gettimeslot.php")
    Call<LocationResponse> getTimeSlot(@Field("user_id") String user_id, @Field("doctor_id") String doctor_id,
                                       @Field("location_id") String location_id);

    // get reviews
    @FormUrlEncoded
    @POST("getdoctorreview.php")
    Call<ReviewResponse> getDoctorReviews(@Field("page") int page_no, @Field("doctor_id") String doctor_id);

    // add review
    @FormUrlEncoded
    @POST("adddoctorreview.php")
    Call<BaseResponse> addDoctorReview(@Field("user_id") String user_id,
                                       @Field("doctor_id") String doctor_id,
                                       @Field("rating") float user_rating,
                                       @Field("review") String user_review);


    // fetch details for deep link (TODO)
    @FormUrlEncoded
    @POST("getdetail.php")
    Call<FeedbackResponse> getProfileDetails(@Field("id") String id,
                                             @Field("type") String type);


    /*  PATIENT SECTION  */
    @FormUrlEncoded
    @POST("addpatient.php")
    Call<BaseResponse> addPatient(@Field("user_id") String user_id,
                                  @Field("name") String name,
                                  @Field("mobile") String mobile,
                                  @Field("email") String email,
                                  @Field("age") String age,
                                  @Field("gender") String gender,
                                  @Field("is_self") String value);

    @FormUrlEncoded
    @POST("deletepatient.php")
    Call<BaseResponse> deletePatient(@Field("user_id") String user_id,
                                     @Field("patient_id") String patient_id);

    @FormUrlEncoded
    @POST("getpatient.php")
    Call<PatientBaseResponse> getPatientList(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("updatepatient.php")
    Call<BaseResponse> updatePatient(@Field("user_id") String user_id,
                                     @Field("patient_id") String patient_id,
                                     @Field("name") String name,
                                     @Field("mobile") String mobile,
                                     @Field("email") String email,
                                     @Field("age") String age,
                                     @Field("gender") String gender);

    /*  APPOINTMENT SECTION  */
    @FormUrlEncoded
    @POST("addappointment.php")
    Call<SaveOrderResponse> addAppointment(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("orderreview.php")
    Call<AppointmentResponse> getAppointmentReview(@Field("user_id") String user_id, @Field("appointment_order_id") String appointment_id);

    @FormUrlEncoded
    @POST("confirmappointment.php")
    Call<SaveOrderResponse> confirmAppointment(@Field("user_id") String user_id, @Field("appointment_order_id") String appointment_id);

    @FormUrlEncoded
    @POST("orderdetail.php")
    Call<OrderDetailResponse> getappointmentorderdetail(@Field("user_id") String user_id, @Field("appointment_order_id") String appointment_order_id);

    @FormUrlEncoded
    @POST("fullorderdetail.php")
    Call<AppointmentResponse> getAppointmentDetail(@Field("user_id") String user_id, @Field("appointment_id") String appointment_id);


    @FormUrlEncoded
    @POST("deleteappointment.php")
    Call<BaseResponse> deleteAppointment(@Field("user_id") String user_id, @Field("appointment_id") String appointment_id);

    @FormUrlEncoded
    @POST("getallappointments.php")
    Call<AppointmentListResponse> getAllApointment(@Field("user_id") String user_id, @Field("page") int page_no);



    /*  BLOG SECTION  */

    @FormUrlEncoded
    @POST("addfavblog.php")
    Call<BaseResponse> addFavouriteBlog(@Field("user_id") String user_id, @Field("blog_id") String blog_id);

    @FormUrlEncoded
    @POST("removefavblog.php")
    Call<BaseResponse> removeFavouriteBlog(@Field("user_id") String user_id,
                                           @Field("blog_id") String blog_id);

    @FormUrlEncoded
    @POST("getblogdetail.php")
    Call<BlogDetailResponse> getBlogDetail(@Field("blog_id") String blog_id, @Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("getblogcomments.php")
    Call<ReviewResponse> getBlogComments(@Field("page") int page_no, @Field("blog_id") String blog_id);

    @FormUrlEncoded
    @POST("addblogcomment.php")
    Call<BaseResponse> addComment(@Field("user_id") String user_id,
                                  @Field("blog_id") String blog_id,
                                  @Field("rating") String rating,
                                  @Field("comment") String comment);



    /*  SEARCH SECTION  */

    @FormUrlEncoded
    @POST("savesearch.php")
    Call<BaseResponse> saveSearchData(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("doctorsearch.php")
    Call<SearchingResponse> getSearchData(@Field("search") String input, @Field("search_type") String search_type);

    @FormUrlEncoded
    @POST("searchhistory.php")
    Call<SearchingResponse> getRecentSearchData(@Field("device_token") String input,
                                                @Field("user_id") String user_id,
                                                @Field("type") String type,
                                                @Field("item_type") String item_type);

    // doctor dashboard
    @GET("dtpl.php")
    Call<IndexResponse> dtpl();

    // banner
    @FormUrlEncoded
    @POST("banner.php")
    Call<TypeResponse> getbanner(@Field("type") String type);


    @Multipart
    @POST("upload.php")
    Call<PrescriptionResponse> uploadPrescriptions(@Part("user_id") RequestBody user_id,
                                                   @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("enquiry.php")
    Call<BaseResponse> enquiry(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("order.php")
    Call<BaseResponse> order(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("request_callback.php")
    Call<BaseResponse> requestCallback(@Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("medicine.php")
    Call<MedicineResponse> getMedicine(@Field("medicine_id") String medicine_id);

    @FormUrlEncoded
    @POST("checkpincode.php")
    Call<CheckPincodeBaseResponse> checkPincode(@Field("pincode") String pincode);

}
