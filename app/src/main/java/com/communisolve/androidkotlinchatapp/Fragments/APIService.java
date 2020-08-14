package com.communisolve.androidkotlinchatapp.Fragments;

import com.communisolve.androidkotlinchatapp.Notifications.MyResponse;
import com.communisolve.androidkotlinchatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:Key=AAAAImS_Yjo:APA91bHXsBwfZtWkL4mL_JFJoUa2wHDE6Y3ySQhzKDz4ZA82cwwc2QsYmtXQI3i3hDZBW5iE3qdqA_AAXpvcsvwSvoGV535xUJo4bspD4PLruHponu9CV0w7fR4jHag4S_Uxp_SiExlF"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
