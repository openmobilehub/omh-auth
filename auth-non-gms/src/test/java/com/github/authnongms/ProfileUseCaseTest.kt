package com.github.authnongms

import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.domain.user.UserRepository
import com.github.openmobilehub.auth.OmhUserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class ProfileUseCaseTest {

    private val userRepository: UserRepository = mock()
    private val useCase = ProfileUseCase(userRepository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when idToken and clientId are provided the token is handled`() = runTest {
        val idToken = "idToken"
        val clientId = "clientId"

        useCase.resolveIdToken(idToken, clientId)

        verify(userRepository).handleIdToken(idToken, clientId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when idToken or clientId are empty the token is not handled`() = runTest {
        val idToken = " "
        val clientId = " "

        useCase.resolveIdToken(idToken, clientId)

        verifyNoInteractions(userRepository)
    }

    @Test
    fun `when no profile data is available a null is returned`() {
        whenever(userRepository.getProfileData()).doReturn(null)

        val result = useCase.getProfileData()

        assertNull(result)
    }

    @Test
    fun `when profile data is available an object is returned`() {
        val profileData: OmhUserProfile = mock()
        whenever(userRepository.getProfileData()).doReturn(profileData)

        val result = useCase.getProfileData()

        assertEquals(result, profileData)
    }
}
