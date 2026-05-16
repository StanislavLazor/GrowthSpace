package com.lazor.growthspace.ui.components

import android.content.Context
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

fun launchVideoCall(context: Context, userName: String) {
    try {
        val userInfo = JitsiMeetUserInfo().apply {
            displayName = userName
        }

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(URL("https://meet.ffmuc.net"))
            .setRoom("GrowthSpace_MVP_Room_2026")
            .setUserInfo(userInfo)
            .setAudioMuted(false)
            .setVideoMuted(false)
            // ВИМИКАЄМО ЛОБІ ТА ЕКРАНИ ОЧІКУВАННЯ
            .setFeatureFlag("prejoinpage.enabled", false)
            .setFeatureFlag("welcomepage.enabled", false)
            .setFeatureFlag("lobby-mode.enabled", false)
            .build()

        JitsiMeetActivity.launch(context, options)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}