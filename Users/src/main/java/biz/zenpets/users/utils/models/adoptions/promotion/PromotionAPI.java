package biz.zenpets.users.utils.models.adoptions.promotion;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PromotionAPI {

    /** CREATE A NEW ADOPTION LISTING **/
    @POST("publishAdoptionPromotion")
    @FormUrlEncoded
    Call<Promotion> publishAdoptionPromotion(
            @Field("adoptionID") String adoptionID,
            @Field("optionID") String optionID,
            @Field("paymentID") String paymentID,
            @Field("promotedFrom") String promotedFrom,
            @Field("promotedTo") String promotedTo,
            @Field("promotedTimestamp") String promotedTimestamp);
}