package art.bangmarcel.passwordlesskotlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform