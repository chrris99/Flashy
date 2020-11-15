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
import hu.bme.aut.flashy.adapter.FlashcardAdapter
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.data.flashcard.Flashcard
import hu.bme.aut.flashy.data.flashcard.FlashcardDatabase
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import hu.bme.aut.flashy.fragments.NewFlashcardDialogFragment
import hu.bme.aut.flashy.helper.SwipeToDeleteCallback
import kotlin.concurrent.thread

class FlashcardActivity : AppCompatActivity(), FlashcardAdapter.FlashcardClickListener,
    NewFlashcardDialogFragment.NewFlashcardDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private lateinit var database: FlashcardDatabase

    private var collectionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        findViewById<View>(R.id.addFlashcardFab).setOnClickListener{
            NewFlashcardDialogFragment().show(
                supportFragmentManager,
                NewFlashcardDialogFragment.TAG
            )
        }

        collectionId = intent.getLongExtra("collectionId", 0)

        database = Room.databaseBuilder(
            applicationContext,
            FlashcardDatabase::class.java,
            "flashcard-list"
        ).build()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.FlashcardRecyclerView)
        adapter = FlashcardAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as FlashcardAdapter
                adapter.removeFlashcard(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadItemsInBackground() {
        thread {
            val flashcards = database.flashcardDao().getAll(collectionId)
            runOnUiThread {
                adapter.updateFlashcard(flashcards)
            }
        }
    }

    override fun onFlashcardChanged(flashcard: Flashcard) {
        thread {
            database.flashcardDao().update(flashcard)
            Log.d("MainActivity", "Flashcard update was successful")
        }
    }

    override fun onFlashcardCreated(newFlashcard: Flashcard) {
        thread {
            var newFlashcard = newFlashcard.copy(
                collectionId = this.collectionId
            )
            val newId = database.flashcardDao().insert(newFlashcard)
            newFlashcard = newFlashcard.copy(
                id = newId
            )
            Log.d("MainActivity", "${newFlashcard.id}, ${newFlashcard.collectionId}, ${newFlashcard.term}, ${newFlashcard.definition}")
            runOnUiThread {
                adapter.addFlashcard(newFlashcard)
            }
        }
    }

    override fun onFlashcardRemoved(flashcard: Flashcard) {
        thread {
            database.flashcardDao().deleteItem(flashcard)
            Log.d("MainActivity", "Flashcard was removed succesfully")
        }
    }
}