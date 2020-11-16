package com.fintek.utils_androidx.common

import android.provider.CallLog

sealed class QueryOrder {
    internal abstract val toSortOrder: String
}

sealed class SmsQueryOrder : QueryOrder() {
    object DateDESC : SmsQueryOrder() {
        override val toSortOrder: String = "date desc"
    }

    object DateASC : SmsQueryOrder() {
        override val toSortOrder: String = "date asc"
    }
}

sealed class CallQueryOrder : QueryOrder() {

    object DateDESC : CallQueryOrder() {
        override val toSortOrder: String = "${CallLog.Calls.DATE} desc"
    }

    object DateASC : CallQueryOrder() {
        override val toSortOrder: String = "${CallLog.Calls.DATE} asc"
    }
}