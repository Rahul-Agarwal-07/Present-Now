package com.example.presentationmaker.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.substring
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random
import com.example.presentationmaker.GoogleSignIn
import com.example.presentationmaker.LoadTopics
import com.example.presentationmaker.SetupNavGraph
import com.example.presentationmaker.SignInState
import com.example.presentationmaker.response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import network.chaintech.kmp_date_time_picker.utils.now
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

fun vibratePhone(context: Context) {

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

class DbQueries : ComponentActivity(){

    fun createPpt(
        topicTitle: String,
        adminName: String,
        adminId: String,
        submissionDate: String,
        maxMem : Int
    ) {
        Log.d("Enter","id")
        val pptId = pptIdGenerator(adminId)

        val getUserRef = getUserRef()

        getUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount.toInt() == 0){
                    getUserRef.child(adminId).setValue("")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val idRef = getIdRef(adminId)

        Log.d("idKey",idRef.key.toString())
        idRef.child(pptId).setValue("")

        val homeRef = getHomeRef(pptId)
        homeRef.child(Topic_Name_Query).setValue(topicTitle)
        homeRef.child("$Admin_Name_Query$adminId").setValue(adminName)
        homeRef.child(Submission_Date_Query).setValue(submissionDate)
        homeRef.child(MAX_MEMBER_QUERY).setValue(maxMem)

    }


    private fun pptIdGenerator(adminId: String) : String
    {
        Log.d("enter","ppt")
        var pptId = Random.nextInt(100001,999999)
        val userRef = getPPTRef()

        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataSnapshot in snapshot.children)
                {
                    if(dataSnapshot.key.toString().equals(pptId.toString()))
                    {
                        pptId = Random.nextInt(100001,999999)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val pptIdRef = userRef.child(pptId.toString())
        val memberRef = pptIdRef.child(MEMBERS_DB)

        val googleAuth = GoogleSignIn(this)
        val photoUrl = googleAuth.getPhotoUri()

        memberRef.child("admin"+adminId.uppercase()).setValue(photoUrl)

        return pptId.toString()
    }

    fun joinPpt(
        code : String,
        adminId : String,
        context: Context)
    {
        val googleAuth = GoogleSignIn(this)
        val userId = googleAuth.getUserId().toString()

        val idRef = getIdRef(id = userId)

        var isFound = false

        idRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(data in snapshot.children)
                {
                    if(data.key.equals(code))
                    {
                        isFound = true
                    }
                }

                if(!isFound) joinPptPrivate(code = code, adminId = adminId)

                else{

                    Toast.makeText(
                        context,
                        "PPT Already Joined",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        Log.d("Join PPT", "Last Log of the block")
    }

    private fun joinPptPrivate(code: String, adminId: String)
    {
        val pptRef = getPPTRef()
        val googleAuth = GoogleSignIn(this)

        pptRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataSnapshot in snapshot.children)
                {
                    Log.d("Join PPT",dataSnapshot.key.toString())
                    if(dataSnapshot.key.toString().equals(code))
                    {
                        val memberRef = getMembersRef(code)

                        val photoUrl = googleAuth.getPhotoUri()
                        val userName = googleAuth.getUserId()

                        val idRef = getIdRef(adminId)
                        idRef.child(code).setValue("")

                        Log.d("Join PPT", adminId)

                        if (userName != null)
                        {
                            memberRef.child("false" + userName.uppercase()).setValue(photoUrl)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    suspend fun exitGroup(
        context : Context,
        pptId: String,
        pptDelete : Boolean,
        removeMem : String = "")
    {



        Log.d("exitGroup", "removeMem : $removeMem")
        val googleSignIn = GoogleSignIn(context)

        val userId = googleSignIn.getUserId().toString()
        val userName = googleSignIn.getUserName().toString()

        val groupLeadRef = getGroupAdminRefFromUser(code = pptId, id = if(removeMem.isNotEmpty()) removeMem else userId)
        val pptMemRef = getMembersRef(code = pptId)

        var groupAdminExists = false
        var groupAdmin = ""
        var isAdmin = false

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


        delay(2000)

        if(isAdmin && !pptDelete)
        {
            removePPTAdmin(pptId = pptId)
            val pptRef = getPPTRef()
            pptRef.child(pptId).removeValue()
        }

        else {

            groupLeadRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("exitGroup", snapshot.childrenCount.toString())

                    for (data in snapshot.children) {
                        groupAdmin = data.key.toString()
                        groupAdminExists = true
                        Log.d("exitGroup", groupAdmin)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            delay(3000)

            if (!groupAdminExists && pptDelete && removeMem.isNotEmpty()) {
                Log.d("exitGroup", "if block entered")
                val memRef = getMembersRef(code = pptId)
                memRef.child("false${removeMem.uppercase()}").removeValue()

                val idRef = getIdRef(id = removeMem)
                idRef.child(pptId).removeValue()
            } else {
                Log.d("exitGroup", "else block entered")
                Log.d("exitGroup", groupAdmin)

                val groupAdminRef = getGroupAdminRef(code = pptId, id = groupAdmin)
                val memRef = getMembersRef(pptId)

                groupAdminRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        Log.d("exitGroup", "Children Count : ${snapshot.childrenCount}")

                        for (data in snapshot.children) {
                            Log.d("exitGroup", "inside memref snapshot")

                            Log.d("exitGroup", "Data : $data")
                            memRef.child("true${data.key.toString().uppercase()}").removeValue()
                            memRef.child("false${data.key.toString().uppercase()}")
                                .setValue(data.value.toString())

                            if (data.key.toString().lowercase().equals(removeMem) && pptDelete) {
                                val userRef = getIdRef(data.key.toString().lowercase())
                                userRef.child(pptId).removeValue()
                            } else {
                                val userRef = getIdRef(data.key.toString().lowercase())
                                userRef.child(pptId).setValue("")
                            }
                        }

                        if (pptDelete) {
                            memRef.child("false${removeMem.uppercase()}").removeValue()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

                delay(2000)

                groupAdminRef.removeValue()

                Log.d("groupAdmin", "Exit $groupAdmin")
                val topicRef = getTopicRef(pptId)
                topicRef.child(groupAdmin).removeValue()
            }
        }
    }

    private fun removePPTAdmin(pptId: String) {

        val pptMemRef = getMembersRef(code = pptId)

        pptMemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(data in snapshot.children)
                {
                    var memId = ""

                    if(data.key.toString().startsWith("true"))
                    {
                        memId = data.key?.substring(4)?.lowercase().toString()
                    }

                    else
                    {
                        memId = data.key?.substring(5)?.lowercase().toString()
                    }

                    val idRef = getIdRef(id = memId)
                    idRef.child(pptId).removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun setTopic(topic : String, pptId : String, context: Context)
    {
        val topicRef = getTopicRef(pptId)
        val userId = GoogleSignIn(context = context).getUserId()

        val currTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
        val dateTime = formatter.format(currTime)

        val pair = Pair<String,String>(dateTime.toString(),topic)

        topicRef.child(userId.toString()).setValue(pair)
    }

    fun deleteTopic(context: Context,pptId: String, groupAdmin : String)
    {
        Log.d("deleteTopic","enter")

        val topicRef = getTopicRef(pptId)

        if(!groupAdmin.equals(""))
        {
            Log.d("deleteTopic","else block")
            topicRef.child(groupAdmin).removeValue().isSuccessful

            Toast.makeText(
                context,
                "Topic Deleted Successfully",
                Toast.LENGTH_SHORT
            ).show()

        }

        else
        {
            Log.d("deleteTopic","enter")

            Toast.makeText(
                context,
                "Group Not Yet Created",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun changePPTDate(pptId: String,newDate : String)
    {
        val pptRef = getHomeRef(code = pptId)
        pptRef.child(Submission_Date_Query).setValue(newDate)
    }

    fun changePPTTitle(pptId: String, newTitle : String)
    {
        val pptRef = getHomeRef(code = pptId)
        pptRef.child(Topic_Name_Query).setValue(newTitle)
    }

    suspend fun deletePptFromHomeActivity(pptId: String, context: Context, pptDelete: Boolean, removeMem: String) : Boolean
    {
        val userId = GoogleSignIn(context).getUserId().toString()
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        Log.d("ExitGroup", pptId)

        coroutineScope.launch {
            exitGroup(context = context,pptId = pptId, pptDelete = pptDelete, removeMem = removeMem)
        }

        delay(3000)

        val idRef = getIdRef(id = userId)
        idRef.child(pptId).removeValue()

        deleteMember(pptId = pptId, userId = userId)

        return false
    }

    private fun deleteMember(pptId: String, userId: String)
    {
        val memRef = getMembersRef(code = pptId)
        memRef.child("false${userId.uppercase()}").removeValue()

        Log.d("Clicked", "DbQueries")
    }

    fun getPPTRef() : DatabaseReference
    {
        val Ref = FirebaseDatabase.getInstance().getReference(PPT_REF)
        return Ref
    }

    fun getUserRef() : DatabaseReference
    {
        val Ref = FirebaseDatabase.getInstance().getReference(USERS_REF)
        return Ref
    }

    fun getMembersRef(code: String) : DatabaseReference
    {
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$MEMBERS_DB")
        return Ref
    }

    fun getHomeRef(code: String) : DatabaseReference
    {
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$HOME_DB")
        return Ref
    }

    fun getIdRef(id : String) : DatabaseReference
    {
        Log.d("idKey","enter")
        val Ref = FirebaseDatabase.getInstance().getReference("$USERS_REF/$id")
        return Ref
    }

    fun getGroupRef(code : String) : DatabaseReference
    {
        Log.d("idKey","enter")
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$GROUPS_DB")
        return Ref
    }

    fun getTopicRef(code : String) : DatabaseReference
    {
        Log.d("idKey","enter")
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$TOPICS_DB")
        return Ref
    }

    fun getTopicAdminRef(code : String, userId : String) : DatabaseReference
    {
        Log.d("idKey","enter")
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$TOPICS_DB/$userId")
        return Ref
    }

    fun getGroupAdminRef(code : String, id : String) : DatabaseReference
    {
        Log.d("idKey","enter")
        val Ref = FirebaseDatabase.getInstance().getReference("$PPT_REF/$code/$GROUPS_DB/$id")
        return Ref
    }

    fun getGroupAdminRefFromUser(code : String, id : String) : DatabaseReference
    {
        Log.d("idKey","groupAdminRef")
        val Ref = FirebaseDatabase.getInstance().getReference("$USERS_REF/$id/$code")
        return Ref
    }
}
