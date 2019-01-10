package com.trungvu.chatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAeXm-tfo:APA91bFGhXpWAbojoYrBu9UDnGqzUOXH7jRejB41broR3AnnNpiL_3v-HUJpFB8aiHApribwrQyT6O6SwfswWQsyLZZJd65PvpDEbwrqXhK0-pPU9k_KXt6gWY50f6IfyADoaXreK_Sy"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
