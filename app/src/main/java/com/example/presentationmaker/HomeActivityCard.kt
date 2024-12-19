package com.example.presentationmaker

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.ui.theme.Cream10
import com.example.presentationmaker.ui.theme.Cream50
import com.example.presentationmaker.ui.theme.Grey10
import com.example.presentationmaker.ui.theme.LightBlue
import com.example.presentationmaker.ui.theme.LightRed
import com.example.presentationmaker.ui.theme.Pink40
import com.example.presentationmaker.ui.theme.Purple40
import com.example.presentationmaker.ui.theme.Purple80
import com.example.presentationmaker.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.ui.timepicker.WheelTimePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import network.chaintech.kmp_date_time_picker.utils.now
import org.checkerframework.common.value.qual.StaticallyExecutable
import java.lang.Error
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random


@Composable
fun ExpandableCard(
    color: Color = Color.Black,
    title: String = "New Presentation",
    adminName : String = "Admin Name",
    padding: Dp = 15.dp,
    titleWeight: FontWeight = FontWeight.Bold,
) {

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
                        colors = listOf(
                            if (isSystemInDarkTheme()) randomColor2 else randomColor1,
                            color
                        )
                    )
                )
                .padding(padding),

            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,

            ) {

            Row() {

                Text(
                    modifier = Modifier,
                    text = title,
                    color = Color.White,
                    fontWeight = titleWeight,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(5.dp))

            Row() {

                Text(
                    modifier = Modifier,
                    text = adminName,
                    color = Color.White,
                    fontWeight = titleWeight,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDialogCreate() {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(Cream10)
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DialogTabs()
    }
}



@Composable
fun textFieldCompose(
    label: String = "Title",
    charLimit : Int,
    SignInError: Boolean = false) : String {


    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    var text by rememberSaveable {
        mutableStateOf("")
    }

    Log.d("signIn",SignInError.toString())

    var isError by rememberSaveable {
        mutableStateOf(!SignInError)
    }

    fun validate(text : String)
    {
        isError = text.isEmpty() || text.length > charLimit
    }

    Column(
        Modifier
            .padding(10.dp)
            .wrapContentSize()
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                validate(text)
            },


            isError = isError,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize(),

            label = { Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold) },

            supportingText = {

                if(!SignInError)
                {
                    Text(text = "Sign In Required")
                    isError = true
                }

                else if(isError && text.isEmpty())
                {
                    Text(text = "$label Required")
                }

                else if(isError && text.isNotEmpty())
                {
                    Text(text = "Char Limit Exceeded : ${text.length}/$charLimit")
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
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = borderColor,
                focusedSupportingTextColor = Color.Red,
                unfocusedSupportingTextColor = Color.Red,
                errorSupportingTextColor = Color.Red,
                errorContainerColor = MaterialTheme.colorScheme.background,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                unfocusedBorderColor = borderColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                unfocusedTextColor = textColor,
                focusedTextColor = textColor,
                unfocusedContainerColor = Purple80
            ),

            textStyle = MaterialTheme.typography.bodyMedium,

            singleLine = true
        )
    }

    return text
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun participantsSelect() : Int {
    var count by remember {
        mutableStateOf(0)
    }

    val context = LocalContext.current
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    Row(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent)
            .padding(10.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            modifier = Modifier.weight(3.5f),
            text = "MAX PER GROUP",
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier
                .background(borderColor, shape = CircleShape)
                .clip(CircleShape)
                .wrapContentSize()
                .padding(10.dp)
                .weight(1.5f),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (count < 10) "0$count" else "$count",
                fontWeight = FontWeight.Bold,
                color = if(isSystemInDarkTheme()) Color.Black else Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {

                if(count == 6)
                {
                    Toast.makeText(context, "MAX LIMIT REACHED",Toast.LENGTH_SHORT).show()
                }

                else count += 1
            }) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "inc_participants",
                modifier = Modifier
                    .background(shape = CircleShape, color = borderColor)
                    .rotate(180f),
                tint = MaterialTheme.colorScheme.background
            )
        }

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { if (count > 0) count -= 1 }) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "inc_participants",
                Modifier
                    .background(color = borderColor, shape = CircleShape),

                tint = MaterialTheme.colorScheme.background
            )
        }
    }

    return count
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dateTimePicker(
    buttonText : String = "Select Date"
) : String
{
    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    var datePickerState by remember {
        mutableStateOf(false)
    }

    var dateSelected by remember {
        mutableStateOf("")
    }

    OutlinedButton(

        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),

        border = BorderStroke(2.dp,borderColor),

        colors = ButtonDefaults.buttonColors(if(isSystemInDarkTheme()) Color.Black else Purple80),

        onClick = { datePickerState = !datePickerState })
        {
            Text(
                text = if(dateSelected.isEmpty()) buttonText else dateSelected,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

    if(Build.VERSION.SDK_INT >= 26)
    {
        WheelDatePickerView(

            modifier = Modifier.padding(top = 10.dp),

            title = "Select Submission Date",
            titleStyle = MaterialTheme.typography.bodyLarge,

            containerColor = Color.White,
            dateTextColor = Color.Black,
            dateTextStyle = MaterialTheme.typography.bodyMedium,

            dragHandle = {},

            showShortMonths = true,

            rowCount = 5,

            onDoneClick = {
                datePickerState = false
                dateSelected = "$it"},

            onDismiss = { datePickerState = !datePickerState },

            showDatePicker = datePickerState,
            height = 300.dp,
            dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
            selectorProperties = WheelPickerDefaults.selectorProperties())
    }

    else if(datePickerState)
    {
       DatePickerModal(
           onDismiss = {
           datePickerState = false
       },

           onDateSelected = {
               if(it != null)
               {
                   val date = Date(it)
                   val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date)
                   dateSelected = formattedDate
               }

           })
    }

    return dateSelected
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Date().apply {
                    time = utcTimeMillis
                }
                val currentDate = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                return date.after(currentDate)
            }
        })


    DatePickerDialog(

        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background),

        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {

                Log.d("date",datePickerState.selectedDateMillis.toString())

                if(datePickerState.selectedDateMillis != null)
                {
                    onDateSelected(datePickerState.selectedDateMillis)
                }

                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            state = datePickerState)
    }
}

