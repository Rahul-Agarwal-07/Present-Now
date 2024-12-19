package com.example.presentationmaker

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.data.vibratePhone
import com.example.presentationmaker.ui.theme.LightRed
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.PurpleGrey80
import com.example.presentationmaker.ui.theme.clashGrotesk
import com.example.presentationmaker.ui.theme.lightPurple
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun Activity1(
    navController: NavHostController,
    pptIds : MutableList<PptInfo>)
{
    Log.d("enter","yes")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    ) {

        Column(){
            Row(Modifier.wrapContentSize()) {
                TopActionBar(isHomeActivity = true)
            }

            Box()
            {
                Row(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(5.dp),

                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start)
                {
                    PptGroups(PptIds = pptIds, navController = navController)
                }

                Row(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    CreateButton()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopActionBar(
    title: String = stringResource(id = R.string.app_name),
    isHomeActivity : Boolean = false)
{
    val context = LocalContext.current

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple80,
            titleContentColor = Purple40,
        ),

        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                if(isHomeActivity)
                {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.app_icon),
                        contentDescription = "App Icon",
                        tint = Purple40
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)

            }
        },
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateButton()
{
    var dialogState by remember {
        mutableStateOf(false)
    }

    FloatingActionButton(

        containerColor = Purple80,
        onClick = {
            dialogState = !dialogState
        }) {
        Icon(imageVector = Icons.Filled.Add,
            contentDescription = "create")
    }

    if(dialogState)
    {
        Dialog(onDismissRequest = { dialogState = !dialogState }) {
            HomeDialogCreate()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PptGroups(
    navController: NavHostController,
    PptIds : MutableList<PptInfo>){

    val dbQueries = DbQueries()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userId = GoogleSignIn(context).getUserId().toString()

    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    var longPressed by remember {
        mutableStateOf(false)
    }

    Log.d("Clicked", "Recompose")

    var pptTobeDeleted by remember {
        mutableStateOf("")
    }

    var isDeleted by remember {
        mutableStateOf(false)
    }

    Box {

        Row(Modifier.align(Alignment.Center)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(PptIds.isNotEmpty()) {

                items(PptIds)
                { item ->

                    Log.d("Clicked", "new Id")

                    Surface(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    navController.navigate(
                                        ScreenNav.PptScreen.pass(
                                            topic = item.topicTitle,
                                            adminName = item.adminName,
                                            pptId = item.pptId.toString(),
                                            date = item.date,
                                            maxMem = item.maxMem
                                        )
                                    ) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },

                                onLongClick = {
                                    vibratePhone(context)
                                    longPressed = true
                                    pptTobeDeleted = item.pptId.toString()
                                }
                            )
                    ) {


                        Log.d("Clicked", item.topicTitle)

                        ExpandableCard(title = item.topicTitle, adminName = item.adminName)

                        Log.d("Clicked", item.topicTitle)

                    }
                }

                }
            }
        }
    }

    if(longPressed)
    {
        AlertDialog(

            modifier = Modifier
                .border(BorderStroke(3.dp, borderColor), shape = RoundedCornerShape(12.dp)),

            shape = RoundedCornerShape(12.dp),

            title = {
                Text(
                    text = "EXIT PRESENTATION",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold)
            },

            text = {
                Text(
                    text = "Do you want to exit ?",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor.copy(0.8f),
                    fontWeight = FontWeight.Bold)
            },

            onDismissRequest = { longPressed = false },

            confirmButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(Purple40),
                    onClick = {

                        longPressed = false

                        isDeleted = true

                        coroutineScope.launch {
                            isDeleted = dbQueries.deletePptFromHomeActivity(
                                pptId = pptTobeDeleted,
                                context = context,
                                pptDelete = true,
                                removeMem = userId)
                        }

                    }
                )
                {
                    Text(
                        text = "Proceed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)
                }
            },

            dismissButton = {

                Button(
                    border = BorderStroke(2.dp, borderColor),
                    colors = ButtonDefaults.buttonColors(lightPurple),
                    onClick = { longPressed = false })
                {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Purple40)
                }
            },

            containerColor = if(isSystemInDarkTheme()) Color.Black else Purple80
        )

    }

    if(isDeleted)
    {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)

        ) {

        Column(
            modifier = Modifier
                .width(400.dp)
                .height(150.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp)),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            LoadingAnimation(circleColor = borderColor)
        }

        }
    }
}

@Composable
fun SetHomeActivityData(
    navController: NavHostController,
    viewModel: PptCardViewModel)
{
    when(val result = viewModel.response.value)
    {
        is DataState.Loading ->
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                Log.d("viewmodel","loading")
                CircularProgressIndicator()
            }
        }

        is DataState.Success ->
        {
            Log.d("viewmodel","success  ${result.data.size}")
            Activity1(pptIds = result.data,navController = navController)
        }

        is DataState.Failure ->
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                Text(text = result.msg,
                    style = MaterialTheme.typography.titleLarge)
            }
        }

        else ->
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Please Relaunch the app",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}



data class PptInfo(
    val topicTitle : String,
    val adminName : String,
    val pptId : Int,
    val date : String,
    val maxMem : Int
)

