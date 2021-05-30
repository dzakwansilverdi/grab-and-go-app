package com.bangkit.grab_and_go_android.data.source.remote

import com.bangkit.grab_and_go_android.data.User
import com.bangkit.grab_and_go_android.data.vo.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val _currentUser = firebaseAuth.currentUser

    suspend fun getCurrentUser(): Response<User> {
        val firebaseUser = _currentUser
        if(firebaseUser != null) {
            val uid = firebaseUser.uid
            val email = firebaseUser.email
            val name = firebaseUser.displayName
            val user = User(
                uid = uid, name = name, email = email
            )
            return Response.Success(user)
        }
        return Response.Error(Exception("Null current user"))
    }

    suspend fun signUp(user: User): Response<String> {
        return withContext(ioDispatcher) {
            try {
                var email: String = user.email!!
                val password: String = user.password!!
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                _currentUser?.let {
                    it.sendEmailVerification().addOnCompleteListener { emailVerificationTask ->
                        if(!emailVerificationTask.isSuccessful) {
                            email = ""
                        }
                    }.await()
                }
                return@withContext Response.Success(email)
            } catch (e: Exception) {
                return@withContext Response.Error(e)
            }
        }
    }

    suspend fun signIn(email: String, password: String): Response<User> {
        return withContext(ioDispatcher) {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val uid = firebaseAuth.currentUser?.uid
                val user = User(
                    uid = uid,
                    email = email
                )
                return@withContext Response.Success(user)
            } catch (e: Exception) {
                return@withContext Response.Error(e)
            }

        }
    }

    fun signOut() {

    }

    fun registerUser() {

    }

    fun verifyEmail() {

    }

}