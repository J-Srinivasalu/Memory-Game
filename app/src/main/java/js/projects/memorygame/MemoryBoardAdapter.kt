package js.projects.memorygame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import js.projects.memorygame.models.BoardSize
import js.projects.memorygame.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.MemoryViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListener{
        fun onCardClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)

        val layoutParams =
            view.findViewById<CardView>(R.id.card_view).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCard

    inner class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton: ImageButton = itemView.findViewById(R.id.image_button)
        fun bind(position: Int) {
            val memoryCard = cards[position]
            imageButton.setImageResource(if (memoryCard.isFaceUp)  memoryCard.identifier else R.drawable.ic_launcher_background)

            imageButton.alpha = if(memoryCard.isMatched) .4f else 1.0f
            imageButton.setOnClickListener {
                Log.i(TAG, "$TAG: $position")
                cardClickListener.onCardClickListener(position)
            }
        }
    }
}
