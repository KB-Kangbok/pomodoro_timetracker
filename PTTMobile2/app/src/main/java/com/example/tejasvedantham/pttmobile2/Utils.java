package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void displayExceptionMessage(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
