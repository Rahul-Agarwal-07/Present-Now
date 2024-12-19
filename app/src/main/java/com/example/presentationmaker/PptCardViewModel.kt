package com.example.presentationmaker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.substring
import androidx.lifecycle.ViewModel
import com.example.presentationmaker.data.ADMIN_PARCEL
import com.example.presentationmaker.data.Admin_Name_Query
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.data.MAX_MEMBER_QUERY
import com.example.presentationmaker.data.Submission_Date_Query
import com.example.presentationmaker.data.Topic_Name_Query
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.wait
import kotlin.coroutines.resume

class PptCardViewModel(context : Context): ViewModel() {

    val response : MutableState<DataState> = mutableStateOf(DataState.Loading)

    init {
        Log.d("viewModelReturn", "Init")
        Log.d("check","ppt1")
        fetchUserData(context)
    }

    private fun fetchUserData(context: Context) {

        Log.d("viewModelReturn", "FetchUserData")

        val googleId = GoogleSignIn(context = context)
        val userId = googleId.getUserId()
        val set = mutableSetOf<String>()

        Log.d("viewModelReturn", "FetchUserData $userId")

        if(userId.equals(null))
        {
            Log.d("viewModelReturn", "FetchUserData userIdNull Block")
            fetchIdData(set)
            return
        }

        Log.d("userId",userId.toString())

        val dbQueries = DbQueries()
        val userRef = userId?.let { dbQueries.getIdRef(it) }
        var prev = 0

        userRef?.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("viewModel"," User callback  ${snapshot.key.toString()}")

                if(snapshot.childrenCount.toInt() == 0){

                    Log.d("viewModel","FetchIdCall Block")
                    fetchIdData(set)

                }

                if (prev != snapshot.childrenCount.toInt()) {

                    set.clear()
                    prev = snapshot.childrenCount.toInt()

                    Log.d("prev",prev.toString())

                    for (dataSnap in snapshot.children) {
                        Log.d("set", dataSnap.key.toString())
                        set.add(dataSnap.key.toString())
                    }

                    fetchIdData(set)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchIdData(set: MutableSet<String>)
    {
        Log.d("viewModelReturn", "FetchIdData")
        val dbQueries = DbQueries()
        val pptIdRef = dbQueries.getPPTRef()
        val list = mutableListOf<PptInfo>()
        var prev = 0

        if(set.isEmpty())
        {
            Log.d("viewModel","Exit Through Set $set")
            response.value = DataState.Success(list)
            return
        }

        response.value = DataState.Loading

        pptIdRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(rootSnap: DataSnapshot) {

                Log.d("viewModel","onDataChange")

                if (rootSnap.childrenCount.toInt() == 0 || set.isEmpty()) {

                    Log.d("viewModelReturn", "FetchIdData EmptyBlock $set")
                    response.value = DataState.Success(list)

                }

                Log.d("size", rootSnap.childrenCount.toString() + " " + prev)

                list.clear()

                if (rootSnap.childrenCount.toInt() != prev) {

                    prev = rootSnap.childrenCount.toInt()

                    for (dataSnapshot in rootSnap.children) {

                        if(set.contains(dataSnapshot.key.toString())){

                            Log.d("enter","yes")

                            var name = ""
                            var title = ""
                            var date = ""
                            var maxMem = 0

                            val pptId = dataSnapshot.key?.toInt() ?: 0
                            val homeRef = dbQueries.getHomeRef(dataSnapshot.key.toString())

                            Log.d("viewmodel","enter")

                            homeRef.addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if(list.size.toLong() != set.size.toLong()) {

                                        for (data in snapshot.children) {

                                            Log.d("viewmodel", "loaded ${list.size}")

                                            if (data.key.equals(MAX_MEMBER_QUERY)) {
                                                Log.d("enter", "key")
                                                maxMem = Integer.parseInt(data.value.toString())
                                            }

                                            else if (data.key.equals(Submission_Date_Query)) {
                                                date = data.value.toString()
                                            }

                                            else if (data.key?.substring(0, 5).equals(Admin_Name_Query))
                                            {
                                                name = data.value.toString()
                                            }

                                            else if (data.key.toString().equals(Topic_Name_Query)) {
                                                title = data.value.toString()
                                            }
                                        }

                                        list.add(
                                            PptInfo(
                                                topicTitle = title,
                                                adminName = name,
                                                pptId = pptId,
                                                date = date,
                                                maxMem = maxMem
                                            )
                                        )
                                    }

                                    if (list.size.toLong() == set.size.toLong()) {
                                        Log.d("viewmodelReturn","${list.size} ${set.size}")
                                        response.value = DataState.Success(list)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    response.value = DataState.Failure(error.message)
                                }
                            })

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = DataState.Failure(error.message)
            }
        })

        Log.d("viewModel","Last Log of the block")

        }
    }



sealed class DataState{
    class Success(val data : MutableList<PptInfo>) : DataState()
    class Failure(val msg : String) : DataState()
    object Loading : DataState()
    object Empty : DataState()
}