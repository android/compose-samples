/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.jetsurvey.Destinations.SIGN_IN_ROUTE
import com.example.compose.jetsurvey.Destinations.SIGN_UP_ROUTE
import com.example.compose.jetsurvey.Destinations.SURVEY_RESULTS_ROUTE
import com.example.compose.jetsurvey.Destinations.SURVEY_ROUTE
import com.example.compose.jetsurvey.Destinations.WELCOME_ROUTE
import com.example.compose.jetsurvey.signinsignup.SignInScreen
import com.example.compose.jetsurvey.signinsignup.SignInViewModel
import com.example.compose.jetsurvey.signinsignup.SignInViewModelFactory
import com.example.compose.jetsurvey.signinsignup.SignUpScreen
import com.example.compose.jetsurvey.signinsignup.SignUpViewModel
import com.example.compose.jetsurvey.signinsignup.SignUpViewModelFactory
import com.example.compose.jetsurvey.signinsignup.WelcomeScreen
import com.example.compose.jetsurvey.signinsignup.WelcomeViewModel
import com.example.compose.jetsurvey.signinsignup.WelcomeViewModelFactory
import com.example.compose.jetsurvey.survey.PhotoUriManager
import com.example.compose.jetsurvey.survey.SurveyResultScreen
import com.example.compose.jetsurvey.survey.SurveyRoute
import com.example.compose.jetsurvey.survey.SurveyViewModel
import com.example.compose.jetsurvey.survey.SurveyViewModelFactory

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val SIGN_UP_ROUTE = "signup/{email}"
    const val SIGN_IN_ROUTE = "signin/{email}"
    const val SURVEY_ROUTE = "survey"
    const val SURVEY_RESULTS_ROUTE = "surveyresults"
}

@Composable
fun JetsurveyNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = WELCOME_ROUTE,
    ) {
        composable(WELCOME_ROUTE) {
            val welcomeViewModel: WelcomeViewModel = viewModel(factory = WelcomeViewModelFactory())
            WelcomeScreen(
                onSignInSignUp = { email ->
                    welcomeViewModel.handleContinue(
                        email = email,
                        onNavigateToSignIn = {
                            navController.navigate("signin/$it")
                        },
                        onNavigateToSignUp = {
                            navController.navigate("signup/$it")
                        },
                    )
                },
                onSignInAsGuest = {
                    welcomeViewModel.signInAsGuest {
                        navController.navigate(SURVEY_ROUTE)
                    }
                },
            )
        }

        composable(SIGN_IN_ROUTE) {
            val signInViewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory())
            val startingEmail = it.arguments?.getString("email")
            SignInScreen(
                email = startingEmail,
                onSignInSubmitted = { email, password ->
                    signInViewModel.signIn(email, password) {
                        navController.navigate(SURVEY_ROUTE)
                    }
                },
                onSignInAsGuest = {
                    signInViewModel.signInAsGuest {
                        navController.navigate(SURVEY_ROUTE)
                    }
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(SIGN_UP_ROUTE) {
            val signUpViewModel: SignUpViewModel = viewModel(factory = SignUpViewModelFactory())
            val startingEmail = it.arguments?.getString("email")
            SignUpScreen(
                email = startingEmail,
                onSignUpSubmitted = { email, password ->
                    signUpViewModel.signUp(email, password) {
                        navController.navigate(SURVEY_ROUTE)
                    }
                },
                onSignInAsGuest = {
                    signUpViewModel.signInAsGuest {
                        navController.navigate(SURVEY_ROUTE)
                    }
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(SURVEY_ROUTE) {
            val surveyViewModel: SurveyViewModel = viewModel(
                factory = SurveyViewModelFactory(PhotoUriManager(LocalContext.current))
            )
            SurveyRoute(
                viewModel = surveyViewModel,
                onSurveyComplete = { navController.navigate(SURVEY_RESULTS_ROUTE) },
                onNavUp = navController::navigateUp,
            )
        }

        composable(SURVEY_RESULTS_ROUTE) {
            SurveyResultScreen {
                navController.popBackStack(WELCOME_ROUTE, false)
            }
        }
    }
}
