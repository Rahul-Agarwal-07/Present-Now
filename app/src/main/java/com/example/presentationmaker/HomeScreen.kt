package com.example.presentationmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.PurpleGrey40
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.presentationmaker.ui.theme.LightPurple40
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    topic : String,
    adminName : String,
    pptId : String,
    date: String,
    maxMem : Int) {

    val context = LocalContext.current
    val googleSignIn = GoogleSignIn(context)
    val dbQueries = DbQueries()

    val user = googleSignIn.getUserId()
    val homeRef = dbQueries.getHomeRef(pptId)
    val userRef = dbQueries.getGroupAdminRefFromUser(code = pptId, id = user.toString())

    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Log.d("groupAdmin",userRef.key.toString())

    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }

    var isMenuItemSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var isAddedInGroup by remember {
        mutableStateOf(false)
    }

    var topicSelected by remember {
        mutableStateOf(false)
    }

    var groupAdmin by remember {
        mutableStateOf("")
    }

    var isGroupAdminAndUserSame by remember {
        mutableStateOf(false)
    }

    var prevDropDownBtn by remember {
        mutableStateOf("")
    }

    homeRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            for(dataSnap in snapshot.children)
            {
                if(dataSnap.key.toString().equals("admin$user"))
                {
                    isAdmin = true
                }

                if(dataSnap.key.toString().equals("true$user"))
                {
                    isAddedInGroup = true
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("Error",error.message)
        }

    })

    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            for(data in snapshot.children)
            {
                groupAdmin = data.key.toString()
                if(groupAdmin.equals(user.toString())) isGroupAdminAndUserSame = true
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    val topicRef = dbQueries.getTopicRef(code = pptId)

    topicRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(data in snapshot.children)
            {
                if(data.key.toString().equals(groupAdmin))
                    topicSelected = true
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val coroutineScope = rememberCoroutineScope()

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = Purple40,
            ),

            title = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            },

            actions = {

                Log.d("Home","DropDown Menu")

                val str = pptHomeDropDownMenu(context = context, pptId = pptId, groupAdmin = groupAdmin,adminName = adminName)

                Log.d("Home","Val : $str $isAddedInGroup")

                if(str.equals("Exit") && !str.equals(prevDropDownBtn))
                {
                    Log.d("Home","Val : $str")
                    isAddedInGroup = false
                }

                else if(str.equals("Delete") && !str.equals(prevDropDownBtn))
                {
                    topicSelected = false
                }

                else if(str.equals("Change") && !str.equals(prevDropDownBtn))
                {
                    topicSelected = false
                }

                prevDropDownBtn = str
            }
        )

        Log.d("isadded",isAddedInGroup.toString())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val viewModelMember = MemberViewModel(pptId = pptId)

            var clicked by remember {
                mutableStateOf(false)
            }

            var topicBtnClicked by remember {
                mutableStateOf(false)
            }

            if(clicked)
            {
                Dialog(
                    content = { SetMemberSelectData(viewModel = viewModelMember, clickedState = clicked, maxMem = maxMem,pptId = pptId) },
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    onDismissRequest = { clicked = !clicked })
            }

            if(topicBtnClicked)
            {
                Dialog(
                    content = { topicSelected = addTopicDialog(pptId = pptId) },
                    onDismissRequest = { topicBtnClicked = false })
            }

            if(topicSelected) topicBtnClicked = false

            if(isAdmin) HomeScreenCard(title = topic, adminName = adminName,pptId = pptId, date = date)
            else HomeScreenCard(title = topic, adminName = adminName, date = date)

            Spacer(modifier = Modifier.height(10.dp))

            val memName = googleSignIn.getUserId()?.uppercase()
            val memRef = DbQueries().getMembersRef(pptId)

            memRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.child("true$memName").exists()) isAddedInGroup = true
                    else if(snapshot.child("admin$memName").exists()) isAddedInGroup = true

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            Log.d("check","memRef")

            if(!isAddedInGroup) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(LightPurple40, Purple40)
                            ),

                            shape = RoundedCornerShape(12.dp)
                        )
                    ,

                    onClick = {
                        clicked = !clicked
                    },

                    color = Color.Transparent,

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth().height(40.dp)
                            .background(Color.Transparent),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create Group",
                            tint = Color.White
                        )

                        Text(
                            text = "Create Group",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            else
            {
                val viewModel = HomeGroupCard(context = context, pptId = pptId)
                if(!isAdmin) SetGroupInfo(viewModel = viewModel)

                Spacer(modifier = Modifier.height(10.dp))

                if(!topicSelected && isGroupAdminAndUserSame)
                {
                    Surface(

                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(LightPurple40, Purple40)
                                ),

                                shape = RoundedCornerShape(12.dp)
                            ),

                        color = Color.Transparent,

                        onClick = {
                            topicBtnClicked = !topicBtnClicked
                        },

                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Topic",
                                tint = Color.White
                            )

                            Text(
                                text = "Add Topic",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                else if(topicSelected)
                {
                    val topicViewModel = HomeTopicCard(pptId = pptId, groupAdmin = groupAdmin)
                    SetTopicInfo(viewModel = topicViewModel)
                }
            }

        }
    }
}

