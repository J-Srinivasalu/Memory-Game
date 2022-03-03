package js.projects.memorygame

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import js.projects.memorygame.databinding.ActivityMainBinding
import js.projects.memorygame.models.BoardSize
import js.projects.memorygame.models.MemoryGame

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var boardSize: BoardSize = BoardSize.HARD
    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpTheBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.restart -> {
                if(memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game", null) {
                        setUpTheBoard()
                    }
                }else setUpTheBoard()
                return true
            }
            R.id.newSize -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        when(boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialog(resources.getString(R.string.choose_new_size), boardSizeView) {
            // set a new value of board size
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.HARD
                else -> BoardSize.HARD
            }
            setUpTheBoard()
        }
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok"){_, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpTheBoard() {

        when(boardSize){
            BoardSize.EASY -> {
                binding.moves.text = "Easy 4x2"
                binding.pairsFound.text = "Pairs: 0/4"
            }
            BoardSize.MEDIUM -> {
                binding.moves.text = "Medium 6x3"
                binding.pairsFound.text = "Pairs: 0/9"
            }
            BoardSize.HARD -> {
                binding.moves.text = "Hard 6x4"
                binding.pairsFound.text = "Pairs: 0/12"
            }
        }

        binding.pairsFound.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards,
            object : MemoryBoardAdapter.CardClickListener{
                override fun onCardClickListener(position: Int) {
                    updateGameWithFlip(position)
                }
            })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(this,boardSize.getWidth())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateGameWithFlip(position: Int) {
        //Error checking
        if(memoryGame.haveWonGame()) {
            Snackbar.make(binding.rootView, "Already found pair",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)) {
            Snackbar.make(binding.rootView, "Invalid move",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.flipCard(position)){
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            binding.pairsFound.setTextColor(color)
            val pairs = "Pairs ${memoryGame.numPairsFound}/ ${boardSize.getNumPairs()}"
            binding.pairsFound.text = pairs
            if(memoryGame.haveWonGame()) Snackbar.make(binding.rootView, "You Won!", Snackbar.LENGTH_LONG).show()
        }
        val moves = "Moves: ${memoryGame.getNumMoves()}"
        binding.moves.text = moves
        adapter.notifyDataSetChanged()
    }
}