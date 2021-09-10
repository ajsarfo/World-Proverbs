package com.sarftec.worldproverbs.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.sarftec.worldproverbs.APP_START_COUNT
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.databinding.ActivitySplashBinding
import com.sarftec.worldproverbs.editSettings
import com.sarftec.worldproverbs.readSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class SplashActivity : BaseActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val item = SplashManager(this).getItem()
        binding.splashMainText.apply {
            setTextColor(item.textColor)
            text = item.title
            item.typeface?.let {
                typeface = it
            }
        }
        binding.splashBottomText.apply {
            setTextColor(item.textColor)
            text = item.subtitle
            item.typeface?.let {
                typeface = it
            }
        }
        binding.splashImageLayout.setBackgroundColor(item.backgroundColor)
        setStatusColor(item.backgroundColor)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            3500L
        )
    }

    private class SplashManager(private val context: Context) {

        val fonts = arrayOf("bebas.otf", "comfortaa.ttf", "dephion.otf", "limerock.ttf")

        val message = arrayOf(
            "Give a man a fish and you feed him for a day… Teach a man how to fish and you feed him for a lifetime." to "Chinese Proverb",
            "Do not look where you fell, but where you slipped." to "Liberian Proverb",
            "Don't ask God to guide your footsteps if you are not willing to move your feet." to "Portuguese Proverb",
            "God gives food to every bird, but does not throw it into the nest." to "Montenegrin Proverb",
            "Sometimes you have to be silent to be heard." to "Swiss Proverb",
            "If you dream of moving mountains tomorrow, you must start by lifting small stones today." to "Equatorial Guinean Proverb",
            "He who asks is a fool for five minutes... But he who does not ask remains a fool forever." to "Chinese Proverb",
            "Words are like spears -- Once they leave your lips they can never come back." to "Gambian Proverb",
            "Listen to what they say about others and you will know what they say about you." to "Cuban Proverb",
            "A wise man hears one word and understands two." to "Jewish Proverb",
            "Having two ears and one tongue, we should listen twice as much as we speak." to "Turkish Proverb",
            "The hunter in pursuit of an elephant does not stop to throw stones at birds." to "Ugandan Proverb",
            "No one tests the depth of the river with both feet." to "Ghanaian Proverb",
            "t's not enough to learn how to ride -- You must also learn how to fall." to "Mexican Proverb",
            "Don't insult the alligator until you've crossed the river." to "Haitian Proverb",
            "The frog does not jump in the daytime without reason." to "Nigerian Proverb",
            "The path is made by walking." to "South African Proverb",
            "Stupidity is a disease without a medicine." to "Saudi Proverb",
            "By the time the fool has learned the game, the players have dispersed." to "Guinea Bissau Proverb",
            "You can bend a young twig, but it is hard to bend an old tree." to "Laotian Proverb",
            "Those who speak aloud are boastful... Those who speak softly are unsure... Those who don’t speak are dangerous." to "Singaporean Proverb",
            "First think -- Then talk" to "Tajik Proverb",
            "He who knows much, does not speak much." to "Ethiopian Proverb",
            "If you always walk down the same path, you’ll only go where you’ve already been." to "Chadian Proverb",
            "What is said over the dead lion’s body cannot be said to him alive." to "Congolese Proverb",
            "He who doesn't look ahead, gets left behind." to "Venezuelan Proverb",
            "Only he who wanders finds new paths." to "Norwegian Proverb",
            "In a fight with a fool, it's the wise man who quits." to "Madagascar Proverb",
            "Even the fool is thought wise if he remains silent." to "Swedish Proverb",
            "By learning to obey, you will know how to command." to "Italian Proverb",
            "It is not the hen that cackles the loudest that hatches the most eggs" to "French Proverb",
            "Where people are promising you much, bring a small bag." to "Bosnian Proverb",
            "The person who recognizes his major mistakes is on the road to wisdom." to "Colombian Proverb",
            "Cocks that crow too early will quickly find themselves in the pot." to "Turkish Proverb",
            "By crawling a child learns to stand." to "African Proverb",
            "You learn how to cut down trees by cutting them down." to "Bateke proverb",
            "A fight between grasshoppers is a joy to the crow." to "Lesotho Proverb",
            "Where a woman rules, streams run uphill." to "Ethiopian proverb",
            "Two ants do not fail to pull one grasshopper." to "Tanzanian proverb",
            "Only the foolish visit the land of the cannibals." to "New Zealander Proverb",
            "A full stomach causes a bird to sing, a man to laugh." to "New Zealander Proverb",
            "An intelligent enemy is better than a stupid friend" to "Senegalese proverb",
            "Where you will sit when you are old shows where you stood in youth" to "Yoruba proverb",
            "Wisdom is like a baobab tree; no one individual can embrace it." to "Ghanaian Proverb",
            "Do not follow a person who is running away" to "Kenyan proverb"
        )

        class Item(
            val typeface: Typeface?,
            val title: String,
            val subtitle: String,
            val textColor: Int,
            val backgroundColor: Int
        )

        fun getItem(): Item {
            val split = f23727b.random().split("@")
            val typeface = Typeface.createFromAsset(context.assets, "fonts/" + fonts.random())
            val pair = message.random()
            return Item(
                typeface = typeface,
                title = "“${pair.first}”",
                subtitle = "-${pair.second}-",
                textColor = m19660d(split[1]),
                backgroundColor = m19660d(split[0])
            )
        }

        /**********************************************/
        /* renamed from: a */

        /*
         var f23726a = intArrayOf(
             R.color.color11,
             R.color.color12,
             R.color.color13,
             R.color.color14,
             R.color.color15,
             R.color.color16,
             R.color.color17,
             R.color.color18,
             R.color.color27
         )
         */


        /* renamed from: b */
        var f23727b = arrayOf(
            "#fdcd00@#26231c",
            "#1c1b21@#ffffff",
            "#3D155F@#DF678C",
            "#4831D4@#CCF381",
            "#317773@#E2D1F9",
            "#121c37@#ffa937",
            "#79bbca@#39324b",
            "#ffadb1@#202f34",
            "#373a3c@#e3b94d",
            "#e38285@#fbfdea",
            "#eebb2c@#6c2c4e",
            "#170e35@#94daef"
        )

        /* renamed from: c */
        fun m19659c(str: String): String {
            return if (str.contains("#")) str else "#$str"
        }

        /* renamed from: d */
        fun m19660d(str: String): Int {
            return Color.parseColor(m19659c(str))
        }
    }
}