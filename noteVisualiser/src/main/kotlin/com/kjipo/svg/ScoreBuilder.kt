package com.kjipo.svg

class ScoreBuilder : ElementConsumer<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    private val bars = mutableListOf<BAR>()


    private var counter = 0


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value
        bar.scoreRenderingElements.addAll(currentElements)
        currentElements.clear()
        bars.add(bar)
    }

    override fun onNoteAdded(note: NOTE) {
        // TODO Set proper location
        val scoreRenderingElement = NoteElement(counter, note.pitch)
        scoreRenderingElement.notes.add(note)

        currentElements.add(scoreRenderingElement)
        // TODO Set correct counter value that takes into account multiple measures
        counter += note.duration
    }


    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<RenderingElement>()
        val points = mutableListOf<Point>()
        bars.forEach {
            val (renderingElement, point) = it.build()
            renderingElements.addAll(renderingElement)
            points.addAll(point)
        }

        return RenderingSequence(renderingElements, points)
    }


}