package com.getyoteam.budamind.interfaces;



public class ApiUtils {

//    public static final String BASE_URL = "http://Meditation-env.bxpwz3hhm3.us-east-2.elasticbeanstalk.com/api/";
//    public static final String BASE_URL = "http://157.230.107.195:8080/ClarityApp/api/";
//    public static final String BASE_URL = "http://budamind.ca-central-1.elasticbeanstalk.com/api/";
    public static final String BASE_URL = "http://budamind.ca-central-1.elasticbeanstalk.com/api/";

    private ApiUtils() {

    }
    public static ClarityAPI getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(ClarityAPI.class);
    }
}
