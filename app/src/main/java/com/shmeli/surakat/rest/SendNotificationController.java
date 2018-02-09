package com.shmeli.surakat.rest;

import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.shmeli.surakat.data.ERRORS;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Serghei Ostrovschi on 2/8/18.
 */

public class SendNotificationController {

    private RestClientEvent restClientEventListener;

    private String privateKey = "-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDf6+isrSAMMqkP\\nTCrJohW0T2CEKKX+xKLcMcA+xP9by9LrAqm5jU6fHI06UHvmH7dlA13oKj1LKrSP\\n7BR+0og9n+g+v63TSxn1DS+0elGCrr3947kq+QybnqCZfANZ/kLlYD9pEgAm/9oO\\nbsBmAWKvBiMRuFoZtwWs/mbMVnJO691lLwB0FYpCEj5LYIrTdfwSd5QEW+o5uCLH\\njhMTrd07iiDy+eXafV1jgTwoScY/1MSw2huqSkweL9PHcjHy62AFGBwCmri6M6Gr\\ngMuUMEbXF+HOEqRDGmGZdrK9fbH4TJdJOqgtoxk+p3/9DCW1cnZ5vgvKXuDBvJWH\\n8iG3N3iHAgMBAAECggEAANU8CNawT+a7fB5MDrwZHds7u7/Vh/SU3ss/J5yes2mA\\nVGzA5ZZx5+zmVeAgDdy7oGlaS25LFDgAYKj2p/uLOA3AS6CaMSTOTY5FW80CHC9E\\nE83FIDS5rMfyxyGrUmEp0mXlVDQV3eqzTJhNTQ9EHexp2Wk9W/2OqA/efL0Z3ne1\\nXLpQ5ZytQWQcSF1qnsU4oZHRJw9d/Nd7AU8mFwQsdc+2UosjCKvP7GGIxTLPG9Pc\\n+ArgvYPLgOjVArcev1elcmORFrC1zGzVag+VmBMlrKXdbpJa3DWLMCVS9xQhBNX2\\nnIz7l7M4E1Yc/sP7grAny2WLZHBy3u4WcWiiEP/UeQKBgQDygN774PHpCb7MSsJQ\\nTFNJ7U36WEi8hQCCV3apgGREzjzsMSD1MooMHgfQouuLwF1osVFZYese5bifCjMZ\\n1p7TzOO0vU4jI6TwAZ/Bx0cY97EyA3iH7A+K/bAc4tc9pnr+ulmmPPfVMiVbMZOc\\nLr+e6h7OBjKE3JcJW04B66pb3wKBgQDsYkmpSprOOmq9fFIv1KJ2NX5Zq4d2/mhu\\nB16gFRLKLCoJgm8ZNi0MgwZZkylANH0McIGPsL63eCth6z0brN/u95vv46A6r+vL\\n87ALu8fqDmOtdSrfERYfUQ5AOQ+DIg7IP39Fx5zgAYJSE5OMzCGSMxT/08qsgVsC\\n1vpVb7N4WQKBgQDWfC89Hpc4I5ty+4hkFy3vjAlVLDKhMw3hOtTEVL/ar4Lo8QcS\\nd/Hrh8Anw4GMKMc1auflo5qeyFYDrLTH3LC76lNYL7tljwg6E2WmscfriGBcOP+p\\nUJvqtjjHQ8dWSuIsXMwgIXqeQW0S/IVvNVXci3xVgtu58ZVQ/EXBoxwZhwKBgCCQ\\nGZ2cjV7DZVjc+2VjYU3vaIxpuKuHuFMP6NUckg4Pr2rH8Q4EDIigtAs63n/duywv\\ng2mL63nSsAc5JJ3fXqnIqk6NiNEM0uqxkxrKr2tquAh5D4LTE70UqWXndl+WHXsw\\nZBP0v4OMMaukzrqilC1TpBzpzA2fdC4b9zg1vv9hAoGBAId/MhUq7oNr1Co21fdD\\n3MRGsWk5F4dmKLl/YGtNheOZXVxjHl4dEi4UGn7PVVWi8ucO+p1EOXyZARzQs8dJ\\nJXMG7xA/sHRVMajFpE+eaCN2VHzy6SeQXBtWUpZMI8YAMIFLCfmvj5oUvuMrUN4/\\nAUoC3GgAk99y/NMeLKKscorV\\n-----END PRIVATE KEY-----\\n";

