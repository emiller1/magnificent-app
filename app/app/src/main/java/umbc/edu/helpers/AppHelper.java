package umbc.edu.helpers;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elishiah miller on 4/8/2017.
 */

public class AppHelper {

    private static AppHelper appHelper;
    private static List<String> attributeDisplaySeq;
    private final static String poolId = " us-east-1_JU6AE4FW4";
    private final static String clientId = "1hq4kobqvkbsm0sdcf37qb2av1";
    private final static String clientSecret = "3vj0jq92fov76qfa484ib0hcln4n2fhlkenpja24q5ulpqrrqeb";
    private static CognitoUserPool userPool;
    private static CognitoUserSession currSession;
    private static CognitoDevice newDevice;

    //COnstant Values Used For Intent Broadcast Actions
    public static final String SIGN_UP_FAILED = "SIGN_UP_FAILED";
    public static final String USER_CONFIRMED = "USER_CONFIRMED";
    public static final String USER_NOT_CONFIRMED = "USER_NOT_CONFIRMED";

    public static void init(Context context) {

        if (appHelper != null && userPool != null) {
            return;
        }

        if (appHelper == null) {
            appHelper = new AppHelper();
        }

        // Create a user pool with default ClientConfiguration
        if (userPool == null) {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            userPool = new CognitoUserPool(context, poolId, clientId, clientSecret, clientConfiguration);
        }
    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static  CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static String getPoolId(){
        return poolId;
    }
}
