package hu.bme.aut.flashy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.flashy.adapter.CollectionAdapter
import hu.bme.aut.flashy.data.collection.Collection
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import hu.bme.aut.flashy.helper.SwipeToDeleteCallback
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), CollectionAdapter.CollectionClickListener,
    NewCollectionDialogFragment.NewCollectionDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private lateinit var database: CollectionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.addCollectionFab).setOnClickListener{
            NewCollectionDialogFragment().show(
                supportFragmentManager,
                NewCollectionDialogFragment.TAG
            )
        }
        database = Room.databaseBuilder(
            applicationContext,
            CollectionDatabase::class.java,
            "collection-list"
        ).build()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.CollectionRecyclerView)
        adapter = CollectionAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as CollectionAdapter
                adapter.removeCollection(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadItemsInBackground() {
        thread {
            val collections = database.collectionDao().getAll()
            runOnUiThread {
                adapter.updateCollection(collections)
            }
        }
    }

    override fun onCollectionChanged(collection: Collection) {
        thread {
            database.collectionDao().update(collection)
            Log.d("MainActivity", "Collection update was successful")
        }
    }

    override fun onCollectionCreated(newCollection: Collection) {
        thread {
            val newId = database.collectionDao().insert(newCollection)
            val newCollection = newCollection.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addCollection(newCollection)
            }
        }
    }

    override fun onCollectionRemoved(collection: Collection) {
        thread {
            database.collectionDao().deleteItem(collection)
            Log.d("MainActivity", "Collection was removed succesfully")
        }
    }

}