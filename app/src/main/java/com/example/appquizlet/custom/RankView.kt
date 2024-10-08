package com.example.appquizlet.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.appquizlet.R
import com.example.appquizlet.model.RankResultModel
import com.google.gson.Gson

class RankView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val top1ImageView: ImageView
    private val top2ImageView: ImageView
    private val top3ImageView: ImageView

    private val top1TextView: TextView
    private val top2TextView: TextView
    private val top3TextView: TextView

    private val otherRanksLayout: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.view_rank, this, true)
        orientation = VERTICAL

        top1ImageView = findViewById(R.id.top1ImageView)
        top2ImageView = findViewById(R.id.top2ImageView)
        top3ImageView = findViewById(R.id.top3ImageView)

        top1TextView = findViewById(R.id.top1TextView)
        top2TextView = findViewById(R.id.top2TextView)
        top3TextView = findViewById(R.id.top3TextView)

        otherRanksLayout = findViewById(R.id.otherRanksLayout)
    }

    fun setTopRanks(rank1: String, rank2: String, rank3: String, img1: Int, img2: Int, img3: Int) {
        top1TextView.text = rank1
        top2TextView.text = rank2
        top3TextView.text = rank3

        top1ImageView.setImageResource(img1)
        top2ImageView.setImageResource(img2)
        top3ImageView.setImageResource(img3)
    }

    fun setOtherRanks(rankList: List<RankResultModel>) {
        otherRanksLayout.removeAllViews()
        for (i in rankList.indices) {
            val rankView = LayoutInflater.from(context)
                .inflate(R.layout.rank_leader_board_item, otherRanksLayout, false)
            val rankTextView = rankView.findViewById<TextView>(R.id.txtTopName)
            val rankPoint = rankView.findViewById<TextView>(R.id.txtTopPoint)
            val rankOrder = rankView.findViewById<TextView>(R.id.txtOrder)

            // Assuming rankList[i] provides an object that has rankSystem, currentScore, and currentRank properties
            val rank = rankList[i]
            Log.d("RANKADĐ", Gson().toJson(rank))
            rankTextView.text = rank.rankSystem.userRanking[i].userName
            rankPoint.text = rank.currentScore.toString()
            rankOrder.text = rank.currentRank.toString()

            otherRanksLayout.addView(rankView)
        }
    }
}