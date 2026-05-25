package com.example.fortpointproperties.features.messaging.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.messaging.data.model.ConversationDto
import com.example.fortpointproperties.features.messaging.data.model.MessagingSocketEvent
import com.example.fortpointproperties.features.messaging.data.repository.MessagingLoginRequiredException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingLoadException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingRepository
import com.example.fortpointproperties.features.messaging.data.repository.MessagingRoleException
import com.example.fortpointproperties.features.messaging.data.repository.MessagingSendException
import com.example.fortpointproperties.features.messaging.network.MessagingSocketClient
import com.example.fortpointproperties.features.messaging.ui.adapter.ConversationAdapter
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch

class ConversationsActivity : AppCompatActivity() {

    private val messagingRepository = MessagingRepository()
    private lateinit var authApi: AuthApi
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var stateContainer: View
    private lateinit var stateMessage: TextView
    private lateinit var stateAction: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnNewConversation: Button

    private var currentUser: UserResponse? = null
    private var socketClient: MessagingSocketClient? = null
    private var isLoadingConversations = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_conversations)

        bindViews()
        bindActions()

        if (!TokenManager.isLoggedIn()) {
            showAuthState(getString(R.string.messages_login_required))
            return
        }

        resolveCurrentUserAndLoadConversations()
    }

    override fun onStart() {
        super.onStart()
        connectSocketIfReady()
    }

    override fun onStop() {
        socketClient?.disconnect()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (currentUser != null && !isLoadingConversations) {
            loadConversations(showProgress = false)
        }
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        btnNewConversation = findViewById(R.id.btnNewConversation)
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.lvConversations)
        emptyView = findViewById(R.id.tvEmptyState)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)

        conversationAdapter = ConversationAdapter(
            context = this,
            onItemClick = { conversation -> openConversationDetail(conversation) }
        )

        listView.adapter = conversationAdapter
        listView.emptyView = emptyView
    }

    private fun bindActions() {
        btnBack.setOnClickListener {
            finish()
        }

        btnNewConversation.setOnClickListener {
            showNewConversationDialog()
        }

        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun resolveCurrentUserAndLoadConversations() {
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
                                loadConversations()
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
                showErrorState(error.message ?: getString(R.string.messages_failed_load_conversations))
            }
        }
    }

    private fun loadConversations(showProgress: Boolean = true) {
        if (showProgress) {
            showLoadingState()
        }
        isLoadingConversations = true
        lifecycleScope.launch {
            try {
                val conversations = messagingRepository.getConversations()
                renderConversations(conversations)
            } catch (error: MessagingLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.messages_login_required))
            } catch (error: MessagingRoleException) {
                showUnavailableState(error.message ?: getString(R.string.messages_only_registered))
            } catch (error: MessagingLoadException) {
                showErrorState(error.message ?: getString(R.string.messages_failed_load_conversations))
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_load_conversations))
            } finally {
                isLoadingConversations = false
            }
        }
    }

    private fun renderConversations(conversations: List<ConversationDto>) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE

        val currentUserId = currentUser?.id
        conversationAdapter.submitList(conversations, currentUserId)
        connectSocketIfReady()

        if (conversations.isEmpty()) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = getString(R.string.messages_no_conversations)
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }

    private fun showNewConversationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_conversation, null)
        val input = dialogView.findViewById<EditText>(R.id.etConversationMessage)
        val errorText = dialogView.findViewById<TextView>(R.id.tvConversationError)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancelConversation)
        val createButton = dialogView.findViewById<Button>(R.id.btnCreateConversation)

        input.doAfterTextChanged {
            if (errorText.visibility == View.VISIBLE && !it.isNullOrBlank()) {
                errorText.visibility = View.GONE
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            createButton.setOnClickListener {
                val content = input.text?.toString()?.trim().orEmpty()
                if (content.isBlank()) {
                    errorText.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                dialog.dismiss()
                createConversation(content)
            }
        }

        dialog.show()
    }

    private fun createConversation(content: String) {
        showLoadingState()
        lifecycleScope.launch {
            try {
                val conversation = messagingRepository.createConversation(content)
                val conversationId = conversation.id
                if (conversationId != null) {
                    openConversationDetail(conversation)
                } else {
                    loadConversations()
                }
            } catch (error: MessagingLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.messages_login_required))
            } catch (error: MessagingRoleException) {
                showUnavailableState(error.message ?: getString(R.string.messages_only_registered))
            } catch (error: MessagingSendException) {
                showErrorState(error.message ?: getString(R.string.messages_failed_start))
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: getString(R.string.messages_failed_start))
            }
        }
    }

    private fun openConversationDetail(conversation: ConversationDto) {
        val conversationId = conversation.id ?: return
        val intent = Intent(this, ConversationDetailActivity::class.java).apply {
            putExtra(ConversationDetailActivity.EXTRA_CONVERSATION_ID, conversationId)
        }
        startActivity(intent)
    }

    private fun connectSocketIfReady() {
        if (currentUser == null || !TokenManager.isLoggedIn()) {
            return
        }

        if (socketClient == null) {
            socketClient = MessagingSocketClient(
                onConnectionChanged = { connected ->
                    Log.d(TAG, "Conversations socket connected=$connected")
                },
                onEvent = { event ->
                    // Refresh from REST so previews and unread counts match the server read-state calculation.
                    val shouldReload = event.isConversationListUpdate() && !isLoadingConversations
                    Log.d(
                        TAG,
                        "Conversations socket event type=${event.type} conversationId=${event.conversationId} reload=$shouldReload"
                    )
                    if (shouldReload) {
                        loadConversations(showProgress = false)
                    }
                },
                onError = { error ->
                    Log.w(TAG, "Conversations socket error: $error")
                },
                tokenProvider = { TokenManager.getAccessToken() }
            )
        }

        socketClient?.connect()
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
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
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Retry"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            loadConversations()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> getString(R.string.messages_admin_unavailable)
            "AGENT" -> getString(R.string.messages_agent_unavailable)
            else -> getString(R.string.messages_only_registered)
        }
    }

    private fun MessagingSocketEvent.isConversationListUpdate(): Boolean {
        return conversationId != null &&
            (type.equals("NEW_MESSAGE", ignoreCase = true) || type.isNullOrBlank())
    }

    companion object {
        private const val TAG = "MessagingSocket"
    }
}
