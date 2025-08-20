package com.flashlight.flashalert.oncall.sms.features.intro.model

import com.flashlight.flashalert.oncall.sms.R

object Intro {
    val intros = listOf(
        IntroItem(0, R.drawable.img_intro_1, R.string.intro_title_1, R.string.intro_extra_title_1),
        IntroItem(1, R.drawable.img_intro_2, R.string.intro_title_2, R.string.intro_extra_title_2),
        IntroItem(2, R.drawable.img_intro_3, R.string.intro_title_3, R.string.intro_extra_title_3),
    )
}

data class IntroItem(
    val index: Int,
    val image: Int,
    val title: Int,
    val text: Int,
)