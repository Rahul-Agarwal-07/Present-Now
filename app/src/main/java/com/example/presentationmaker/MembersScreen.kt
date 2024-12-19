package com.example.presentationmaker

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import com.example.presentationmaker.data.vibratePhone
import com.example.presentationmaker.ui.theme.Orange
import kotlinx.coroutines.delay


@Composable
fun MembersListCompose(
    list: MutableList<MemberInfo>,
    adminName: String,
    pptId : String
)
{
    val dbQueries = DbQueries()
    val context = LocalContext.current

    val googleSignIn = GoogleSignIn(context)
    val userId = googleSignIn.getUserId().toString()
    val userName = googleSignIn.getUserName()

    val coroutineScope = rememberCoroutineScope()

    var adminClicked by remember {
        mutableStateOf(false)
    }

    var kickOutMember by remember {
        mutableStateOf("")
    }

    var AdminId by remember {
        mutableStateOf("")
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {

        TopActionBar(title = "Members")

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 70.dp)) {

            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(list)
                { item ->
                    val name : String

                    if(item.name.startsWith("admin")) name = item.name.substring(5)
                    else if(item.name.startsWith("false")) name = item.name.substring(5)
                    else name = item.name.substring(4)

                    if(item.isAdmin) AdminId = name.lowercase()

                    Log.d("Members","Admin : $AdminId  User : $userId")

                    Surface(
                        modifier = Modifier
                            .pointerInput(Unit)
                            {
                                detectTapGestures(
                                    onLongPress = {

                                        if(userId.equals(AdminId) && !item.isAdmin)
                                        {
                                            vibratePhone(context)
                                            adminClicked = !adminClicked
                                            kickOutMember = item.name
                                        }

                                    }
                                )
                            }
                    ) {

                        MemberCard(
                            imgPath = item.url,
                            userName = name,
                            isAdmin = item.isAdmin)
                    }

                }
            }

        }
    }

    if(adminClicked)
    {
        AlertDialog(

            shape = RoundedCornerShape(12.dp),

            modifier = Modifier
                .border(BorderStroke(2.dp,MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp)),

            containerColor = if(isSystemInDarkTheme()) Color.Black else Purple80,

            title = {
                Text(
                    text = "Remove Member",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.8f)
                )
            },

            text = {
                Text(
                    text = "Do you want to remove?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            },

            onDismissRequest = { adminClicked = !adminClicked },

            confirmButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(Purple40),
                    onClick = {

                        if (kickOutMember.isNotEmpty() && !kickOutMember.equals(userId)) {
                            adminClicked = !adminClicked

                           if(kickOutMember.startsWith("true"))
                           {
                               coroutineScope.launch {
                                   dbQueries.exitGroup(
                                       context = context,
                                       pptId = pptId,
                                       pptDelete = true,
                                       removeMem = kickOutMember.substring(4).lowercase()
                                   )

                               }
                           }

                            else{
                               coroutineScope.launch {
                                   dbQueries.exitGroup(
                                       context = context,
                                       pptId = pptId,
                                       pptDelete = true,
                                       removeMem = kickOutMember.substring(5).lowercase()
                                   )

                               }
                           }
                        }

                        if (kickOutMember.equals(userId)) {
                            Toast.makeText(
                                context,
                                "Cannot Remove Admin",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
                {
                    Text(
                        text = "Proceed",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                }
            },

            dismissButton = {

                Button(
                    colors = ButtonDefaults.buttonColors(),
                    onClick = { adminClicked = !adminClicked })
                {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

@Composable
fun SetMemberData(viewModel: MemberViewModel, adminName : String, pptId: String)
{

    when(val result = viewModel.response.value)
    {
        is LoadMembers.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "No Members",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        is LoadMembers.Failure -> {

        }

        is LoadMembers.Loading -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
            {
                TopActionBar(title = "Members")

                repeat(7)
                {
                    ShimmerEffect()
                }
            }
        }

        is LoadMembers.Success -> {
            MembersListCompose(result.list, adminName = adminName, pptId = pptId)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun MemberCard(
    imgPath: String,
    userName: String,
    isAdmin: Boolean
)
{
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp))
            .padding(start = 10.dp, end = 10.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically)
    {

        AsyncImage(
            model = imgPath,
            contentDescription = "Profile Pic",
            modifier = Modifier.clip(CircleShape).size(36.dp))
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = userName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1)

        if(isAdmin)
        {
            Spacer(modifier = Modifier.width(10.dp))

            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.star),
                contentDescription = "admin mark",
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 2.dp, start = 5.dp),
                tint = Orange)

        }
    }
}




