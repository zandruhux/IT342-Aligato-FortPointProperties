package com.example.fortpointproperties.features.messaging.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.messaging.data.model.ConversationDto
import com.example.fortpointproperties.features.messaging.data.model.MessagingSocketEvent
import com.example.fortpointproperties.features.messaging.data.model.MessageDto
import com.example.fortpointproperties.features.messaging.data.repository.MessagingLoginRequiredException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingLoadException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingRepository
import com.example.fortpointproperties.features.messaging.data.repository.MessagingRoleException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingSendException
import com.example.fortpointproperties.features.messaging.network.MessagingSocketClient
import com.example.fortpointproperties.features.messaging.ui.adapter.MessageAdapter
import com.example.fortpointproperties.features.messaging.ui.adapter.MessagingAvatarBinder
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch

class ConversationDetailActivity : AppCompatActivity() {

    private val messagingRepository = MessagingRepository()
    private lateinit var authApi: AuthApi
    private lateinit var progressBar: ProgressBar
    private lateinit var stateContainer: View
    private lateinit var stateMessage: TextView
    private lateinit var stateAction: Button
    private lateinit var btnBack: ImageButton
    private lateinit var ivConversationAvatar: ImageView
    private lateinit var tvConversationAvatar: TextView
    private lateinit var tvConversationName: TextView
    private lateinit var tvConversationSubtitle: TextView
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var layoutComposer: View
    private lateinit var etMessageInput: EditText
    private lateinit var btnSend: ImageButton

