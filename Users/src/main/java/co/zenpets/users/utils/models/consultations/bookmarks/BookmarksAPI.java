package co.zenpets.users.utils.models.consultations.bookmarks;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BookmarksAPI {

    /** CHECK IF THE USER HAS BOOKMARKED A CONSULTATION **/
    @GET("getUserConsultationBookmarkStatus")
    Call<Bookmark> getUserConsultationBookmarkStatus(
            @Query("consultationID") String consultationID,
            @Query("userID") String userID);

    /** ADD A NEW CONSULTATION BOOKMARK **/
    @POST("newConsultationBookmark")
    @FormUrlEncoded
    Call<Bookmark> newConsultationBookmark(
            @Field("consultationID") String consultationID,
            @Field("userID") String userID);

    /** DELETE A CONSULTATION BOOKMARK **/
    @POST("removeConsultationBookmark")
    @FormUrlEncoded
    Call<Bookmark> removeConsultationBookmark(
            @Field("consultationID") String consultationID,
            @Field("userID") String userID);
}