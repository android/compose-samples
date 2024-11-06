package com.example.jetnews.data.authentication

import com.example.jetnews.model.authentication.AuthResult
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthResult<FirebaseUser> =
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                AuthResult.Success(it)
            } ?: AuthResult.Error("Authentication failed")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An error occurred while logging in")
        }

    suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): AuthResult<FirebaseUser> =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates =
                    UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(name)
                        .build()
                user.updateProfile(profileUpdates).await()
                AuthResult.Success(user)
            } ?: AuthResult.Error("User creation failed")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        }

    suspend fun signInWithGoogle(credential: AuthCredential): AuthResult<FirebaseUser> =
        withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithCredential(credential).await()
                result.user?.let {
                    AuthResult.Success(it)
                } ?: AuthResult.Error("Google sign-in failed")
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Google sign-in failed")
            }
        }

    fun signOut() {
        auth.signOut()
    }
}
