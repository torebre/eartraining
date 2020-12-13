//Some pages detailing how to get the import working
//https://kotlinlang.org/docs/reference/js-modules.html
//https://github.com/Kotlin/dukat/issues/106
//https://discuss.kotlinlang.org/t/how-to-configure-new-kotlinjs-plugin/14105

@file:JsModule("rfc6902")
@file:JsNonModule

package rfc6902

external class Operation {
    val op: String
    val from: String?
    val path: String?
    val value: String?

}

external fun createPatch(oldData: Any, newData: Any): Array<Operation>

external fun applyPatch(data: Any, patch: Array<Operation>): Array<String>