package com.example.presentationmaker

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.LightPurple40
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.PurpleGrey40
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeTopicCard(pptId: String,groupAdmin : String): ViewModel() {

    val response : MutableState<TopicInfoState> = mutableStateOf(TopicInfoState.Loading)

    init {
        fetchTopic(pptId = pptId,groupAdmin = groupAdmin)
    }

    private fun fetchTopic(pptId : String,groupAdmin: String)
    {
        Log.d("groupAdmin",groupAdmin)
        val dbQueries = DbQueries()
        val topicRef = dbQueries.getTopicAdminRef(code = pptId, userId = groupAdmin)

        topicRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var dateTime = ""
                var topic = ""

                for(data in snapshot.children)
                {
                    if(data.key.toString().equals("first"))
                    {
                        dateTime = data.value.toString()
                    }

                    else if(data.key.toString().equals("second"))
                        topic = data.value.toString()
                }

                Log.d("groupAdmin", "$dateTime $topic")
                response.value = TopicInfoState.Success(Pair(topic,dateTime))
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = TopicInfoState.Failure(error.message)
            }
        })
    }

}

@Composable
fun SetTopicInfo(viewModel: HomeTopicCard)
{
    when(val result = viewModel.response.value)
    {
        is TopicInfoState.Loading ->
        {
            CircularProgressIndicator(color = if(isSystemInDarkTheme()) Purple80 else Purple40)
        }

        is TopicInfoState.Success ->
        {
            TopicCard(data = result.data, bgColor = Purple40)
        }

        is TopicInfoState.Empty ->
        {
            CircularProgressIndicator()
        }

        is TopicInfoState.Failure ->
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
fun TopicCard(
    data : Pair<String,String>,
    title : String = "Topic Info",
    paddingValues: PaddingValues = PaddingValues(0.dp),
    color: Color = Color.White,
    bgColor: Color = Color.Transparent,
    brush: Brush = Brush.linearGradient(
        listOf(LightPurple40, Purple40)
    ),
)
{
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth().height(40.dp)
            .padding(paddingValues)
            .background(brush = brush, shape = RoundedCornerShape(12.dp)),

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )

    }

    Spacer(modifier = Modifier.height(10.dp))

    Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = data.second,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = data.first,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold)
            }
        }
}

sealed class TopicInfoState{
    class Success(val data : Pair<String,String>) : TopicInfoState()
    class Failure(val msg : String) : TopicInfoState()
    object Loading : TopicInfoState()
    object Empty : TopicInfoState()
}

@Composable
@Preview
fun Topic()
{
    TopicCard(data = Pair("HEllo", "WOrld"))
}
