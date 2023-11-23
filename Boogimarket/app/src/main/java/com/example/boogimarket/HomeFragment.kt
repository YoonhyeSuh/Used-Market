package com.example.boogimarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.ItemLayoutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var firestore: FirebaseFirestore? = null
    private lateinit var recyclerview: RecyclerView

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

        // price sorting, product sold state Add 필요.....

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var post: ArrayList<ProductInformation> = arrayListOf()

        init {
            // post 컬렉션의 변경 사항 감지 후 recyclerview 업데이트
            firestore?.collection("post")?.addSnapshotListener { querySnapshot, _ ->
                post.clear()
                for (snapshot in querySnapshot!!.documents) {
                    val item = snapshot.toObject(ProductInformation::class.java)
                    post.add(item!!)
                }
                notifyDataSetChanged()
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
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: ProductInformation) {
                binding.listTitle.text = item.title
                binding.listLocation.text = item.location
                binding.listPrice.text = "${item.price}원"

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
