package com.kjipo.score


class ScoreBuilderImpl(override val debug: Boolean = false) : ScoreBuilderInterface<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    private val scoreData = ScoreSetup()
    private var restCounter = 0
    private var noteCounter = 0


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value

        with(bar.barData) {
            scoreRenderingElements.addAll(currentElements)
            currentElements.clear()
            if (scoreData.bars.isNotEmpty()) {
                // TODO Is this the correct direction? In which direction are the bars added through the method calls?
                previousBar = scoreData.bars.last()
            }
            scoreData.bars.add(this)
        }
    }

    override fun onNoteAdded(note: NOTE): String {
        val id = "note-${noteCounter++}"
        val noteElement = NoteElement(note.note, note.octave, note.duration, id)

        currentElements.add(noteElement)
        return id
    }

    override fun onRestAdded(rest: REST) {
        val restElement = RestElement(rest.duration, "rest-${restCounter++}")

        currentElements.add(restElement)
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build() = scoreData.build()

}