    public SendNotificationController(RestClientEvent restClientEventListener) throws Exception {
        Log.e("LOG", "-------------------------------------");
        Log.e("LOG", "SendNotificationController: constructor.");

        if(restClientEventListener == null)
            throw new Exception(ERRORS.REST_CLIENT_EVENT_NULL_ERROR);

        this.restClientEventListener = restClientEventListener;
    }

//    private String getAccessToken() throws IOException {
//        GoogleCredential googleCredential = GoogleCredential
//                .fromStream(new FileInputStream("service-account.json"))
//                .createScoped(Arrays.asList(SCOPES));
//        googleCredential.refreshToken();
//
//        return googleCredential.getAccessToken();
//    }

    public void sendDirectNotification(String   senderDeviceToken,
                                       String   recipientDeviceToken,
                                       String   notificationTitle,
                                       String   notificationBody) throws Exception {
        Log.e("LOG", "SendNotificationController: sendDirectNotification()");

        Log.e("LOG", "SendNotificationController: sendDirectNotification(): senderDeviceToken: "    +senderDeviceToken);
        Log.e("LOG", "SendNotificationController: sendDirectNotification(): recipientDeviceToken: " +recipientDeviceToken);
        Log.e("LOG", "SendNotificationController: sendDirectNotification(): notificationTitle: "    +notificationTitle);
        Log.e("LOG", "SendNotificationController: sendDirectNotification(): notificationBody: "     +notificationBody);

        if(TextUtils.isEmpty(senderDeviceToken)) {
            throw new Exception(ERRORS.SENDER_ID_IS_NULL_ERROR);
        }

        if(TextUtils.isEmpty(recipientDeviceToken)) {
            throw new Exception(ERRORS.RECIPIENT_ID_IS_NULL_ERROR);
        }

        if(TextUtils.isEmpty(notificationTitle))
            notificationTitle = "";

        if(TextUtils.isEmpty(notificationBody))
            notificationBody = "";


        StringBuilder userAuthKey = new StringBuilder("");
        userAuthKey.append("Bearer ");
        //userAuthKey.append(senderDeviceToken);
        userAuthKey.append(privateKey);

        // --------------------------------------------------------------- //

        JSONObject notificationObj  = new JSONObject();
        notificationObj.put("body",     notificationBody);
        notificationObj.put("title",    notificationTitle);

        JSONObject messageObj       = new JSONObject();
        messageObj.put("token",         recipientDeviceToken);
        messageObj.put("notification",  notificationObj);

        JSONObject rootObj          = new JSONObject();
        rootObj.put("message",          messageObj);

        // --------------------------------------------------------------- //

        Log.e("LOG", "SendNotificationController: sendDirectNotification(): userAuthKey: " +userAuthKey.toString());

        Log.e("LOG", "SendNotificationController: sendDirectNotification(): result Request Body: " +rootObj.toString());

        restClientEventListener.sendDirectNotification( "application/json",
                                                        userAuthKey.toString(),
                                                        rootObj)
                                .enqueue(sendDirectNotificationCallback);
    }

    // ------------------------------------- CALL --------------------------------------------- //

    private Callback<RequestBody> sendDirectNotificationCallback = new Callback<RequestBody>() {

        @Override
        public void onResponse(Call<RequestBody>        call,
                               Response<RequestBody>    response) {
            Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: onResponse()");

            if (response != null) {
                Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: response code= " + response.code());

                if (response.body() != null) {

                    Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: response body: " +response.body().toString());
                }
                else {
                    Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: onResponse(): response body is null");
                }
            }
            else {
                Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: onResponse(): response is null");
            }
        }

        @Override
        public void onFailure(Call<RequestBody> call,
                              Throwable         t) {
            Log.e("LOG", "SendNotificationController: sendDirectNotificationCallback: onFailure(): " +t.getMessage());
        }
    };
}
