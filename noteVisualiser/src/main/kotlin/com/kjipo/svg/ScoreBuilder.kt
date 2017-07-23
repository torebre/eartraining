package com.kjipo.svg

class ScoreBuilder : ElementConsumer<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    private val bars = mutableListOf<BAR>()

    private var counter = 0


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value
        bar.scoreRenderingElements.addAll(currentElements)
        currentElements.clear()
        if(bars.isNotEmpty()) {
            // TODO Is this the correct direction? In which direction are the bars added through the method calls?
            bar.previousBar = bars.last()
            bar.scoreRenderingElements.filter { it is NoteElement }.map { it as NoteElement }.forEach { it.bar = bar }
        }
        bars.add(bar)
    }

    override fun onNoteAdded(note: NOTE) {
        // TODO Set proper location
        val scoreRenderingElement = NoteElement(note.pitch, counter, calculateVerticalOffset(note.pitch))
        scoreRenderingElement.notes.add(note)

        currentElements.add(scoreRenderingElement)
        // TODO Set correct counter value that takes into account multiple measures
        counter += note.duration
    }


    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        bars.forEach {
            renderingElements.addAll(it.build())
        }

        return RenderingSequence(renderingElements)
    }

}