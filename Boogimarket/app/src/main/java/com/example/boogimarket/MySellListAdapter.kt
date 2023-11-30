package com.example.boogimarket

//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//
//class MySellListAdapter(private val context: Context, private val posts: ArrayList<Post>) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
////    inner class ViewHolder(private val binding: MyselllistDesignBinding) :
////        RecyclerView.ViewHolder(binding.root) {
////
////        fun bind(post: Post) {
////            binding.apply {
////                sellListTitle.text = post.title
////                sellListPrice.text = post.price
////                sellListSold.text = post.sold
////                sellListLocation.text = post.location
////            }
////        }
////    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_myselllist, parent, false)
//        return view
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.bind(posts[position])
//    }
//
//    override fun getItemCount(): Int = posts.size
//}

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.MyselllistDesignBinding
import com.squareup.picasso.Picasso

class MySellListAdapter(private val posts: ArrayList<Post>) :
    RecyclerView.Adapter<MySellListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MyselllistDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                sellListTitle.text = post.title
                sellListPrice.text = post.price
                sellListSold.text = if (post.sold == true) "판매 완료" else "거래 가능"
                sellListLocation.text = post.location

                // Picasso를 사용하여 sellListImageView에 이미지 로드
                Picasso.get().load(post.imageUrl) // 실제 이미지 URL이나 리소스로 교체하세요
                    .into(sellListImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MyselllistDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}
