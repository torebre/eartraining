@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsQualifier("Snap")
package Snap

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

external var filter: Filter = definedExternally
external var path: Path = definedExternally
external fun Matrix(): Unit = definedExternally
external fun matrix(): Matrix = definedExternally
external fun matrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number): Matrix = definedExternally
//external fun matrix(svgMatrix: SVGMatrix): Matrix = definedExternally
external fun ajax(url: String, postData: String, callback: Function<*>, scope: Any? = definedExternally /* null */): XMLHttpRequest = definedExternally
external fun ajax(url: String, postData: Any, callback: Function<*>, scope: Any? = definedExternally /* null */): XMLHttpRequest = definedExternally
external fun ajax(url: String, callback: Function<*>, scope: Any? = definedExternally /* null */): XMLHttpRequest = definedExternally
external fun format(token: String, json: Any): String = definedExternally
external fun fragment(varargs: Any): Fragment = definedExternally
external fun getElementByPoint(x: Number, y: Number): Element = definedExternally
external fun `is`(o: Any, type: String): Boolean = definedExternally
external fun load(url: String, callback: Function<*>, scope: Any? = definedExternally /* null */): Unit = definedExternally
external fun plugin(f: Function<*>): Unit = definedExternally
external fun select(query: String): Element = definedExternally
external fun selectAll(query: String): Any = definedExternally
external fun snapTo(values: Array<Number>, value: Number, tolerance: Number? = definedExternally /* null */): Number = definedExternally
external fun animate(from: Number, to: Number, updater: (n: Number) -> Unit, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): mina.MinaAnimation = definedExternally
external fun animate(from: Number, to: Array<Number>, updater: (n: Number) -> Unit, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): mina.MinaAnimation = definedExternally
external fun animate(from: Array<Number>, to: Number, updater: (n: Number) -> Unit, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): mina.MinaAnimation = definedExternally
external fun animate(from: Array<Number>, to: Array<Number>, updater: (n: Number) -> Unit, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): mina.MinaAnimation = definedExternally
external fun animation(attr: Any, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Animation = definedExternally
external fun color(clr: String): RGBHSB = definedExternally
external fun getRGB(color: String): RGB = definedExternally
external fun hsb(h: Number, s: Number, b: Number): HSB = definedExternally
external fun hsl(h: Number, s: Number, l: Number): HSL = definedExternally
external fun rgb(r: Number, g: Number, b: Number): RGB = definedExternally
external fun hsb2rgb(h: Number, s: Number, v: Number): RGB = definedExternally
external fun hsl2rgb(h: Number, s: Number, l: Number): RGB = definedExternally
external fun rgb2hsb(r: Number, g: Number, b: Number): HSB = definedExternally
external fun rgb2hsl(r: Number, g: Number, b: Number): HSL = definedExternally
external fun angle(x1: Number, y1: Number, x2: Number, y2: Number, x3: Number? = definedExternally /* null */, y3: Number? = definedExternally /* null */): Number = definedExternally
external fun rad(deg: Number): Number = definedExternally
external fun deg(rad: Number): Number = definedExternally
external fun sin(angle: Number): Number = definedExternally
external fun cos(angle: Number): Number = definedExternally
external fun tan(angle: Number): Number = definedExternally
external fun asin(angle: Number): Number = definedExternally
external fun acos(angle: Number): Number = definedExternally
external fun atan(angle: Number): Number = definedExternally
external fun atan2(angle: Number): Number = definedExternally
external fun len(x1: Number, y1: Number, x2: Number, y2: Number): Number = definedExternally
external fun len2(x1: Number, y1: Number, x2: Number, y2: Number): Number = definedExternally
external fun parse(svg: String): Fragment = definedExternally
external fun parsePathString(pathString: String): Array<Any> = definedExternally
external fun parsePathString(pathString: Array<String>): Array<Any> = definedExternally
external fun parseTransformString(TString: String): Array<Any> = definedExternally
external fun parseTransformString(TString: Array<String>): Array<Any> = definedExternally
external fun closest(x: Number, y: Number, X: Number, Y: Number): Boolean = definedExternally
external interface RGB {
    var r: Number
    var g: Number
    var b: Number
    var hex: String
}
external interface HSB {
    var h: Number
    var s: Number
    var b: Number
}
external interface RGBHSB {
    var r: Number
    var g: Number
    var b: Number
    var hex: String
    var error: Boolean
    var h: Number
    var s: Number
    var v: Number
    var l: Number
}
external interface HSL {
    var h: Number
    var s: Number
    var l: Number
}
external interface BBox {
    var cx: Number
    var cy: Number
    var h: Number
    var height: Number
    var path: Number
    var r0: Number
    var r1: Number
    var r2: Number
    var vb: String
    var w: Number
    var width: Number
    var x2: Number
    var x: Number
    var y2: Number
    var y: Number
}
external interface TransformationDescriptor {
    var string: String
    var globalMatrix: Matrix
    var localMatrix: Matrix
    var diffMatrix: Matrix
    var global: String
    var local: String
    override fun toString(): String
}
external interface `T$0` {
    @nativeGetter
    operator fun get(attr: String): dynamic /* String | Number | Boolean | Any */
    @nativeSetter
    operator fun set(attr: String, value: String)
    @nativeSetter
    operator fun set(attr: String, value: Number)
    @nativeSetter
    operator fun set(attr: String, value: Boolean)
    @nativeSetter
    operator fun set(attr: String, value: Any)
}
external interface Animation {
    var attr: `T$0`
    var duration: Number
    var easing: ((num: Number) -> Number)? get() = definedExternally; set(value) = definedExternally
    var callback: (() -> Unit)? get() = definedExternally; set(value) = definedExternally
}
external interface `T$1` {
    @nativeGetter
    operator fun get(attr: String): dynamic /* String | Number | Boolean | BBox | Any */
    @nativeSetter
    operator fun set(attr: String, value: String)
    @nativeSetter
    operator fun set(attr: String, value: Number)
    @nativeSetter
    operator fun set(attr: String, value: Boolean)
    @nativeSetter
    operator fun set(attr: String, value: BBox)
    @nativeSetter
    operator fun set(attr: String, value: Any)
}
external interface `T$2` {
    var x: Number
    var y: Number
    var alpha: Number
}
external interface `T$3` {
    var anim: Animation
    var mina: mina.AnimationDescriptor
    var curStatus: Number
    var status: (n: Number? /*= null*/) -> Number
    var stop: () -> Unit
}
external interface Element {
    fun add(el: Element): Element
    fun add(el: Set): Element
    fun addClass(value: String): Element
    fun after(el: Element): Element
    fun align(el: Element, way: String): Element
    fun animate(animation: Any): Element
    fun animate(attrs: `T$0`, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Element
    fun append(el: Element): Element
    fun append(el: Set): Element
    fun appendTo(el: Element): Element
    fun asPX(attr: String, value: String? = definedExternally /* null */): Number
    fun attr(param: String /* "viewBox" */): BBox
    fun attr(param: String): String
    fun attr(params: `T$1`): Element
    fun before(el: Element): Element
    fun children(): Array<Element>
    fun clone(): Element
    fun data(key: String, value: Any? = definedExternally /* null */): Any
    fun getAlign(el: Element, way: String): String
    fun getBBox(): BBox
    fun getPointAtLength(length: Number): `T$2`
    fun getSubpath(from: Number, to: Number): String
    fun getTotalLength(): Number
    fun hasClass(value: String): Boolean
    fun inAnim(): Array<`T$3`>
    fun innerSVG(): String
    fun insertAfter(el: Element): Element
    fun insertBefore(el: Element): Element
    fun marker(x: Number, y: Number, width: Number, height: Number, refX: Number, refY: Number): Element
    var node: HTMLElement
    fun outerSVG(): String
    var paper: Paper? get() = definedExternally; set(value) = definedExternally
    fun parent(): Element
    fun pattern(x: Any, y: Any, width: Any, height: Any): Element
    fun prepend(el: Element): Element
    fun prependTo(el: Element): Element
    fun remove(): Element
    fun removeClass(value: String): Element
    fun removeData(key: String? = definedExternally /* null */): Element
    fun select(query: String): Element
    fun stop(): Element
    fun toDefs(): Element
    fun toJSON(): Any
    fun toggleClass(value: String, flag: Boolean): Element
    fun toPattern(x: Number, y: Number, width: Number, height: Number): Any
    fun toPattern(x: String, y: String, width: String, height: String): Any
    override fun toString(): String
    fun transform(): TransformationDescriptor
    fun transform(tstr: String): Element
    var type: String
    fun use(): Any
    fun selectAll(): Set
    fun selectAll(query: String): Set
    fun click(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun dblclick(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun mousedown(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun mousemove(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun mouseout(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun mouseover(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun mouseup(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun touchstart(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun touchmove(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun touchend(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun touchcancel(handler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun unclick(handler: ((event: MouseEvent) -> Unit)? = definedExternally /* null */): Element
    fun undblclick(handler: (event: MouseEvent) -> Unit): Element
    fun unmousedown(handler: (event: MouseEvent) -> Unit): Element
    fun unmousemove(handler: (event: MouseEvent) -> Unit): Element
    fun unmouseout(handler: (event: MouseEvent) -> Unit): Element
    fun unmouseover(handler: (event: MouseEvent) -> Unit): Element
    fun unmouseup(handler: (event: MouseEvent) -> Unit): Element
    fun untouchstart(handler: (event: MouseEvent) -> Unit): Element
    fun untouchmove(handler: (event: MouseEvent) -> Unit): Element
    fun untouchend(handler: (event: MouseEvent) -> Unit): Element
    fun untouchcancel(handler: (event: MouseEvent) -> Unit): Element
    fun hover(hoverInHandler: (event: MouseEvent) -> Unit, hoverOutHandler: (event: MouseEvent) -> Unit, thisArg: Any? = definedExternally /* null */): Element
    fun hover(hoverInHandler: (event: MouseEvent) -> Unit, hoverOutHandler: (event: MouseEvent) -> Unit, inThisArg: Any? = definedExternally /* null */, outThisArg: Any? = definedExternally /* null */): Element
    fun unhover(hoverInHandler: (event: MouseEvent) -> Unit, hoverOutHandler: (event: MouseEvent) -> Unit): Element
    fun drag(): Element
    fun drag(onMove: (dx: Number, dy: Number, x: Number, y: Number, event: MouseEvent) -> Unit, onStart: (x: Number, y: Number, event: MouseEvent) -> Unit, onEnd: (event: MouseEvent) -> Unit, moveThisArg: Any? = definedExternally /* null */, startThisArg: Any? = definedExternally /* null */, endThisArg: Any? = definedExternally /* null */): Element
    fun undrag(onMove: (dx: Number, dy: Number, event: MouseEvent) -> Unit, onStart: (x: Number, y: Number, event: MouseEvent) -> Unit, onEnd: (event: MouseEvent) -> Unit): Element
    fun undrag(): Element
    fun hover(hoverInHandler: (event: MouseEvent) -> Unit, hoverOutHandler: (event: MouseEvent) -> Unit): Element
}
external interface Gradient : Element {
    var stops: () -> Array<Element>
    var addStop: (color: String, offset: Number) -> Gradient
    var setStops: (str: String) -> Gradient
}
external interface Fragment {
    fun select(query: String): Element
    fun selectAll(query: String? = definedExternally /* null */): Set
}
external interface Matrix {
    fun add(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number): Matrix
    fun add(matrix: Matrix): Matrix
    fun clone(): Matrix
    fun determinant(): Number
    fun invert(): Matrix
    fun rotate(a: Number, x: Number? = definedExternally /* null */, y: Number? = definedExternally /* null */): Matrix
    fun scale(x: Number, y: Number? = definedExternally /* null */, cx: Number? = definedExternally /* null */, cy: Number? = definedExternally /* null */): Matrix
    fun split(): ExplicitTransform
    fun toTransformString(): String
    fun translate(x: Number, y: Number): Matrix
    fun x(x: Number, y: Number): Number
    fun y(x: Number, y: Number): Number
}
external interface ExplicitTransform {
    var dx: Number
    var dy: Number
    var scalex: Number
    var scaley: Number
    var shear: Number
    var rotate: Number
    var isSimple: Boolean
}
external interface Paper : Element {
    fun clear()
    fun el(name: String, attr: Any): Element
    fun filter(filstr: String): Element
    fun gradient(gradient: String): Gradient
    fun g(varargs: Any? = definedExternally /* null */): Paper
    fun group(vararg els: Any): Paper
    fun mask(varargs: Any): Any
    fun ptrn(x: Number, y: Number, width: Number, height: Number, vbx: Number, vby: Number, vbw: Number, vbh: Number): Any
    fun svg(x: Number, y: Number, width: Number, height: Number, vbx: Number, vby: Number, vbw: Number, vbh: Number): Any
    fun toDataUrl(): String
    override fun toString(): String
    fun use(id: String? = definedExternally /* null */): Any
    fun use(id: Element? = definedExternally /* null */): Any
    fun circle(x: Number, y: Number, r: Number): Element
    fun ellipse(x: Number, y: Number, rx: Number, ry: Number): Element
    fun image(src: String, x: Number, y: Number, width: Number, height: Number): Element
    fun line(x1: Number, y1: Number, x2: Number, y2: Number): Element
    fun path(pathSpec: String): Element
    fun path(pathSpec: Array<Array<dynamic /* String | Number */>>): Element
    fun polygon(varargs: Array<Any>): Element
    fun polyline(varargs: Array<Any>): Element
    fun rect(x: Number, y: Number, width: Number, height: Number, rx: Number? = definedExternally /* null */, ry: Number? = definedExternally /* null */): Element
    fun text(x: Number, y: Number, text: String): Element
    fun text(x: Number, y: Number, text: Number): Element
    fun text(x: Number, y: Number, text: Array<dynamic /* String | Number */>): Element
    fun symbol(vbx: Number, vby: Number, vbw: Number, vbh: Number): Element
    override fun use(): Any
}
external interface `T$4` {
    var attrs: Any
    var duration: Number
    var easing: (num: Number) -> Number
    var callback: (() -> Unit)? get() = definedExternally; set(value) = definedExternally
}
external interface Set {
    fun animate(attrs: `T$0`, duration: Number, easing: ((num: Number) -> Number)? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Element
    fun animate(vararg params: `T$4`): Element
    fun attr(params: `T$1`): Element
    fun attr(param: String /* "viewBox" */): BBox
    fun attr(param: String): String
    fun bind(attr: String, callback: Function<*>): Set
    fun bind(attr: String, element: Element): Set
    fun bind(attr: String, element: Element, eattr: String): Set
    fun clear(): Set
    fun exclude(element: Element): Boolean
    fun forEach(callback: Function<*>, thisArg: Any? = definedExternally /* null */): Set
    fun pop(): Element
    fun push(el: Element): Element
    fun push(els: Array<Element>): Element
    fun remove(): Set
    fun splice(index: Number, count: Number, insertion: Array<Any>? = definedExternally /* null */): Array<Element>
}
external interface Filter {
    fun blur(x: Number, y: Number? = definedExternally /* null */): String
    fun brightness(amount: Number): String
    fun contrast(amount: Number): String
    fun grayscale(amount: Number): String
    fun hueRotate(angle: Number): String
    fun invert(amount: Number): String
    fun saturate(amount: Number): String
    fun sepia(amount: Number): String
    fun shadow(dx: Number, dy: Number, blur: Number, color: String, opacity: Number): String
    fun shadow(dx: Number, dy: Number, color: String, opacity: Number): String
    fun shadow(dx: Number, dy: Number, opacity: Number): String
}
external interface Path {
    fun bezierBBox(vararg args: Number): BBox
    fun bezierBBox(bez: Array<Number>): BBox
    fun findDotsAtSegment(p1x: Number, p1y: Number, c1x: Number, c1y: Number, c2x: Number, c2y: Number, p2x: Number, p2y: Number, t: Number): Any
    fun getBBox(path: String): BBox
    fun getPointAtLength(path: String, length: Number): Any
    fun getSubpath(path: String, from: Number, to: Number): String
    fun getTotalLength(path: String): Number
    fun intersection(path1: String, path2: String): Array<IntersectionDot>
    fun isBBoxIntersect(bbox1: BBox, bbox2: BBox): Boolean
    fun isPointInside(path: String, x: Number, y: Number): Boolean
    fun isPointInsideBBox(bbox: BBox, x: Number, y: Number): Boolean
    fun map(path: String, matrix: Matrix): String
    fun map(path: String, matrix: Any): String
    fun toAbsolute(path: String): Array<Any>
    fun toCubic(pathString: String): Array<Any>
    fun toCubic(pathString: Array<String>): Array<Any>
    fun toRelative(path: String): Array<Any>
}
external interface IntersectionDot {
    var x: Number
    var y: Number
    var t1: Number
    var t2: Number
    var segment1: Number
    var segment2: Number
    var bez1: Array<Number>
    var bez2: Array<Number>
}
