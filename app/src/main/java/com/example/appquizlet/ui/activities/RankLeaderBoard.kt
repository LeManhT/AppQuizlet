package com.example.appquizlet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.RankItemAdapter
import com.example.appquizlet.databinding.ActivityRankLeaderBoardBinding
import com.example.appquizlet.model.RankItemModel
import com.example.appquizlet.model.UserM


class RankLeaderBoard : AppCompatActivity() {
    private lateinit var binding: ActivityRankLeaderBoardBinding
    private lateinit var rankItemAdapter: RankItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgAvatar.setOnClickListener {
            val i = Intent(this, ViewImage::class.java)
            startActivity(i)
        }

        val listRankItems = mutableListOf<RankItemModel>()
        val dataRanking = UserM.getDataRanking()
        rankItemAdapter = RankItemAdapter(listRankItems, this)
        dataRanking.observe(this) {
            listRankItems.addAll(it.rankSystem.userRanking)
            binding.txtMyOrder.text = (it.currentRank + 1).toString()
            binding.txtMyPoint.text = it.currentScore.toString()
            binding.txtMyPointGain.text = it.currentScore.toString()
            binding.txtTop1Name.text = it.rankSystem.userRanking[0].userName
            binding.txtTop1Point.text = it.rankSystem.userRanking[0].score.toString()
            rankItemAdapter.notifyDataSetChanged()

            binding.rankView.setTopRanks(
                it.rankSystem.userRanking[0].userName,
                it.rankSystem.userRanking[1].userName,
                it.rankSystem.userRanking[2].userName,
                R.raw.top1_removebg_preview,
                R.raw.t2_removebg_preview,
                R.raw.t3_removebg_preview
            )
        }
        binding.rvLeaderboard.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvLeaderboard.adapter = rankItemAdapter

        binding.imgBack.setOnClickListener {
            finish()
        }
//        binding.btnViewMore.setOnClickListener {
//            rankItemAdapter.setIsExpanded()
//            if (rankItemAdapter.isExpanded) {
//                binding.btnViewMore.text = resources.getString(R.string.view_less)
//            } else {
//                binding.btnViewMore.text = resources.getString(R.string.view_more)
//            }
//        }
    }


}