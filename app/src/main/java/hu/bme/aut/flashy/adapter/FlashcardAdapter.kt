package hu.bme.aut.flashy.adapter

import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.flashy.FlashcardActivity
import hu.bme.aut.flashy.MainActivity
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.flashcard.Flashcard

class FlashcardAdapter(private val listener: FlashcardActivity) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardHolder>() {

    private var actionMode: ActionMode? = null
    private val flashcards = mutableListOf<Flashcard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardHolder {
        val flashcardView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.flashcard_item, parent, false)
        return FlashcardHolder(flashcardView)
    }

    override fun onBindViewHolder(holder: FlashcardHolder, position: Int) {

        val actionModeCallback = object : ActionMode.Callback {
            // Called when the action mode is created; startActionMode() was called
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.context_menu, menu)
                return true
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.ContextMenuEdit -> {
                        Toast.makeText(listener, "Edit selected", Toast.LENGTH_SHORT).show()
                        mode.finish() // Action picked, so close the CAB
                        true
                    }
                    R.id.ContextMenuDelete -> {
                        removeFlashcard(position)
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }

            // Called when the user exits the action mode
            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
            }
        }

        val flashcard = flashcards[position]
        holder.termTextView.text = flashcard.term
        holder.definitionTextView.text = flashcard.definition

        holder.flashcard = flashcard

        holder.itemView.setOnLongClickListener { view ->
            when (actionMode) {
                null -> {
                    // Start the CAB using the ActionMode.Callback defined above
                    actionMode = listener?.startActionMode(actionModeCallback)
                    view.isSelected = true
                    true
                }
                else -> false
            }
        }
    }

    override fun getItemCount(): Int {
        return flashcards.size
    }

    fun addFlashcard(flashcard: Flashcard) {
        flashcards.add(flashcard)
        notifyItemInserted(flashcards.size - 1)
    }

    fun updateFlashcard(flashcards: List<Flashcard>) {
        this.flashcards.clear()
        this.flashcards.addAll(flashcards)
        notifyDataSetChanged()
    }

    fun removeFlashcard(position: Int) {
        val flashcard = flashcards[position]
        flashcards.removeAt(position);
        notifyItemRemoved(position);
        listener.onFlashcardRemoved(flashcard)
    }

    interface FlashcardClickListener {
        fun onFlashcardChanged(flashcard: Flashcard)
        fun onFlashcardRemoved(flashcard: Flashcard)
    }

    inner class FlashcardHolder(flashcardView: View) : RecyclerView.ViewHolder(flashcardView){
        val termTextView: TextView = flashcardView.findViewById(R.id.FlashcardTerm)
        val definitionTextView: TextView = flashcardView.findViewById(R.id.FlashcardDefinition)

        var flashcard: Flashcard? = null

        init {
            itemView.setOnClickListener{
                if (termTextView.visibility == TextView.VISIBLE)
                {
                    termTextView.visibility = TextView.GONE
                    definitionTextView.visibility = TextView.VISIBLE
                }

                else {
                    termTextView.visibility = TextView.VISIBLE
                    definitionTextView.visibility = TextView.GONE
                }

            }
        }
    }
}