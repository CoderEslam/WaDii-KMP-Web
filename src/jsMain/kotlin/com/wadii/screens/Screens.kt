package com.wadii.screens

import androidx.compose.runtime.Composable
import com.wadii.navigation.Screen
import com.wadii.pages.admin.AdminDashboardPage
import com.wadii.pages.admin.AdsPage
import com.wadii.pages.admin.ProviderRequestsPage
import com.wadii.pages.admin.ServicesPage
import com.wadii.pages.auth.LoginPage
import com.wadii.pages.auth.RegisterPage
import com.wadii.pages.provider.ProviderDashboardPage
import com.wadii.pages.provider.ProviderOffersPage
import com.wadii.pages.provider.ProviderOrdersPage
import com.wadii.pages.provider.RespondToOrderPage
import com.wadii.pages.shared.ChatPage
import com.wadii.pages.shared.NotificationsPage
import com.wadii.pages.shared.ProfilePage
import com.wadii.pages.user.HomePage
import com.wadii.pages.user.NewOrderPage
import com.wadii.pages.user.OrderDetailPage
import com.wadii.pages.user.OrdersPage
import com.wadii.pages.user.ProviderDetailPage
import com.wadii.pages.user.SavedOffersPage
import com.wadii.pages.user.SearchPage

// Auth
object LoginScreen : Screen { @Composable override fun Content() = LoginPage() }
object RegisterScreen : Screen { @Composable override fun Content() = RegisterPage() }

// User
object HomeScreen : Screen { @Composable override fun Content() = HomePage() }
object SearchScreen : Screen { @Composable override fun Content() = SearchPage() }
object OrdersScreen : Screen { @Composable override fun Content() = OrdersPage() }
object NewOrderScreen : Screen { @Composable override fun Content() = NewOrderPage() }
object SavedOffersScreen : Screen { @Composable override fun Content() = SavedOffersPage() }
data class OrderDetailScreen(val orderId: Long) : Screen { @Composable override fun Content() = OrderDetailPage(orderId) }
data class ProviderDetailScreen(val providerId: Long) : Screen { @Composable override fun Content() = ProviderDetailPage(providerId) }

// Provider
object ProviderDashboardScreen : Screen { @Composable override fun Content() = ProviderDashboardPage() }
object ProviderOrdersScreen : Screen { @Composable override fun Content() = ProviderOrdersPage() }
object ProviderOffersScreen : Screen { @Composable override fun Content() = ProviderOffersPage() }
data class RespondToOrderScreen(val orderId: Long) : Screen { @Composable override fun Content() = RespondToOrderPage(orderId) }

// Admin
object AdminDashboardScreen : Screen { @Composable override fun Content() = AdminDashboardPage() }
object ProviderRequestsScreen : Screen { @Composable override fun Content() = ProviderRequestsPage() }
object AdsScreen : Screen { @Composable override fun Content() = AdsPage() }
object ServicesScreen : Screen { @Composable override fun Content() = ServicesPage() }

// Shared
object ChatScreen : Screen { @Composable override fun Content() = ChatPage() }
object NotificationsScreen : Screen { @Composable override fun Content() = NotificationsPage() }
object ProfileScreen : Screen { @Composable override fun Content() = ProfilePage() }
