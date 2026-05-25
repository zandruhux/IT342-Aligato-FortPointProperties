package com.example.fortpointproperties.features.properties.data.repository

open class PropertyException(message: String) : Exception(message)

class PropertyLoginRequiredException(message: String) : PropertyException(message)

class PropertyRoleException(message: String) : PropertyException(message)

class PropertyLoadException(message: String) : PropertyException(message)
