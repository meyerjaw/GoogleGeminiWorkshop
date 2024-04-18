package com.meyerjaw.geminiworkshop

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meyerjaw.geminiworkshop.chatexample.ChatViewModel
import com.meyerjaw.geminiworkshop.textexample.TextOnlyViewModel
import com.meyerjaw.geminiworkshop.textimageexample.TextImageViewModel
import com.meyerjaw.geminiworkshop.ui.AppModalDrawer
import com.meyerjaw.geminiworkshop.ui.ChatScreen
import com.meyerjaw.geminiworkshop.ui.TextImageScreen
import com.meyerjaw.geminiworkshop.ui.TextOnlyScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    textOnlyViewModel: TextOnlyViewModel,
    textImageViewModel: TextImageViewModel,
    chatViewModel: ChatViewModel,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = MainDestinations.TEXT_ONLY_ROUTE,
    navActions: MainNavigationActions = remember(navController) {
        MainNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MainDestinations.TEXT_ONLY_ROUTE) {
            AppModalDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navigationActions = navActions
            ) {
                TextOnlyScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    viewModel = textOnlyViewModel
                )
            }
        }
        composable(MainDestinations.TEXT_AND_IMAGE_ROUTE) {
            AppModalDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navigationActions = navActions
            ) {
                TextImageScreen (
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    viewModel = textImageViewModel
                )
            }
        }
        composable(MainDestinations.CHAT_ROUTE) {
            AppModalDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navigationActions = navActions
            ) {
                ChatScreen (
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    viewModel = chatViewModel
                )
            }
        }
    }
}