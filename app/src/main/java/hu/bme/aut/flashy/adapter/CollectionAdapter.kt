package hu.bme.aut.flashy.adapter

import android.content.Intent
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.flashy.FlashcardActivity
import hu.bme.aut.flashy.MainActivity
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.collection.Collection
import hu.bme.aut.flashy.fragments.EditCollectionDialogFragment
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import org.w3c.dom.Text


class CollectionAdapter(private val listener: MainActivity) :
    RecyclerView.Adapter<CollectionAdapter.CollectionHolder>() {

    private var actionMode: ActionMode? = null
    private val collections = mutableListOf<Collection>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        val collectionView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return CollectionHolder(collectionView)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {

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
                        val editCollectionDialogFragment = EditCollectionDialogFragment()
                        editCollectionDialogFragment.setCollection(holder.collection)

                        editCollectionDialogFragment.show(
                            listener.supportFragmentManager,
                            NewCollectionDialogFragment.TAG
                        )
                        holder.itemView.isSelected = false
                        mode.finish()
                        true
                    }
                    R.id.ContextMenuDelete -> {
                        removeCollection(position)
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

        val collection = collections[position]
        holder.nameTextView.text = collection.name
        holder.descriptionTextView.text = collection.description
        holder.cardCountTextView.text = listener.resources.getQuantityString(R.plurals.number_of_cards, collection.flashcardCount, collection.flashcardCount)
        holder.backgroundColor.background = getDrawable(listener.resources, getBackgroundResource(collection.color), null)

        holder.progressBar.max = collection.flashcardCount
        holder.progressBar.progress = collection.learnedFlashcardCount

        holder.collection = collection

        holder.itemView.setOnLongClickListener { view ->
            when (actionMode) {
                null -> {
                    // Start the CAB using the ActionMode.Callback defined above
                    actionMode = listener.startActionMode(actionModeCallback)
                    view.isSelected = true
                    true
                }
                else -> false
            }
        }
    }

    @DrawableRes
    private fun getBackgroundResource(color: Collection.CollectionColor) = when (color) {
        Collection.CollectionColor.YELLOW -> R.drawable.collection_item_background_yellow
        Collection.CollectionColor.BLUE -> R.drawable.collection_item_background_blue
        Collection.CollectionColor.PURPLE -> R.drawable.collection_item_background_purple
        Collection.CollectionColor.GREEN -> R.drawable.collection_item_background_green
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    fun addCollection(collection: Collection) {
        collections.add(collection)
        notifyItemInserted(collections.size - 1)
    }

    fun updateCollection(collections: List<Collection>) {
        this.collections.clear()
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun removeCollection(position: Int) {
        val collection = collections[position]
        collections.removeAt(position);
        notifyItemRemoved(position);
        Toast.makeText(listener.applicationContext,"Collection has been removed",Toast.LENGTH_SHORT).show()
        listener.onCollectionRemoved(collection)
    }

    interface CollectionClickListener {
        fun onCollectionRemoved(collection: Collection)
    }

    inner class CollectionHolder(collectionView: View) : RecyclerView.ViewHolder(collectionView){
        val nameTextView: TextView = collectionView.findViewById(R.id.CollectionName)
        val descriptionTextView: TextView = collectionView.findViewById(R.id.CollectionDescription)
        val cardCountTextView: TextView = collectionView.findViewById(R.id.CollectionFlashcardCount)
        val backgroundColor: LinearLayout = collectionView.findViewById(R.id.Collection)
        val progressBar: ProgressBar = collectionView.findViewById(R.id.CollectionStudyProgress)

        var collection: Collection? = null

        init {
            itemView.setOnClickListener{
                val flashcardListIntent = Intent(listener, FlashcardActivity::class.java)
                flashcardListIntent.putExtra("collectionId", collection!!.id)
                flashcardListIntent.putExtra("collectionName", collection!!.name)
                listener.startActivity(flashcardListIntent)
            }
        }
    }


}