//external fun apply_patch(document: String, patch: String): String


@JsModule("rfc6902")
@JsNonModule
external fun createPatch(oldData: String, newData: String): String

@JsModule("semver")
@JsNonModule
external fun inc(version: String, identifier: String, identifier2: String) : String


@JsModule("is-sorted")
@JsNonModule
external fun <T> sorted(a: Array<T>): Boolean