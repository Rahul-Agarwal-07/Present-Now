package com.example.presentationmaker

import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.substring
import com.example.presentationmaker.data.DbQueries
import com.example.presentationmaker.data.USERS_REF
import com.example.presentationmaker.data.USER_ID_REF
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GoogleSignIn(
    private val context : Context
){
    private val tag = "GoogleSignInClient : "
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val user = FirebaseAuth.getInstance().currentUser

    fun getUserName() : String?
    {
        if(user != null) return user.displayName.toString()
        else return null
    }

    fun getUserId() : String?
    {
        if(user != null) return user.email?.substring(0, user.email!!.length-10)
        else return null
    }

    fun getPhotoUri() : String?
    {
        if(user != null) return user.photoUrl.toString()
        else return null
    }

    fun isSignedIn() : Boolean
    {
        if(firebaseAuth.currentUser != null)
        {

            println(tag + "is signed in")
            return true
        }
        else return false
    }

    suspend fun signIn() : Boolean
    {

        Log.d("SignIn","Yes")

        if(isSignedIn()) return true

        Log.d("SignIn","Yes")

        try {

            Log.d("SignIn","Yes")

            val result = getCredentialRequest()
            return handleSignIn(result)

        } catch (e : Exception)
        {
            e.printStackTrace()
            if(e is CancellationException) throw e

            Log.d("SignIn","Yes")

            println(tag + "SignIn error : ${e.message}")
            return false
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) : Boolean
    {
        val credential = result.credential

        if(
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ){

            try {

               val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                println("Client Name : ${tokenCredential.displayName}")
                println("Client Email : ${tokenCredential.id}")
                println("Client ProfilePic : ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken,null)
                val authRes = firebaseAuth.signInWithCredential(authCredential).await()

                response.value = SignInState.SignedIn

                addUser(tokenCredential.id.substring(0,tokenCredential.id.length-10))

                return authRes.user != null

            }catch (e : Exception)
            {
                println(tag + "GoogleIdTokenParsingException : ${e.message}")
                return false
            }

        }

        else{
            println(tag + "is not GoogleIdTokenCredential")
            return false
        }
    }

    private suspend fun getCredentialRequest() : GetCredentialResponse
    {

        val signInBuilder = GetSignInWithGoogleOption.Builder(serverClientId = context.getString(R.string.default_sign_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInBuilder)
            .build()

        return credentialManager.getCredential(
            context = context, request = request
        )

    }

    private fun addUser(id : String)
    {
        val dbQueries = DbQueries()
        val userRef = dbQueries.getUserRef()

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var isFound = false

                for(data in snapshot.children)
                {
                    if(data.key.toString().equals(id))
                    {
                        isFound = true
                    }
                }

                if(!isFound)
                {
                    userRef.child(id).setValue("")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Sign In error", error.message)
            }
        })
    }

    suspend fun signOut()
    {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )

        response.value = SignInState.SignedOut

        firebaseAuth.signOut()
    }
}

