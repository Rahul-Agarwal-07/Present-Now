package com.example.presentationmaker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.poppins
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp

@Composable
fun pptHomeDropDownMenu(context: Context, pptId : String, groupAdmin : String, adminName : String) : String
{
    var expanded by remember {
        mutableStateOf(false)
    }

    var returnVal by remember {
        mutableStateOf("")
    }

    var changeTopicClicked by remember {
        mutableStateOf(false)
    }

    var isChanged by remember {
        mutableStateOf(false)
    }

    var isDeleted by remember {
        mutableStateOf(false)
    }

    var changeDate by remember {
        mutableStateOf(false)
    }

    var changeTitle by remember {
        mutableStateOf(false)
    }

    var isAdmin by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val dbQueries = DbQueries()
    val googleSignIn = GoogleSignIn(context)
    val pptMemRef = dbQueries.getMembersRef(code = pptId)
    val userId = googleSignIn.getUserId().toString()
    val userName = googleSignIn.getUserName().toString()

    Log.d("Exit Group", "$userId $adminName")

    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    pptMemRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            for(data in snapshot.children)
            {
                if(userId.equals(data.key?.substring(5)?.lowercase()))
                {
                    isAdmin = true
                }
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    Box()
    {
        IconButton(onClick = { expanded = !expanded })
        {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu Drawer", tint = Purple40)
        }

        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = { expanded = !expanded }) {

            DropdownMenuItem(
                text = {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = if(isAdmin) "Delete PPT" else "Exit Group",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },

                leadingIcon = {
                    Icon(
                        modifier = Modifier.rotate(180f),
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Exit Group",
                        tint = textColor)
                },

                onClick = {
                    coroutineScope.launch {

                        isDeleted = true

                        expanded = !expanded

                        dbQueries.exitGroup(context = context, pptId = pptId, pptDelete = false)

                        delay(5000)

                        returnVal = "Exit"

                        isDeleted = false

                        Toast.makeText(
                            context,
                            "Group Exited Successfully",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                })

            if(isAdmin)
            {
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "Change Title",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Change Title",
                            tint = textColor
                        )
                    },

                    onClick = {

                        returnVal = "Change Title"
                        expanded = !expanded
                        changeTitle = !changeTitle

                    })

                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "Change Date",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Change Date",
                            tint = textColor
                        )
                    },

                    onClick = {

                        returnVal = "Change Date"
                        expanded = !expanded
                        changeDate = !changeDate
                    })
            }

            if(userId.equals(groupAdmin))
            {
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "Delete Topic",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Topic",
                            tint = textColor
                        )
                    },

                    onClick = {

                        returnVal = "Delete"
                        expanded = !expanded
                        dbQueries.deleteTopic(pptId = pptId, groupAdmin = groupAdmin, context = context)

                    })

                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = "Change Topic",
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Change Topic",
                            tint = textColor
                        )
                    },

                    onClick = {

                        changeTopicClicked = !changeTopicClicked

                    })

                if(changeTopicClicked)
                {
                    Dialog(
                        content = { isChanged = addTopicDialog(pptId = pptId) },
                        onDismissRequest = { changeTopicClicked = false })

                    Log.d("changeState",isChanged.toString())
                }

                if(isChanged)
                {
                    returnVal = "Change"
                    changeTopicClicked = false
                    isChanged = false
                    expanded = !expanded
                }
            }

        }
    }

    if(changeTitle)
    {
        var title = ""

        Dialog(
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .safeDrawingPadding(),

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Add Title",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)

                    title = textFieldCompose(charLimit = 50, SignInError = true)

                    TextButton(
                        border = BorderStroke(2.dp,MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.textButtonColors(containerColor = Purple80, contentColor = Purple40),
                        modifier = Modifier
                            .padding(bottom = 10.dp, end = 10.dp)
                            .align(Alignment.End),
                        onClick = {
                            if(title.isNotEmpty()) dbQueries.changePPTTitle(pptId = pptId, newTitle = title)
                            changeTitle = !changeTitle })
                    {
                        Text(
                            text = "CONFIRM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold)
                    }
                }
            },

            onDismissRequest = { changeTitle = !changeTitle }
        )
    }

    if(changeDate)
    {
        var date = ""

        Dialog(
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .safeDrawingPadding(),

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Select New Date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)

                    date = dateTimePicker(buttonText = "Open Date Picker")

                    TextButton(
                        border = BorderStroke(2.dp,MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.textButtonColors(containerColor = Purple80, contentColor = Purple40),
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.End),
                        onClick = {
                            if(date.isNotEmpty()) dbQueries.changePPTDate(pptId = pptId, newDate = date)
                            changeDate = !changeDate })
                    {
                        Text(
                            text = "CONFIRM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold)
                    }
                }
            },

            onDismissRequest = { changeDate = !changeDate }
        )
    }

    if(isDeleted)
    {
        Dialog(
            onDismissRequest = {  },

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

    return returnVal
}