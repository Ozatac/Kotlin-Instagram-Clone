package com.tunahanozatac.kotlininstagram.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tunahanozatac.kotlininstagram.R
import kotlinx.android.synthetic.main.activity_upload.*
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UploadActivity : AppCompatActivity() {

    private var selectedPicture: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun imageViewClicked() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //İzin yoksa istedik
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            //varsa galeriye gittik.
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //varsa galeriye gittik.
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPicture = data.data //Seçilen resmin Uri aldık.

            try {
                if (selectedPicture != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        uploadImageView.setImageBitmap(bitmap)


                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        uploadImageView.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadClicked() {

        //UUID -> image name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val storage = FirebaseStorage.getInstance()
        val reference = storage.reference
        val imagesReference = reference.child("images").child(imageName)

        if (selectedPicture != null) {
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener {
                //Database -- FireStore
                val uploadedPictureReference =
                    FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString() //downloadUrl verecek
                    val postMap = hashMapOf<String, Any>()
                    postMap["downloadUrl"] = downloadUrl
                    postMap["userEmail"] = mAuth.currentUser!!.email.toString()
                    postMap["comment"] = uploadCommentText.text.toString()
                    postMap["date"] = Timestamp.now()
                    db.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if (task.isComplete && task.isSuccessful) {
                            //Back
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        //Exception
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}