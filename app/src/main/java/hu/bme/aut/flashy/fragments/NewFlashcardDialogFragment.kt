package hu.bme.aut.flashy.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.flashcard.Flashcard

class NewFlashcardDialogFragment : DialogFragment() {

    private lateinit var termEditText: EditText
    private lateinit var definitionEditText: EditText

    interface NewFlashcardDialogListener {
        fun onFlashcardCreated(newFlashcard: Flashcard)
    }

    private lateinit var listener: NewFlashcardDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewFlashcardDialogListener
            ?: throw RuntimeException("Activity must implement the NewFlashcardDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_flashcard)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onFlashcardCreated(getFlashcard())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = termEditText.text.isNotEmpty() && definitionEditText.text.isNotEmpty()

    private fun getFlashcard() =
        Flashcard(
            id = null,
            term = termEditText.text.toString(),
            definition = definitionEditText.text.toString(),
            collectionId = 4
        )

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_flashcard, null)
        termEditText = contentView.findViewById(R.id.FlashcardTermEditText)
        definitionEditText = contentView.findViewById(R.id.FlashcardDefinitionEditText)

        return contentView
    }

    companion object {
        const val TAG = "NewFlashcardDialogFragment"
    }
}