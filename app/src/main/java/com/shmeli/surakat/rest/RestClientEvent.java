package com.shmeli.surakat.rest;

import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.model.rest.SendNotificationModel;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Serghei Ostrovschi on 2/8/18.
 */

public interface RestClientEvent {

    @POST(CONST.SEND_MESSAGE_URL)
    Call<RequestBody> sendDirectNotification(   @Header("ContentData-Type") String  contentType,
                                                @Header("Authorization")    String  userAuthKey,
                                                @Body JSONObject            sendNotificationModel);
//                                                @Body SendNotificationModel sendNotificationModel);
}
