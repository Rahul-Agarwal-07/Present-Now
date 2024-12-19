package com.example.presentationmaker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.presentationmaker.data.DbQueries
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class GroupCardViewModel(pptId : String) : ViewModel(){

    val response : MutableState<GroupDataState> = mutableStateOf(GroupDataState.Empty)

    init {
        fetchGroupData(pptId = pptId)
    }

    private fun fetchGroupData(pptId: String)
    {
        val dbQueries = DbQueries()
        val groupRef = dbQueries.getGroupRef(code = pptId)
        val list = mutableListOf<MutableList<String>>()

        response.value = GroupDataState.Loading

        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("CallBack", "Received")

                for(datasnap in snapshot.children)
                {
                    val listMem = mutableListOf<String>()
                    listMem.add(datasnap.key.toString().uppercase())

                    for(members in datasnap.children)
                    {
                        if(!members.key.toString().equals(listMem[0]))
                        {
                            Log.d("group",members.key.toString())
                            listMem.add(members.key.toString())
                        }
                    }

                    list.add(listMem)
                }

                response.value = GroupDataState.Success(list)

            }


            override fun onCancelled(error: DatabaseError) {
                response.value = GroupDataState.Failure(error.message)
            }

        })
    }

}

sealed class GroupDataState{
    class Success(val data : MutableList<MutableList<String>>) : GroupDataState()
    class Failure(val msg : String) : GroupDataState()
    object Loading : GroupDataState()
    object Empty : GroupDataState()
}
