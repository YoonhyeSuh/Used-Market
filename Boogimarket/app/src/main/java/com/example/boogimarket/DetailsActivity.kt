package com.example.boogimarket


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore



class DetailsActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityDetailBinding
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        // Extracting data from the intent
        val title = intent.getStringExtra("item_title")
        val location = intent.getStringExtra("item_location")
        val price = intent.getStringExtra("item_price")
        val explain = intent.getStringExtra("item_explain")
        val sold = intent.getBooleanExtra("item_sold", false)
        val userId = intent.getStringExtra("item_userId")
        // Retrieve other data as needed

        // Create an instance of the generated binding class for the layout
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewTitle.setText(title).toString()
        binding.textViewExplain.setText(explain).toString()
        binding.textViewPrice.setText(price).toString()
        binding.textViewLocation.setText(location).toString()
        if (sold) {
            binding.textViewSoldStatus.text = "팔림"
        } else {
            binding.textViewSoldStatus.text = "구매 가능"
        }

        db.collection("users")
            .whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val name = document.getString("name")
                    binding.textViewName.text = name
                }
            }




    }
}