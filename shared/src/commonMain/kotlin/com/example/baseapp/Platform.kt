package com.example.baseapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform