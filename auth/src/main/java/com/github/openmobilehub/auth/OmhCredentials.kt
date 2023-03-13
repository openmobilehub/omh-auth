package com.github.openmobilehub.auth

interface OmhCredentials {

    /**
     * This is a blocking async call and should never be called from the main thread. Use the
     * functional interfaces to handle failure and success scenarios of the refresh functionality.
     *
     * @param onRefreshFailure -> functional interface to handle the refresh token failure scenario.
     * The user should be logged out and a new login request should be extended.
     * @return the newly minted access token for ease of use. Do take into account that it's automatically
     * stored and accessible in the future through [accessToken]. In case of a failure, a null value
     * is returned.
     */
    fun refreshAccessToken(onRefreshFailure: OnRefreshFailure): String?

    val accessToken: String?

    fun interface OnRefreshFailure {
        fun onFailure(exception: Exception)
    }
}
