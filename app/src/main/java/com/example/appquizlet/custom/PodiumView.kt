package com.example.appquizlet.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.appquizlet.R
import com.example.appquizlet.model.RankResultModel

class PodiumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val imgFirst: ImageView
    private val imgSecond: ImageView
    private val imgThird: ImageView
    private val txtFirstName: TextView
    private val txtSecondName: TextView
    private val txtThirdName: TextView
    private val txtFirstScore: TextView
    private val txtSecondScore: TextView
    private val txtThirdScore: TextView
    private val txtItem1Value: TextView
    private val txtItem1Label: TextView
    private val txtItem2Value: TextView
    private val txtItem2Label: TextView
    private val txtItem3Value: TextView
    private val txtItem3Label: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_podium, this, true)
        orientation = VERTICAL

        imgFirst = findViewById(R.id.imgFirst)
        imgSecond = findViewById(R.id.imgSecond)
        imgThird = findViewById(R.id.imgThird)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtSecondName = findViewById(R.id.txtSecondName)
        txtThirdName = findViewById(R.id.txtThirdName)
        txtFirstScore = findViewById(R.id.txtFirstScore)
        txtSecondScore = findViewById(R.id.txtSecondScore)
        txtThirdScore = findViewById(R.id.txtThirdScore)
        txtItem1Value = findViewById(R.id.txtItem1Value)
        txtItem1Label = findViewById(R.id.txtItem1Label)
        txtItem2Value = findViewById(R.id.txtItem2Value)
        txtItem2Label = findViewById(R.id.txtItem2Label)
        txtItem3Value = findViewById(R.id.txtItem3Value)
        txtItem3Label = findViewById(R.id.txtItem3Label)
    }

    fun setPodiumData(
        firstUser: RankResultModel,
        secondUser: RankResultModel,
        thirdUser: RankResultModel,
        item1: Pair<String, String>,
        item2: Pair<String, String>,
        item3: Pair<String, String>
    ) {
        txtFirstName.text = firstUser.rankSystem.userRanking[0].userName
        txtSecondName.text = secondUser.rankSystem.userRanking[1].userName
        txtThirdName.text = thirdUser.rankSystem.userRanking[2].userName
        txtFirstScore.text = firstUser.rankSystem.userRanking[0].score.toString()
        txtSecondScore.text = secondUser.rankSystem.userRanking[1].score.toString()
        txtThirdScore.text = thirdUser.rankSystem.userRanking[2].score.toString()

        txtItem1Value.text = item1.first
        txtItem1Label.text = item1.second
        txtItem2Value.text = item2.first
        txtItem2Label.text = item2.second
        txtItem3Value.text = item3.first
        txtItem3Label.text = item3.second

//        imgFirst.setImageResource(firstUser.imageRes)
//        imgSecond.setImageResource(secondUser.imageRes)
//        imgThird.setImageResource(thirdUser.imageRes)
    }

//    data class UserRank(
//        val name: String,
//        val score: String,
//        val imageRes: Int
//    )
}
