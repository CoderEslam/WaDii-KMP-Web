package com.wadii.domain.model.auth

enum class Role(val userType: Int) {
    USER(0),  //0
    PROVIDER(1),  //1
    ADMIN(2)//2
}
