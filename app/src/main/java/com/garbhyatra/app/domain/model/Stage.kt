package com.garbhyatra.app.domain.model

/** Pregnancy stage selected during onboarding. */
enum class Stage(val code: String) {
    PLANNING("planning"),
    T1("t1"),
    T2("t2"),
    T3("t3");

    companion object {
        fun fromCode(code: String?): Stage =
            entries.firstOrNull { it.code == code } ?: T1
    }
}
