package com.example.presentationmaker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.presentationmaker.data.DbQueries
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MemberViewModel(pptId : String) : ViewModel()
{
    val response : MutableState<LoadMembers> = mutableStateOf(LoadMembers.Empty)

    init {
        fetchData(pptId)
    }

    private fun fetchData(pptId: String)
    {
        val dbQueries = DbQueries()
        val memberRef = dbQueries.getMembersRef(pptId)
        val prev = 0

        response.value = LoadMembers.Loading
        val list = mutableListOf<MemberInfo>()

        memberRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                if (snapshot.childrenCount.toInt() != prev) {

                    for (dataSnap in snapshot.children) {
                        var isAdmin = false
                        val userName = dataSnap.key.toString()
                        val photoUrl = dataSnap.value.toString()

                        if(userName.substring(0,5).equals("admin")) isAdmin = true

                        list.add(
                            MemberInfo(
                                name = userName,
                                url = photoUrl,
                                isAdmin = isAdmin
                            )
                        )

                        if(isAdmin)
                        {
                            val item = list[list.size-1]
                            list[list.size-1] = list[0]
                            list[0] = item
                        }
                    }

                    if(list.isEmpty()) response.value = LoadMembers.Empty
                    else response.value = LoadMembers.Success(list)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = LoadMembers.Failure(error.message)
            }

        })


    }
}

sealed class LoadMembers{
    class Success(val list : MutableList<MemberInfo>) : LoadMembers()
    class Failure(val msg: String) : LoadMembers()
    object Loading : LoadMembers()
    object Empty : LoadMembers()
}

data class MemberInfo(
    var name : String,
    var url : String,
    var isAdmin : Boolean
)