package com.tunahanozatac.kotlininstagram.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tunahanozatac.kotlininstagram.R
import kotlinx.android.synthetic.main.activity_feed.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FeedActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var userEmailFromFB: ArrayList<String> = ArrayList()
    var userCommentFromFB: ArrayList<String> = ArrayList()
    var userImageFromFB: ArrayList<String> = ArrayList()
    var adapter: RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        getDataFromFireStore()

        // recyclerView
        val layoutManager =
            LinearLayoutManager(this)//recyclerView'e atayacagımız Linear Layout Manager
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerAdapter(userEmailFromFB, userCommentFromFB, userImageFromFB)
        recyclerView.adapter = adapter
    }

    private fun getDataFromFireStore() {
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    //Hata varsa
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Hata yoksa
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            userEmailFromFB.clear() //Yapılmaz ise aynı görseller
                            userImageFromFB.clear() //birden fazla göstermemize
                            userCommentFromFB.clear() // sebeb olabilir

                            val documents = snapshot.documents
                            for (document in documents) {
                                val commnet = document.get("comment") as String
                                val userEmail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val timestamp = document.get("date") as Timestamp
                                timestamp.toDate()
                                userEmailFromFB.add(userEmail)
                                userCommentFromFB.add(commnet)
                                userImageFromFB.add(downloadUrl)
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Secildikten sonra ne yapacagımızı yazıyoruz.
        if (item.itemId == R.id.add_post) {
            //go to Upload activity
            val intent = Intent(applicationContext, UploadActivity::class.java)
            startActivity(intent)

        } else if (item.itemId == R.id.logout) {
            mAuth.signOut()
            Toast.makeText(
                this,
                "${mAuth.currentUser?.email.toString()} Signed out",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}