package com.example.presentationmaker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Snackbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.lato
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PresentationActivity(
    topic : String,
    adminName : String,
    pptId: String,
    date : String,
    maxMem : Int)
{

    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                Column {
                    HorizontalDivider(
                        thickness = 0.4.dp,
                        color = if(isSystemInDarkTheme()) Color.White else Color.Black
                    )

                    BottomNavBar(navController = navController, context = context)
                }
            }
        ){
            BottomNavGraph(
                navController = navController,
                topic = topic,
                adminName = adminName,
                pptId = pptId,
                date = date,
                maxMem = maxMem)
        }





    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavBar(navController: NavHostController, context: Context)
{
    val items = listOf(
        BottomNavItems.Home,
        BottomNavItems.Members,
        BottomNavItems.Groups,
        BottomNavItems.Topics
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
    )
    {
        items.forEach {
            item->

            AddItem(
                context = context,
                item = item,
                currentDest = currentDest,
                navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.AddItem(
    item : BottomNavItems,
    currentDest : NavDestination?,
    navController: NavHostController,
    context: Context
)
{
    val tooltipPos = TooltipDefaults.rememberPlainTooltipPositionProvider(spacingBetweenTooltipAndAnchor = 20.dp)
    val tooltipState = rememberTooltipState(isPersistent = false)

    val isSelected = currentDest?.hierarchy?.any{
        it.route == item.route
    } == true

    BottomNavigationItem(

        modifier = Modifier
            .padding(top = 10.dp),

        label = {

                if(isSelected)
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1)

                else
                {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1)
                }
        },

        icon = {

            if(isSelected)
            {
                TooltipBox(positionProvider = tooltipPos, tooltip = {

                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(
                                color = if (isSystemInDarkTheme()) Color.White.copy(0.9f) else Color.Black.copy(0.6f)
                            )
                    )
                    {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                }, state = tooltipState) {
                    Icon(
                        modifier = Modifier
                            .width(50.dp)
                            .align(Alignment.CenterVertically)
                            .padding(bottom = 10.dp)
                            .background(
                                Purple80,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        painter = painterResource(id = item.filledicon),
                        contentDescription = item.label,
                        tint = Purple40)
                }
            }

            else
            {
                TooltipBox(
                    positionProvider = tooltipPos,
                    tooltip = {

                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(
                                color = if (isSystemInDarkTheme()) Color.White.copy(0.9f) else Color.Black.copy(0.6f)
                            )
                    )
                    {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                    },

                    state = tooltipState) {

                        Icon(
                            modifier = Modifier
                                .absolutePadding(bottom = 10.dp),
                            painter = painterResource(id = item.unfilledIcon),
                            contentDescription = item.label)
                    }
            }
        },

        selected = isSelected,

        onClick = {

            navController.navigate(item.route){
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }

        }
    )
}

@Composable
fun BottomNavGraph(
    topic : String,
    adminName : String,
    pptId : String,
    date: String,
    maxMem: Int,
    navController : NavHostController)
{
    NavHost(navController = navController, startDestination = BottomNavItems.Home.route)
    {
        composable(route = BottomNavItems.Home.route){
            HomeScreen(topic = topic, adminName = adminName,pptId = pptId, date = date,maxMem = maxMem)
        }

        composable(route = BottomNavItems.Members.route){
            val viewModel = MemberViewModel(pptId = pptId)
            SetMemberData(viewModel = viewModel, adminName = adminName, pptId = pptId)
        }

        composable(route = BottomNavItems.Groups.route){
            val viewModel = GroupCardViewModel(pptId)
            SetGroupData(viewModel)
        }

        composable(route = BottomNavItems.Topics.route){
            TopicScreen(pptId = pptId)
        }
    }
}

sealed class BottomNavItems(val route : String, val filledicon : Int, val unfilledIcon : Int, val label : String)
{
    object Home : BottomNavItems("home/{id}",R.drawable.home_filled,R.drawable.home_unfilled,"Home")
    object Members : BottomNavItems("members",R.drawable.person_filled,R.drawable.person_unfilled,"Members")
    object Groups : BottomNavItems("groups", R.drawable.groups_filled,R.drawable.groups,"Groups")
    object Topics : BottomNavItems("topics",R.drawable.task_filled,R.drawable.task_24px,"Topics")
}

