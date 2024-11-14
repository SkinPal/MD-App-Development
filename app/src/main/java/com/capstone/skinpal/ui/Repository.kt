package com.capstone.skinpal.ui


class Repository {

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            /*apiService: ApiService,
            eventDao: EventDao*/
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository()
            }.also { instance = it }
    }
}