@Composable
fun DialogTabs() {

    Log.d("Dialog Tabs","enter")

    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black

    val tabItems = listOf(
        TabItem(
            title = "Create",
            unFilledIcon = Icons.Outlined.Create,
            filledIcon = Icons.Filled.Create
        ),

        TabItem(
            title = "Join",
            unFilledIcon = Icons.Outlined.Add,
            filledIcon = Icons.Filled.Add
        ),
    )

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    val pagerState = rememberPagerState {
        tabItems.size
    }

    val googleSignIn = GoogleSignIn(LocalContext.current)

    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(
            selectedTabIndex,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    LaunchedEffect(key1 = pagerState.currentPage, key2 = pagerState.isScrollInProgress) {

        if (!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }


    Column(
        modifier = Modifier
            .wrapContentSize()
    ) {
        TabRow(

            indicator = {
                tabPositions ->

                if(selectedTabIndex < tabPositions.size)
                {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex]),

                        color = if(isSystemInDarkTheme()) Purple40 else Purple80
                    )
                }
            },

            contentColor = if(isSystemInDarkTheme()) Color.Black else Color.White,
            containerColor = borderColor,
            selectedTabIndex = selectedTabIndex
        ) {
            tabItems.forEachIndexed { index, tabItem ->

                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {

                        if(index == selectedTabIndex)
                        {
                            Text(
                                text = tabItem.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if(isSystemInDarkTheme()) Color.Black else Color.White)
                        }

                        else
                        {
                            Text(
                                text = tabItem.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                color = if(isSystemInDarkTheme()) Color.Black else Color.White)
                        }

                    },

                    icon = {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                tabItem.filledIcon
                            } else tabItem.unFilledIcon,
                            contentDescription = tabItem.title
                        )
                    }

                )
            }
        }

        HorizontalPager(pagerState) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f),

                contentAlignment = Alignment.Center
            ) {
                val dbQueries = DbQueries()
                val context = LocalContext.current

                when (pagerState.currentPage) {
                    0 -> Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp),

                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val userId = googleSignInButton()
                        val inputTxt = textFieldCompose(charLimit = 50, SignInError = googleSignIn.isSignedIn())
                        Log.d("Dialog Tabs","enter")
                        val date = dateTimePicker()
                        val count = participantsSelect()

                        Log.d("Dialog Tabs","enter")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        )
                        {
                            ElevatedButton(
                                onClick = {

                                    if(count <= 0) Toast.makeText(context,"Members must be at least 1",Toast.LENGTH_SHORT).show()
                                    else if(inputTxt.isEmpty()) Toast.makeText(context,"Title Not Set",Toast.LENGTH_SHORT).show()
                                    else if(date.isEmpty()) Toast.makeText(context,"Date Not Set",Toast.LENGTH_SHORT).show()

                                    else if(userId != null && inputTxt.isNotEmpty())
                                    {
                                        googleSignIn.getUserName()?.let { it1 ->
                                            dbQueries.createPpt(
                                                topicTitle = inputTxt,
                                                adminName = it1,
                                                adminId = userId,
                                                submissionDate = date,
                                                maxMem = count
                                            )
                                        }

                                        Log.d("check",userId)
                                    }
                                },
                                colors = ButtonColors(
                                    containerColor = if(isSystemInDarkTheme()) Color.Black else Purple80,
                                    borderColor,
                                    Color.White,
                                    Color.White
                                ),
                                border = BorderStroke(2.dp, borderColor)
                            ) {
                                Text(
                                    text = "Create",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if(isSystemInDarkTheme()) Color.White else Purple40
                                )
                            }
                        }
                    }

                    1 -> Column(
                        Modifier
                            .wrapContentSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp),

                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        var isFound by rememberSaveable {
                          mutableStateOf(false)
                        }

                        var signInError by rememberSaveable {
                            mutableStateOf(googleSignIn.isSignedIn())
                        }

                        val userId = googleSignInButton()

                        val inputCode = textFieldCompose(
                            label = "Code",
                            charLimit = 6,
                            SignInError = googleSignIn.isSignedIn())

                        Spacer(modifier = Modifier.height(70.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        )
                        {
                            ElevatedButton(
                                onClick = {
                                    if(userId != null && inputCode.isNotEmpty() && googleSignIn.isSignedIn())
                                    {
                                        Log.d("escape","success")

                                        dbQueries.joinPpt(
                                            code = inputCode,
                                            adminId = userId,
                                            context = context)

                                        signInError = googleSignIn.isSignedIn()
                                    }
                                },
                                colors = ButtonColors(
                                    containerColor = if(isSystemInDarkTheme()) Color.Black else Purple80,
                                    Color.Unspecified,
                                    Color.Unspecified,
                                    Color.Unspecified
                                ),
                                border = BorderStroke(2.dp, borderColor)
                            ) {
                                Text(
                                    text = "Join",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if(isSystemInDarkTheme()) Color.White else Purple40
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun googleSignInButton() : String? {

    val context = LocalContext.current

    val googleAuth = GoogleSignIn(context)
    val coroutineScope = rememberCoroutineScope()

    var IsSignIn by rememberSaveable {
        mutableStateOf(googleAuth.isSignedIn())
    }

    val userName = googleAuth.getUserName()
    val userId = googleAuth.getUserId()

    val borderColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary
    val textColor = if(isSystemInDarkTheme()) Color.White else Color.Black


    Column(
        modifier = Modifier
            .wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {


        if (!IsSignIn) {
            OutlinedButton(
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = Color.Unspecified
                ),

                border = BorderStroke(2.dp,borderColor),
                onClick = {
                    Log.d("check","sign")
                    coroutineScope.launch {
                        IsSignIn = googleAuth.signIn()
                    }
                },
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(MaterialTheme.colorScheme.background),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.icons8_google),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                }
            }
        }

        else
        {
            OutlinedButton(
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                border = BorderStroke(2.dp,borderColor),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = Color.Unspecified
                ),
                onClick = {

                    Log.d("check","sign")
                    coroutineScope.launch {

                        googleAuth.signOut()
                        IsSignIn = false
                    }

                },
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(MaterialTheme.colorScheme.background),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.icons8_google),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {

                        Text(
                            text = "Signed in as $userName",
                            color = textColor,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Click to sign out",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }


}

    return userId
}

data class TabItem(
    val title: String,
    val filledIcon: ImageVector,
    val unFilledIcon: ImageVector
)

@Preview
@Composable

fun Preview() {
 googleSignInButton()
}
