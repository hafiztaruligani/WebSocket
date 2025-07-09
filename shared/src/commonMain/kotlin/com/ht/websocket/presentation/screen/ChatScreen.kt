package com.ht.websocket.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ht.websocket.data.chat.api.model.Message
import com.ht.websocket.presentation.ChatScreenRoute
import com.ht.websocket.presentation.MyColor
import com.ht.websocket.presentation.PopBackStackRoute
import com.ht.websocket.presentation.Route
import com.ht.websocket.presentation.screen.chat.ChatAction
import com.ht.websocket.presentation.screen.chat.ChatUiState
import com.ht.websocket.presentation.screen.chat.ChatViewModel
import com.ht.websocket.util.Platform
import com.ht.websocket.util.getPlatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import websocket.shared.generated.resources.Res
import websocket.shared.generated.resources.ic_add
import websocket.shared.generated.resources.ic_admin_check
import websocket.shared.generated.resources.ic_left_arrow
import websocket.shared.generated.resources.ic_send
import websocket.shared.generated.resources.logo


private val platform: Platform = getPlatform()

fun NavGraphBuilder.chatScreenRoute(navigate: (Route) -> Unit) {
    composable<ChatScreenRoute> {
        val viewModel = koinViewModel<ChatViewModel>()
        val uiState = viewModel.uiState.collectAsState()
        ChatScreen(navigate, uiState.value, viewModel::action)
    }
}


@Composable
fun ChatScreen(
    navigate: (Route) -> Unit,
    uiState: ChatUiState,
    action: (ChatAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.displayCutout,
        topBar = {
            Row(
                modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_left_arrow),
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        navigate(PopBackStackRoute)
                    }
                )

                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    "Admin Akunmu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = vectorResource(Res.drawable.ic_admin_check),
                    contentDescription = "",
                    tint = MyColor.Blue
                )

                Text("${uiState.connectionStatus}")

            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ChatContainer(modifier = Modifier.weight(1f), uiState)

            InputMessage(modifier = Modifier, uiState, action)
        }
    }

}

@Composable
fun InputMessage(modifier: Modifier.Companion, uiState: ChatUiState, action: (ChatAction) -> Unit) {
    Row(
        modifier = modifier.padding(16.dp).windowInsetsPadding(WindowInsets.navigationBars),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_add),
            contentDescription = "",
            modifier = Modifier.size(40.dp)
        )

        val keyboardController = LocalSoftwareKeyboardController.current
        BasicTextField(
            uiState.inputText,
            {
                action(ChatAction.InputTextChanged(it))
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = MyColor.Black
            ),
            modifier = Modifier.weight(1f).heightIn(min = 40.dp)
                .background(MyColor.Grey1, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Image(
            imageVector = vectorResource(Res.drawable.ic_send),
            contentDescription = "",
            modifier = Modifier.size(40.dp).clickable {
                keyboardController?.hide()
                action(ChatAction.SendMessage)
            }
        )
    }
}

@Composable
private fun ChatContainer(modifier: Modifier, uiState: ChatUiState) {
    val listState = rememberLazyListState()
    val size = uiState.chat.messages.size
    LaunchedEffect(size) {
        if (size>1)
        listState.animateScrollToItem(index = uiState.chat.messages.lastIndex)
    }
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        state = listState
    ) {

        items(uiState.chat.messages, key = {it.id}) {
            MessageBubble(
                it,
                Modifier
            )
        }

    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val arrangement: Arrangement.Horizontal = when(message.userType) {
        Message.UserType.Client -> Arrangement.End
        Message.UserType.Server -> Arrangement.Start
    }
    val fontColor = when(message.userType) {
        Message.UserType.Client -> MaterialTheme.colorScheme.background
        Message.UserType.Server -> MyColor.Black
    }

    val background = when(message.userType) {
        Message.UserType.Client -> MyColor.Blue
        Message.UserType.Server -> MyColor.White
    }

    Box(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = arrangement
        ) {

            if (message.userType is Message.UserType.Client) Spacer(Modifier.fillMaxWidth(0.2f))
            Box(modifier = Modifier.background(color = background, shape = RoundedCornerShape(16.dp))) {

                Column(
                    modifier = Modifier.padding(12.dp).zIndex(1f).widthIn(min = 100.dp)
                ) {
                    Text(
                        text = message.text,
                        color = fontColor,
                        fontSize = 12.sp,
                        modifier = Modifier
                    )
                    Text(
                        text = message.time,
                        color = fontColor,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End),
                    )

                }
            }
            if (message.userType is Message.UserType.Server) Spacer(Modifier.fillMaxWidth(0.2f))
        }
    }
}

private val messagesSample = listOf(
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),
    Message(
        "1",
        text = "laskdjhfkjashdflksajdjlf sadjkf lsjd flkjsad fkjsdf",
        userType = Message.UserType.Client
    ),
    Message(
        "1",
        text = """
                            Hai kak! Ini detail akun Netflix yang bisa kakak gunakan ya 👇
                            🎬 Layanan: Netflix  📧 Email: netflix.share@email.com  🔑 Password: akunmu1234
                            🔁 Mohon jangan mengubah email atau password, agar semua anggota grup tetap bisa mengakses ya, kak.
                            Kalau ada kendala login atau butuh bantuan lainnya, silakan kabari kami kapan saja 😊
                        """.trimIndent(),
        userType = Message.UserType.Server
    ),

    )


