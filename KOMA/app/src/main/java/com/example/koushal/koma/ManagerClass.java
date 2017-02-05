package com.example.koushal.koma;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

public class ManagerClass {

    public CognitoCachingCredentialsProvider getCerdentials(Context context)
    {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-west-2:c5f854ab-5b9b-439d-a4d2-e70d5b587f23", // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        return credentialsProvider;
    }
}
