package com.example.presentationmaker

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.MutableIntState
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
import com.example.presentationmaker.ui.theme.Cream10
import com.example.presentationmaker.ui.theme.LightPurple40
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshots
import kotlinx.coroutines.flow.collect

class TopicsViewModel(pptId: String) : ViewModel() {

    val response : MutableState<LoadTopics> = mutableStateOf(LoadTopics.Loading)

    init {
        fetchGroupMemberCount(pptId)
    }

    private fun fetchGroupMemberCount(pptId: String)
    {
        val dbQueries = DbQueries()
        val groupRef = dbQueries.getGroupRef(code = pptId)
        val memberMap = mutableMapOf<String,Int>()

        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children)
                {
                    memberMap.set(data.key.toString(), data.childrenCount.toInt())
                }

                if(memberMap.size == snapshot.childrenCount.toInt())
                {
                    fetchTopics(pptId,memberMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchTopics(pptId : String,memCount : MutableMap<String,Int>)
    {
        val dbQueries = DbQueries()

        val topicRef = dbQueries.getTopicRef(code = pptId)
        val list = mutableListOf<TopicInfo>()

        topicRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var topic = ""
                var dateTime = ""
                var topicAdmin = ""

                if(snapshot.childrenCount.toInt() == 0)
                    response.value = LoadTopics.Success(list)

                for(admin in snapshot.children)
                {
                    topicAdmin = admin.key.toString()

                    for(data in admin.children)
                    {
                        if(data.key.toString().equals("first"))
                            dateTime = data.value.toString()

                        else if(data.key.toString().equals("second"))
                            topic = data.value.toString()
                    }

                    val members = memCount.get(admin.key.toString())

                    if(members != null)
                    {
                        list.add(
                            TopicInfo(
                                topic = topic,
                                admin = topicAdmin,
                                dateTime = dateTime,
                                memCount = members
                            )
                        )
                    }

                    if(list.size == snapshot.childrenCount.toInt())
                    {
                        response.value = LoadTopics.Success(list)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = LoadTopics.Failure(error.message)
            }
        })
    }
}

@Composable
fun SetTopicData(viewModel: TopicsViewModel)
{
    when(val result = viewModel.response.value)
    {
        is LoadTopics.Loading ->
        {
            TopActionBar(title = "Topics")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = if(isSystemInDarkTheme()) Purple80 else Purple40)
            }
        }

        is LoadTopics.Success ->
        {
            TopicsList(list = result.data)
        }

        is LoadTopics.Empty ->
        {
            CircularProgressIndicator()
        }

        is LoadTopics.Failure ->
        {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = result.msg,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TopicsList(list : MutableList<TopicInfo>)
{
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {

        TopActionBar(title = "Topics")

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
                {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        LightPurple40, Purple40
                                    )
                                ), shape = RoundedCornerShape(12.dp)
                            )

                            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp)),

                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Log.d("memCount",it.memCount.toString())

                        TopicCard(
                            data = Pair(it.topic,it.dateTime),
                            title = if(it.memCount > 1) "${it.admin.uppercase()} and Others..." else "${it.admin.uppercase()} ",
                            paddingValues = PaddingValues(top = 10.dp),
                            color = Color.White
                        )
                    }

                }
            }

        }
    }
}

sealed class LoadTopics{
    class Success(val data : MutableList<TopicInfo>) : LoadTopics()
    class Failure(val msg: String) : LoadTopics()
    object Loading : LoadTopics()
    object Empty : LoadTopics()
}

data class TopicInfo(
    val topic : String,
    val dateTime : String,
    val admin : String,
    val memCount : Int
)