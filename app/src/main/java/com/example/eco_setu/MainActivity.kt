package com.example.eco_setu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Dummy R file for placeholder resources ---
// In a real project, these would be in your res/drawable folder.
object R {
    object drawable {
        const val ic_onboarding_recycle = 0 // Placeholder
        const val ic_onboarding_map = 1     // Placeholder
        const val ic_onboarding_qr = 2      // Placeholder
        const val ic_eco_setu_logo = 3      // Placeholder
        const val ic_google_logo = 4        // Placeholder
        const val ic_apple_logo = 5         // Placeholder
        const val ic_recycle_earth = 6      // Placeholder
    }
}

// --- Dummy Theme and Colors ---
// In a real project, this would be in your ui/theme/ package.
val EcoSetuPrimary = Color(0xFF4CAF50)
val LightGreenBackground = Color(0xFFF1F8E9)
val CardPink = Color(0xFFFCE4EC)
val AccentLime = Color(0xFFF4FF81)
val CardYellow = Color(0xFFFFFDE7)
val CardBlue = Color(0xFFE3F2FD)

@Composable
fun EcoSetuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = EcoSetuPrimary,
            background = Color.White,
            surface = Color.White
        ),
        content = content
    )
}

// --- Dummy ViewModel ---
// In a real project, this would be in its own file.
data class HomeUiState(
    val earnedAmount: String = "$125.50",
    val treesSaved: String = "15",
    val wasteCollected: String = "89 kg",
    val co2Averted: String = "150 kg"
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun onQrCodeScanned() {
        // Handle QR code scan logic
        println("QR Code Scanned")
    }
}


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoSetuTheme {
                var screen by remember { mutableStateOf("splash") }
                when (screen) {
                    "splash" -> SplashScreen(onTimeout = { screen = "onboarding" })
                    "onboarding" -> OnboardingScreen(onContinue = { screen = "auth" })
                    "auth" -> AuthScreen(
                        onSignIn = { screen = "login" },
                        onSignUp = { screen = "signup" }
                    )
                    "login" -> LoginScreen(
                        onLogin = { screen = "home" },
                        onBack = { screen = "auth" }
                    )
                    "signup" -> SignUpScreen(
                        onSignUp = { screen = "home" },
                        onBack = { screen = "auth" }
                    )
                    "home" -> HomeScreen(
                        onScan = { screen = "scan" },
                        onImpact = { screen = "impact" },
                        onRewards = { screen = "rewards" }
                    )
                    "scan" -> QRScanScreen(onBack = { screen = "home" })
                    "impact" -> ImpactScreen(onBack = { screen = "home" })
                    "rewards" -> RewardsScreen(onBack = { screen = "home" })
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: (() -> Unit)? = null) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 seconds splash
        onTimeout?.invoke()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoSetuPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Eco Setu",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(androidx.compose.foundation.pager.ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(page = page)
        }

        // Pager Indicator
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) EcoSetuPrimary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }) {
                    Text("Back", color = EcoSetuPrimary)
                }
            } else {
                TextButton(onClick = onContinue) {
                    Text("Skip", color = EcoSetuPrimary)
                }
            }

            if (pagerState.currentPage < pagerState.pageCount - 1) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoSetuPrimary)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoSetuPrimary)
                ) {
                    Text("Get started")
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    // Using placeholder icons since R.drawable is not available directly
    val (icon, title, subtitle) = when (page) {
        0 -> Triple(Icons.Default.Recycling, "Join the Green Movement", "Help reduce waste and protect our planet by recycling.")
        1 -> Triple(Icons.Default.Map, "Find Drop-off Points", "Easily locate nearby recycling centers and drop-off points.")
        else -> Triple(Icons.Default.QrCodeScanner, "Smart Waste Identification", "Scan the QR code on waste items to learn how to recycle them properly.")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(250.dp),
            tint = EcoSetuPrimary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = EcoSetuPrimary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = subtitle, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun AuthScreen(onSignIn: () -> Unit, onSignUp: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(EcoSetuPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco, // Placeholder
                contentDescription = "Eco Setu Logo",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Explore the app", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Sign in or create an account to get started.", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EcoSetuPrimary)
            ) {
                Text("Sign In", modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create account", modifier = Modifier.padding(8.dp), color = EcoSetuPrimary)
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(EcoSetuPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco, // Placeholder
                contentDescription = "Eco Setu Logo",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .background(Color.White)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EcoSetuPrimary)
            ) {
                Text("Login", modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack) {
                Text("Back", color = EcoSetuPrimary)
            }
        }
    }
}

@Composable
fun SignUpScreen(onSignUp: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(EcoSetuPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco, // Placeholder
                contentDescription = "Eco Setu Logo",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .background(Color.White)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            SocialButton(
                text = "Continue with Google",
                icon = Icons.Default.AccountCircle, // Placeholder
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SocialButton(
                text = "Continue with Apple",
                icon = Icons.Default.PhoneIphone, // Placeholder
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SocialButton(
                text = "Continue with Email",
                icon = Icons.Default.Email,
                onClick = onSignUp
            )
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onBack) {
                Text("Back", color = EcoSetuPrimary)
            }
        }
    }
}

@Composable
fun SocialButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, bgColor: Color) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = Color.Black, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onImpact: () -> Unit,
    onRewards: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = LightGreenBackground,
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = EcoSetuPrimary, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Place, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                    // Spacer for the FAB
                    Spacer(modifier = Modifier.width(56.dp))
                    IconButton(onClick = onImpact) {
                        Icon(Icons.Filled.Cloud, contentDescription = "Impact", tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onRewards) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = "Store", tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScan,
                containerColor = EcoSetuPrimary,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(36.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(LightGreenBackground)
            ) {
                // Top Bar
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Eco, contentDescription = "Eco Setu Logo", tint = EcoSetuPrimary, modifier = Modifier.size(36.dp))
                        Text("Eco Setu", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = EcoSetuPrimary, modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = EcoSetuPrimary, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = EcoSetuPrimary, modifier = Modifier.size(28.dp))
                    }
                }
                // Greeting
                Text("Hi, Kartick", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = EcoSetuPrimary, modifier = Modifier.padding(start = 20.dp))
                Text("Let's contribute to our earth.", fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 20.dp, bottom = 16.dp))
                // Location Input
                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Filled.Place, contentDescription = "Location", tint = EcoSetuPrimary)
                        Text("Bengaluru, 560001", color = Color.DarkGray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                // Recycling Guide
                Text("Recycling Guide", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoSetuPrimary, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp))
                val guideItems = listOf(
                    Pair(Icons.Filled.BatteryChargingFull, "Battery"),
                    Pair(Icons.Filled.Eco, "Organic"),
                    Pair(Icons.Filled.Checkroom, "Clothes"),
                    Pair(Icons.Filled.Description, "Paper"),
                    Pair(Icons.Filled.LocalDrink, "Plastic"),
                    Pair(Icons.Filled.Devices, "E-waste")
                )
                LazyRow(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                ) {
                    items(guideItems) { (icon, label) ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(80.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(icon, contentDescription = label, tint = EcoSetuPrimary, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
                // Impact Stats Grid
                Text("Your Impact", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoSetuPrimary, modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp))
                val statCards = listOf(
                    Triple("Earned So far", uiState.earnedAmount, CardPink),
                    Triple("Trees saved", uiState.treesSaved, AccentLime),
                    Triple("Waste Collected", uiState.wasteCollected, CardYellow),
                    Triple("CO2 Averted", uiState.co2Averted, CardBlue)
                )
                val statIcons = listOf(
                    Icons.Filled.Money,
                    Icons.Filled.NaturePeople,
                    Icons.Filled.Delete,
                    Icons.Filled.Cloud
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .height(240.dp),
                    userScrollEnabled = false
                ) {
                    items(4) { i ->
                        StatCard(
                            title = statCards[i].first,
                            value = statCards[i].second,
                            icon = statIcons[i],
                            bgColor = statCards[i].third
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun QRScanScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder for QR scanner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scanner", modifier = Modifier.size(100.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Point your camera at the recycling station QR code to deposit waste.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impact of Recycle", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoSetuPrimary
                )
            )
        },
        containerColor = LightGreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EcoSetuPrimary.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("This year's Recycle", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ImpactDataCard("This Month", "10.90T")
                        ImpactDataCard("This year", "598.95T")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.Public, // Placeholder for ic_recycle_earth
                        contentDescription = "Recycle Earth",
                        modifier = Modifier.size(150.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("View chart", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ImpactDataCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = EcoSetuPrimary)
        }
    }
}

@Composable
fun RewardsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Rewards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = "Rewards", modifier = Modifier.size(120.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Redeem your points for exciting rewards!",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "[Rewards List Here]",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}