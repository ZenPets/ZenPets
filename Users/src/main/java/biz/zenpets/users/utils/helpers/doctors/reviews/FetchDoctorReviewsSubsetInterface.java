package biz.zenpets.users.utils.helpers.doctors.reviews;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.reviews.Review;

interface FetchDoctorReviewsSubsetInterface {
    void onReviewSubset(ArrayList<Review> data);
}