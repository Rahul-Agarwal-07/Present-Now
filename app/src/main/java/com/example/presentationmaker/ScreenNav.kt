package com.example.presentationmaker

import android.util.Log
import com.example.presentationmaker.data.ADMIN_PARCEL
import com.example.presentationmaker.data.DATE_PARCEL
import com.example.presentationmaker.data.JSON_PARCEL
import com.example.presentationmaker.data.MAX_MEMBER_QUERY
import com.example.presentationmaker.data.MEMBERS_REF_PARCEL
import com.example.presentationmaker.data.PPT_ID_PARCEL
import com.example.presentationmaker.data.TOPIC_PARCEL


sealed class ScreenNav(val route : String)
{
    object Splash : ScreenNav(route = "Splash")
    object Home : ScreenNav(route = "Home")
    object PptScreen : ScreenNav(route = "pptScreen/{$TOPIC_PARCEL}/{$ADMIN_PARCEL}/{$PPT_ID_PARCEL}/{$DATE_PARCEL}/{$MAX_MEMBER_QUERY}")

    fun pass(
        topic : String,
        adminName : String,
        pptId : String,
        date : String,
        maxMem : Int) : String
    {
        Log.d("maxMem",maxMem.toString())
        return "pptScreen/$topic/$adminName/$pptId/$date/$maxMem"
    }
}