package com.example.presentationmaker

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.presentationmaker.data.ADMIN_PARCEL
import com.example.presentationmaker.data.DATE_PARCEL
import com.example.presentationmaker.data.JSON_PARCEL
import com.example.presentationmaker.data.MAX_MEMBER_QUERY
import com.example.presentationmaker.data.MEMBERS_REF_PARCEL
import com.example.presentationmaker.data.PPT_ID_PARCEL
import com.example.presentationmaker.data.TOPIC_PARCEL

@Composable
fun SetupNavGraph(
    navController: NavHostController
)
{
    Log.d("check","nav")

    NavHost(
        navController = navController,
        startDestination = ScreenNav.Splash.route) {

        Log.d("check","nav")

        composable(route = ScreenNav.Home.route)
        {
            Log.d("viewmodel","nav")
            val viewModel = PptCardViewModel(LocalContext.current)
            SetHomeActivityData(navController = navController, viewModel = viewModel)
        }

        composable(route = ScreenNav.Splash.route)
        {
            AnimatedSplashScreen(navController = navController)
        }

        composable(
            route = ScreenNav.PptScreen.route,
            arguments = listOf(

                navArgument(TOPIC_PARCEL){
                    type = NavType.StringType
                },

                navArgument(ADMIN_PARCEL){
                    type = NavType.StringType
                },

                navArgument(PPT_ID_PARCEL){
                    type = NavType.StringType
                },

                navArgument(DATE_PARCEL){
                    type = NavType.StringType
                },

                navArgument(MAX_MEMBER_QUERY){
                    type = NavType.IntType
                },


            )
        )
        {

            Log.d("check","ppt")

            val topic = it.arguments?.getString(TOPIC_PARCEL).toString()
            val adminName = it.arguments?.getString(ADMIN_PARCEL).toString()
            val pptId = it.arguments?.getString(PPT_ID_PARCEL).toString()
            val date = it.arguments?.getString(DATE_PARCEL).toString()
            val maxMem = it.arguments?.getInt(MAX_MEMBER_QUERY)

            Log.d("maxMem",maxMem.toString())

            if (maxMem != null) {
                PresentationActivity(topic = topic,adminName = adminName, pptId = pptId, date = date,maxMem = maxMem)
            }

        }

    }
}