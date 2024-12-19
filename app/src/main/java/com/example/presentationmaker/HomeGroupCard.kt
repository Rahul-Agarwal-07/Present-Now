package com.example.presentationmaker

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.data.USERS_REF
import com.example.presentationmaker.ui.theme.LightPurple40
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.PurpleGrey40
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeGroupCard(context : Context, pptId: String) : ViewModel()
{
    val response : MutableState<GroupInfoState> = mutableStateOf(GroupInfoState.Empty)

    init {
        fetchGroupAdmin(pptId = pptId, context = context)
    }

    private fun fetchGroupAdmin(context: Context, pptId: String)
    {

        response.value = GroupInfoState.Loading

        val dbQueries = DbQueries()
        val userId = GoogleSignIn(context = context).getUserId()
        val userRef = FirebaseDatabase.getInstance().getReference("$USERS_REF/$userId/$pptId")

        var groupAdmin = ""

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataSnap in snapshot.children)
                {
                    groupAdmin = dataSnap.key.toString()
                }

                fetchGroupMem(pptId = pptId, groupAdmin = groupAdmin)

            }

            override fun onCancelled(error: DatabaseError) {
                response.value = GroupInfoState.Failure(error.message)
            }
        })

        Log.d("Success","Admin : $groupAdmin")
    }

    private fun fetchGroupMem(pptId: String, groupAdmin : String)
    {
        val groupRef = DbQueries().getGroupAdminRef(code = pptId, id = groupAdmin)
        val list = mutableListOf<MemberInfo>()

        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var name = ""
                var url = ""

                for(data in snapshot.children)
                {
                    name = data.key.toString()
                    url = data.value.toString()

                    Log.d("url",url)

                    list.add(
                        MemberInfo(
                            isAdmin = false,
                            name = name,
                            url = url
                        )
                    )

                    Log.d("Success","size : $name")
                }

                Log.d("viewModel","children : ${snapshot.childrenCount}  $groupAdmin")

                if(snapshot.childrenCount.toInt() == list.size)
                    response.value = GroupInfoState.Success(list)
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = GroupInfoState.Failure(error.message)
            }
        })
    }
}

@Composable
fun SetGroupInfo(viewModel : HomeGroupCard)
{
    when(val result = viewModel.response.value)
    {
        is GroupInfoState.Loading ->
        {
            CircularProgressIndicator(color = if(isSystemInDarkTheme()) Purple80 else Purple40)
        }

        is GroupInfoState.Success ->
        {
            if(result.data.size > 0) HomeGroupList(list = result.data)
        }

        is GroupInfoState.Empty ->
        {
            CircularProgressIndicator()
        }

        is GroupInfoState.Failure ->
        {

        }


    }
}

@Composable
fun HomeGroupList(list : MutableList<MemberInfo>)
{
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth().height(40.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(LightPurple40, Purple40)
                    ),
                    shape = RoundedCornerShape(12.dp)),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){

            Text(
                modifier = Modifier.padding(5.dp),
                text = "Group Info",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }

        LazyColumn(
            modifier = Modifier.padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Log.d("Success","size : ${list.size}")

            items(list)
            {
                Log.d("Success","name : ${it.url}")
                MemberCard(imgPath = it.url, userName = it.name, isAdmin = it.isAdmin)
            }

        }

    }
}

sealed class GroupInfoState{
    class Success(val data : MutableList<MemberInfo>) : GroupInfoState()
    class Failure(val msg : String) : GroupInfoState()
    object Loading : GroupInfoState()
    object Empty : GroupInfoState()
}