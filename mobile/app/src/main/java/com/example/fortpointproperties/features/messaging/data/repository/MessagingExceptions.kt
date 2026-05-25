package com.example.fortpointproperties.features.messaging.data.repository

open class MessagingException(message: String) : Exception(message)

class MessagingLoginRequiredException(message: String) : MessagingException(message)

class MessagingRoleException(message: String) : MessagingException(message)

class MessagingLoadException(message: String) : MessagingException(message)

class MessagingSendException(message: String) : MessagingException(message)
