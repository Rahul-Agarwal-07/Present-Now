package com.example.presentationmaker

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.DarkBlue
import com.example.presentationmaker.ui.theme.LightBlue
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.PurpleGrey40
import com.example.presentationmaker.ui.theme.PurpleGrey80

@Composable
fun GroupScreen(list : MutableList<MutableList<String>>)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopActionBar(title = "Groups")

        LazyColumn()
        {
            items(list)
            {
                GroupCard(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectGroupMembers(list: MutableList<MemberInfo>, maxMem : Int, pptId: String)
{
    val context = LocalContext.current
    val selectList = mutableSetOf<MemberInfo>()
    val isEmpty = list.size == 1
    val dbQueries = DbQueries()
    val userId = GoogleSignIn(context).getUserId().toString().uppercase()


    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        var isAdminSelected by remember {
            mutableStateOf(false)
        }

        if(isEmpty)
        {
            Text(
                text = "NO MEMBERS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
        }

        if(!isEmpty){

        Scaffold(modifier = Modifier
            .fillMaxSize()
            .weight(10f)
            .background(MaterialTheme.colorScheme.background)) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = innerPadding
            ) {

                items(list)
                { item ->

                    if (!item.isAdmin && !item.name.startsWith("true")) {

                        var clicked by remember {
                            mutableStateOf(false)
                        }

                        ListItem(

                            colors = ListItemColors(
                                containerColor = if(isSystemInDarkTheme()) Color.Black else Color.White,
                                headlineColor = if(isSystemInDarkTheme()) Color.White else Color.Black,
                                leadingIconColor = Color.Black,
                                overlineColor = Color.Black,
                                supportingTextColor = if(isSystemInDarkTheme()) Color.White else Color.Black,
                                trailingIconColor = Color.Black,
                                disabledHeadlineColor = Color.Black,
                                disabledLeadingIconColor = Color.Black,
                                disabledTrailingIconColor = Color.Black,
                            ),

                            headlineContent = {

                                val name : String

                                if(item.name.startsWith("admin")) name = item.name.substring(5)
                                else if(item.name.startsWith("false")) name = item.name.substring(5)
                                else name = item.name.substring(4)

                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if(isSystemInDarkTheme()) Color.White else Color.Black,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            },

                            leadingContent = {

                                AsyncImage(
                                    model = item.url,
                                    contentDescription = "Profile Pic",
                                    modifier = Modifier.clip(CircleShape)
                                )
                            },

                            trailingContent = {

                                Checkbox(

                                    colors = CheckboxDefaults.colors(
                                        checkedColor = if(isSystemInDarkTheme()) darkColorScheme().primary
                                        else lightColorScheme().primary
                                    ),

                                    checked = clicked,

                                    onCheckedChange = {

                                        clicked = !clicked

                                        if (clicked) {
                                            Log.d("user", "false$userId  ${item.name}")
                                            if(item.name.equals("false$userId")) isAdminSelected = true
                                            selectList.add(item)
                                        } else {
                                            Log.d("user", item.name)
                                            if(item.name.equals("false$userId")) isAdminSelected = false
                                            selectList.remove(item)
                                        }

                                    })
                            },

                            modifier = Modifier
                                .clickable {
                                    clicked = !clicked

                                    if (clicked) {
                                        if (item.name.equals("false$userId")) isAdminSelected = true
                                        Log.d("user", item.name)
                                        selectList.add(item)
                                    } else {
                                        Log.d("user", item.name)
                                        if (item.name.equals("false$userId")) isAdminSelected =
                                            false
                                        selectList.remove(item)
                                    }

                                }
                                .background(Color.Black)
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = if(isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }


                }

            }
        }

             Row(modifier = Modifier
                 .weight(1f)
                 .padding(10.dp))
             {
                 Surface(
                     modifier = Modifier
                         .fillMaxWidth()
                         .background(
                             brush = Brush.linearGradient(
                                 listOf(LightBlue, DarkBlue)
                             ),

                             shape = RoundedCornerShape(12.dp)
                         ),

                     color = Color.Transparent,

                     onClick = {

                         if(!isAdminSelected)
                         {
                             Toast.makeText(context,"ONE MEMBER MUST BE USER",Toast.LENGTH_SHORT).show()
                         }

                         else if(selectList.size <= maxMem)
                         {
                             Log.d("size",selectList.size.toString())
                             Toast.makeText(context,"Group SuccessFully Created",Toast.LENGTH_SHORT).show()

                             val groupRef = dbQueries.getGroupRef(pptId)
                             val token = GoogleSignIn(context).getUserId()

                             for(data in selectList)
                             {
                                 val name : String

                                 if(data.name.startsWith("admin")) name = data.name.substring(5)
                                 else if(data.name.startsWith("false")) name = data.name.substring(5)
                                 else name = data.name.substring(4)

                                 groupRef.child(token.toString()).child(name).setValue(data.url)

                                 val memRef = dbQueries.getMembersRef(pptId)
                                 memRef.child("false$name").removeValue()
                                 memRef.child("true$name").setValue(data.url)

                                 val userRef = dbQueries.getIdRef(name.lowercase())
                                 userRef.child(pptId).removeValue()
                                 userRef.child(pptId).child(token.toString()).setValue("")
                             }

                         }

                         else
                         {
                             Toast.makeText(context,"Max Group Size is : $maxMem",Toast.LENGTH_SHORT).show()
                         }

                     }) {

                     Row(
                         modifier = Modifier.fillMaxSize(),
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.Center
                     )
                     {
                         Text(
                             text = "CLICK TO CREATE GROUP",
                             style = MaterialTheme.typography.titleMedium,
                             fontWeight = FontWeight.Bold,
                             color = Color.White)
                     }
                 }
             }



            }

        }

}

@Composable
fun SetMemberSelectData(
    viewModel: MemberViewModel,
    clickedState : Boolean,
    maxMem: Int,
    pptId: String)
{
    if(clickedState)
    {
        when(val result = viewModel.response.value)
        {
            is LoadMembers.Loading ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center){

                    CircularProgressIndicator()
                }
            }

            is LoadMembers.Success ->
            {
                SelectGroupMembers(list = result.list, maxMem = maxMem,pptId = pptId)
            }

            is LoadMembers.Failure ->
            {

            }

            else ->
            {

            }
        }
    }
}

@Composable
fun GroupCard(
    groupList : MutableList<String>
)
{
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    var expandedState by remember {
        mutableStateOf(false)
    }

    val rotationState by animateFloatAsState(
        targetValue = if(expandedState) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp))

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .height(50.dp)
                .padding(start = 10.dp, end = 10.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start

        ) {
            Text(
                modifier = Modifier.weight(6f),
                text = groupList.first(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                modifier = Modifier.rotate(rotationState),
                onClick = { expandedState = !expandedState }) {

                Icon(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand Arrow")
            }
        }

        if(expandedState)
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSystemInDarkTheme()) Purple40 else Purple80)
                    .padding(10.dp),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                for(data in groupList)
                {
                    Text(
                        text = data,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SetGroupData(groupViewModel: GroupCardViewModel)
{
    when(val result = groupViewModel.response.value)
    {
        is GroupDataState.Loading ->
        {
            Column(modifier = Modifier.fillMaxSize())
            {
                TopActionBar(title = "Groups")

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = if(isSystemInDarkTheme()) Purple80 else Purple40)
                }
            }
        }

        is GroupDataState.Success ->
        {
            GroupScreen(result.data)
        }

        is GroupDataState.Failure ->
        {
            Column(
                modifier = Modifier.fillMaxSize(),
            ){
                TopActionBar(title = "Groups")

                Text(
                    text = result.msg,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        is GroupDataState.Empty->
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){

                TopActionBar(title = "Groups")

                Text(
                    text = "NO GROUPS CREATED",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

