package com.app.findyourlobster.data.messagingservice;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqINPb80:APA91bEiXBWJhn11JBKTeXImbQArBUpa2CON3CUhbM8NGSdPE8okvOSkj5zfWpLsGZ43UgVvd2yQOPPgVkpGx43XQshm-53TLwYEdHCOruM9kVB9JX7kVdOVWkN-t0emhia5nv36qaY-" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}