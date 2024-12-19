package com.example.presentationmaker

import androidx.compose.runtime.Composable

@Composable
fun TopicScreen(pptId : String)
{
    val topicsViewModel = TopicsViewModel(pptId = pptId)
    SetTopicData(viewModel = topicsViewModel)
}