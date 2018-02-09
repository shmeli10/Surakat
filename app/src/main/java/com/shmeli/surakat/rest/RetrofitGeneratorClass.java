package com.shmeli.surakat.rest;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.data.ERRORS;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Serghei Ostrovschi on 2/8/18.
 */

public class RetrofitGeneratorClass {
    private static Retrofit retrofitHTTP    = null;
    private static Retrofit retrofitHTTPS   = null;
    private static Retrofit retrofit        = null;

    private static Context mContext = null;

    private static int RETROFIT_ID  = 1;

    /**
     * Method gets instance of retrofit
     *
     * @param context   application context
     * @param baseURL   base url for retrofit requests
     * @return			instance of {@link Retrofit} class
     */
    public static Retrofit getRestClient(Context    context,
                                         String     baseURL) throws Exception {
        String baseURLScheme = getBaseURLScheme(baseURL);

        if (baseURLScheme.equals(CONST.HTTP_SCHEME)) {
            //Log.e("RetrofitGeneratorClass: getRestClient(): set retrofit to getHTTPRestClient");
            retrofit = getHTTPRestClient(   baseURL,
                                            RETROFIT_ID);
        } else if (baseURLScheme.equals(CONST.HTTPS_SCHEME)) {
            if (context != null) {
                mContext = context;
                retrofit = getHTTPSRestClient(baseURL);
            }
        } else throw new Exception(ERRORS.UNKNOWN_URL_SCHEME_ERROR);

        return retrofit;
    }

    /**
     * Method gets instance of retrofit for HTTP requests
     *
     * @param baseURL   base url for retrofit requests
     * @return          instance of retrofit for HTTP requests
     */
    private static Retrofit getHTTPRestClient(String    baseURL,
                                              int       retrofitType) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(CONST.CONNECT_TIMEOUT, 	TimeUnit.SECONDS);
        httpClient.readTimeout(CONST.READ_TIMEOUT, 			TimeUnit.SECONDS);
        httpClient.writeTimeout(CONST.WRITE_TIMEOUT, 		TimeUnit.SECONDS);
        httpClient.addInterceptor(interceptor);

        OkHttpClient client = httpClient.build();

        if(retrofitType == RETROFIT_ID) {

            if (retrofitHTTP == null) {
                retrofitHTTP = new Retrofit.Builder()
                        .baseUrl(baseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();


            }

            return retrofitHTTP;
        }
        else
            return null;
    }

    /**
     * Method gets instance of retrofit for HTTPS requests
     *
     * @param baseURL   base url for retrofit requests
     * @return          instance of retrofit for HTTPS requests
     */
    private static Retrofit getHTTPSRestClient(String baseURL) {
        if (retrofitHTTPS == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpsClient = new OkHttpClient.Builder();
            httpsClient.connectTimeout(CONST.CONNECT_TIMEOUT,   TimeUnit.SECONDS);
            httpsClient.readTimeout(CONST.READ_TIMEOUT,         TimeUnit.SECONDS);
            httpsClient.writeTimeout(CONST.WRITE_TIMEOUT,       TimeUnit.SECONDS);
            httpsClient.addInterceptor(interceptor);

            try {
                httpsClient.sslSocketFactory(getSSLConfig(mContext).getSocketFactory());
            }
            catch(Exception exc) {
                Log.e("LOG", "RetrofitGeneratorClass: getHTTPSRestClient(): error: " +exc.getMessage());
            }

            OkHttpClient client = httpsClient.build();

            retrofitHTTPS = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofitHTTPS;
    }

    /**
     * Method gets instance of SSLConfig for enabling sending HTTPS request
     *
     * @param context   				application context
     * @return  						instance of {@link SSLContext} class
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext getSSLConfig(Context context) throws 	CertificateException,
                                                                    IOException,
                                                                    KeyStoreException,
                                                                    NoSuchAlgorithmException,
                                                                    KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        InputStream cert = null;

        try {
            cert = context.getResources().openRawResource(R.raw.star_lab_ca_chain);
            ca = cf.generateCertificate(cert);
            //Log.e("ca=" + ((X509Certificate) ca).getSubjectDN());
        }
        finally {
            if(cert != null)
                cert.close();
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    /**
     * Method gets URL scheme:
     * <ul>
     *     <li>http</li>
     *     <li>https</li>
     * </ul>
     *
     * @param baseURL   base url for retrofit requests
     * @return          URL scheme
     */
    private static String getBaseURLScheme(String baseURL) {
        // Log.e("RetrofitGeneratorClass: getBaseURLScheme(): Base URL Scheme: " +Uri.parse(baseURL).getScheme());
        return Uri.parse(baseURL).getScheme();
    }
}
