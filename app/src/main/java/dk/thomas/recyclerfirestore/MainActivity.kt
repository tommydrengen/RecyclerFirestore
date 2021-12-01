package dk.thomas.recyclerfirestore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()

        //dummy data:
        for (i in 1..10){userArrayList.add(User("a"+i,"a"+i, 1+i))}

        myAdapter = MyAdapter(userArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Users").orderBy("firstName", Query.Direction.ASCENDING).
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                    ) {
                        if(error != null){
                            Log.e("Firestore error", error.message.toString())
                            return
                        }
                        for (dc: DocumentChange in value?.documentChanges!!){
                            if( dc.type == DocumentChange.Type.ADDED){
                                userArrayList.add(dc.document.toObject(User::class.java))
                            }
                        }
                        myAdapter.notifyDataSetChanged()
                    }
                })
    }
}