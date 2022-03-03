package js.projects.memorygame.models

import js.projects.memorygame.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {
    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var indexOfSingleCard: Int? = null
    private var numCardFlip = 0

    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map{ MemoryCard(it)}

    }

    fun flipCard(position: Int): Boolean {
        numCardFlip++
        val card = cards[position]
        //Three cases:
        // 0 cards previously flipped over => flip over the selected card
        // 1 card previously flipped over => flip over the selected card + if the image match
        // 2 cards previously flipped over => restore cards + flip over the selected card
        var foundMatch = false
        if(indexOfSingleCard == null){
            restoreCards()
            indexOfSingleCard = position
        }else{
            foundMatch = checkForMatch(indexOfSingleCard!!, position)
            indexOfSingleCard = null
        }

        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if(cards[position1].identifier != cards[position2].identifier){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card in cards){
            if(!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlip/2
    }
}
