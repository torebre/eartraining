var noteOnColour = "red";
var noteOffColor = "yellow";


function noteOn(noteId) {
    var noteElement = Snap.select("#" + noteId);
    noteElement.attr({
        fill: noteOnColour
    });
}

function noteOff(noteId) {
    var noteElement = Snap.select("#" + noteId);
    noteElement.attr({
        fill: noteOffColor
    });
}