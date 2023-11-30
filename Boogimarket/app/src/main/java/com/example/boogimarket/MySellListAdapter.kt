package com.example.boogimarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.MyselllistDesignBinding
import com.squareup.picasso.Picasso

class MySellListAdapter(private val posts: ArrayList<ProductInformation>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MySellListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(clickedItem: ProductInformation)
    }

    inner class ViewHolder(private val binding: MyselllistDesignBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(post: ProductInformation) {
            binding.apply {
                sellListTitle.text = post.title
                sellListPrice.text = post.price
                sellListSold.text = if (post.sold == true) "판매 완료" else "거래 가능"
                sellListLocation.text = post.location

                // Picasso를 사용하여 sellListImageView에 이미지 로드
                Picasso.get().load(post.imgUri)
                    .into(sellListImageView)
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemClick(posts[position])
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

