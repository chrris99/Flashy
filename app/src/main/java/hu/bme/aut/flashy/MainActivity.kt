package hu.bme.aut.flashy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.flashy.adapter.FlashcardCollectionAdapter
import hu.bme.aut.flashy.data.FlashcardCollection
import hu.bme.aut.flashy.data.FlashcardCollectionDatabase
import hu.bme.aut.flashy.fragments.NewFlashcardCollectionDialogFragment
import hu.bme.aut.flashy.helper.SwipeToDeleteCallback
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), FlashcardCollectionAdapter.FlashcardCollectionClickListener,
    NewFlashcardCollectionDialogFragment.NewFlashcardCollectionDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardCollectionAdapter
    private lateinit var database: FlashcardCollectionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.fab).setOnClickListener{
            NewFlashcardCollectionDialogFragment().show(
                supportFragmentManager,
                NewFlashcardCollectionDialogFragment.TAG
            )
        }
        database = Room.databaseBuilder(
            applicationContext,
            FlashcardCollectionDatabase::class.java,
            "flashcardcollection-list"
        ).build()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.MainRecyclerView)
        adapter = FlashcardCollectionAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as FlashcardCollectionAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadItemsInBackground() {
        thread {
            val flashcardCollections = database.flashcardCollectionDao().getAll()
            runOnUiThread {
                adapter.update(flashcardCollections)
            }
        }
    }

    override fun onFlashcardCollectionChanged(flashcardCollection: FlashcardCollection) {
        thread {
            database.flashcardCollectionDao().update(flashcardCollection)
            Log.d("MainActivity", "FlashcardCollection update was successful")
        }
    }

    override fun onFlashcardCollectionCreated(newFlashcardCollection: FlashcardCollection) {
        thread {
            val newId = database.flashcardCollectionDao().insert(newFlashcardCollection)
            val newFlashcardCollection = newFlashcardCollection.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newFlashcardCollection)
            }
        }
    }

    override fun onFlashcardCollectionRemoved(flashcardCollection: FlashcardCollection) {
        thread {
            database.flashcardCollectionDao().deleteItem(flashcardCollection)
            Log.d("MainActivity", "FlashcardCollection was removed succesfully")
        }
    }

}