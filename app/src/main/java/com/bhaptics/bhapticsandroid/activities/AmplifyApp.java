package com.bhaptics.bhapticsandroid.activities;


import android.app.Application;
import android.content.Context;

import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthChannelEventName;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.InitializationStatus;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.HRate;
import com.amplifyframework.datastore.generated.model.Todo;
import com.amplifyframework.hub.HubChannel;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class AmplifyApp extends Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        context = getBaseContext();

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }

        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );

        /*
        Amplify.Auth.signUp(
                "cg",
                "Caseygauss1",
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "caseygauss@gmail.com").build(),
                result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
                error -> Log.e("AuthQuickStart", "Sign up failed", error)
        );

        */
        // Confirm sign up with code
        /*
        Amplify.Auth.confirmSignUp(
                "cg",
                "537034",
                result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
        */


        //sign in

        /*
        Amplify.Auth.signIn(
                "cg",
                "Caseygauss1",
                result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
        */

        /*
        HRate post = HRate.builder()
                .currentRate(111)
                .build();

        Amplify.DataStore.save(post,
                result -> Log.i("MyAmplifyApp", "Created a new HR post successfully"),
                error -> Log.e("MyAmplifyApp",  "Error creating post", error)
        );


        Amplify.API.query(
                ModelQuery.list(HRate.class, HRate.CURRENT_RATE.gt(0)),
                response -> {
                    for (HRate rate : response.getData()) {
                        Log.i("MyAmplifyApp", String.valueOf(rate.getCurrentRate()));
                    }
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );

         */

        Amplify.Hub.subscribe(HubChannel.AUTH,
                hubEvent -> {
                    if (hubEvent.getName().equals(InitializationStatus.SUCCEEDED.toString())) {
                        Log.i("AuthQuickstart", "Auth successfully initialized");
                    } else if (hubEvent.getName().equals(InitializationStatus.FAILED.toString())) {
                        Log.i("AuthQuickstart", "Auth failed to succeed");
                    } else {
                        switch (AuthChannelEventName.valueOf(hubEvent.getName())) {
                            case SIGNED_IN:
                                Log.i("AuthQuickstart", "Auth just became signed in.");
                                break;
                            case SIGNED_OUT:
                                Log.i("AuthQuickstart", "Auth just became signed out.");
                                break;
                            case SESSION_EXPIRED:
                                Log.i("AuthQuickstart", "Auth session just expired.");
                                break;
                            default:
                                Log.w("AuthQuickstart", "Unhandled Auth Event: " + AuthChannelEventName.valueOf(hubEvent.getName()));
                                break;
                        }
                    }
                }
        );
    }
}
