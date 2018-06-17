@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsQualifier("mina")
package mina

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface MinaAnimation {
    var id: String
    fun duration(): Number
    fun easing(): Number
    fun speed(): Number
    fun status(): Number
    fun stop()
}
external interface AnimationDescriptor {
    var id: String
    var start: Number
    var end: Number
    var b: Number
    var s: Number
    var dur: Number
    var spd: Number
    fun get(): Number
    fun set(slave: Number): Number
    fun easing(input: Number): Number
    fun status(): Number
    fun status(newStatus: Number)
    fun speed(): Number
    fun speed(newSpeed: Number)
    fun duration(): Number
    fun duration(newDuration: Number)
    fun stop()
    fun pause()
    fun resume()
    fun update()
}
external fun backin(n: Number): Number = definedExternally
external fun backout(n: Number): Number = definedExternally
external fun bounce(n: Number): Number = definedExternally
external fun easein(n: Number): Number = definedExternally
external fun easeinout(n: Number): Number = definedExternally
external fun easeout(n: Number): Number = definedExternally
external fun elastic(n: Number): Number = definedExternally
external fun getById(id: String): AnimationDescriptor = definedExternally
external fun linear(n: Number): Number = definedExternally
external fun time(): Number = definedExternally
