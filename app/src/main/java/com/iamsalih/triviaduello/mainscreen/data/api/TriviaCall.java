package com.iamsalih.triviaduello.mainscreen.data.api;

import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by muhammedsalihguler on 26.11.17.
 */

public interface TriviaCall {

    @GET("api.php")
    Call<QuestionList> getQuestions(@Query("amount") int amount, @Query("type") String type);

}
