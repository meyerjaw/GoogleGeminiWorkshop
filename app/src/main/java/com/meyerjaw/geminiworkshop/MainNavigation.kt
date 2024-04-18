package com.meyerjaw.geminiworkshop

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.meyerjaw.geminiworkshop.Screens.CHAT_SCREEN
import com.meyerjaw.geminiworkshop.Screens.TEXT_AND_IMAGE_SCREEN
import com.meyerjaw.geminiworkshop.Screens.TEXT_ONLY_SCREEN

/**
 * Screens used in [MainDestinations]
 */
private object Screens {
    const val TEXT_ONLY_SCREEN = "textOnly"
    const val TEXT_AND_IMAGE_SCREEN = "textAndImage"
    const val CHAT_SCREEN = "chatScreen"
}

/**
 * Arguments used in [MainDestinations]
 */
object MainDestinationArgs {

}

/**
 * Destinations used in the [MainActivity]
 */
object MainDestinations {
    const val TEXT_ONLY_ROUTE = TEXT_ONLY_SCREEN
    const val TEXT_AND_IMAGE_ROUTE = TEXT_AND_IMAGE_SCREEN
    const val CHAT_ROUTE = CHAT_SCREEN
}

/**
 * Models the navigation actions in the app
 */
class MainNavigationActions(private val navController: NavHostController) {
    fun navigateToTextOnly() {
        navController.navigate(MainDestinations.TEXT_ONLY_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToTextAndImage() {
        navController.navigate(MainDestinations.TEXT_AND_IMAGE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToChat() {
        navController.navigate(MainDestinations.CHAT_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}