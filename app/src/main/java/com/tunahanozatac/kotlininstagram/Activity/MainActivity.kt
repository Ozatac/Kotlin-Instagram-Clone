package com.tunahanozatac.kotlininstagram.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tunahanozatac.kotlininstagram.R
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val curruntUser = mAuth.currentUser //Son kullanıcı alındı.
        if (curruntUser != null) {
            Toast.makeText(
                applicationContext,
                "Welcome ${mAuth.currentUser?.email.toString()}",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signUpClicked() {
        val email = userEmailText.text.toString()
        val password = passwordText.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this, "Please enter the required fields", Toast.LENGTH_LONG).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signInClicked() {
        val email = userEmailText.text.toString()
        val password = passwordText.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this, "Please enter the required fields", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Welcome ${mAuth.currentUser?.email.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(applicationContext, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }


}
