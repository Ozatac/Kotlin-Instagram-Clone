package com.tunahanozatac.kotlininstagram.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tunahanozatac.kotlininstagram.R

class RecyclerAdapter(
    private val userEmailArray: ArrayList<String>,
    private val userCommentArray: ArrayList<String>,
    private val userImageArray: ArrayList<String>
) : RecyclerView.Adapter<RecyclerAdapter.PostHolder>() {

    class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
        // recycler_view_row buradaki tasarımları burada tanımlayacagız.
        //Viewları görünmleri tutan class
        var recyclerEmailText: TextView? = null
        var recyclerCommentText: TextView? = null
        var recyclerImageView: ImageView? = null

        init {
            recyclerEmailText = view.findViewById(R.id.userEmailText)
            recyclerCommentText = view.findViewById(R.id.recyclerCommentText)
            recyclerImageView = view.findViewById(R.id.recyclerImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        // tasarımları birbirie baglayacagız.
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_row, parent, false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        // Kaç tana recyclerview row olacagını söyleyecgiz.
        return userEmailArray.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        // view ların içeriginde neler olacagını tanımlayacagız
        holder.recyclerEmailText?.text = userEmailArray[position]
        holder.recyclerCommentText?.text = userCommentArray[position]
        Picasso.get().load(userImageArray[position]).into(holder.recyclerImageView)
    }
}