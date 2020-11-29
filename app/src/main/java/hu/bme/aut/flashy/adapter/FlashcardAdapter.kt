package hu.bme.aut.flashy.adapter

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import hu.bme.aut.flashy.FlashcardActivity
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.flashcard.Flashcard
import hu.bme.aut.flashy.fragments.EditFlashcardDialogFragment
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment

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
                        val editFlashcardDialogFragment = EditFlashcardDialogFragment()
                        editFlashcardDialogFragment.setFlashcard(holder.flashcard)

                        editFlashcardDialogFragment.show(
                            listener.supportFragmentManager,
                            NewCollectionDialogFragment.TAG
                        )

                        holder.itemView.isSelected = false
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
                holder.itemView.isSelected = false
                actionMode = null
            }
        }

        val flashcard = flashcards[position]
        holder.card.background = ResourcesCompat.getDrawable(
            listener.resources,
            getFlashcardColor(flashcard.learned),
            null
        )

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

        holder.correctButton.setOnClickListener {
            var nextPosition = holder.adapterPosition + 1
            if (nextPosition > flashcards.lastIndex) nextPosition = 0

            listener.onFlashcardLearned(nextPosition, flashcard)

            holder.termTextView.visibility = MaterialCardView.VISIBLE
            holder.definitionTextView.visibility = MaterialCardView.GONE
            holder.correctButton.visibility = MaterialButton.GONE
            holder.incorrectButton.visibility = MaterialButton.GONE
        }

        holder.incorrectButton.setOnClickListener {
            holder.termTextView.visibility = MaterialCardView.VISIBLE
            holder.definitionTextView.visibility = MaterialCardView.GONE
            holder.correctButton.visibility = MaterialButton.GONE
            holder.incorrectButton.visibility = MaterialButton.GONE

            listener.onFlashcardNotLearned(flashcard)
        }
    }

    private fun getFlashcardColor(learned: Boolean) = when (learned) {
        true -> R.drawable.flashcard_item_background_learned
        else -> R.drawable.flashcard_item_background_not_learned
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
        notifyItemRangeChanged(position, getItemCount());
        listener.onFlashcardRemoved(flashcard)
    }

    interface FlashcardClickListener {
        fun onFlashcardRemoved(flashcard: Flashcard)
        fun onFlashcardLearned(position: Int, flashcard: Flashcard)
        fun onFlashcardNotLearned(flashcard: Flashcard)
    }

    inner class FlashcardHolder(flashcardView: View) : RecyclerView.ViewHolder(flashcardView){
        val termTextView: MaterialTextView = flashcardView.findViewById(R.id.FlashcardTermText)
        val definitionTextView: MaterialTextView = flashcardView.findViewById(R.id.FlashcardDefinitionText)
        val correctButton: MaterialButton = flashcardView.findViewById(R.id.FlashcardCorrectButton)
        val incorrectButton: MaterialButton = flashcardView.findViewById(R.id.FlashcardIncorrectButton)

        val card: LinearLayout = flashcardView.findViewById(R.id.Flashcard)

        var flashcard: Flashcard? = null

        init {
            itemView.setOnClickListener{
                if (termTextView.visibility == MaterialCardView.VISIBLE)
                {
                    termTextView.visibility = MaterialCardView.GONE
                    definitionTextView.visibility = MaterialCardView.VISIBLE
                    correctButton.visibility = MaterialButton.VISIBLE
                    incorrectButton.visibility = MaterialButton.VISIBLE
                }

                else {
                    termTextView.visibility = MaterialCardView.VISIBLE
                    definitionTextView.visibility = MaterialCardView.GONE
                    correctButton.visibility = MaterialButton.GONE
                    incorrectButton.visibility = MaterialButton.GONE
                }
            }
        }
    }
}