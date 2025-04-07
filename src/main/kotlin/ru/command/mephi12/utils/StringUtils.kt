package ru.command.mephi12.utils


fun String.containsAnyPath(vararg paths: String) = paths.any { Regex("$it(\\W|\$)").containsMatchIn(this) }