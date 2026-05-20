package com.lazor.growthspace.data.model

data class SessionBooking(
    val id: String = "",
    val coachId: String = "",
    val coachName: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val date: String = "",
    val time: String = "",
    val durationMin: Int = 60,
    val status: String = "available",
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val privateNotes: String = "",
    val photoUrl: String = "",
    val coachPhotoUrl: String? = null,
    val clientPhotoUrl: String? = null
) {

    fun getDisplayName(isCurrentUserCoach: Boolean): String {
        return if (isCurrentUserCoach) {
            if (clientName.isNotBlank()) clientName else "Вільний слот"
        } else {
            coachName
        }
    }

    fun getDisplayPhoto(isCurrentUserCoach: Boolean): String {
        return if (isCurrentUserCoach) {
            clientPhotoUrl ?: "" // Коуч бачить фото клієнта
        } else {
            coachPhotoUrl ?: ""  // Клієнт бачить фото коуча
        }
    }
}