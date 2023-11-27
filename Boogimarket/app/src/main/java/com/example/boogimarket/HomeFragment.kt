package com.example.boogimarket

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.ItemLayoutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerview: RecyclerView

    private var soldCheckBox: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.activity_itemlist, container, false)

        firestore = FirebaseFirestore.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview = view.findViewById(R.id.recyclerView)
        recyclerview.adapter = RecyclerViewAdapter()

        val layoutManager = LinearLayoutManager(view.context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerview.layoutManager = layoutManager

        // recyclerview 아이템 간 구분선 추가
        recyclerview.addItemDecoration(
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        )

        // 상품 등록 버튼 클릭시 WriteDialog 열기
        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            WriteDialog().show(childFragmentManager, "WriteDialog")
        }

       // 체크 박스 선택 시 판매 완료된 상품 제외
        view.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { _, isChecked ->
            soldCheckBox = isChecked
            (recyclerview.adapter as? RecyclerViewAdapter)?.soldProductFilter()
        }

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var post: ArrayList<ProductInformation> = arrayListOf()

        init {
            firestore?.collection("post")
               ?.orderBy("timestamp", Query.Direction.ASCENDING)
                ?.addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot != null) {
                        post.clear()
                        for (snapshot in querySnapshot.documents) {
                            val item = snapshot.toObject(ProductInformation::class.java)
                            post.add(item!!)
                        }
                        notifyDataSetChanged()
                    } else {
                        Log.e("Firestore", "Query snapshot is null.")
                    }
                }
        }
        fun soldProductFilter() {
            firestore?.collection("post")?.orderBy("timestamp", Query.Direction.ASCENDING)?.get()
                ?.addOnSuccessListener { querySnapshot ->
                post.clear()
                for (snapshot in querySnapshot) {
                    val item = snapshot.toObject(ProductInformation::class.java)
                    if (soldCheckBox && !item.sold) {
                        post.add(item)
                    }
                    else if(!soldCheckBox) {
                        post.add(item)
                    }
                }
                notifyDataSetChanged()
            }?.addOnFailureListener { exception ->
            }
        }


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        inner class ViewHolder(private val binding: ItemLayoutBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener {


            init {
                // Set click listener for the ViewHolder's view
                binding.root.setOnClickListener(this)
            }


            override fun onClick(view: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = post[position]

                    // Create an Intent to start the DetailsActivity
                    val intent = Intent(view.context, DetailsActivity::class.java)

                    // Pass data to the DetailsActivity using intent extras
                    intent.putExtra("item_title", clickedItem.title)
                    intent.putExtra("item_location", clickedItem.location)
                    intent.putExtra("item_price", clickedItem.price)
                    intent.putExtra("item_explain",clickedItem.explain)
                    intent.putExtra("item_sold",clickedItem.sold)
                    intent.putExtra("item_userId",clickedItem.userId)
                    intent.putExtra("item_imgUrl",clickedItem.imgUri)
                    intent.putExtra("item_documentId",clickedItem.documentID)
                    // Add other data you want to pass

                    // Start the DetailsActivity
                    view.context.startActivity(intent)
                }
            }



            fun bind(item: ProductInformation) {
                binding.listTitle.text = item.title
                binding.listLocation.text = item.location
                binding.listPrice.text = "${item.price}원"
                if(item.sold){
                    binding.listSold.setText("판매 완료")
                    binding.listSold.setTextColor(Color.parseColor("#8DB3A9CA"))

                }else{

                    binding.listSold.setText("거래 가능")
                    binding.listSold.setTextColor(Color.parseColor("#FF3700B3"))
                }

                val imageUrl = item.imgUri

                Picasso.get()
                    .load(imageUrl)
                    .into(binding.listImageView)

            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ViewHolder
            viewHolder.bind(post[position])
        }

        override fun getItemCount(): Int {
            // 아이템 수 반환
            return post.size
        }
    }
}