@Composable
fun HomeScreenCard(
    color: Color = Color.Black,
    date : String,
    title: String = "New Presentation",
    adminName : String = "Admin Name",
    pptId: String = "",
    padding: Dp = 15.dp,
    titleWeight: FontWeight = FontWeight.Bold,
)
{
    val randomColor1 = remember {
        Color(
            Random.nextInt(from = 40, until = 150),
            Random.nextInt(from = 60, until = 150),
            Random.nextInt(from = 60, until = 150)
        )
    }

    val randomColor2 = remember {
        Color(
            Random.nextInt(from = 150, until = 250),
            Random.nextInt(from = 150, until = 250),
            Random.nextInt(from = 150, until = 250)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),

        shape = shapes.medium,

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(if(isSystemInDarkTheme()) randomColor2 else randomColor1, color)
                    )
                )
                .padding(padding),
            ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    modifier = Modifier,
                    text = pptId,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = titleWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        modifier = Modifier,
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = titleWeight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(5.dp))

                Row(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    )
                    {
                        Text(
                            modifier = Modifier,
                            text = date,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = titleWeight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    )
                    {
                        Text(
                            modifier = Modifier,
                            text = adminName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = titleWeight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }


        }

    }
}

@Composable
fun addTopicDialog(pptId: String) : Boolean
{
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary

    val context = LocalContext.current

    var text by remember {
        mutableStateOf("")
    }

    var isError by remember {
        mutableStateOf(false)
    }

    var clicked by remember {
        mutableStateOf(false)
    }

    fun isValidate(inputTxt : String)
    {
        isError = inputTxt.isEmpty()
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp))
    )
    {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Add Topic",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if(isSystemInDarkTheme()) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = text,

                onValueChange = { newText->
                    text = newText
                    isValidate(text)
                },
                
                label = {
                    Text(
                        text = "Topic",
                        style = MaterialTheme.typography.bodyMedium)
                },

                supportingText = {

                    if(isError && text.isEmpty())
                    {
                        Text(
                            text = "Topic Required",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                },

                trailingIcon =
                {
                    if(isError)
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.error),
                            contentDescription = "Error Icon",
                            tint = Color.Red)
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if(isSystemInDarkTheme()) Color.Black else Color.White,
                    focusedBorderColor = borderColor,
                    focusedSupportingTextColor = Color.Red,
                    unfocusedSupportingTextColor = Color.Red,
                    errorSupportingTextColor = Color.Red,
                    errorContainerColor = Color.White,
                    errorBorderColor = Color.Red,
                    errorLabelColor = textColor,
                    unfocusedBorderColor = borderColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = Color.Black,
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor,
                    unfocusedContainerColor = Purple80
                ),

                textStyle = TextStyle.Default,

                singleLine = true

                )

            Spacer(modifier = Modifier.height(10.dp))

            Button(

                colors = ButtonDefaults.buttonColors(Color.Red),
                onClick = {

                    if(text.isEmpty())
                    {
                        Toast.makeText(
                            context,
                            "Topic must not be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if(!isError && text.isNotEmpty())
                    {
                        DbQueries().setTopic(topic = text, pptId = pptId, context = context)
                        clicked = true
                    }

                },

                ){

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center)
                {

                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Confirm Button",
                        tint = Color.White)

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "CLICK TO CONFIRM",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)
                }
            }
            }
        }

        return clicked
}








