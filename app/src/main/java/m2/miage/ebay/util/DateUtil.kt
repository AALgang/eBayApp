package m2.miage.ebay.util

import java.time.LocalDateTime
import java.time.ZoneOffset

class DateUtil {

    companion object {

        /**
         * Vérifie si l'enchère actuelle est disponible ou non
         * @param date : Date de début de l'enchère
         */
        fun isOfferActive(date: LocalDateTime) : Boolean {

            var response = false
            val now = LocalDateTime.now()

            // Vérifie si l'enchère est démarrée et si les 5 min sont écoulées
            if (date.toEpochSecond(ZoneOffset.UTC) <= now.toEpochSecond(ZoneOffset.UTC)
                && now.toEpochSecond(ZoneOffset.UTC) < date.toEpochSecond(ZoneOffset.UTC) + 3000000000000) {
                response = true
            }

            return response
        }
    }
}