    private val messageAdapter by lazy { MessageAdapter(this) }
    private var socketClient: MessagingSocketClient? = null
    private var currentUser: UserResponse? = null
    private var currentConversation: ConversationDto? = null
    private var currentConversationId: Long = -1L
    private var isSending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_conversation_detail)

        currentConversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1L)
        if (currentConversationId <= 0L) {
            finish()
            return
        }

        bindViews()
        bindActions()

        if (!TokenManager.isLoggedIn()) {
            showAuthState(getString(R.string.messages_login_required))
            return
        }

        resolveCurrentUserAndLoadConversation()
    }

    override fun onStart() {
        super.onStart()
        connectSocketIfReady()
    }

    override fun onStop() {
        socketClient?.disconnect()
        super.onStop()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        ivConversationAvatar = findViewById(R.id.ivConversationAvatar)
        tvConversationAvatar = findViewById(R.id.tvConversationAvatar)
        tvConversationName = findViewById(R.id.tvConversationName)
        tvConversationSubtitle = findViewById(R.id.tvConversationSubtitle)
        listView = findViewById(R.id.lvMessages)
        emptyView = findViewById(R.id.tvEmptyState)
        layoutComposer = findViewById(R.id.layoutComposer)
        etMessageInput = findViewById(R.id.etMessageInput)
        btnSend = findViewById(R.id.btnSend)

        listView.adapter = messageAdapter
        listView.emptyView = emptyView
        btnSend.isEnabled = false
    }

    private fun bindActions() {
        btnBack.setOnClickListener { finish() }

        btnSend.setOnClickListener {
            submitMessage()
        }

        etMessageInput.doAfterTextChanged {
            btnSend.isEnabled = !it.isNullOrBlank() && !isSending
        }

        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun resolveCurrentUserAndLoadConversation() {
        showLoadingState()
        lifecycleScope.launch {
            try {
                val response = authApi.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    if (user != null) {
                        TokenManager.saveUserRole(user.role)
                        when {
                            SessionManager.isRegisteredUserRole(user.role) -> {
                                currentUser = user
                                loadConversationState()
                            }

                            SessionManager.isPrivilegedRole(user.role) -> {
                                showUnavailableState(roleUnavailableMessage(user.role))
                            }

                            else -> {
                                TokenManager.clear()
                                showAuthState(getString(R.string.messages_only_registered))
                            }
                        }
                    } else {
                        showErrorState("Unable to read the current user profile.")
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    showAuthState(getString(R.string.messages_session_expired))
                } else {
                    showErrorState("Unable to verify the current session.")
                }
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_load_messages))
            }
        }
    }

    private fun loadConversationState(showProgress: Boolean = true) {
        if (showProgress) {
            showLoadingState()
        }
        lifecycleScope.launch {
            try {
                val conversations = messagingRepository.getConversations()
                currentConversation = conversations.firstOrNull { it.id == currentConversationId }
                    ?: currentConversation?.copy(id = currentConversationId)
                    ?: ConversationDto(id = currentConversationId)
                updateHeader(currentConversation)
                loadMessagesInternal(showProgress = false)
            } catch (error: MessagingLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.messages_login_required))
            } catch (error: MessagingRoleException) {
                showUnavailableState(error.message ?: getString(R.string.messages_only_registered))
            } catch (error: MessagingLoadException) {
                showErrorState(error.message ?: getString(R.string.messages_failed_load_messages))
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_load_messages))
            }
        }
    }

    private fun loadMessagesInternal(showProgress: Boolean = true) {
        if (showProgress) {
            showLoadingState()
        }
        lifecycleScope.launch {
            try {
                val messages = messagingRepository.getMessages(currentConversationId)
                renderMessages(messages)
                connectSocketIfReady()
            } catch (error: MessagingLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.messages_login_required))
            } catch (error: MessagingRoleException) {
                showUnavailableState(error.message ?: getString(R.string.messages_only_registered))
            } catch (error: MessagingLoadException) {
                showErrorState(error.message ?: getString(R.string.messages_failed_load_messages))
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_load_messages))
            }
        }
    }

    private fun updateHeader(conversation: ConversationDto?) {
        val displayName = conversation?.let { resolveConversationDisplayName(it) } ?: "Fort Point Properties"
        val displayImage = conversation?.let { resolveConversationDisplayImage(it) }
        MessagingAvatarBinder.bind(
            context = this,
            imageView = ivConversationAvatar,
            initialsView = tvConversationAvatar,
            displayName = displayName,
            profileImageUrl = displayImage
        )
        tvConversationName.text = displayName
        tvConversationSubtitle.text = statusText(conversation?.status)
    }

    private fun resolveConversationDisplayName(conversation: ConversationDto): String {
        return conversation.assignedAgentName
            ?.takeIf { it.isNotBlank() }
            ?: if (conversation.latestMessageSenderId == conversation.assignedAgentId) {
                conversation.latestMessageSenderName?.takeIf { it.isNotBlank() }
            } else {
                null
            }
            ?: "Fort Point Properties"
    }

    private fun resolveConversationDisplayImage(conversation: ConversationDto): String? {
        return conversation.assignedAgentProfileImageUrl
            ?.takeIf { it.isNotBlank() }
            ?: if (conversation.latestMessageSenderId == conversation.assignedAgentId) {
                conversation.latestMessageSenderProfileImageUrl?.takeIf { it.isNotBlank() }
            } else {
                null
            }
    }

    private fun statusText(status: String?): String {
        return when (status?.trim()?.uppercase()) {
            "OPEN" -> "Waiting for an agent"
            "ASSIGNED" -> "Conversation in progress"
            "CLOSED" -> "Conversation closed"
            else -> getString(R.string.messages_title)
        }
    }

    private fun renderMessages(messages: List<MessageDto>) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE
        layoutComposer.visibility = View.VISIBLE
        messageAdapter.submitList(messages, currentUser?.id, currentUserDisplayName())
        btnSend.isEnabled = !etMessageInput.text.isNullOrBlank() && !isSending
        if (messages.isEmpty()) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
            listView.post {
                if (messageAdapter.count > 0) {
                    listView.setSelection(messageAdapter.count - 1)
                }
            }
        }
    }

    private fun submitMessage() {
        val content = etMessageInput.text?.toString()?.trim().orEmpty()
        if (content.isBlank()) {
            etMessageInput.error = getString(R.string.messages_message_required)
            return
        }

        if (isSending) {
            return
        }

        setSendingState(true)
        lifecycleScope.launch {
            try {
                val savedMessage = messagingRepository.sendMessage(currentConversationId, content)
                val currentMessages = MutableList(messageAdapter.count) { index ->
                    messageAdapter.getItem(index)
                }
                if (savedMessage.id == null || currentMessages.none { it.id == savedMessage.id }) {
                    currentMessages.add(savedMessage)
                }
                messageAdapter.submitList(currentMessages, currentUser?.id, currentUserDisplayName())
                etMessageInput.setText("")
                renderMessages(currentMessages)
                loadConversationState(showProgress = false)
            } catch (error: MessagingLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.messages_login_required))
            } catch (error: MessagingRoleException) {
                showUnavailableState(error.message ?: getString(R.string.messages_only_registered))
            } catch (error: MessagingSendException) {
                showErrorState(error.message ?: getString(R.string.messages_failed_send))
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_send))
            } finally {
                setSendingState(false)
            }
        }
    }

    private fun connectSocketIfReady() {
        if (currentUser == null || !TokenManager.isLoggedIn()) {
            return
        }

        if (socketClient == null) {
            socketClient = MessagingSocketClient(
                onConnectionChanged = { connected ->
                    Log.d(TAG, "Detail socket connected=$connected")
                },
                onEvent = { event ->
                    val eventConversationId = event.conversationId
                    // WebSocket is receive-only; REST reload keeps the thread aligned with backend read-state rules.
                    val refreshActiveThread = eventConversationId == currentConversationId
                        && event.isMessageUpdate()
                    Log.d(
                        TAG,
                        "Detail socket event type=${event.type} conversationId=$eventConversationId activeConversationId=$currentConversationId refresh=$refreshActiveThread"
                    )
                    if (refreshActiveThread) {
                        loadConversationState(showProgress = false)
                    }
                },
                onError = { error ->
                    Log.w(TAG, "Detail socket error: $error")
                },
                tokenProvider = { TokenManager.getAccessToken() }
            )
        }

        socketClient?.connect()
    }

    private fun setSendingState(sending: Boolean) {
        isSending = sending
        progressBar.visibility = if (sending) View.VISIBLE else View.GONE
        btnSend.isEnabled = !sending && !etMessageInput.text.isNullOrBlank()
        etMessageInput.isEnabled = !sending
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        stateContainer.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        layoutComposer.visibility = View.GONE
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        layoutComposer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Open Login"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUnavailableState(message: String) {
        TokenManager.clear()
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        layoutComposer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Open Login"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        layoutComposer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Retry"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            loadConversationState()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun currentUserDisplayName(): String {
        val user = currentUser ?: return "You"
        return MobileHeaderBinder.buildDisplayName(user.firstname, user.lastname, user.email)
    }

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> getString(R.string.messages_admin_unavailable)
            "AGENT" -> getString(R.string.messages_agent_unavailable)
            else -> getString(R.string.messages_only_registered)
        }
    }

    private fun MessagingSocketEvent.isMessageUpdate(): Boolean {
        return type.equals("NEW_MESSAGE", ignoreCase = true) || (type.isNullOrBlank() && conversationId != null)
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        private const val TAG = "MessagingSocket"
    }
}
