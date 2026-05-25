package com.example.fortpointproperties.features.favorites.data.repository

open class FavoriteException(message: String) : Exception(message)

class FavoriteLoginRequiredException(message: String) : FavoriteException(message)

class FavoriteRoleException(message: String) : FavoriteException(message)

class FavoriteLoadException(message: String) : FavoriteException(message)
