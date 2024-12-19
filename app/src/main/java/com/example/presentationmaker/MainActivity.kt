package com.example.presentationmaker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.presentationmaker.ui.theme.PresentationMakerTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.properties.Delegates

val response : MutableState<SignInState> = mutableStateOf(SignInState.SignedOut)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val appUpdater = AppUpdater(this)

        appUpdater
            .setDisplay(Display.DIALOG)
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://raw.githubusercontent.com/Rahul-Agarwal-07/About-Me/refs/heads/main/updatelog.json")
            .start()

        val googleSignIn = GoogleSignIn(context = this)
        val currUser = googleSignIn.getUserId()

        if(currUser != null)
        {
            response.value = SignInState.SignedIn
        }

        setContent {
            PresentationMakerTheme {

                val isDark = isSystemInDarkTheme()
                val systemUiController = rememberSystemUiController()
                lateinit var navController : NavHostController

                SideEffect {
                    systemUiController.setNavigationBarColor(
                        color = if(isDark) Color.Black else Color.White
                    )
                }


                Surface(
                    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
                ){
                    navController = rememberNavController()

                    when(response.value)
                    {
                        is SignInState.SignedOut ->
                        {
                            Log.d("userState","signed out")
                            SetupNavGraph(navController = navController)
                        }

                        is SignInState.SignedIn -> {

                            Log.d("userState","signed in")
                            SetupNavGraph(navController = navController)
                        }
                    }
                }



            }
        }

    }
}

sealed class SignInState{
    object SignedIn : SignInState()
    object SignedOut : SignInState()
}






