package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by activityViewModels()
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.game_fragment,
            container,
            false
        )

        binding.correctButton.setOnClickListener {
            viewModel.onCorrect()
        }

        binding.skipButton.setOnClickListener {
            viewModel.onSkip()
        }

        return binding.root
    }

    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.score.observe(viewLifecycleOwner, { newScore ->
            binding.scoreText.text = newScore.toString()
        })

        viewModel.word.observe(viewLifecycleOwner, { newWord ->
            binding.wordText.text = newWord
        })

        viewModel.eventGameFinished.observe(viewLifecycleOwner, { hasFinished ->
            if (hasFinished) {
                gameFinished()
                viewModel.onGameFinishComplete()
            }
        })

        viewModel.currentTime.observe(viewLifecycleOwner, { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
        })
    }

}
